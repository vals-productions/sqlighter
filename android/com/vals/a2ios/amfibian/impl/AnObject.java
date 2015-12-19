package com.vals.a2ios.amfibian.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by vsayenko on 9/26/15.
 *
 * AmfibiaN Object management class.
 *
 */
public class AnObject<T> {
    @SuppressWarnings("rawtypes")
    private AnObject parentAnObject;
    private Map<String, Object> nativeObjectMap;
    private Map<String, Object> jsonMap;
    private T nativeObject;
    protected Class<T> nativeClass;
    private Map<String, AnAttrib> attribMap = new LinkedHashMap<>();

    public void resetNativeObject() throws Exception {
        setNativeObject(nativeClass.newInstance());
    }

	@SuppressWarnings("unchecked")
	protected void setNativeObject(T o) throws Exception {
        this.nativeObject = o;
        if (parentAnObject != null) {
            parentAnObject.setNativeObject(o);
        }
    }

    protected T getNativeObject() {
        return nativeObject;
    }

    public Class<T> getNativeClass() {
        return nativeClass;
    }

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

    public AnAttrib getAttrib(String propertyName) {
        AnAttrib a = attribMap.get(propertyName);
        if (a == null && parentAnObject != null) {
            a = parentAnObject.getAttrib(propertyName);
        }
        return a;
    }

    @SuppressWarnings("unchecked")
	protected void setNativeClass(Class<T> anObjClass) {
        this.nativeClass = anObjClass;
        Class<?> superClass = anObjClass.getSuperclass();
        if (superClass != null) {
            if (parentAnObject != null) {
                parentAnObject.setNativeClass(superClass);
            }
        }
    }
    
    public AnObject() {
    }
    
    public AnObject(Class<T> anObjClass, AnObject<?> parentMapper) {
       init(anObjClass, parentMapper);
    }
    
    protected void init(Class<T> anObjClass, AnObject<?> parentMapper) {
        this.parentAnObject = parentMapper;
        setNativeClass(anObjClass);
    }
    
    public AnObject(Class<T> anObjClass, AnAttrib[] propertyMappers, AnObject<?> parentMapper) {
        init(anObjClass, propertyMappers, parentMapper);
    }
    
    protected void init(Class<T> anObjClass, AnAttrib[] propertyMappers , AnObject<?> parentMapper) {
        init(anObjClass, parentMapper);
        initAttribs(propertyMappers);
    }
    
    public AnObject(Class<T> anObjClass, AnAttrib[] propertyMappers) {
        init(anObjClass, propertyMappers);
    }
    
    protected void init(Class<T> anObjClass, AnAttrib[] propertyMappers) {
        initAttribs(propertyMappers);
        setNativeClass(anObjClass);
    }
    
    public AnObject(Class<T> anObjClass, String[] propertyNames, AnObject<?> parentMapper) {
        init(anObjClass, propertyNames, parentMapper);
    }
    
    protected void init(Class<T> anObjClass, String[] propertyNames, AnObject<?> parentMapper) {
    		this.parentAnObject = parentMapper;
    		init(anObjClass, propertyNames);
    }
    
    public AnObject(Class<T> anObjClass, String[] propertyNames) {
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
                        AnAttrib a = new AnAttrib(propertyName);
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
    
    public void addAttrib(AnAttrib anAttribMapper) {
        anAttribMapper.setAnObject(this);
        attribMap.put(anAttribMapper.getAttribName(), anAttribMapper);
    }

    protected Map<String, Object> getJsonMap() throws Exception {
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

    public synchronized JSONObject asJSONObject(T nativeObject) throws Exception {
        setNativeObject(nativeObject);
        asMap(nativeObject);
        return new JSONObject(getJsonMap());
    }

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
    
    public synchronized T asNativeObject(String jsonString) throws Exception {
        return asNativeObject(new JSONObject(jsonString));
    }

    public synchronized List<T> asList(String jsonArrayString) throws Exception {
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

    public synchronized String asJsonString(T nativeObject) throws Exception {
        return asJSONObject(nativeObject).toString();
    }

    /**
     *
     * @param someValue - this is expected to be
     *                  either nativeObject, or,
     *                  json string or json object
     * @throws Exception
     */
    private void setValue(Object someValue) throws Exception {
        if (someValue != null) {
            if (someValue instanceof String) {
                asNativeObject((String) someValue);
            } else if (someValue instanceof JSONObject) {
                asNativeObject((JSONObject) someValue);
            } else {
            	@SuppressWarnings("unchecked")
				T t = (T)someValue;
                setNativeObject(t);
            }
        }
     }
}
