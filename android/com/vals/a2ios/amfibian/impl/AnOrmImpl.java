package com.vals.a2ios.amfibian.impl;

import com.vals.a2ios.amfibian.intf.AnAttrib;
import com.vals.a2ios.amfibian.intf.AnIncubator;
import com.vals.a2ios.amfibian.intf.AnObject;
import com.vals.a2ios.amfibian.intf.AnOrm;
import com.vals.a2ios.sqlighter.intf.SQLighterDb;
import com.vals.a2ios.sqlighter.intf.SQLighterRs;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by vsayenko on 9/30/15.
 */
public class AnOrmImpl<T> extends AnSqlImpl<T> implements AnOrm<T> {
    protected SQLighterDb sqlighterDb;
    private AnIncubator incubator;

    public AnOrmImpl() {
        super();
    }

    public AnOrmImpl(SQLighterDb sqLighterDb, String tableName, Class<T> anObjClass, AnAttrib[] attribList, AnObject<?> parentAnObject) {
        super(tableName, anObjClass, attribList, parentAnObject);
        this.sqlighterDb = sqLighterDb;
    }

    public AnOrmImpl(SQLighterDb sqLighterDb, String tableName, Class<T> anObjClass, String[] attribColumnList, AnObject<?> parentAnObject) {
        super(tableName, anObjClass, attribColumnList, parentAnObject);
        this.sqlighterDb = sqLighterDb;
    }

    @Override
    public Collection<T> getRecords() throws Exception {
        return getRecords(null);
    }

    @Override
    public void setIncubator(AnIncubator incubator) {
        this.incubator = incubator;
    }

    private SQLighterDb getDbEngine() throws Exception {
        if(sqlighterDb == null) {
            throw new Exception("DB engine is not set.");
        }
        return sqlighterDb;
    }

    private AnIncubator getIncubator() throws Exception {
        if(incubator == null) {
            throw new Exception("Incubator is not set");
        }
        return incubator;
    }

    @Override
    public Collection<T> getRecords(Collection<T> collectionToUse) throws Exception {
        String queryStr = this.getQueryString();
        if (collectionToUse == null) {
            collectionToUse = new LinkedList<T>();
        }
        applyParameters();

        SQLighterRs rs = getDbEngine().executeSelect(queryStr);
        while(rs.hasNext()) {
            resetNativeObject();
            int columnIndex = 0;
            for (String attribName: this.getAttribNameList()) {
                if(!isSkipAttr(attribName)) {
                    Object columnValue = rs.getObject(columnIndex++);
                    if (columnValue != null) {
                        AnAttrib attrib = getAttrib(attribName);
                        setValue(attrib.getDbSetAdapter(),
                                getDbSetAdapter(),
                                attrib, columnValue);
                    }
                }
            }
            Object objectValue = getNativeObject();
            @SuppressWarnings("unchecked")
            T ov = (T) objectValue;
            collectionToUse.add(ov);
        }
        rs.close();
        return collectionToUse;
    }



    @Override
    public Collection<JSONObject> getJSONObjectRecords() throws Exception {
        return getJSONObjectRecords(null);
    }

    @Override
    public Collection<JSONObject> getJSONObjectRecords(Collection<T> collectionToUse) throws Exception {
        Collection<T> rc = getRecords(collectionToUse);
        Collection<JSONObject> joc = new LinkedList<>();
        for (T c: rc) {
            joc.add(asJSONObject(c));
        }
        return joc;
    }

    @Override
    public T getSingleResult() throws Exception {
        Collection<T> l = getRecords(null);
        if (l == null || l.size() != 1) {
            return null;
        }
        return l.iterator().next();
    }

    @Override
    public T getFirstResultOrNull() throws Exception {
        Collection<T> l = getRecords(null);
        if (l == null || l.size() == 0) {
            return null;
        }
        Iterator<T> i = l.iterator();
        return i.next();
    }

