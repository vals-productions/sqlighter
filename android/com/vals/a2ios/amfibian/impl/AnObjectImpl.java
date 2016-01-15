package com.vals.a2ios.amfibian.impl;

import com.vals.a2ios.amfibian.intf.AnObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.vals.a2ios.amfibian.intf.AnAttrib;

/**
 * Created by vsayenko on 9/26/15.
 *
 */
public class AnObjectImpl<T> implements AnObject<T> {
    @SuppressWarnings("rawtypes")
    private AnObject parentAnObject;
    private Map<String, Object> nativeObjectMap;
    private Map<String, Object> jsonMap;
    private T nativeObject;
    protected Class<T> nativeClass;
    private Map<String, AnAttrib> attribMap = new LinkedHashMap<>();

    public AnObjectImpl() {
    }

    public AnObjectImpl(Class<T> anObjClass, AnObject<?> parentMapper) {
        init(anObjClass, parentMapper);
    }

    public AnObjectImpl(Class<T> anObjClass, String[] propertyNames, AnObject<?> parentMapper) {
        init(anObjClass, propertyNames, parentMapper);
    }

    public AnObjectImpl(Class<T> anObjClass, AnAttrib[] propertyMappers, AnObject<?> parentMapper) {
        init(anObjClass, propertyMappers, parentMapper);
    }

    public AnObjectImpl(Class<T> anObjClass, String[] propertyNames) {
        init(anObjClass, propertyNames);
    }

    public AnObjectImpl(Class<T> anObjClass, AnAttrib[] propertyMappers) {
        init(anObjClass, propertyMappers);
    }

    @Override
    public void resetNativeObject() throws Exception {
        setNativeObject(nativeClass.newInstance());
    }

	@SuppressWarnings("unchecked")
	public void setNativeObject(T o) throws Exception {
        this.nativeObject = o;
        if (parentAnObject != null) {
            parentAnObject.setNativeObject(o);
        }
    }

    public T getNativeObject() {
        return nativeObject;
    }

    @Override
    public Class<T> getNativeClass() {
        return nativeClass;
    }

    @Override
    @SuppressWarnings("unchecked")
	public Map<String, AnAttrib> getAttribList() {
        Map<String, AnAttrib> p = new HashMap<>();
        if(parentAnObject != null) {
            p.putAll(parentAnObject.getAttribList());
        }
        if (attribMap != null) {
            p.putAll(attribMap);
        }
        return p;
    }

    @Override
    public AnAttrib getAttrib(String propertyName) {
        AnAttrib a = attribMap.get(propertyName);
        if (a == null && parentAnObject != null) {
            a = parentAnObject.getAttrib(propertyName);
        }
        return a;
    }

    @SuppressWarnings("unchecked")
	public void setNativeClass(Class<T> anObjClass) {
        this.nativeClass = anObjClass;
        Class<?> superClass = anObjClass.getSuperclass();
        if (superClass != null) {
            if (parentAnObject != null) {
                parentAnObject.setNativeClass(superClass);
            }
        }
    }

    protected void init(Class<T> anObjClass, AnObject<?> parentMapper) {
        this.parentAnObject = parentMapper;
        setNativeClass(anObjClass);
    }
    
    protected void init(Class<T> anObjClass, AnAttrib[] propertyMappers , AnObject<?> parentMapper) {
        init(anObjClass, parentMapper);
        initAttribs(propertyMappers);
    }
    
    protected void init(Class<T> anObjClass, AnAttrib[] propertyMappers) {
        initAttribs(propertyMappers);
        setNativeClass(anObjClass);
    }

    protected void init(Class<T> anObjClass, String[] propertyNames, AnObject<?> parentMapper) {
    		this.parentAnObject = parentMapper;
    		init(anObjClass, propertyNames);
    }

    protected void init(Class<T> anObjClass, String[] propertyNames) {    
        AnAttrib[] list = stringsToAttribs(propertyNames);
        init(anObjClass, list);
    }
    
