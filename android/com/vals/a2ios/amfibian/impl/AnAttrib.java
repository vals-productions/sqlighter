package com.vals.a2ios.amfibian.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by vsayenko on 9/26/15.
 *
 * AmfibiaN AnObject's attribute descriptor/handler.
 *
 * It is used for JSON/Database <-> Native object conversion
 * as necessary.
 */
public class AnAttrib {
    private AnObject<?> parentAnObject;
    private String attribName;
    private String columnName;
    private String jsonName;
    /**
     * Converter value may be used to implement customized
     * attribute value conversions for specific cases.
     */
    public static interface CustomConverter {
        public Object convert(Object value);
    }

    private List<String> conversionMessages = new LinkedList<>();
    
    public static final String NONAME_CONVERSION_KEY = "nonameConverter";
    
    public String defaultConverterKey = NONAME_CONVERSION_KEY;
    public String defaultGetConverterKey = NONAME_CONVERSION_KEY;
    
    private Map<String, CustomConverter> converterMap = new HashMap<>();   
    private Map<String, CustomConverter> getConverterMap = new HashMap<>();    
    
    public void setCustomSetConverter(CustomConverter converter) {
        setCustomSetConverter(NONAME_CONVERSION_KEY, converter);
    }
    public void setCustomSetConverter(String key, CustomConverter converter) {
        converterMap.put(key, converter);
    }
    public CustomConverter getCustomSetConverter(String key) {
        return converterMap.get(key);
    }
    public CustomConverter getCustomSetConverter() {
        return converterMap.get(defaultConverterKey);
    }
    public void clearCustomSetConverters() {
        converterMap.clear();
    }
    public void setDefaultSetConversionKey(String key) {
    	defaultConverterKey = key;
    }

    public void setCustomGetConverter(CustomConverter converter) {
        setCustomGetConverter(NONAME_CONVERSION_KEY, converter);
    }
    public void setCustomGetConverter(String key, CustomConverter converter) {
        getConverterMap.put(key, converter);
    }
    public CustomConverter getCustomGetConverter(String key) {
        return getConverterMap.get(key);
    }
    public CustomConverter getCustomGetConverter() {
        return getConverterMap.get(defaultGetConverterKey);
    }
    public void clearCustomGetConverters() {
        getConverterMap.clear();
    }
    public void setDefaultGetConversionKey(String key) {
    	defaultGetConverterKey = key;
    }
    public void setAnObject(AnObject<?> anObject) {
        this.parentAnObject = anObject;
    }

    public AnAttrib(String attribName, String columnName, String jsonName) {
        this(attribName);
        this.columnName = columnName;
        this.jsonName = jsonName;
    }

    public AnAttrib(String attribColumnJsonName) {
        if (attribColumnJsonName.indexOf(",") != -1) {
            String[] propColumn = attribColumnJsonName.split(",");
            this.attribName = propColumn[0].trim();
            if (propColumn.length > 1 && propColumn[1] != null) {
            	this.columnName = propColumn[1].trim();
            }
            if (propColumn.length > 2 && propColumn[2] != null) {
            	this.jsonName = propColumn[2].trim();
            }
        } else {
            this.attribName = attribColumnJsonName;
        }
    }

    public String getAttribName() {
        return attribName;
    }

