package com.vals.a2ios.amfibian.impl;

import com.vals.a2ios.amfibian.intf.AnSql;
import com.vals.a2ios.amfibian.intf.AnUpgrade;
import com.vals.a2ios.sqlighter.intf.SQLighterDb;
import com.vals.a2ios.sqlighter.intf.SQLighterRs;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
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
    protected SQLighterDb sqlighterDb;
    private String recoverKey = AnUpgrade.DB_RECOVER_KEY;
    private String logTableName = TABLE_NAME;
    protected AnOrmImpl<Upgrade> anOrm;
    private List<Upgrade> delayedLogs = new LinkedList<Upgrade>();

    /**
     * The version that creates AnUpgrade with overrides.
     * @param sqLighterDb
     * @param tableName - db change log table name if desired to be different than the default name
     * @param recoveryKey - special key to execute in case DB upgrade fails, if desired to be different.
     *
     * @throws Exception
     */
    public AnUpgradeImpl(SQLighterDb sqLighterDb, String tableName, String recoveryKey) throws Exception {
        this(sqLighterDb);
        this.logTableName = tableName;
        this.recoverKey = recoveryKey;
    }

    /**
     *
     * @param sqLighterDb
     * @throws Exception
     */
    public AnUpgradeImpl(SQLighterDb sqLighterDb) throws Exception {
        this.sqlighterDb = sqLighterDb;
        anOrm = new AnOrmImpl<Upgrade>(
                sqLighterDb,
                getLogTableName(),
                Upgrade.class,
                new String[] {
                        "key",
                        "value",
                        "createDate,create_date",
                        "status",
                        "type",
                        "refi",
                        "refd",
                        "refs"
                },
                null);
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
    public String getLogTableName() {
        return logTableName;
    }

    public void setLogTableName(String logTableName) {
        this.logTableName = logTableName;
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public Set<String> getAppliedUpdates() throws Exception {
        Set<String> keys = new HashSet<String>();
        if(!findTable(getLogTableName())) {
            return keys;
        }
        /**
         * Retrieve all update keys, regardless of the status
         * of the update. We will not retry whatever already
         * failed.
         */
        SQLighterRs rs = sqlighterDb.executeSelect("select key from " + TABLE_NAME + " where type = 0");
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
        if(!findTable(getLogTableName())) {
            int returnCode = attemptToRecover(0); // private updates
            if(returnCode == -1) {
                return returnCode;
            }
        }
        int taskCount = applyUpdates(0); // private updates
        if(taskCount == -1) {
            return taskCount;
        }
        taskCount = applyUpdates(1); // public updates
        return taskCount;
    }

    /** 
     * Applies all available updates for either private (AnUpgrade internal storage upgrades) or public - user DB schema updates.
     *
     * @param privatePublic - either 0 (priv) or 1 (public)
     * @throws Exception
     */
    private int applyUpdates(int privatePublic) throws Exception {
        int taskCount = 0;
        /**
         * Get the list of update keys that already
         * had been applied.
         */
        Set<String> appliedKeys = getAppliedUpdates();
        List<String> keys = (privatePublic == 0) ? getPrivateUpdateKeys():getUpdateKeys();
        for (String updKey: keys) {
            /**
             * Skip DB recovery key, we do not use them during normal
             * operations.
             */
            if(updKey.equals(recoverKey)) {
                continue;
            }
            /**
             * For every available non recovery key.
             */
            if(!appliedKeys.contains(updKey)) { // exclude already applied keys
                List<Object> tasks =
                        (privatePublic == 0) ?
                                getPrivateTasksByKey(updKey):
                                getTasksByKey(updKey);
                if(!applyUpdate(updKey, tasks)) {
                    /**
                     * failure during db upgrade
                     */
                    return -1;
                }
                /**
                 * Success, try next key.
                 */
                taskCount++;
            }
        }
        return taskCount;
    }

    /** 
     *
     * Apply single DB Update step.
     *
     * @param key
     * @param statementList
     *
     * @return true if no errors, false if failed
     */
    protected boolean applyUpdate(String key, List<Object> statementList) {
        try {
            for (Object task : statementList) {
                String sqlStr = null;
                Long result = null;
                if (task instanceof String) {
                    /**
                     * Run raw SQL query.
                     */
                    sqlStr = (String) task;
                    result = sqlighterDb.executeChange(sqlStr);
                } else if (task instanceof AnSql<?>) {
                    /**
                     * Auto create AnObject
                     */
                    AnOrmImpl<?> createObjectTask = (AnOrmImpl<?>) task;
                    createObjectTask.setSqlighterDb(sqlighterDb);
                    createObjectTask.startSqlCreate();
                    sqlighterDb.executeChange("drop table if exists " +
                            createObjectTask.getTableName());
                    result = createObjectTask.apply();
                }
                /**
                 * Log upgrade step
                 */
                logUpgradeStep(key, sqlStr, result);
            } // end for

            /**
             * Mark key as success
             */
            logKey(key, 1);

            /*
                Success
             */
            return true;
        } catch (Throwable t) {
            try {
                /**
                 * Log the key as failure if possible
                 */
                logKey(key, 0);
            } catch (Throwable failureMarkExcp) {
                // it's not ok, but lets continue
            }
            /**
             * Something failed
             */
            return false;
        }
    }

    @Override
    public int attemptToRecover() throws Exception {
        /*
         * First apply internal/private recovery so
         * that we have db support for logging updates
         */
        int returnCode = attemptToRecover(0); // private
        if(returnCode == -1) {
            return returnCode;
        }
        // then user updates
        returnCode  = attemptToRecover(1); // public
        return returnCode;
    }

    /**
     * Attempts to execute recover tasks for either private or public scope.
     *
     * @throws Exception 
     *
     * @return task count >= 0 in case of success, or -1 in case of failure
     */
    private int attemptToRecover(int privatePublic) throws Exception {
        List<Object> recoverTasks =
                (privatePublic == 0) ?
                        getPrivateTasksByKey(PRIVATE_REC_KEY) :
                        getTasksByKey(recoverKey);
        //TODO rename to task count                
        int rc = 0;
        if(recoverTasks.size() > 0) {
            /**
             * recover key tasks provided
             */
            List<String> keys = (privatePublic == 0) ? getPrivateUpdateKeys() : getUpdateKeys();
            for(String key: keys) {
                String recKey = (privatePublic == 0) ? PRIVATE_REC_KEY:recoverKey;
                if(key.equals(recKey)) {
                    boolean result = applyUpdate(key, recoverTasks);
                    if(!result) {
                        // failed to apply, at this point
                        // we just report failure and do
                        // not attempt to apply any more
                        // incremantal updates on top of it.
                        return -1;
                    }
                    // success
                    rc++;
                }
                if(!key.equals(recoverKey)) {
                    /*
                     * enter non recovery key so that we
                     * do not apply it again
                     */
                    logKey(key, 0);
                }
            }
        }
        return rc;
    }

    @Override
    public void setRecoverKey(String recoverKey) {
        this.recoverKey = recoverKey;
    }

    /**
     * Logs a single upgrade step within upgrade key.
     * @throws Exception
     */
    private void logUpgradeStep(String key, String sqlStr, Long result) throws Exception {
        System.out.println("result: " + result + " for " + sqlStr);
        Upgrade appUpdate = new Upgrade();
        appUpdate.setKey(key);
        appUpdate.setValue(sqlStr);
        appUpdate.setCreateDate(new Date());
        appUpdate.setStatus(1);
        appUpdate.setType(1);
        saveLog(appUpdate);
    }

    /**
     * Logs upgrade key.
     * @throws Exception
     */
    private void logKey(String key, Integer status) throws Exception {
        Upgrade appUpdateMark = new Upgrade();
        appUpdateMark.setKey(key);
        appUpdateMark.setStatus(status);
        appUpdateMark.setCreateDate(new Date());
        appUpdateMark.setType(0);
        saveLog(appUpdateMark);
    }

    /**
     * Either inserts, or adds Upgrade entity to delay log in case there's no db storage for log entries (yet).
     * @throws Exception
     */
    private void saveLog(Upgrade appUpdateEntry) throws Exception {
        if (!findTable(getLogTableName())) {
            delayedLogs.add(appUpdateEntry);
        } else {
            for (Upgrade upgradeLog: delayedLogs) {
                anOrm.startSqlInsert(upgradeLog);
                anOrm.apply();
            }
            delayedLogs.clear();
            anOrm.startSqlInsert(appUpdateEntry);
            anOrm.apply();
        }
    }

    /**
     * Checks if table exists.
     * @throws Exception
     */
    private boolean findTable(String searchTableName) throws Exception {
        SQLighterRs rs = sqlighterDb.executeSelect(
                "SELECT name FROM sqlite_master " +
                        "WHERE type='table' " +
                        "ORDER BY name");
        boolean isFound = false;
        while (rs.hasNext()) {
            String tableName = rs.getString(0);
            if (tableName.equals(getLogTableName())) {
                isFound = true;
                break;
            }
        }
        rs.close();
        return isFound;
    }

    /** 
     * Entity for upgrade auditing. 
     *
     * Reflects "app_db_maint" table structure.
     *
     * Created by vsayenko on 12/23/15.
     */
    public static class Upgrade {
        /**
         * Upgrade key
         */
        private String key;
        
        /**
         * NULL if the record contains upgrade key mark
         *
         * SQL statement if the record contais upgrade key step
         */
        private String value;
        
        /**
         *
         */
        private Date createDate;
        
        /**
         * 0 - failed, 1 - success
         */
        private Integer status; 
        
        /**
         * 0 - key , 1 - statement under the key
         */
        private Integer type;
        
        /**
         * reserved
         */
        private Integer refi;
        
        /**
         * reserved
         */
        private Double refd;
        
        /**
         * reserved
         */
        private Integer refs;

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

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }

        public Integer getRefi() {
            return refi;
        }

        public void setRefi(Integer refi) {
            this.refi = refi;
        }

        public Double getRefd() {
            return refd;
        }

        public void setRefd(Double refd) {
            this.refd = refd;
        }

        public Integer getRefs() {
            return refs;
        }

        public void setRefs(Integer refs) {
            this.refs = refs;
        }
    }

    /**
     * Private operations variables. 
     *
     * Private operations manage internal 
     * log audit storage structure. Normally this feature
     * should not be abused.
     *
     * In case internal or user DB upgrade fails it is unsafe to continue
     * normal business operations. It is safer to recreate DB structure to
     * the latest state, ask app. user to log-in and accumulate\synchronize data 
     * as it goes.
     *
     * In this case AnUpgrade will re-create its internal log audit structure first by
     * executing private\internal recovery key tasks.
     *
     * Next, it will execute user recovery key.
     *
     * Since recovery keys are responsible for building DB structure up to its
     * latest state, all other available update keys will be marked as applied
     * without their actual application.
     *
     */

    /**
     * Prefix to use for all internal augit keys. Should
     * not interfere with user keys.
     */
    private static final String PRIVATE_PREFIX = "an-upg-";
    
    /**
     * recovery key that contains tasks to recreate internal audit 
     * db structure.
     */
    private static final String PRIVATE_REC_KEY = PRIVATE_PREFIX + DB_RECOVER_KEY;
    /**
     * actual update key
     */
    private static final String PRIVATE_KEY1 = PRIVATE_PREFIX + "init-1";
    /**
     *
     */
    private List<String> getPrivateUpdateKeys() {
        return Arrays.asList(PRIVATE_KEY1, PRIVATE_REC_KEY);
    }

    /**
     * Internal\private key area. Be careful here.
     */
    protected List<Object> getPrivateTasksByKey(String key) {
        List<Object> tasks = new LinkedList<Object>();
        if(PRIVATE_REC_KEY.equals(key)) {
            /**
             *  Initial DB state. 
             */
            tasks.add(anOrm); // executes "drop if exists" followed bu "create table"
            // performance index.
            tasks.add("create index " + getLogTableName() + "_idx on " + getLogTableName() +
                            "(key, type, status)");
        } else if(PRIVATE_KEY1.equals(key)) {
            /**
             * Adding columns for future use and faster search with the index.
             */
            tasks.add("alter table " + getLogTableName() + " add column type INTEGER");
            tasks.add("alter table " + getLogTableName() + " add column refi INTEGER");
            tasks.add("alter table " + getLogTableName() + " add column refd REAL");
            tasks.add("alter table " + getLogTableName() + " add column refs TEXT");
            tasks.add("create index " +
                        getLogTableName() + "_idx on " +
                        getLogTableName() + "(key, type, status)");
            tasks.add("update " + getLogTableName() + " set type = 0 where value is null");
            tasks.add("update " + getLogTableName() + " set type = 1 where value is not null");
        }
        return tasks;
    }

}