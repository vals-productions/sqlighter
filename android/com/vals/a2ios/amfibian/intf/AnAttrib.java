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
     * Converter value may be used to implement customized
     * attribute value conversions for specific cases.
     *
     * CustomConverters are used inside of getValue() and setValue(...)
     * methods at the level of each individual attribute. This way you can  
     * control value conversion on a case by case basis in case default conversions
     * do not suit your needs.
     *
     * Custom conversion takes place before getValue() returns the value to the caller.
     * Consumers of getValue() include database or JSON operations when they need to get 
     * native object'  attribute value to, most likely, save it to the database, use in
     * where clause, or set as JSON object property..
     *
     * Custom conversion takes place after the caller invokes setValue(,,,) but before
     * the value gets assigned to the native object. Callers of setValue(...) include 
     * database or JSON operations, typically, when the value gets retrieved from database
     * or JSON and is being assigned to Native object.
     *
     * Custom converters are unaware who the consumer or caller is. They work uniformly for
     * all the cases.
     *
     * It is possible to dynamically set or unset (set to null) the converters as your program flows,
     * so you can inject custom behavior for a particular situation.
     *
     */
    public static interface CustomConverter {
        public Object convert(Object value);
    }

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
     * Gives JSON attribute name, if different from
     * attribute name. Otherwise return attribute name.
     *
     * @return
     */
    String getJsonOrAttribName();

    /**
     * Gives DB column name, if different from attribute name.
     * Otherwise return attribute name.
     *
     * @return
     */
    String getColumnOrAttribName();

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
     *
     * @param converter
     */
    void setCustomSetConverter(CustomConverter converter);

    /**
     *
     * @param key
     * @param converter
     */
    @Deprecated
    void setCustomSetConverter(String key, CustomConverter converter);

    /**
     *
     * @param key
     * @return
     */
    @Deprecated
    CustomConverter getCustomSetConverter(String key);

    /**
     *
     * @return
     */
    CustomConverter getCustomSetConverter();

    /**
     *
     */
    @Deprecated
    void clearCustomSetConverters();

    /**
     *
     * @param key
     */
    @Deprecated
    void setDefaultSetConversionKey(String key);

    /**
     *
     * @param converter
     */
    void setCustomGetConverter(CustomConverter converter);

    /**
     *
     * @param key
     * @param converter
     */
    @Deprecated
    void setCustomGetConverter(String key, CustomConverter converter);

    /**
     *
     * @param key
     * @return
     */
    @Deprecated
    CustomConverter getCustomGetConverter(String key);

    /**
     *
     * @return
     */
    CustomConverter getCustomGetConverter();

    /**
     *
     */
    @Deprecated
    void clearCustomGetConverters();

    /**
     *
     * @param key
     */
    @Deprecated
    void setDefaultGetConversionKey(String key);

}
