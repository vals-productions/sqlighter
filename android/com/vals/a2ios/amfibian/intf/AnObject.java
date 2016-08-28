package com.vals.a2ios.amfibian.intf;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Map;

/**
 * Created by vsayenko on 1/8/16.
 *
 * AmfibiaN Object management interface.
 *
 */
public interface AnObject<T> {

    /**
     *
     * @throws Exception
     */
    public void resetNativeObject() throws Exception;

    /**
     *
     * @return
     */
    public Class<T> getNativeClass();

    /**
     *
     * @return
     */
    public Map<String, AnAttrib> getAllAttribMap();

    /**
     *
     * @param attribName
     * @return
     */
    public AnAttrib getAttrib(String attribName);

    /**
     *
     * @param anAttrib
     */
    public void addAttrib(AnAttrib anAttrib);

    /**
     * Returns <String (parameter name), value> map of object's attributes.
     * This might be useful if you'd like to build HTTP request's GET/POST
     * parameters.
     *
     * @param nativeObject - native object to convert to the map
     * @return
     * @throws Exception
     */
    public Map<String, Object> asNativeMap(T nativeObject) throws Exception;

    /**
     * Use asNativeMap
     *
     * @param nativeObject
     * @return
     * @throws Exception
     */
    @Deprecated
    public Map<String, Object> getMap(T nativeObject) throws Exception;

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

    /**
     *
     * @return
     */
    public T getNativeObject();

    /**
     *
     * @return
     * @throws Exception
     */
    public Map<String, Object> asJsonMap() throws Exception;

    /**
     *
     * @param anObjClass
     */
    public void setNativeClass(Class<T> anObjClass);

    /**
     *
     * @param o
     * @throws Exception
     */
    public void setNativeObject(T o) throws Exception;

    /**
     *
     * @return
     */
    AnAttrib[] getAllAttribs();

    /**
     *
     * @return
     */
    AnObject getParentAnObject();

    /**
     *
     * @return
     */
    AnAttrib[] getOwnAttribs();

    /**
     *
     * @param objects
     * @return
     * @throws Exception
     */
    JSONArray asJSONArray(Collection<T> objects) throws Exception;

    /**
     *
     * @param objects
     * @return
     * @throws Exception
     */
    String asJsonArrayString(Collection<T> objects) throws Exception;


    public static interface CustomConverter {
        public Object convert(AnAttrib attrib, Object value);
    }

    /**
     *
     * @return
     */
    CustomConverter getJsonCustomSetConverter();

    /**
     *
     * @return
     */
    void setJsonCustomSetConverter(CustomConverter objectCustomSetConverter);

    /**
     *
     * @return
     */
    public CustomConverter getJsonCustomGetConverter();

    /**
     *
     * @return
     */
    public void setJsonCustomGetConverter(CustomConverter objectCustomGetConverter);
}