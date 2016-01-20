package com.vals.a2ios.amfibian.intf;

import java.util.List;
import java.util.Set;

/**
 * Created by vsayenko on 1/7/16.
 *
 * AnUpgrade maintains database structural
 * changes as needed. Every version update has updateKey associated with it. Multiple update statements may be associated with the version update. AnUpgrade logs changes that had been applied in a table named "app_db_maint" by default.
 */
public interface AnUpgrade {
    /**
     * This method goes through available update keys and applies statements associated with them as needed.
     * @throws Exception
     */
    int applyUpdates() throws Exception;

    /**
     * Sets the global list of available updateKeys. The key is just a unique String constant that marks a group of SQL statements. Updates are applied in the order of keys in the List.
     * @param updateKeys  - sequential list of update keys.
     */
    void setUpdateKeys(List<String> updateKeys);

    /**
     * Gets the global list of available keys.
     * @return
     */
    List<String> getUpdateKeys();

    /**
     * Returns a set of updateKeys that had already been applied to the database. 
     * @return
     * @throws Exception
     */
    Set<String> getAppliedUpdates() throws Exception;

    /**
     * TODO: rename to *Tasks*. This method is abstract and you sould implement it for your tasks.
     * @param key
     * @return
     */
    List<Object> getTaskByKey(String key);

}
