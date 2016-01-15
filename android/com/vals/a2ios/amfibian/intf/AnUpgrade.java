package com.vals.a2ios.amfibian.intf;

import java.util.List;
import java.util.Set;

/**
 * Created by developer on 1/7/16.
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

    void setUpdateKeys(List<String> updateKeys);

    List<String> getUpdateKeys();

    Set<String> getAppliedUpdates() throws Exception;

}
