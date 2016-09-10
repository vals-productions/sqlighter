package com.vals.a2ios.amfibian.intf;

import com.vals.a2ios.amfibian.intf.AnOrm;

import org.json.JSONException;

import java.util.Map;

/**
 * Allows to load definitions from json file/string.
 * Adds ability to control AmfibiaN object creation.
 *
 * At the moment this implementation is not stable.
 *
 * Created by vsayenko
 */
public interface AnIncubator {

    /**
     *
     * @param jsonString
     * @throws JSONException
     */
    void load(String jsonString) throws Exception;

    /**
     *
     * @param name
     * @return
     * @throws Exception
     */
    AnOrm make(String name) throws Exception;

    /**
     *
     * @param cluss
     * @param <T>
     * @return
     * @throws Exception
     */
    <T> AnOrm<T> make(Class<T> cluss) throws Exception;

    /**
     *
     * @param name
     * @return
     */
    Class<?> getClassByName(String name);
}
