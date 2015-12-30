package com.vals.a2ios.amfibian.impl;

import com.vals.a2ios.sqlighter.intf.SQLighterDb;
import com.vals.a2ios.sqlighter.intf.SQLighterRs;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * AnUpgrade maintains database structural
 * changes as needed.
 *
 * Created by vsayenko on 12/18/15.
 */
public abstract class AnUpgrade {

    private Map<Integer, List<String>> map;
    private SQLighterDb sqlighterDb;
    private AnOrm<Upgrade> ap;

    public AnUpgrade(SQLighterDb sqLighterDb) throws Exception {
        this.sqlighterDb = sqLighterDb;
        ap = new AnOrm<>(
                sqLighterDb,
                getTableName(),
                Upgrade.class,
                new String[] { "key", "value" },
                null);
        ensureStorage();
    }

    protected abstract List<Object> getTaskByKey(String key);
    protected abstract List<String> getAllKeys();

    protected String getTableName() {
        return Upgrade.TABLE;
    }

    private void ensureStorage() throws Exception {
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
            String createSql = ap.startSqlCreate().getQueryString();
            sqlighterDb.executeChange(createSql);
        }
    }

    private Set<String> getAppliedTasks() throws Exception {
        Set<String> keys = new HashSet<>();
        SQLighterRs rs = sqlighterDb.executeSelect("select key from " + Upgrade.TABLE + " where value is not null");
        while (rs.hasNext()) {
            String key = rs.getString(0);
            keys.add(key);
        }
        rs.close();
        return keys;
    }

    public void applyTasks() throws Exception {
        Set<String> appliedKeys = getAppliedTasks();
        for (String updKey: getAllKeys()) {
            if(!appliedKeys.contains(updKey)) {
                applyUpdate(updKey, getTaskByKey(updKey));
            }
        }
    }

    private void applyUpdate(String key, List<Object> statementList) throws Exception {
        sqlighterDb.beginTransaction();

        for (Object task: statementList) {
            String sqlStr = null;
            if (task instanceof String) {
                sqlStr = (String) task;
                sqlighterDb.executeChange(sqlStr);
            } else if (task instanceof AnSql<?>) {
                AnOrm<?> sql = (AnOrm<?>)task;
                sql.setSqlighterDb(sqlighterDb);
                sql.startSqlCreate();
                sqlStr = sql.getQueryString();
                sqlighterDb.executeChange(sqlStr);
            }
            Upgrade appUpdate = new Upgrade();
            appUpdate.setKey(key);
            appUpdate.setValue(sqlStr);
            ap.startSqlInsert(appUpdate);
            ap.apply();
        }

        Upgrade appUpdateMark = new Upgrade();
        appUpdateMark.setKey(key);
        ap.startSqlInsert(appUpdateMark);
        ap.apply();

        sqlighterDb.commitTransaction();
    }

    /**
     * Created by developer on 12/23/15.
     */
    public static class Upgrade {

        public static final String TABLE = "app_db_maint";

        private String key;
        private String value;

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

    }
}
