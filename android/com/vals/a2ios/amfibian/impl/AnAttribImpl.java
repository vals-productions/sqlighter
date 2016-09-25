package com.vals.a2ios.amfibian.impl;

import com.vals.a2ios.amfibian.intf.AnAdapter;
import com.vals.a2ios.amfibian.intf.AnAttrib;
import com.vals.a2ios.amfibian.intf.AnObject;

import java.lang.reflect.Method;

/**
 * Created by vsayenko on 9/26/15.
 */
public class AnAttribImpl implements AnAttrib {
    private AnObject<?> parentAnObject;
    private String attribName;
    private String columnName;
    private String jsonName;
    private String dbColumnDefinition;

    private AnAdapter jsonSetAdapter;
    private AnAdapter jsonGetAdapter;
    private AnAdapter dbSetAdapter;
    private AnAdapter dbGetAdapter;

    private Method getter;
    private Method setter;

    public AnAttribImpl() {
    }

    /**
     *
     * @parameter attribName - native object's attribute name
     * @parameter columnName - database table's column name, if null - the same as
     * attribName, if string literal "null" - no mapping will be provided
     * @parameter jsonName - json attribute's name, if null - the same as
     * attribName, if string literal "null" - no mapping will be provided
     */
    public AnAttribImpl(String attribName, String columnName, String jsonName) {
        init(attribName, columnName, jsonName);
    }

    private void init(String attribName, String columnName, String jsonName) {
        this.attribName = attribName;
        if(columnName == null || "".equals(columnName.trim())) {
            this.columnName = attribName;
        } else if("null".equals(columnName.trim())) {
            this.columnName = null;
        } else {
            this.columnName = columnName;
        }
        if(jsonName == null || "".equals(jsonName.trim())) {
            this.jsonName = attribName;
        } else if("null".equals(jsonName.trim())) {
            this.jsonName = null;
        } else {
            this.jsonName = jsonName;
        }
    }

    /**
     *
     * @param attribColumnJsonName - may be comma delimited list
     *                             of attribName, columnName, jsonName names,
     *                             or just attribName. In the latter case
     *                             columnName and jsonName will be set as attribName.
     *                             json or db mappings will not be provided if
     *                             either equals to string literal "null"
     */
    public AnAttribImpl(String attribColumnJsonName) {
        if (attribColumnJsonName.indexOf(",") != -1) {
            String an = null, cn = null, jn = null;
            String[] propColumn = attribColumnJsonName.split(",");
            an = propColumn[0].trim();
            if (propColumn.length > 1 && propColumn[1] != null) {
                cn = propColumn[1].trim();
            }
            if (propColumn.length > 2 && propColumn[2] != null) {
                jn = propColumn[2].trim();
            }
            init(an, cn, jn);
        } else {
            init(attribColumnJsonName, null, null);
        }
    }

    @Override
    public String getDbColumnDefinition() {
        return dbColumnDefinition;
    }

    @Override
    public void setDbColumnDefinition(String dbColumnDefinition) {
        this.dbColumnDefinition = dbColumnDefinition;
    }

    public void setJsonSetAdapter(AnAdapter adapter) {
        this.jsonSetAdapter = adapter;
    }

    public AnAdapter getJsonSetAdapter() {
        return jsonSetAdapter;
    }

    public void setJsonGetAdapter(AnAdapter adapter) {
        this.jsonGetAdapter = adapter;
    }

    public AnAdapter getJsonGetAdapter() {
        return this.jsonGetAdapter;
    }

    public void setDbSetAdapter(AnAdapter adapter) {
        this.dbSetAdapter = adapter;
    }

    public AnAdapter getDbSetAdapter() {
        return dbSetAdapter;
    }

    public void setDbGetAdapter(AnAdapter adapter) {
        this.dbGetAdapter = adapter;
    }

    public AnAdapter getDbGetAdapter() {
        return this.dbGetAdapter;
    }

    @Override
    public void setAnObject(AnObject<?> anObject) {
        this.parentAnObject = anObject;
    }

    @Override
    public AnObject<?> getAnObject() {
        return this.parentAnObject;
    }

    @Override
    public String getAttribName() {
        return attribName;
    }

    @Override
    public void setAttribName(String attribName) {
        this.attribName = attribName;
    }

    @Override
    public String getJsonName() {
        return jsonName;
    }

    @Override
    public String getColumnName() {
        return columnName;
    }

    @Override
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    @Override
    public void setValue(Object value) throws Exception {
        Method m = getSetter();
        m.invoke(parentAnObject.getNativeObject(), value);
    }

    @Override
    public void setValue(Object value, AnAdapter adapter) throws Exception {
        if(adapter != null) {
            setValue(adapter.convert(this, value));
        } else {
            setValue(value);
        }
    }

    @Override
    public Object getValue() throws Exception {
        return getValue(null);
    }

    @Override
    public Object getValue(AnAdapter adapter) throws Exception {
        Object value = null;
        Method m = getGetter();
        if(m != null ) {
            value = m.invoke(parentAnObject.getNativeObject());
            if (adapter != null) {
                value = adapter.convert(this, value);
                return value;
            }
        }
        return value;
    }
    
    @Override
    public Method getGetter() {
        if(getter == null) {
            Method[] methods = parentAnObject.getNativeClass().getMethods();
            for (Method m : methods) {
                if (m.getName().equalsIgnoreCase("get" + attribName)) {
                    getter = m;
                    return m;
                }
            }
        }
        return getter;
    }
    
    @Override
    public Method getSetter() {
        if(setter == null) {
            Method[] methods = parentAnObject.getNativeClass().getMethods();
            for (Method m : methods) {
                if (m.getName().equalsIgnoreCase("set" + attribName)) {
                    setter = m;
                    return m;
                }
            }
        }
        return setter;
    }
    
    @Override
    public Class<?> getAttribClass() {
        Method m = getGetter();
        if (m != null) {
                Class<?> rt = m.getReturnType();
                return rt;
        }
        return null;
    }

    @Override
    public void setJsonName(String jsonName) {
        this.jsonName = jsonName;
    }
}
