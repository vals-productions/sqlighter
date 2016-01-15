package com.vals.a2ios.amfibian.impl;

import com.vals.a2ios.amfibian.intf.AnAttrib;
import com.vals.a2ios.amfibian.intf.AnObject;
import com.vals.a2ios.amfibian.intf.AnOrm;
import com.vals.a2ios.sqlighter.intf.SQLighterDb;
import com.vals.a2ios.sqlighter.intf.SQLighterRs;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by vsayenko on 9/30/15.
 */
public class AnOrmImpl<T> extends AnSqlImpl<T> implements AnOrm<T> {
    protected SQLighterDb sqlighterDb;

    protected AnOrmImpl() {
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

    private void applyParameters() {
        List<Object> parameters = this.getParameters();
        for (Object par: parameters) {
            sqlighterDb.addParamObj(par);
        }
    }

    @Override
    public Long apply() throws Exception {
        if (this.getType() == AnSqlImpl.TYPE_INSERT ||
            this.getType() == AnSqlImpl.TYPE_UPDATE ||
            this.getType() == AnSqlImpl.TYPE_DELETE ||
            this.getType() == AnSqlImpl.TYPE_CREATE) {
            String q = this.getQueryString();
            applyParameters();
            Long updateInfo = sqlighterDb.executeChange(q);
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
}
