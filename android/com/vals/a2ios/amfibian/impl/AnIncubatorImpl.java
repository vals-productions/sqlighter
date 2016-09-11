package com.vals.a2ios.amfibian.impl;

/**
 * Created by vsayenko.
 */

import com.vals.a2ios.amfibian.intf.AnIncubator;
import com.vals.a2ios.amfibian.intf.AnAttrib;
import com.vals.a2ios.amfibian.intf.AnObject;
import com.vals.a2ios.amfibian.intf.AnOrm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
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
    private static final String DEPENDS = "extends";

    private static final String JSON_SET_CONVERTER = "jsonSetConverter";
    private static final String JSON_GET_CONVERTER = "jsonGetConverter";
    private static final String DB_SET_CONVERTER = "dbSetConverter";
    private static final String DB_GET_CONVERTER = "dbGetConverter";

    private Map<String, AnObjectRecord>  entityRecordMap;

    private class AnJsonSchema {
        String name;
        String version;
        Class<? extends AnObject> entityClass;
        Map<String, String> directPropertiesMap;
    }

    private class AnObjectRecord {
        Map<String, String> directPropertiesMap;
        Collection<Map<String, String>> propertiesMap;
        Collection<String> extraProperties;
        AnObjectRecord parent;
    }

    @Override
    public void load(String jsonString) throws Exception {
        jsonString = removeComments(jsonString, "/*", "*/");
        JSONObject jo = new JSONObject(jsonString);
        AnJsonSchema anSchema = new AnJsonSchema();
        anSchema.name = ensureProperty(NAME, jo, null);
        anSchema.version = ensureProperty(VERSION, jo, null);
        if(jo.has(OBJECTS)) {
            JSONArray ja = jo.getJSONArray(OBJECTS);
            entityRecordMap = loadObjects(ja);
        }
    }

    @Override
    public AnOrm make(String name) throws Exception {
        return make(name, entityRecordMap);
    }

    @Override
    public <T> AnOrm<T> make(Class<T> cluss) throws Exception {
        return make(cluss.getName(), entityRecordMap);
    }

    @Override
    public abstract Class<?> getClassByName(String name);

    private AnOrm make(String name, Map<String, AnObjectRecord> records) throws ClassNotFoundException {
        AnObjectRecord anObjRec = records.get(name);
        String deps  = anObjRec.directPropertiesMap.get(DEPENDS);
        anObjRec.parent = records.get(deps);
        AnOrm anOrm = new AnOrmImpl();
        if (anObjRec.parent != null) {
            AnOrm dependsOn = make(anObjRec.parent.directPropertiesMap.get(CLASS_NAME), records);
            anOrm.setParentAnObject(dependsOn);
        }
        anOrm.setNativeClass(getClassByName(anObjRec.directPropertiesMap.get(CLASS_NAME)));
        String assignedTableName = anObjRec.directPropertiesMap.get(TABLE_NAME);
        if(assignedTableName == null) {
            anOrm.setTableName(anOrm.getNativeClass().getSimpleName());
        } else {
            anOrm.setTableName(assignedTableName);
        }
        Collection<AnAttrib> mipColl = new ArrayList<>();
        for (Map<String, String> m: anObjRec.propertiesMap) {
            String attribName = m.get(ATTRIB_NAME);
            String columnName = m.get(COLUMN_NAME);
            String jsonName = m.get(JSON_NAME);
            String columnDef = m.get(DB_COLUMN_DEFINITION);
            AnAttrib anAttrib = null;
            if (attribName.indexOf(',') != -1) {
                anAttrib = new AnAttribImpl(attribName);
            } else {
                anAttrib = new AnAttribImpl(attribName, columnName, jsonName);
            }
            anAttrib.setDbColumnDefinition(columnDef);
            mipColl.add(anAttrib);
        }
        anOrm.setOwnAttribs(mipColl.toArray(new AnAttrib[mipColl.size()]));
        Collection<String> xpColl = new ArrayList<String>();
        if(anObjRec.extraProperties != null) {
            for (String s: anObjRec.extraProperties) {
                xpColl.add(s);
            }
        }
        return anOrm;
    }

    private Map<String, AnObjectRecord> loadObjects(JSONArray jsonArray) throws JSONException {
        Map<String, AnObjectRecord> mil = new HashMap<>();

        int itemCount = jsonArray.length();

        for (int i = 0; i < itemCount; i++) {
            JSONObject jo = jsonArray.getJSONObject(i);
            AnObjectRecord mi = loadAnObjectRecord(jo);
            mil.put(mi.directPropertiesMap.get(CLASS_NAME), mi);
        }
        return mil;
    }

    private AnObjectRecord loadAnObjectRecord(JSONObject jo) throws JSONException {
        AnObjectRecord rec = new AnObjectRecord();
        rec.directPropertiesMap = new HashMap<>();
        ensureProperty(CLASS_NAME, jo, rec.directPropertiesMap);
        ensureProperty(TABLE_NAME, jo, rec.directPropertiesMap);
        ensureProperty(DEPENDS, jo, rec.directPropertiesMap);
        rec.propertiesMap = ensureAttributes(jo);
        return rec;
    }

    private String ensureProperty(String name, JSONObject jo, Map<String, String> propertiesMap) throws JSONException {
        if(jo.has(name)) {
            String value = jo.getString(name);
            if(propertiesMap != null) {
                propertiesMap.put(name, value);
            }
            return value;
        }
        return null;
    }

    private Collection<Map<String, String>> ensureAttributes(JSONObject jo) throws JSONException {
        if(jo.has(ATTRIBUTES)) {
            Collection<Map<String, String>> propColl = new ArrayList<>();
            JSONArray detailsArray = jo.getJSONArray(ATTRIBUTES);
            for (int i = 0; i < detailsArray.length(); i++) {
                JSONObject propertyObject = detailsArray.getJSONObject(i);
                Map<String, String> map = new HashMap<>();
                ensureProperty(ATTRIB_NAME, propertyObject, map);
                ensureProperty(COLUMN_NAME, propertyObject, map);
                ensureProperty(JSON_NAME, propertyObject, map);
                ensureProperty(DB_COLUMN_DEFINITION, propertyObject, map);
                propColl.add(map);
            }
            return propColl;
        }
        return null;
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

}