    public void setAttribName(String attribName) {
        this.attribName = attribName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    /**
    * Sets native object's attribute value
    *
    * @param value - value to set. Sometimes it may not directly match 
    * destination value type. For this case there will be an attempt to
    * auto match the value, or, custom converter might be supplied
    */
    public void setValue(Object value) throws Exception {
        Method m = getSetter();
        if(m != null) {
                Object convertedValue = null;
               /**
                * See if custom converter is supplied
                */
                CustomConverter cc = getCustomSetConverter();
                if (cc != null) {
                    convertedValue = cc.convert(value);
                } else {
                    convertedValue = matchSetMethodParameterType(m, value);
                }
                // System.out.println("before setting " + attribName + " curr value: " + getValue() + " to " + value);
                m.invoke(parentAnObject.getNativeObject(), convertedValue);
                // System.out.println("after setting " + attribName + " curr value: " + getValue());
        }
    }
    
    /**
     * (Auto) converts from source obj type to target if different.
     *
     * For example, sometimes json representation is different from object's expected
     * representation, like a Date could be represented by long (milli) seconds. 
     */
    private Object matchSetMethodParameterType(Method m, Object obj) throws Exception {
        if (obj != null) {
            Class<?>[] paramTypes = m.getParameterTypes();
            Class<?> p = paramTypes[0];
            Class<?> objClass = obj.getClass();
            if (!p.equals(objClass)) {
                /**
                * if not equivalent, try to autoconvert
                */
                Constructor<?>[] cs = p.getConstructors();
//                System.out.println("Try to set " + attribName + ", " + cs.length + " possible constructors");
                for (Constructor<?> c : cs) {
                  Class<?>[] cParamTypes = c.getParameterTypes();
//                    System.out.println(c.getName() + "Try to set " + attribName + ", constr. params " + cParamTypes.length);
                    if (cParamTypes.length == 1) {
                      try {
//                          System.out.println("Try to set " + attribName + ", constr. param " + cParamTypes[0].getName() + " simple name" + cParamTypes[0].getSimpleName());
                           /*
                            * Work through single parameter constructors
                            */
                          if (cParamTypes[0].equals(objClass)) {
                            /*
                             * There's a constructor with source obj class as an input.
                             */
                              Object newObject = c.newInstance(obj);
                              return newObject;
                          } else if (objClass.getSimpleName().equalsIgnoreCase(cParamTypes[0].getSimpleName())) {
                            /*
                             * Try to instantiate through "similar" constructor
                             * Example - source - Long (millisec)
                             * target - Date
                             * // java
                             * Date d = new Date(Long.longValue());
                             * iOS - JavaUtilDate will be used
                             */
                              Object newObject = c.newInstance(obj);
                              return newObject;
                          } else if (cParamTypes[0].equals(String.class)) {
                            /*
                             * Through string constructor...
                             * Integer source;
                             * Long target = new Long(source.toString());
                             */
                              Object newObject = c.newInstance(obj.toString());
                              return newObject;
                          }
                      } catch (Throwable t) {
                          conversionMessages.add("Error setting " + attribName + " from: " + objClass.getName() +
                                  "Constr. param " + cParamTypes[0].getName() + 
                                  " simple name" + cParamTypes[0].getSimpleName());
                      }
                  }
                }
                conversionMessages.add("*** Final. Could not set " + attribName + " from: " + objClass.getName());
                return null;
            }
        }
        return obj;
    }

    public Object getValue() throws Exception {
        Object value = null;
        Method m = getGetter();
        if(m != null ) {
            value = m.invoke(parentAnObject.getNativeObject());
            CustomConverter cc = getCustomGetConverter();
            if (cc != null) {
                value = cc.convert(value);
                return value;
            }
        }
        return value;
    }
    
    public Method getGetter() {
        Method[] methods = parentAnObject.getNativeClass().getMethods();
        for (Method m: methods) {
            if (m.getName().equalsIgnoreCase("get" + attribName)) {
                return m;
            }
        }
        return null;
    }
    
    public Method getSetter() {
        Method[] methods = parentAnObject.getNativeClass().getMethods();
        for (Method m: methods) {
            if (m.getName().equalsIgnoreCase("set" + attribName)) {
                return m;
            }
        }
        return null;
    }
    
    public Class<?> getAttribClass() {
        Method m = getGetter();
        if (m != null) {
                Class<?> rt = m.getReturnType();
                return rt;
        }
        return null;
    }
    
    public String getJsonOrAttribName() {
    	if(jsonName != null) {
    		return jsonName;
    	}
    	return attribName;
    }
    public String getColumnOrAttribName() {
    	if(columnName != null) {
    		return columnName;
    	}
    	return attribName;
    }
    
}
