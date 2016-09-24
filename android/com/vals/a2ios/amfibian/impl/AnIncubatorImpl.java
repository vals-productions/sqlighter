package com.vals.a2ios.amfibian.impl;

/**
 * Created by vsayenko.
 */
import com.vals.a2ios.amfibian.intf.AnAdapter;
import com.vals.a2ios.amfibian.intf.AnIncubator;
import com.vals.a2ios.amfibian.intf.AnAttrib;
import com.vals.a2ios.amfibian.intf.AnObject;
import com.vals.a2ios.amfibian.intf.AnOrm;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class AnIncubatorImpl implements AnIncubator {
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

    public static final String ASSOCIATIONS = "associations";
    public static final String ASSOCIATE = "fetch";
    public static final String OBJECT = "object";
    public static final String SRC = "srcAttribName";
    public static final String DST = "trgAttribName";

    private boolean isLoaded = false;

    private class ErrorContext {
        String contextSchema;
        String contextObject;
        String contextAttribute;
        String contextAssociation;
        String contextAdapter;
        String step;
    }

    private ErrorContext eCtx;
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
        Map<String, AnAssociateRecord> associationMap = new HashMap<>();
    }

    private class AnAssociateRecord {
        String assocName;
        String objectName;
        String srcAttribName;
        String trgAttribName;
    }

    private String getContext() {
        return
                " Last known position: " +
                        ((eCtx.step == null)? "" : "Step: " + eCtx.step + ". ") +
                        ((eCtx.contextObject == null) ? "" : "Object: " + eCtx.contextObject + ". ") +
                        ((eCtx.contextAttribute == null) ? "" : "Attrib: " + eCtx.contextAttribute + ". ") +
                        ((eCtx.contextAssociation == null) ? "" : "Association: " + eCtx.contextAssociation + ". ") +
                        ((eCtx.contextAdapter == null) ? "":"Adapter: " + eCtx.contextAdapter);
    }

    /**
     * Will parse jsonString and prepare object hierarchy for
     * fast object making. jsonString is discarded upon loading.
     * This method will throw Exception if JSON structure error
     * occurs with best guess diagnostic message and the most
     * current position.
     */
    @Override
    public void load(String jsonString) throws Exception {
        eCtx = new ErrorContext();
        eCtx.step = "schema";
        jsonString = removeComments(jsonString, "/*", "*/");
        JSONObject jo = new JSONObject(jsonString);
        anSchema = new AnJsonSchema();
        anSchema.name = ensureProperty(NAME, jo, true);
        anSchema.version = ensureProperty(VERSION, jo, true);
        eCtx.contextSchema = anSchema.name;
        eCtx.step = "schema adapters";
        loadSchemaAdapters(jo);
        eCtx.step = "schema adapter maps";
        loadAdapterMap(jo, anSchema.adapterByNameMap);
        if(jo.has(OBJECTS)) {
            JSONArray ja = jo.getJSONArray(OBJECTS);
            anSchema.entityRecordMap = loadObjects(ja);
        }
        eCtx = null;
        isLoaded = true;
    }

    /**
     * Releases memory for situations like low memory conditions.
     * The object can be re-loaded by calling
     * load(...) adain.
     */
    @Override
    public void unload() {
        anSchema = null;
        isLoaded = false;
    }

    @Override
    public boolean isLoaded() {
        return isLoaded;
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
        eCtx.step = OBJECTS;
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
        eCtx.step = OBJECT;
        rec.orm = new AnOrmImpl();
        rec.className = ensureProperty(CLASS_NAME, jo, true);
        eCtx.contextObject = rec.className;
        rec.extendObject = ensureProperty(EXTENDS, jo, false);
        rec.orm.setNativeClass(getClassByName(rec.className));
        ensureAnObjectAdapters(rec.orm, jo);
        String assignedTableName = ensureProperty(TABLE_NAME, jo, false);
        if(assignedTableName == null) {
            rec.orm.setTableName(rec.orm.getNativeClass().getSimpleName());
        } else {
            rec.orm.setTableName(assignedTableName);
        }
        ensureAttributes(rec.orm, jo, rec);
        eCtx.contextAttribute = null;
        ensureAssociations( jo, rec);
        eCtx.contextAssociation = null;
        return rec;
    }

    private String ensureProperty(String name, JSONObject jo, boolean required) throws Exception {
        if(jo.has(name)) {
            String value = jo.getString(name);
            return value;
        }
        if(required) {
            throw new Exception("missing: " + name + getContext());
        }
        return null;
    }

    private void ensureAttributes(AnOrm orm, JSONObject jo, AnObjectRecord rec) throws Exception {
        if(jo.has(ATTRIBUTES)) {
            eCtx.step = ATTRIBUTES;
            JSONArray attribArray = jo.getJSONArray(ATTRIBUTES);
            for (int i = 0; i < attribArray.length(); i++) {
                JSONObject attribDefinition = attribArray.getJSONObject(i);
                String attribName = ensureProperty(ATTRIB_NAME, attribDefinition, true);
                eCtx.contextAttribute = attribName;
                String columnName = ensureProperty(COLUMN_NAME, attribDefinition, false);
                String jsonName = ensureProperty(JSON_NAME, attribDefinition, false);
                String columnDef = ensureProperty(DB_COLUMN_DEFINITION, attribDefinition, false);
                AnAttrib anAttrib = null;
                if (attribName.indexOf(',') != -1) {
                    anAttrib = new AnAttribImpl(attribName);
                } else {
                    anAttrib = new AnAttribImpl(attribName, columnName, jsonName);
                }
                anAttrib.setDbColumnDefinition(columnDef);
                ensureAnAttribAdapters(orm, anAttrib, attribDefinition);
                orm.addAttrib(anAttrib);
            }
        }
    }

    private void ensureAssociations(JSONObject jo, AnObjectRecord rec) throws Exception {
        if(jo.has(ASSOCIATIONS)) {
            eCtx.step = ATTRIBUTES + "/" + ASSOCIATIONS;
            JSONArray assocArray = jo.getJSONArray(ASSOCIATIONS);
            for (int i = 0; i < assocArray.length(); i++) {
                JSONObject associationJo = assocArray.getJSONObject(i);
                AnAssociateRecord ar = new AnAssociateRecord();
                ar.assocName = ensureProperty(NAME, associationJo, true);
                eCtx.contextAssociation = ar.assocName;
                ar.objectName = ensureProperty(OBJECT, associationJo, true);
                ar.srcAttribName = ensureProperty(SRC, associationJo, true);
                ar.trgAttribName = ensureProperty(DST, associationJo, true);
                rec.associationMap.put(ar.assocName, ar);
            }
        }
    }

    private void ensureAnObjectAdapters(AnOrm orm, JSONObject jo) throws Exception {
        Class clussjs = getAdapterClass(JSON_SET_ADAPTER);
        if(clussjs != null) {
            orm.setJsonSetAdapter((AnAdapter)clussjs.newInstance());
        }
        Class clussjg = getAdapterClass(JSON_GET_ADAPTER);
        if(clussjg != null) {
            orm.setJsonGetAdapter((AnAdapter)clussjg.newInstance());
        }
        Class clussds = getAdapterClass(DB_SET_ADAPTER);
        if(clussds != null) {
            orm.setDbSetAdapter((AnAdapter)clussds.newInstance());
        }
        Class clussdg = getAdapterClass(DB_GET_ADAPTER);
        if(clussdg != null) {
            orm.setDbGetAdapter((AnAdapter)clussdg.newInstance());
        }
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
        if(name != null) {
            Class cluss = anSchema.adapterByNameMap.get(name);
            return cluss;
        }
        return null;
    }

    private AnAdapter getAdapterInstance(String adapterName) throws Exception {
        Class cluss = anSchema.adapterByNameMap.get(adapterName);
        if(cluss != null) {
            return (AnAdapter) cluss.newInstance();
        }
        throw new Exception("Missing adapter: " + adapterName + getContext());
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
                String name = ensureProperty(NAME, convertedJo, true); convertedJo.getString(NAME);
                String className = ensureProperty(CLASS, convertedJo, true);
                Class cluss = getClassByName(className);
                converterByNameMap.put(name, cluss);
            }
        }
    }

    /**
     * Makes a new instance of anOrm manager for the class name
     * // todo - remane name to className
     */
    private AnOrm<?> make(String name, Map<String, AnObjectRecord> records) throws Exception {
        AnObjectRecord anObjRec = records.get(name);
        if(anObjRec == null) {
            throw new Exception("Could not find definition for " + name);
        }
        AnOrm anOrm = new AnOrmImpl();
        anOrm.setTableName(anObjRec.orm.getTableName());
        anOrm.setNativeClass(anObjRec.orm.getNativeClass());
        anOrm.setJsonSetAdapter(anObjRec.orm.getJsonSetAdapter());
        anOrm.setJsonGetAdapter(anObjRec.orm.getJsonGetAdapter());
        anOrm.setDbSetAdapter(anObjRec.orm.getDbSetAdapter());
        anOrm.setDbGetAdapter(anObjRec.orm.getDbGetAdapter());
        for (AnAttrib attr: anObjRec.orm.getOwnAttribs()) {
            AnAttrib anAttrib = new AnAttribImpl();
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
        anOrm.setIncubator(this);
        return anOrm;
    }

    /**
     * Makes a new instance of anOrm manager for the classname
     */
    @Override
    public AnOrm make(String className) throws Exception {
        return make(className, anSchema.entityRecordMap);
    }

    /**
     * Makes a new instance of anOrm manager for the class
     */
    @Override
    public <T> AnOrm<T> make(Class<T> cluss) throws Exception {
        return (AnOrm<T>) make(cluss.getName(), anSchema.entityRecordMap);
    }

    @Override
    public Class<?> getClassByName(String name) throws Exception {
        return Class.forName(name);
    }

    private AnAssociateRecord getArAssociateRecord(Class cluss, AnAttrib attrib) throws Exception {
        if(cluss == null || attrib == null) {
            throw new Exception("Wrong parameters");
        }
        AnObjectRecord anObjRec = anSchema.entityRecordMap.get(cluss.getName());
        if(anObjRec == null) {
            throw new Exception("Undefined class: " + cluss.getName());
        }
        AnAssociateRecord ar = anObjRec.associationMap.get(attrib.getAttribName());
        if(ar == null) {
            throw new Exception("Undefined association for: " + attrib.getAttribName());
        }
        return ar;
    }

    @Override
    public String getAssociationTrgClassName(Class cluss, AnAttrib attrib) throws Exception {
        AnAssociateRecord ar = getArAssociateRecord(cluss, attrib);
        return ar.objectName;
    }

    @Override
    public String getAssociationTrgJoinAttribName(Class cluss, AnAttrib attrib) throws Exception {
        AnAssociateRecord ar = getArAssociateRecord(cluss, attrib);
        return ar.trgAttribName;
    }

    @Override
    public String getAssociationSrcJoinAttribName(Class cluss, AnAttrib attrib) throws Exception {
        AnAssociateRecord ar = getArAssociateRecord(cluss, attrib);
        return ar.srcAttribName;
    }

    @Override
    public String getAssociationSrcAttribName(Class cluss, AnAttrib attrib) throws Exception {
        AnAssociateRecord ar = getArAssociateRecord(cluss, attrib);
        return ar.assocName;
    }

}
