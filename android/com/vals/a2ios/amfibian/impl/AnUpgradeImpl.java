package com.vals.a2ios.amfibian.impl;

import com.vals.a2ios.amfibian.intf.AnSql;
import com.vals.a2ios.amfibian.intf.AnUpgrade;
import com.vals.a2ios.sqlighter.intf.SQLighterDb;
import com.vals.a2ios.sqlighter.intf.SQLighterRs;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by vsayenko on 12/18/15.
 *
 * Base implementation of AnUpgrade interface.
 *
 */
public abstract class AnUpgradeImpl implements AnUpgrade {

    private Map<Integer, List<String>> map;
    private SQLighterDb sqlighterDb;
    private AnOrmImpl<Upgrade> anOrm;
    private String lastKey, recoverKey = AnUpgrade.DB_RECOVER_KEY;

    /**
     *
     * @param sqLighterDb
     * @throws Exception
     */
    public AnUpgradeImpl(SQLighterDb sqLighterDb) throws Exception {
        this.sqlighterDb = sqLighterDb;
        anOrm = new AnOrmImpl<>(
                sqLighterDb,
                getTableName(),
                Upgrade.class,
                new String[] {"key","value","createDate,create_date"},
                null);
        ensureStorage(false);
    }

    /**
     *
     * @param key
     * @return
     */
    public abstract List<Object> getTasksByKey(String key);

    /**
     *
     * @return
     */
    public abstract List<String> getUpdateKeys();

    /**
     *
     * @return
     */
    protected String getTableName() {
        return Upgrade.TABLE;
    }

    /**
     *
     * @throws Exception
     */
    private void ensureStorage(boolean isForceRecreate) throws Exception {
        if(isForceRecreate) {
            sqlighterDb.executeChange("drop table if exists " + Upgrade.TABLE);
        }
        SQLighterRs rs = sqlighterDb.executeSelect(
                "SELECT name FROM sqlite_master " +
                "WHERE type='table' " +
                "ORDER BY name");
        boolean isFound = false;
        while (rs.hasNext()) {
            String tableName = rs.getString(0);
            if (tableName.equals(getTableName())) {
                isFound = true;
                break;
            }
        }
        rs.close();
        if (!isFound) {
            String createSql = anOrm.startSqlCreate().getQueryString();
            sqlighterDb.executeChange(createSql);
        }
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public Set<String> getAppliedUpdates() throws Exception {
        Set<String> keys = new HashSet<>();
        SQLighterRs rs = sqlighterDb.executeSelect("select key from " + Upgrade.TABLE + " where value is not null");
        while (rs.hasNext()) {
            String key = rs.getString(0);
            keys.add(key);
        }
        rs.close();
        return keys;
    }

    /**
     *
     * @throws Exception
     */
    @Override
    public int applyUpdates() throws Exception {
        int taskCount = 0;
        Set<String> appliedKeys = getAppliedUpdates();
        for (String updKey: getUpdateKeys()) {
            lastKey = updKey;
            if(!appliedKeys.contains(updKey)) {
                if(!applyUpdate(updKey, getTasksByKey(updKey))) {
                    /**
                     * failure during db upgrade
                     */
                    return -1;
                }
                taskCount++;
            }
        }
        return taskCount;
    }

    /**
     *
     * @param key
     * @param statementList
     * @throws Exception
     */
    protected boolean applyUpdate(String key, List<Object> statementList) {
        try {
            sqlighterDb.beginTransaction();

            for (Object task : statementList) {
                String sqlStr = null;
                if (task instanceof String) {
                    /**
                     * Run raw SQL query.
                     */
                    sqlStr = (String) task;
                    sqlighterDb.executeChange(sqlStr);
                } else if (task instanceof AnSql<?>) {
                    /**
                     * Auto create AnObject
                     */
                    AnOrmImpl<?> createObjectTask = (AnOrmImpl<?>) task;
                    createObjectTask.setSqlighterDb(sqlighterDb);
                    createObjectTask.startSqlCreate();
                    sqlStr = createObjectTask.getQueryString();
                    sqlighterDb.executeChange("drop table if exists " +
                            createObjectTask.getTableName());
                    sqlighterDb.executeChange(sqlStr);
                }
                /**
                 * Log upgrade step
                 */
                Upgrade appUpdate = new Upgrade();
                appUpdate.setKey(key);
                appUpdate.setValue(sqlStr);
                appUpdate.setCreateDate(new Date());
                appUpdate.setStatus(1);
                anOrm.startSqlInsert(appUpdate);
                anOrm.apply();
            } // end for

            /**
             * Mark key as success
             */
            logKey(key, 1);

            sqlighterDb.commitTransaction();
            /*
            Success
             */
            return true;
        } catch (Throwable t) {
            try {
                /**
                 * Something failed during DB upgrade.
                 * Let's try to rollback.
                 */
                sqlighterDb.rollbackTransaction();
            } catch (Throwable rollbackExcp) {
            }
            try {
                /**
                 * Log the key as failure.
                 */
                logKey(key, 0);
            } catch (Throwable failureMarkExcp) {
            }
            /**
             * Something failed
             */
            return false;
        }
    }

    private void logKey(String key, Integer status) throws Exception {
        Upgrade appUpdateMark = new Upgrade();
        appUpdateMark.setKey(key);
        appUpdateMark.setStatus(status);
        appUpdateMark.setCreateDate(new Date());
        anOrm.startSqlInsert(appUpdateMark);
        anOrm.apply();
    }

    @Override
    public void attemptToRecover() throws Exception {
        List<Object> recoverTasks = getTasksByKey(recoverKey);
        if(recoverTasks.size() > 0) {
            /**
             * recover key tasks provided
             */
            ensureStorage(true); // drop/create db upgrade log table
            for(String key: getUpdateKeys()) {
                if(key.equals(recoverKey)) {
                    applyUpdate(key, recoverTasks);
                    continue;
                }
                logKey(key, 0);
            }
        }
    }

    @Override
    public void setRecoverKey(String recoverKey) {
        this.recoverKey = recoverKey;
    }

    public String getLastKey() {
        return lastKey;
    }

    /**
     * Created by vsayenko on 12/23/15.
     */
    public static class Upgrade {

        public static final String TABLE = "app_db_maint";

        private String key;
        private String value;
        private Date createDate;
        private Integer status;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public Date getCreateDate() {
            return createDate;
        }

        public void setCreateDate(Date createDate) {
            this.createDate = createDate;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }
    }
}
