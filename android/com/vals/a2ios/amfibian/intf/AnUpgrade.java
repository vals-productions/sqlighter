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
 * version update. AnUpgrade logs changes
 * that had been applied in a table named
 * "app_db_maint" by default. The table will be automatically created if does not exists.
 *
 */
public interface AnUpgrade {
    /**
     * For the case sequential DB upgrade fails, specify recovery key set of statements. Recovery key is
     * supposed to contain statements to recreate DB from
     * the beginning in its latest structural state. You may choose to execute a series of ```DROP``` statements followed by ```CREATE``` statements, or, delete existing file, create new one, and execute ```CREATE``` statements from there.
     */
    public static final String DB_RECOVER_KEY = "recoveryKey";
    /**
     * This method goes through available update
     * keys and applies statements associated with
     * them as needed.
     *
     * It skips the recovery key.
     *
     * @throws Exception
     */
    int applyUpdates() throws Exception;

    /**
     * Attempts to apply recovery key tasks. At the same time this method will mark all other existing keys as executed. There is no need to execute anything else if the database is in its latest. 
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
     * Gets you value of the last executed key. You might need this to know which key has failed.
     * @return
     */
    String getLastKey();

    /**
     * Sets the global list of available updateKeys.
     * The key is just a unique String constant that
     * marks a group of SQL statements. 
     *
     * @param updateKeys  - sequential list of update keys.
     */
    void setUpdateKeys(List<String> updateKeys);

    /**
     * Gets the global list of available keys. This method is called by AnUpgrade in the process of database upgrade application (applyUpdates ();). Updates are applied in the order of keys in the List. AnUpgrade checks every key against already applied upgrade keys (stored in the "app_db_maint" table). If the key hasn't been applied before, AnUpgrade will request the list of statements associated with the key, and attempt to apply them.
     * @return
     */
    List<String> getUpdateKeys();

    /**
     * Returns a set of updateKeys that had already
     * been applied to the database.
     *
     * @return
     * @throws Exception
     */
    Set<String> getAppliedUpdates() throws Exception;

    /**
     * @param key
     * @return
     */
    List<Object> getTasksByKey(String key);

}
