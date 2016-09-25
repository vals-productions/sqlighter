package com.vals.a2ios.amfibian.intf;

import com.vals.a2ios.amfibian.intf.AnOrm;
import com.vals.a2ios.sqlighter.intf.SQLighterDb;

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

    void unload();

    boolean isLoaded();

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
    Class<?> getClassByName(String name) throws Exception;

    String getAssociationTrgClassName(Class cluss, AnAttrib attrib) throws Exception ;

    String getAssociationTrgJoinAttribName(Class cluss, AnAttrib attrib) throws Exception ;

    String getAssociationSrcJoinAttribName(Class cluss, AnAttrib attrib) throws Exception ;

    String getAssociationSrcAttribName(Class cluss, AnAttrib attrib) throws Exception ;

    SQLighterDb getSqLighterDb();

    void setSqLighterDb(SQLighterDb sqLighterDb);
}
