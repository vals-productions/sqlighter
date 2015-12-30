package com.vals.a2ios.amfibian.impl;

import com.vals.a2ios.sqlighter.intf.SQLighterDb;
import com.vals.a2ios.sqlighter.intf.SQLighterRs;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by vsayenko on 9/30/15.
 *
 * AmfibiaN ORM class.
 *
 * This class facilitates business object persistence and
 * retrieval operations by extending AnSql query generation
 * capabilities.
 */
public class AnOrm<T> extends AnSql<T> {
    protected SQLighterDb sqlighterDb;

    protected AnOrm() {
        super();
    }

    public AnOrm(SQLighterDb sqLighterDb, String tableName, Class<T> anObjClass, AnAttrib[] attribList, AnObject<?> parentAnObject) {
        super(tableName, anObjClass, attribList, parentAnObject);
        this.sqlighterDb = sqLighterDb;
    }

    public AnOrm(SQLighterDb sqLighterDb, String tableName, Class<T> anObjClass, String[] attribColumnList, AnObject<?> parentAnObject) {
        super(tableName, anObjClass, attribColumnList, parentAnObject);
        this.sqlighterDb = sqLighterDb;
    }

    /**
     * Executes SQL select query and returns results.
     *
     * Closes result set.
     * @return
     * @throws Exception
     */
    public Collection<T> getRecords() throws Exception {
        return getRecords(null);
    }

    /**
     * Executes SQL select query and returns results placed
     * in provided collection.
     *
     * Closes result set.
     *
     * @throws Exception
     */
    public Collection<T> getRecords(Collection<T> collectionToUse) throws Exception {
        String queryStr = this.getQueryString();
        if (collectionToUse == null) {
            collectionToUse = new LinkedList<>();
        }
        applyParameters();

        SQLighterRs rs = sqlighterDb.executeSelect(queryStr);
        while(rs.hasNext()) {
            resetNativeObject();
            int columnIndex = 0;
            for (String attribName: this.getAttribNameList()) {
                if(!isSkipAttr(attribName)) {
                    Object columnValue = rs.getObject(columnIndex++);
                    if (columnValue != null) {
                        AnAttrib ap = getAttrib(attribName);
                        ap.setValue(columnValue);
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

    /**
     * Executes SQL select query and returns a single result
     * if the query returns exactly one result.
     *
     * Otherwise returns null.
     *
     * Closes result set.
     *
     * @throws Exception
     */
    public T getSingleResult() throws Exception {
        Collection<T> l = getRecords(null);
        if (l == null || l.size() != 1) {
            return null;
        }
        return l.iterator().next();
    }

    /**
     * Executes SQL select query and returns the first result
     * if the query returns one or more records.
     *
     * Otherwise returns null.
     *
     * Closes result set.
     *
     * @throws Exception
     */
    public T getFirstResultOrNull() throws Exception {
        Collection<T> l = getRecords(null);
        if (l == null || l.size() == 0) {
            return null;
        }
        Iterator<T> i = l.iterator();
        return i.next();
    }

    private void applyParameters() {
        List<Object> parameters = this.getParameters();
        for (Object par: parameters) {
            sqlighterDb.addParamObj(par);
        }
    }

    /**
     * Executes INSERT/UPDATE/DELETE/CREATE type of queries.
     *
     * @return Last inserted row id in case of "insert" statement, affected row count in
     * case of update/delete statements. Check SQLite docs on row id information.
     *
     * @throws Exception
     */
    public Long apply() throws Exception {
        if (this.getType() == AnSql.TYPE_INSERT ||
            this.getType() == AnSql.TYPE_UPDATE ||
            this.getType() == AnSql.TYPE_DELETE ||
            this.getType() == AnSql.TYPE_CREATE) {
            String q = this.getQueryString();
            applyParameters();
            Long updateInfo = sqlighterDb.executeChange(q);
            return updateInfo;
        }
        return null;
    }

    public void setSqlighterDb(SQLighterDb sqlighterDb) {
        this.sqlighterDb = sqlighterDb;
    }

    public SQLighterDb getSqlighterDb() {
        return sqlighterDb;
    }
}
