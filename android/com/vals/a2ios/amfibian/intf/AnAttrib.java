package com.vals.a2ios.amfibian.intf;

import java.lang.reflect.Method;

/**
 * Created by vsayenko on 9/26/15.
 *
 * AmfibiaN AnObjectImpl's attribute descriptor/handler.
 *
 * It is used for JSON/Database/Native object conversion
 * mappings. Simple way of thinking of this  - it is a
 * mapper between native object's attributes and their
 * JSON and Database counterparts. Also, it might define
 * some custom converters between respective attribute
 * values in case default converters do not work for you.
 * Attributes of simple data types are supported. Also, Date is supported. BLOB columns are not supported. BLOBs should be retrieved using SQLighter functionality.
 */
public interface AnAttrib {

    /**
     * Sets an object associated with native object.
     * @param anObject
     */
    void setAnObject(AnObject<?> anObject);

    /**
     * 
     * @return attribute name
     */
    String getAttribName();

    /**
     * Sets attribute name
     * @param attribName
     */
    void setAttribName(String attribName);

    /**
     * Json object attribute name, or null if undefined
     * @return
     */
    String getJsonName();

    /**
     *
     * @param jsonName
     */
    void setJsonName(String jsonName);

    /**
     * 
     * @return DB column name, or null if undefined.
     */
    String getColumnName();

    /**
     * Specifies DB column name associated with this atribute.
     * @param columnName
     */
    void setColumnName(String columnName);

    /**
    * Sets native object's attribute value
    *
    * @param value - value to set. Sometimes it may not directly match
    * destination value type. For this case there will be an attempt to
    * auto match the value, or, custom converter might be supplied
    */
    void setValue(Object value) throws Exception;

    /**
     * Retrieves attribute value fron associated native object using
     * supplied converter
     * @return attribute value
     * @throws Exception in case value extraction experiences issues
     */
    Object getValue(AnAdapter converter) throws Exception;

    /**
     * Retrieves attribute value fron associated native object.
     * @return attribute value
     * @throws Exception in case value extraction experiences issues
     */
    Object getValue() throws Exception;

    /**
     * Returns getter method object for the attribute.
     * @return
     */
    Method getGetter();

    /**
     * Returns setter method object for the attribute.
     * @return
     */
    Method getSetter();

    /**
     *
     * @return
     */
    Class<?> getAttribClass();

    /**
     * DbColumnDefinition will be supplied to database create
     * statement as is next to the column name instead of auto
     * assigned column type (TEXT, INTEGER...). If you
     * specify it, supply column type and optionally constraints
     * CREATE TABLE (
     * ...
     * [column name] ([auto assigned column type] OR [dbColumnDefinition if supplied]),
     * ...
     * );
     */
    void setDbColumnDefinition(String dbColumnDefinition);

    /**
     *
     * @return
     */
    String getDbColumnDefinition();

    /**
     * "Set" is for setting the value of the attribute
     * from external source. It is JSON for now.
     *
     * @param converter
     */
    void setJsonSetAdapter(AnAdapter converter);

    /**
     *
     * @return
     */
    AnAdapter getJsonSetAdapter();

    /**
     * "Get" is for getting the value of the attribute
     * for passing to external source.
     * @param converter
     */
    void setJsonGetAdapter(AnAdapter converter);

    /**
     *
     * @return
     */
    AnAdapter getJsonGetAdapter();

    /**
     * "Set" is for setting the value of the attribute
     * from external source. It is JSON for now.
     *
     * @param converter
     */
    void setDbSetAdapter(AnAdapter converter);

    /**
     *
     * @return
     */
    AnAdapter getDbSetAdapter();

    /**
     * "Get" is for getting the value of the attribute
     * for passing to external source.
     * @param converter
     */
    void setDbGetAdapter(AnAdapter converter);

    /**
     *
     * @return
     */
    AnAdapter getDbGetAdapter();

}