    private void applyParameters() throws Exception {
        List<Object> parameters = this.getParameters();
        for (Object par: parameters) {
            getDbEngine().addParamObj(par);
        }
    }

    @Override
    public Long apply() throws Exception {
        if (this.getType() == AnSqlImpl.TYPE_INSERT ||
                this.getType() == AnSqlImpl.TYPE_UPDATE ||
                this.getType() == AnSqlImpl.TYPE_DELETE
                ) {
            String q = this.getQueryString();
            applyParameters();
            Long updateInfo = getDbEngine().executeChange(q);
            return updateInfo;
        } else if(this.getType() == AnSqlImpl.TYPE_CREATE) {
            String q = this.getQueryString();
            Long updateInfo = getDbEngine().executeChange(q);
            return updateInfo;
        }
        return null;
    }

    @Override
    public void setSqlighterDb(SQLighterDb sqlighterDb) {
        this.sqlighterDb = sqlighterDb;
    }

    @Override
    public SQLighterDb getSqlighterDb() {
        return sqlighterDb;
    }

    @Override
    public void fetch(T entity, String attribName) throws Exception {
        fetch(entity, attribName, null);
    }

    @Override
    public void fetch(Collection<T> entities, String attribName) throws Exception {
        fetch(entities, attribName, null);
    }

    /**
     *
     * @param entity - entity instance to fetch association for
     * @param attribName - name of the attribute for the association to be assigned to
     * @param extraSql - extra condition to be added to the associon retrieval SQL query
     */
    @Override
    public void fetch(T entity, String attribName, String extraSql) throws Exception {
        Collection<T> c = new LinkedList<>();
        c.add(entity);
        fetch(c, attribName, extraSql);
    }

    /**
     * Will retrieve and assign DB associations for
     * entity class' attribuName specified in JSON file
     * definitions.
     *
     * @param entities - list of entity instances to fetch association for
     * @param attribName - name of the attribute for the association to be assigned to
     * @param extraSql - extra condition to be added to the associon retrieval SQL query
     */
    @Override
    public void fetch(Collection<T> entities, String attribName, String extraSql) throws Exception {
        if(sqlighterDb == null) {
            throw new Exception("SQlighterDb is not set");
        }
        if (entities == null || entities.isEmpty() || attribName == null || "".trim().equals(attribName)) {
            throw new Exception("Incorrect parameters.");
        }
        Class<T> cluss = getNativeClass();
        AnOrm<T> sourceOrm = getIncubator().make(cluss);
        if(sourceOrm == null) {
            throw new Exception("No definition found for " + cluss.getName());
        }
        sourceOrm.setSqlighterDb(sqlighterDb);
        AnAttrib attrib = sourceOrm.getAttrib(attribName);
        if(attrib == null) {
            throw new Exception("Attribute " + attribName +  " is not defined");
        }
        String associationClassName = getIncubator().getAssociationTrgClassName(cluss, attrib);
        String associationTrgJoinAttribName = getIncubator().getAssociationTrgJoinAttribName(cluss, attrib);
        String assiciationSrcJoinAttribName = getIncubator().getAssociationSrcJoinAttribName(cluss, attrib);
        String associationSrcAttribName = getIncubator().getAssociationSrcAttribName(cluss, attrib);
        if(associationClassName == null || associationTrgJoinAttribName == null ||
                assiciationSrcJoinAttribName == null || associationSrcAttribName == null) {
            throw new Exception("Association definition is not complete.");
        }
        AnOrm<T> associateOrm = (AnOrm<T>)getIncubator().make(associationClassName);
        if(associateOrm == null) {
            throw new Exception("No definition found for: " + associationClassName);
        }
        associateOrm.setSqlighterDb(sqlighterDb);
        Collection<T> associations = fetchAssociations(
                associationClassName,
                entities,
                assiciationSrcJoinAttribName,
                associationTrgJoinAttribName,
                sourceOrm,
                associateOrm, extraSql);
        Map<Object, Collection<T>> associationMap = mapAssociations(associations, associateOrm, associationTrgJoinAttribName);
        assign(entities, associationMap, assiciationSrcJoinAttribName, associationSrcAttribName, sourceOrm);
    }

