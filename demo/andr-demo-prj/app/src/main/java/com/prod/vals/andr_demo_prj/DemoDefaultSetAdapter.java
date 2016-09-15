package com.prod.vals.andr_demo_prj;

import com.vals.a2ios.amfibian.intf.AnAdapter;
import com.vals.a2ios.amfibian.intf.AnAttrib;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Demo converter
 *
 * Created by vsayenko on 9/12/16.
 */
public class DemoDefaultSetAdapter implements AnAdapter {
    @Override
    public Object convert(AnAttrib attrib, Object value) {
        if (value == null) {
            return null;
        }
        Class<?> objClass = value.getClass();
        Method m = attrib.getSetter();
        String attribName = attrib.getAttribName();
        Class<?>[] paramTypes = m.getParameterTypes();
        Class<?> p = paramTypes[0];
        if (p.equals(objClass)) {
            return value;
        }
        /**
         * if not equivalent, try to auto-convert
         */
        Constructor<?>[] cs = p.getConstructors();
        for (Constructor<?> c : cs) {
            Class<?>[] cParamTypes = c.getParameterTypes();
            if (cParamTypes.length != 1) {
                continue;
            }
            try {
                   /*
                    * Work through single parameter constructors
                    */
                if (cParamTypes[0].equals(objClass)) {
                    /*
                     * There's a constructor with source obj class as an input.
                     */
                    Object newObject = c.newInstance(value);
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
                    Object newObject = c.newInstance(value);
                    return newObject;
                } else if (cParamTypes[0].equals(String.class)) {
                    /*
                     * Through string constructor...
                     * Integer source;
                     * Long target = new Long(source.toString());
                     */
                    Object newObject = c.newInstance(value.toString());
                    return newObject;
                }
            } catch (Throwable t) {
                onWarning(objClass, attribName, value);
            }
        } // end for
        onWarning(objClass, attribName, value);
        return null;
    }

    @Override
    public void onWarning(Class cluss, String attribName, Object value) {

    }
}

