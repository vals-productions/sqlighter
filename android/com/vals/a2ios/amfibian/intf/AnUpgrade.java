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
     * In case sequential DB upgrade fails, check
     * if recovery key is provided, apply. Recovery key is
     * supposed to contain statements to recreate DB from
     * the beginning in its latest structural state.
     */
    public static final String DB_RECOVER_KEY = "recoveryKey";
    /**
     * This method goes through available update
     * keys and applies statements associated with
     * them as needed.
     *
     * @throws Exception
     */
    int applyUpdates() throws Exception;

    /**
     * TBD
     * @throws Exception
     */
    void attemptToRecover() throws Exception;

    /**
     * TBD
     * @param recoverKey
     */
    void setRecoverKey(String recoverKey);

    /**
     * TBD
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
