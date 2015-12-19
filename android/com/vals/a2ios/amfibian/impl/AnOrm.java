package com.vals.a2ios.amfibian.impl;

import com.vals.a2ios.sqlighter.intf.SQLighterDb;
import com.vals.a2ios.sqlighter.intf.SQLighterRs;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by vsayenko on 9/30/15.
 *
 * Amfibian ORM class
 */
public class AnOrm<T> extends AnSql<T> {
    protected SQLighterDb sqLighterDb;

    public AnOrm() {
        super();
    }
    public AnOrm(SQLighterDb sqLighterDb, String tableName, Class<T> anObjClass, AnAttrib[] attribList, AnObject<?> parentAnObject) {
        super(tableName, anObjClass, attribList, parentAnObject);
        this.sqLighterDb = sqLighterDb;
    }
    public AnOrm(SQLighterDb sqLighterDb, String tableName, Class<T> anObjClass, String[] attribColumnList, AnObject<?> parentAnObject) {
        super(tableName, anObjClass, attribColumnList, parentAnObject);
        this.sqLighterDb = sqLighterDb;
    }

    public List<T> getRecords() throws Exception {
        String queryStr = this.getQueryString();
        List<T> resultList = new LinkedList<>();
        applyParameters();

        SQLighterRs rs = sqLighterDb.executeSelect(queryStr);
        while(rs.hasNext()) {
            resetNativeObject();
            int columnIndex = 0;
            for (String attribName: this.getAttribNameList()) {
                Object columnValue = rs.getObject(columnIndex++);
                if (columnValue != null) {
                    AnAttrib ap = getAttrib(attribName);
                    ap.setValue(columnValue);
                }
            }
            Object objectValue = getNativeObject();
            @SuppressWarnings("unchecked")
			T ov = (T) objectValue;
            resultList.add(ov);
        }
        return resultList;
    }

    private void applyParameters() {
        List<Object> parameters = this.getParameters();
        for (Object par: parameters) {
            sqLighterDb.addParamObj(par);
        }
    }

    public Object apply() throws Exception {
        if (this.getType() == AnSql.TYPE_INSERT ||
            this.getType() == AnSql.TYPE_UPDATE ||
            this.getType() == AnSql.TYPE_DELETE) {
            String q = this.getQueryString();
            applyParameters();
            Long updateInfo = getSqLighterDb().executeChange(q);
            return updateInfo;
        }
        return null;
    }

    public SQLighterDb getSqLighterDb() {
        return sqLighterDb;
    }
}
