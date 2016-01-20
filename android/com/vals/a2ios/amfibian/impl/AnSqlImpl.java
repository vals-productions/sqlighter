package com.vals.a2ios.amfibian.impl;

import com.vals.a2ios.amfibian.intf.AnAttrib;
import com.vals.a2ios.amfibian.intf.AnObject;
import com.vals.a2ios.amfibian.intf.AnSql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by vsayenko on 9/23/15.
 */
public class AnSqlImpl<T> extends AnObjectImpl<T> implements AnSql<T> {
    public static final int TYPE_SELECT = 1;
    public static final int TYPE_UPDATE = 2;
    public static final int TYPE_INSERT = 3;
    public static final int TYPE_CREATE = 4;
    public static final int TYPE_DELETE = 5;

    private StringBuilder queryStr;
    private List<Object> parameters = new ArrayList<>();
    private int type;
    private String columnClause;
    protected String tableName;
    private String alias = "";
    private StringBuilder whereClause;
    private boolean isWhere = false;
    private StringBuilder insertParamClause;

    private List<String> attribNameList = new LinkedList<>();
    
    private Set<String> skipAttrNameList = new HashSet<>();
    private Set<String> inclAttrNameList = new HashSet<>();

    public AnSqlImpl(String tableName, Class<T> anObjClass, AnAttrib[] attribList, AnObject<?> parentAnObject) {
        super(anObjClass, attribList, parentAnObject);
        this.tableName = tableName;
    }

    public AnSqlImpl(String tableName, Class<T> anObjClass, String[] attribColumnList, AnObject<?> parentAnObject) {
        super(anObjClass, attribColumnList, parentAnObject);
        this.tableName = tableName;
    }

    public AnSqlImpl(String tableName, AnObject<T> anAllDefinedObject) throws Exception {
        if(anAllDefinedObject.getNativeObject() == null) {
            anAllDefinedObject.resetNativeObject();
        }
        init(anAllDefinedObject.getNativeClass(),
             anAllDefinedObject.getOwnAttribs(),
             anAllDefinedObject.getParentAnObject());
        this.tableName = tableName;
    }

    @Override
    public Set<String> getSkipAttrNameList() {
        return skipAttrNameList;
    }

    @Override
    public void resetSkipInclAttrNameList() {
        skipAttrNameList.clear();
        inclAttrNameList.clear();
    }
    
    @Override
    public void addInclAttribs(String[] names) {
        List<String> nms = Arrays.asList(names);
        inclAttrNameList.addAll(nms);
        skipAttrNameList.clear();
    }
    
    @Override
    public void addSkipAttribs(String[] names) {
        List<String> nms = Arrays.asList(names);
        skipAttrNameList.addAll(nms);
    }

    @Override
    public String getTableName() {
		return tableName;
	}
	@Override
    public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	protected AnSqlImpl() {
    }
    
    @Override
    public List<Object> getParameters() {
        return parameters;
    }

