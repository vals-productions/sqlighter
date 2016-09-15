package com.vals.a2ios.amfibian.impl;

/**
 * Created by vsayenko.
 */
import com.vals.a2ios.amfibian.intf.AnAdapter;
import com.vals.a2ios.amfibian.intf.AnIncubator;
import com.vals.a2ios.amfibian.intf.AnAttrib;
import com.vals.a2ios.amfibian.intf.AnOrm;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public abstract class AnIncubatorImpl implements AnIncubator {
    private static final String VERSION = "version";
    private static final String NAME = "name";

    private static final String OBJECTS = "objects";

    private static final String CLASS_NAME = "className";
    private static final String TABLE_NAME = "tableName";

    private static final String ATTRIBUTES = "attributes";

    private static final String ATTRIB_NAME = "attribName";
    private static final String COLUMN_NAME = "columnName";
    private static final String JSON_NAME = "jsonName";
    private static final String DB_COLUMN_DEFINITION = "dbColumnDefinition";
    private static final String EXTENDS = "extends";

    private static final String JSON_SET_ADAPTER = "jsonSet";
    private static final String JSON_GET_ADAPTER = "jsonGet";
    private static final String DB_SET_ADAPTER = "dbSet";
    private static final String DB_GET_ADAPTER = "dbGet";

    private static final String NULL_STRING = "null";

    public static final String ADAPTERS = "adapters";
    public static final String ADAPTER_MAP = "adapterMap";
    public static final String CLASS = "class";

    private AnJsonSchema anSchema;

    private class AnJsonSchema {
        String name;
        String version;
        Map<String, AnObjectRecord> entityRecordMap;
        Map<String, Class<?>> adapterByNameMap = new HashMap<>();
        Map<String, String> adapters = new HashMap<>();
    }

    private class AnObjectRecord {
        AnOrm orm;
        String className;
        String extendObject;
    }

    @Override
    public void load(String jsonString) throws Exception {
        jsonString = removeComments(jsonString, "/*", "*/");
        JSONObject jo = new JSONObject(jsonString);
        anSchema = new AnJsonSchema();
        anSchema.name = ensureProperty(NAME, jo, null);
        anSchema.version = ensureProperty(VERSION, jo, null);
        loadSchemaAdapters(jo);
        loadAdapterMap(jo, anSchema.adapterByNameMap);
        if(jo.has(OBJECTS)) {
            JSONArray ja = jo.getJSONArray(OBJECTS);
            anSchema.entityRecordMap = loadObjects(ja);
        }
    }

    private void loadSchemaAdapters(JSONObject jo) throws Exception {
        if(jo.has(ADAPTERS)) {
            JSONArray ja = jo.getJSONArray(ADAPTERS);
            int count = ja.length();
            for(int i = 0; i < count; i++) {
                JSONObject adaptJo = ja.getJSONObject(i);
                if(adaptJo.has(JSON_SET_ADAPTER)) {
                    anSchema.adapters.put(JSON_SET_ADAPTER, adaptJo.getString(JSON_SET_ADAPTER));
                } else if (adaptJo.has(JSON_GET_ADAPTER)) {
                    anSchema.adapters.put(JSON_GET_ADAPTER, adaptJo.getString(JSON_GET_ADAPTER));
                } else if (adaptJo.has(DB_SET_ADAPTER)) {
                    anSchema.adapters.put(DB_SET_ADAPTER, adaptJo.getString(DB_SET_ADAPTER));
                } else if (adaptJo.has(DB_GET_ADAPTER)) {
                    anSchema.adapters.put(DB_GET_ADAPTER, adaptJo.getString(DB_GET_ADAPTER));
                }
            }
        }
    }

    private Map<String, AnObjectRecord> loadObjects(JSONArray jsonArray) throws Exception {
        Map<String, AnObjectRecord> objRecMap = new HashMap<>();

        int itemCount = jsonArray.length();

        for (int i = 0; i < itemCount; i++) {
            JSONObject jo = jsonArray.getJSONObject(i);
            AnObjectRecord objectRecord = loadAnObjectRecord(jo);

            objRecMap.put(objectRecord.className, objectRecord);
        }
        return objRecMap;
    }

    private AnObjectRecord loadAnObjectRecord(JSONObject jo) throws Exception {
        AnObjectRecord rec = new AnObjectRecord();
        rec.orm = new AnOrmImpl();
        rec.extendObject = ensureProperty(EXTENDS, jo, null);
        rec.className = ensureProperty(CLASS_NAME, jo, null);
        rec.orm.setNativeClass(getClassByName(rec.className));
        ensureAnObjectAdapters(rec.orm, jo, anSchema.adapterByNameMap);
        String assignedTableName = ensureProperty(TABLE_NAME, jo, null);
        if(assignedTableName == null) {
            rec.orm.setTableName(rec.orm.getNativeClass().getSimpleName());
        } else {
            rec.orm.setTableName(assignedTableName);
        }
        ensureAttributes(rec.orm, jo, rec);
        return rec;
    }

    private String ensureProperty(String name, JSONObject jo, Map<String, String> propertiesMap) throws Exception {
        if(jo.has(name)) {
            String value = jo.getString(name);
            if(propertiesMap != null) {
                propertiesMap.put(name, value);
            }
            return value;
        }
        return null;
    }

    private void ensureAttributes(AnOrm orm, JSONObject jo, AnObjectRecord rec) throws Exception {
        if(jo.has(ATTRIBUTES)) {
            JSONArray detailsArray = jo.getJSONArray(ATTRIBUTES);
            for (int i = 0; i < detailsArray.length(); i++) {
                JSONObject objectDefinition = detailsArray.getJSONObject(i);
                String attribName = ensureProperty(ATTRIB_NAME, objectDefinition, null);
                String columnName = ensureProperty(COLUMN_NAME, objectDefinition, null);
                String jsonName = ensureProperty(JSON_NAME, objectDefinition, null);
                String columnDef = ensureProperty(DB_COLUMN_DEFINITION, objectDefinition, null);
                AnAttrib anAttrib = null;
                if (attribName.indexOf(',') != -1) {
                    anAttrib = new AnAttribImpl(attribName);
                } else {
                    anAttrib = new AnAttribImpl(attribName, columnName, jsonName);
                }
                anAttrib.setDbColumnDefinition(columnDef);
                ensureAnAttribAdapters(orm, anAttrib, objectDefinition);
                orm.addAttrib(anAttrib);
            }
        }
    }

    private void ensureAnObjectAdapters(AnOrm orm, JSONObject jo, Map<String, Class<?>> converterByNameMap) throws Exception {
        Class clussjs = getAdapterClass(JSON_SET_ADAPTER);
        if(clussjs != null) {
            orm.setJsonSetAdapter((AnAdapter)clussjs.newInstance());
        }
//        else {
//            orm.setJsonSetAdapter(null);
//        }
        Class clussjg = getAdapterClass(JSON_GET_ADAPTER);
        if(clussjg != null) {
            orm.setJsonGetAdapter((AnAdapter)clussjg.newInstance());
        }
//        else {
//            orm.setJsonGetAdapter(null);
//        }
        Class clussds = getAdapterClass(DB_SET_ADAPTER);
        if(clussds != null) {
            orm.setDbSetAdapter((AnAdapter)clussds.newInstance());
        }
//        else {
//            orm.setDbSetAdapter(null);
//        }
        Class clussdg = getAdapterClass(DB_GET_ADAPTER);
        if(clussdg != null) {
            orm.setDbGetAdapter((AnAdapter)clussdg.newInstance());
        }
//        else {
//            orm.setDbGetAdapter(null);
//        }
        if(jo.has(ADAPTERS)) {
            JSONArray ja = jo.getJSONArray(ADAPTERS);
            int count = ja.length();
            for (int i = 0; i < count; i++) {
                JSONObject converterJo = ja.getJSONObject(i);
                if(converterJo.has(JSON_SET_ADAPTER)) {
                    String adapterName = converterJo.getString(JSON_SET_ADAPTER);
                    if(adapterName != null && !NULL_STRING.equals(adapterName)) {
                        AnAdapter converter = getAdapterInstance(adapterName);
                        orm.setJsonSetAdapter(converter);
                    } else if (NULL_STRING.equals(adapterName)) {
                        orm.setJsonSetAdapter(null);
                    }
                }
                if(converterJo.has(JSON_GET_ADAPTER)) {
                    String adapterName = converterJo.getString(JSON_GET_ADAPTER);
                    if(adapterName != null && !NULL_STRING.equals(adapterName)) {
                        AnAdapter converter = getAdapterInstance(adapterName);
                        orm.setJsonGetAdapter(converter);
                    } else if (NULL_STRING.equals(adapterName)) {
                        orm.setJsonGetAdapter(null);
                    }
                }
                if(converterJo.has(DB_SET_ADAPTER)) {
                    String adapterName = converterJo.getString(DB_SET_ADAPTER);
                    if(adapterName != null && !NULL_STRING.equals(adapterName)) {
                        AnAdapter converter = getAdapterInstance(adapterName);
                        orm.setDbSetAdapter(converter);
                    } else if (NULL_STRING.equals(adapterName)) {
                        orm.setDbSetAdapter(null);
                    }
                }
                if(converterJo.has(DB_GET_ADAPTER)) {
                    String adapterName = converterJo.getString(DB_GET_ADAPTER);
                    if(adapterName != null && !NULL_STRING.equals(adapterName)) {
                        AnAdapter converter = getAdapterInstance(adapterName);
                        orm.setDbGetAdapter(converter);
                    } else if (NULL_STRING.equals(adapterName)) {
                        orm.setDbGetAdapter(null);
                    }
                }
            }
        }
    }

    private Class getAdapterClass(String adapterName) {
        String name = anSchema.adapters.get(adapterName);
        Class cluss = anSchema.adapterByNameMap.get(name);
        return cluss;
    }

    private AnAdapter getAdapterInstance(String adapterName) throws Exception {
        Class cluss = anSchema.adapterByNameMap.get(adapterName);
        if(cluss != null) {
            return (AnAdapter) cluss.newInstance();
        }
        return null;
    }

    private void ensureAnAttribAdapters(AnOrm orm, AnAttrib attrib, JSONObject jo) throws Exception {
        attrib.setJsonSetAdapter(orm.getJsonSetAdapter());
        attrib.setJsonGetAdapter(orm.getJsonGetAdapter());
        attrib.setDbSetAdapter(orm.getDbSetAdapter());
        attrib.setDbGetAdapter(orm.getDbGetAdapter());
        if(jo.has(ADAPTERS)) {
            JSONArray ja = jo.getJSONArray(ADAPTERS);
            int count = ja.length();
            for (int i = 0; i < count; i++) {
                JSONObject adapterJo = ja.getJSONObject(i);
                if(adapterJo.has(JSON_SET_ADAPTER)) {
                    String adapterName = adapterJo.getString(JSON_SET_ADAPTER);
                    if(adapterName != null && !NULL_STRING.equals(adapterName)) {
                        AnAdapter converter = getAdapterInstance(adapterName);
                        attrib.setJsonSetAdapter(converter);
                    } else if (NULL_STRING.equals(adapterName)) {
                        attrib.setJsonSetAdapter(null);
                    }
                }
                if(adapterJo.has(JSON_GET_ADAPTER)) {
                    String adapterName = adapterJo.getString(JSON_GET_ADAPTER);
                    if(adapterName != null && !NULL_STRING.equals(adapterName)) {
                        AnAdapter converter = getAdapterInstance(adapterName);
                        attrib.setJsonGetAdapter(converter);
                    } else if (NULL_STRING.equals(adapterName)) {
                        attrib.setJsonGetAdapter(null);
                    }
                }
                if(adapterJo.has(DB_SET_ADAPTER)) {
                    String adapterName = adapterJo.getString(DB_SET_ADAPTER);
                    if(adapterName != null && !NULL_STRING.equals(adapterName)) {
                        AnAdapter converter = getAdapterInstance(adapterName);
                        attrib.setDbSetAdapter(converter);
                    } else if (NULL_STRING.equals(adapterName)) {
                        attrib.setDbSetAdapter(null);
                    }
                }
                if(adapterJo.has(DB_GET_ADAPTER)) {
                    String adapterName = adapterJo.getString(DB_GET_ADAPTER);
                    if(adapterName != null && !NULL_STRING.equals(adapterName)) {
                        AnAdapter converter = getAdapterInstance(adapterName);
                        attrib.setDbGetAdapter(converter);
                    } else if (NULL_STRING.equals(adapterName)) {
                        attrib.setDbGetAdapter(null);
                    }
                }
            }
        }
    }

    private String removeComments(String string, String begComm, String endComm) {
        StringBuilder sb = new StringBuilder(string);
        int cb = -1, ce = -1;
        int endCommLength = endComm.length();
        do  {
            cb = sb.indexOf(begComm);
            ce = sb.indexOf(endComm);
            if (cb == -1 && ce == -1) {
                break;
            } else if(cb == -1 || ce == -1) {
                return null;
            } else {
                sb.delete(cb, ce + endCommLength);
            }
        } while(true);
        return sb.toString();
    }

    private void loadAdapterMap(JSONObject jo, Map<String, Class<?>> converterByNameMap) throws Exception {
        if(jo.has(ADAPTER_MAP)) {
            JSONArray ja = jo.getJSONArray(ADAPTER_MAP);
            int count = ja.length();
            for (int i = 0; i < count; i++) {
                JSONObject convertedJo = ja.getJSONObject(i);
                if(convertedJo.has(NAME)) {
                    String name = convertedJo.getString(NAME);
                    String className = convertedJo.getString(CLASS);
                    Class cluss = getClassByName(className);
                    converterByNameMap.put(name, cluss);
                }
            }
        }
    }

    private AnOrm<?> make(String name, Map<String, AnObjectRecord> records) throws Exception {
        AnObjectRecord anObjRec = records.get(name);
        AnOrm anOrm = new AnOrmImpl();
        anOrm.setTableName(anObjRec.orm.getTableName());
        anOrm.setNativeClass(anObjRec.orm.getNativeClass());
        anOrm.setJsonSetAdapter(anObjRec.orm.getJsonSetAdapter());
        anOrm.setJsonGetAdapter(anObjRec.orm.getJsonGetAdapter());
        anOrm.setDbSetAdapter(anObjRec.orm.getDbSetAdapter());
        anOrm.setDbGetAdapter(anObjRec.orm.getDbGetAdapter());
        for (AnAttrib attr: anObjRec.orm.getOwnAttribs()) {
            AnAttrib anAttrib = new AnAttribImpl(); //, , attr.getJsonName());
            anAttrib.setAttribName(attr.getAttribName());
            anAttrib.setColumnName(attr.getColumnName());
            anAttrib.setJsonName(attr.getJsonName());

            anAttrib.setJsonSetAdapter(attr.getJsonSetAdapter());
            anAttrib.setJsonGetAdapter(attr.getJsonGetAdapter());
            anAttrib.setDbSetAdapter(attr.getDbSetAdapter());
            anAttrib.setDbGetAdapter(attr.getDbGetAdapter());

            anAttrib.setDbColumnDefinition(attr.getDbColumnDefinition());
            anOrm.addAttrib(anAttrib);
        }
        if (anObjRec.extendObject != null) {
            AnOrm dependsOn = make(anObjRec.extendObject, records);
            anOrm.setParentAnObject(dependsOn);
        }
        return anOrm;
    }

    @Override
    public AnOrm make(String name) throws Exception {
        return make(name, anSchema.entityRecordMap);
    }

    @Override
    public <T> AnOrm<T> make(Class<T> cluss) throws Exception {
        return (AnOrm<T>) make(cluss.getName(), anSchema.entityRecordMap);
    }

    @Override
    public abstract Class<?> getClassByName(String name);


}
