package com.vals.a2ios.amfibian.intf;

import java.util.List;
import java.util.Set;

/**
 * Created by vsayenko on 1/7/16.
 *
 * AnUpgrade maintains database structural
 * changes as needed. Every version update has
 * updateKey associated with it. Multiple update
 * statements may be associated with the
 * version update.   AnUpdrade maintains your database schema ("user" changes) as well as its own ("private") changes to the table (s) that maintain change logs.  AnUpgrade logs changes that had been applied to a table named "app_db_maint" by default. The table will be automatically created if does not exists.
 *
 */
public interface AnUpgrade {

    /**
     * For the case sequential DB upgrade fails, specify
     * recovery key set of statements. Recovery key is
     * supposed to contain statements to recreate DB from
     * the beginning in its latest structural state.
     * You may choose to execute a series of ```DROP```
     * statements followed by ```CREATE``` statements,
     * or, delete existing file, create new one, and
     * execute ```CREATE``` statements from there. Also check getUpdateKeys method doc. for additional DB recover key information.
     */
    public static final String DB_RECOVER_KEY = "recoveryKey";

    /**
     * Table name for db upgrade logging
     */
    public static final String TABLE_NAME = "app_db_maint";

    /**
     * This method goes through available update
     * keys and applies statements associated with
     * them as needed.
     *
     * It skips the recovery key.
     *
     * @throws Exception - in case of unforeseen failure.
     * @return number of updates successfully applied (>= 0).
     * -1 in case of failure. Failure means that fro some reason
     * some SQL statement failed. Time to think of DB recovery
     * strategy execution.
     */
    int applyUpdates() throws Exception;

    /**
     * This method goes through available update
     * keys and searches for DB RECOVER KEY.
     *
     * Attempts to apply recover key tasks.
     *
     * At the same time this method will mark
     * all other existing keys as executed.
     *
     * There is no need to execute anything
     * else if the database is in its latest.
     *
     * @return 
     * @throws Exception
     */
    int attemptToRecover() throws Exception;

    /**
     * Lets you override recoverKey value.
     * @param recoverKey
     */
    void setRecoverKey(String recoverKey);

    /**
     * Gets the global list of available keys. This method
     * is called by AnUpgrade in the process of database
     * upgrade application (applyUpdates ();). Updates are
     * applied in the order of keys in the List. AnUpgrade
     * checks every key against already applied upgrade keys
     * (stored in the "app_db_maint" table). If the key hasn't
     * been applied before, AnUpgrade will request the list
     * of statements associated with the key, and attempt
     * to apply them.
     *
     * The DB RECOVER KEY is a special key and is not being
     * executed during normal upgrades. It still needs to be included in the list of update keys returned by this method. This lets you enable or disable the key. 
     *
     * @return
     */
    List<String> getUpdateKeys();

    /**
     *
     * @param updateKey
     */
    void setUpdateKeys(List<String> updateKey);

    /**
     * Returns a set of updateKeys that had already
     * been applied to the database.
     *
     * @return
     * @throws Exception
     */
    Set<String> getAppliedUpdates() throws Exception;

    /**
     *
     * @param key
     * @return
     */
    List<Object> getTasksByKey(String key);

}