    private AnAttrib[] stringsToAttribs(String[] propertyNames) {
        AnAttrib[] list = null;
        if (propertyNames != null) {
                list = new AnAttrib[propertyNames.length];
                int idx = 0;
                for (String propertyName: propertyNames) {
                        AnAttrib a = new AnAttribImpl(propertyName);
                        list[idx++] = a;
                }
        }    
        return list;
    }
    
    private void initAttribs(AnAttrib[] attribMappers)  {
        for (AnAttrib pm: attribMappers) {
            addAttrib(pm);
        }
    }
    
    @Override
    public void addAttrib(AnAttrib anAttribMapper) {
        anAttribMapper.setAnObject(this);
        attribMap.put(anAttribMapper.getAttribName(), anAttribMapper);
    }

    @Override
    public Map<String, Object> getJsonMap() throws Exception {
        if (jsonMap == null) {
        	jsonMap = new HashMap<>();
            Set<String> p = attribMap.keySet();
            for (String attrName : p) {
                AnAttrib attr =  attribMap.get(attrName);
                Object value = attr.getValue();
                if (value != null) {
                	jsonMap.put(attr.getJsonOrAttribName(), attr.getValue());
                }
            }
        }
        if (parentAnObject != null) {
            @SuppressWarnings("unchecked")
			Map<String, Object> parentJsonMap = parentAnObject.getJsonMap();
            Set<String> keys = parentJsonMap.keySet();
            for (String k: keys) {
            	jsonMap.put(k, parentJsonMap.get(k));
            }
        }
        return jsonMap;
    }

    @Override
    public synchronized Map<String, Object> asMap(T nativeObject) throws Exception {
        setNativeObject(nativeObject);
        if (nativeObjectMap == null) {
            nativeObjectMap = new HashMap<>();
            Set<String> p = attribMap.keySet();
            for (String pName : p) {
                AnAttrib pm =  attribMap.get(pName);
                Object value = pm.getValue();
                if (value != null) {
                    nativeObjectMap.put(pName, pm.getValue());
                }
            }
        }
        if (parentAnObject != null) {
            @SuppressWarnings("unchecked")
			Map<String, Object> parentMap = parentAnObject.asMap(nativeObject);
            Set<String> keys = parentMap.keySet();
            for (String k: keys) {
                nativeObjectMap.put(k, parentMap.get(k));
            }
        }
        return nativeObjectMap;
    }

    @Override
    public synchronized JSONObject asJSONObject(T nativeObject) throws Exception {
        setNativeObject(nativeObject);
        asMap(nativeObject);
        return new JSONObject(getJsonMap());
    }

    @Override
    public synchronized T asNativeObject(JSONObject jsonObject) throws Exception {
        if (nativeObject == null) {
            resetNativeObject();
        }
        if (parentAnObject != null) {
            parentAnObject.asNativeObject(jsonObject);
        }
        Set<String> attrObjsKeys = attribMap.keySet();
        for (String attribName: attrObjsKeys) {
            AnAttrib attr = attribMap.get(attribName);
            if(!jsonObject.isNull(attr.getJsonOrAttribName())) {
                Object attrValue = jsonObject.get(attr.getJsonOrAttribName());
                if (attrValue != null) {
                    attr.setValue(attrValue);
                }
            }
        }
        return nativeObject;
    }

    @Override
    public synchronized T asNativeObject(String jsonString) throws Exception {
        return asNativeObject(new JSONObject(jsonString));
    }

    @Override
    public synchronized Collection<T> asList(String jsonArrayString) throws Exception {
        JSONArray jsonArray = new JSONArray(jsonArrayString);
        List<T> l = new LinkedList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            Object o = jsonArray.get(i);
            this.resetNativeObject();
            T t = asNativeObject((JSONObject) o);
            l.add(t);
        }
        resetNativeObject();
        return l;
    }

    @Override
    public synchronized String asJsonString(T nativeObject) throws Exception {
        return asJSONObject(nativeObject).toString();
    }
}