    @Override
    public List<String> getAttribNameList() {
        return attribNameList;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public void setType(int type) {
        this.type = type;
    }
    
    private void reset() {
       queryStr = new StringBuilder();
       isWhere = false;
       whereClause = null;
       parameters.clear();
       attribNameList.clear();
    }

    protected boolean isSkipAttr(String propertyName) {
        if (inclAttrNameList.size() > 0) {
            if (inclAttrNameList.contains(propertyName)) {
                return false;
            } else {
                return true;
            }
        } else if (getSkipAttrNameList().size() > 0) {
            if (getSkipAttrNameList().contains(propertyName)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    public void startSqlDelete() throws Exception {
        reset();
        type = TYPE_DELETE;
    }
    
    @Override
    public void startSqlInsert(T objectToInsert) throws Exception {
        reset();
        setNativeObject(objectToInsert);
        type = TYPE_INSERT;
        insertParamClause = new StringBuilder();
        Map<String, AnAttrib> om = getAllAttribMap();
        Set<String> attrNames = om.keySet();
        for (String attrName: attrNames) {
            if (!isSkipAttr(attrName)) {
                AnAttrib attr = om.get(attrName);
                Object value = attr.getValue();
                if (value != null) {
                    queryStr.append(getColumnName(attr));
                    parameters.add(value);
                    insertParamClause.append("?");
                } else {
                    queryStr.append(getColumnName(attr));
                    insertParamClause.append("NULL");
                }
                attribNameList.add(attrName);
                queryStr.append(',');
                insertParamClause.append(",");
        }
        }
        queryStr.replace(queryStr.length() - 1, queryStr.length(), " ");
        insertParamClause.replace(insertParamClause.length() - 1, insertParamClause.length(), " ");
        columnClause = queryStr.toString();
    }

    @Override
    public void startSqlUpdate(T objectToUpdate) throws Exception {
        reset();
        setNativeObject(objectToUpdate);
        type = TYPE_UPDATE;
        Map<String, AnAttrib> om = getAllAttribMap();
        Set<String> attrNames = om.keySet();
        for (String attrName: attrNames) {
            if (!isSkipAttr(attrName)) {
                AnAttrib attrib = om.get(attrName);
                if (attrib != null) {
                    queryStr.append(getColumnName(attrib) + " = ?");
                    parameters.add(attrib.getValue());
                } else {
                    queryStr.append(getColumnName(attrib) + " = NULL");
                }
                attribNameList.add(attrName);
                queryStr.append(',');
            }
        }
        queryStr.replace(queryStr.length() - 1, queryStr.length(), " ");
        columnClause = queryStr.toString();
    }

    @Override
    public String getColumnName(AnAttrib attrib) {
    	return attrib.getColumnOrAttribName();
    }

    @Override
    public AnSql<?> startSqlCreate() {
        reset();
        type = TYPE_CREATE;
        Map<String, AnAttrib> cm =
        getAllAttribMap();
        Set<String> attribNames = cm.keySet();
        for (String attribName: attribNames) {
                AnAttrib attr = cm.get(attribName);
                String colName = getColumnName(attr);
                queryStr.append(colName);
                String columnType = getSqlTypeForClass(attr.getAttribClass());
                queryStr.append(" " + columnType);
                queryStr.append(',');
        }
        queryStr.replace(queryStr.length() - 1, queryStr.length(), " ");
        columnClause = queryStr.toString();
        return this;
    }
    
    @Override
    public String getSqlTypeForClass(Class<?> columnJavaClass) {
        if (columnJavaClass != null) {
            String className = columnJavaClass.getCanonicalName();
            if (Long.class.getCanonicalName().equals(className)) {
                    return "INTEGER";
            } else if(Integer.class.getCanonicalName().equals(className)) {
                    return "INTEGER";
            } else if(Short.class.getCanonicalName().equals(className)) {
                    return "INTEGER";
            } else if(Float.class.getCanonicalName().equals(className)) {
                    return "REAL";
            } else if(Double.class.getCanonicalName().equals(className)) {
                    return "REAL";
            } else if(String.class.getCanonicalName().equals(className)) {
                    return "TEXT";
            } else if(Date.class.getCanonicalName().equals(className)) {
                    return "TEXT";
            }
                // "BLOB"
        }
        return "TEXT";
    }

    private String getAlias() {
        return alias;
    }

    @Override
    public String getAliasedColumn(String columnName) {
        return alias + "." + columnName;
    }

    @Override
    public void startSqlSelect() {
        reset();
        type = TYPE_SELECT;
        alias = tableName + "0";
        Map<String, AnAttrib> cm = getAllAttribMap();
        Set<String> propertyNames = cm.keySet();
        for (String pName: propertyNames) {
            if (!isSkipAttr(pName)) {
                String colName = getColumnName(cm.get(pName));
                queryStr.append(alias);
                queryStr.append('.');
                queryStr.append(colName);
                attribNameList.add(pName);
                queryStr.append(',');
            }
        }
        queryStr.replace(queryStr.length() - 1, queryStr.length(), " ");
        columnClause = queryStr.toString();
    }

    private String ensureFirstCondition(String condition) {
        if (whereClause == null) {
            if (condition.trim().toLowerCase().startsWith("and ")) {
                condition = condition.trim().substring(4);
            }
            if (condition.trim().toLowerCase().startsWith("or ")) {
                condition = condition.trim().substring(3);
            }
            whereClause = new StringBuilder();
        }
        return condition;
    }

    @Override
    public void addWhere(String condition, Object param) {
        if (param != null) {
            addWhere(condition);
            parameters.add(param);
        }
    }

    @Override
    public void addWhere(String condition) {
        condition = ensureFirstCondition(condition);
        isWhere = true;
        addSql(condition);
    }

    @Override
    public void addSql(String sql) {
        if (whereClause == null) {
            whereClause = new StringBuilder();
        }
        queryStr = new StringBuilder();
        queryStr.append(' ');
        queryStr.append(sql);
        queryStr.append(' ');
        whereClause.append(queryStr);
    }

    @Override
    public String getQueryString() {
        if(type == TYPE_CREATE) {
            StringBuilder sb = new StringBuilder();
            sb.append("create table ");
            sb.append(tableName);
            sb.append('(');
            sb.append(columnClause);
            sb.append(')');
            return sb.toString();
        } else if (type == TYPE_SELECT) {
            StringBuilder sb = new StringBuilder();
            sb.append("select ");
            sb.append(columnClause);
            sb.append(" from ");
            sb.append(tableName + ' ' + alias);
            if (isWhere) {
                sb.append(" where ");
            }
            if(whereClause != null) {
                sb.append(whereClause);
            }
            String qString = sb.toString();
            qString = qString.replaceAll("#", getAlias());
            return qString;
        } else if (type == TYPE_UPDATE) {
            StringBuilder sb = new StringBuilder();
            sb.append("update ");
            sb.append(tableName + " ");
            sb.append("set ");
            sb.append(columnClause);
            if (whereClause != null) {
                sb.append(" where ");
                sb.append(whereClause);
            }
            return sb.toString();
        } else if (type == TYPE_INSERT) {
            StringBuilder sb = new StringBuilder();
            sb.append("INSERT INTO ");
            sb.append(tableName);
            sb.append('(');
            sb.append(columnClause);
            sb.append(") VALUES (");
            sb.append(insertParamClause);
            sb.append(')');
            return sb.toString();
        } else if (type == TYPE_DELETE) {
            StringBuilder sb = new StringBuilder();
            sb.append("DELETE FROM ");
            sb.append(tableName);
            if (whereClause != null) {
                sb.append(" where ");
                sb.append(whereClause);
            }
            return sb.toString();
        }
        return null;
    }

}
