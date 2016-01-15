package com.vals.a2ios.amfibian.intf;

import java.util.List;
import java.util.Set;

/**
 * Created by vsayenko on 1/7/16.
 *
 * AnUpgrade maintains database structural
 * changes as needed.
 */
public interface AnUpgrade {
    /**
     *
     * @throws Exception
     */
    int applyUpdates() throws Exception;

    /**
     *
     * @param updateKeys
     */
    void setUpdateKeys(List<String> updateKeys);

    /**
     *
     * @return
     */
    List<String> getUpdateKeys();

    /**
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
    List<Object> getTaskByKey(String key);

}
