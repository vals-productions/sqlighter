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
 * some custom converters between respective attribute values in case default
 * converters do not work for you.
 *
 */
public interface AnAttrib {
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
    void setCustomSetConverter(String key, CustomConverter converter);

    /**
     *
     * @param key
     * @return
     */
    CustomConverter getCustomSetConverter(String key);

    /**
     *
     * @return
     */
    CustomConverter getCustomSetConverter();

    /**
     *
     */
    void clearCustomSetConverters();

    /**
     *
     * @param key
     */
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
    void setCustomGetConverter(String key, CustomConverter converter);

    /**
     *
     * @param key
     * @return
     */
    CustomConverter getCustomGetConverter(String key);

    /**
     *
     * @return
     */
    CustomConverter getCustomGetConverter();

    /**
     *
     */
    void clearCustomGetConverters();

    /**
     *
     * @param key
     */
    void setDefaultGetConversionKey(String key);

    /**
     *
     * @param anObject
     */
    void setAnObject(AnObject<?> anObject);

    /**
     *
     * @return
     */
    String getAttribName();

    /**
     *
     * @param attribName
     */
    void setAttribName(String attribName);

    /**
     *
     * @return
     */
    String getColumnName();

    /**
     *
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
     *
     * @return
     * @throws Exception
     */
    Object getValue() throws Exception;

    /**
     *
     * @return
     */
    Method getGetter();

    /**
     *
     * @return
     */
    Method getSetter();

    /**
     *
     * @return
     */
    Class<?> getAttribClass();

    /**
     *
     * @return
     */
    String getJsonOrAttribName();

    /**
     *
     * @return
     */
    String getColumnOrAttribName();

    /**
     * Converter value may be used to implement customized
     * attribute value conversions for specific cases.
     */
    public static interface CustomConverter {
        public Object convert(Object value);
    }
}