    private void assign(Collection<T> entities,
                            Map<Object, Collection<T>> associationMap,
                            String assiciationSrcJoinAttribName,
                            String assiciationSrcAttribName,
                            AnOrm<T> sourceOrm) throws Exception {
        for (T entity: entities) {
            sourceOrm.setNativeObject(entity);
            AnAttrib srcAttrib = sourceOrm.getAttrib(assiciationSrcJoinAttribName);
            Object assiciationKeyValue = srcAttrib.getValue();
            AnAttrib attrib = sourceOrm.getAttrib(assiciationSrcAttribName);
            if (attrib == null) {
                throw new Exception("Attribute " + assiciationSrcAttribName + " is not defined.");
            }
            Collection<T> associations = associationMap.get(assiciationKeyValue);
            if (isCollection(attrib)) {
                attrib.setValue(associations);
            } else if(associations != null && associations.size() == 1) {
                attrib.setValue(associations.iterator().next());
            } else {
                attrib.setValue(null);
            }
        }
    }

    private <A> Map<Object, Collection<A>> mapAssociations(
            Collection<A> associations,
            AnOrm<A> associateOrm,
            String associationTrgJoinAttribName) throws Exception {
        Map<Object, Collection<A>> associationMap = new LinkedHashMap<>();
        for (A association: associations) {
            associateOrm.setNativeObject(association);
            Object associationColumnValue = associateOrm.getAttrib(associationTrgJoinAttribName).getValue();
            Collection<A> items = associationMap.get(associationColumnValue);
            if (items == null) {
                items = new LinkedList<>();
                associationMap.put(associationColumnValue, items);
            }
            items.add(association);
        }
        return associationMap;
    }


    private boolean isCollection(AnAttrib attrib) {
        Method m = attrib.getGetter();
        if (m != null) {
            Class<?> rtc = m.getReturnType();
            if (rtc != null) {
                return Collection.class.isAssignableFrom(rtc);
            }
        }
        return false;
    }

    private <A> Collection<A> fetchAssociations(
            String associationClassName,
            Collection<T> entities,
            String assiciationSrcJoinAttribName,
            String associationTrgJoinAttribName,
            AnOrm<T> sourceOrm,
            AnOrm<A> associateOrm,
            String extraSql) throws Exception {
        if (associationClassName == null || associationTrgJoinAttribName == null || assiciationSrcJoinAttribName == null) {
            return null;
        }
        associateOrm.startSqlSelect();
        // StringBuilder sb = new StringBuilder();
        int size = entities.size();
        int idx = 0;
        AnAttrib trgAttr = associateOrm.getAttrib(associationTrgJoinAttribName);
        if (trgAttr == null || trgAttr.getColumnName() == null) {
            throw new Exception("Target attribute " + associationTrgJoinAttribName + " is not defined.");
        }
        for (T entity: entities) {
            sourceOrm.setNativeObject(entity);
            AnAttrib scrAttr = sourceOrm.getAttrib(assiciationSrcJoinAttribName);
            if (scrAttr == null) {
                throw new Exception("Source attribute " + associationTrgJoinAttribName + " is not defined.");
            }
            Object parameter = scrAttr.getValue(); // todo: check dupl. through a set?
            if (idx == 0 && idx == size - 1) {
                associateOrm.addWhere("and " + trgAttr.getColumnName() + " = ?", parameter);
            } else if (idx == 0) {
                associateOrm.addWhere("and " + trgAttr.getColumnName() + " in(?", parameter);
            } else if (idx == size - 1) {
                associateOrm.addWhere(",?)", parameter);
            } else {
                associateOrm.addWhere(",?", parameter);
            }
            idx++;
        }
        if (extraSql != null) {
            associateOrm.addSql(extraSql);
        }
        Collection<A> associations = associateOrm.getRecords();
        return associations;
    }


}
