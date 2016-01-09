package com.vals.a2ios.amfibian.intf;

import com.vals.a2ios.amfibian.intf.AnAttrib;

import org.json.JSONObject;

import java.util.Collection;
import java.util.Map;

/**
 * Created by vsayenko on 1/8/16.
 */
public interface AnObject<T> {
    public void resetNativeObject() throws Exception;

    public Class<T> getNativeClass();

    // @SuppressWarnings("unchecked")
    public Map<String, AnAttrib> getAttribList();

    public AnAttrib getAttrib(String propertyName);

    public void addAttrib(AnAttrib anAttribMapper);

    /**
     * Returns <String (parameter name), value> map of object's attributes.
     * This might be useful if you'd like to build HTTP request's GET/POST
     * parameters.
     *
     * @param nativeObject - native object to convert to the map
     * @return
     * @throws Exception
     */
    public Map<String, Object> asMap(T nativeObject) throws Exception;

    /**
     * Transfroms native object into a org.json.JSONObject
     * @param nativeObject
     * @return org.json.JSONObject representation of an object
     *
     * @throws Exception
     */
    public JSONObject asJSONObject(T nativeObject) throws Exception;

    /**
     * Transforms org.json.JSONObject into a native object.
     *
     * @param jsonObject
     * @return T - native object
     * @throws Exception
     */
    public T asNativeObject(JSONObject jsonObject) throws Exception;

    /**
     * JSON String is converted into a native object
     *
     * @param jsonString
     * @return
     * @throws Exception
     */
    public T asNativeObject(String jsonString) throws Exception;

    /**
     * JSON String representing an array of T is being transformed into
     * a collection<T>
     *
     * @param jsonArrayString
     * @return
     * @throws Exception
     */
    public Collection<T> asList(String jsonArrayString) throws Exception;

    /**
     * Native object is converted into a JSON string
     *
     * @param nativeObject
     * @return JSON string
     * @throws Exception
     */
    public String asJsonString(T nativeObject) throws Exception;

    public T getNativeObject();

    public Map<String, Object> getJsonMap() throws Exception;

    public void setNativeClass(Class<T> anObjClass);

    public void setNativeObject(T o) throws Exception;
}
