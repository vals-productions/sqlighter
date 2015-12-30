package com.vals.a2ios.amfibian.impl;

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
 *
 * Amfibian SQL generation object
 */
public class AnSql<T> extends AnObject<T> {
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

    public AnSql(String tableName, Class<T> anObjClass, AnAttrib[] attribList, AnObject<?> parentAnObject) {
        super(anObjClass, attribList, parentAnObject);
        this.tableName = tableName;
    }
    public AnSql(String tableName, Class<T> anObjClass, String[] attribColumnList, AnObject<?> parentAnObject) {
        super(anObjClass, attribColumnList, parentAnObject);
        this.tableName = tableName;
    }

    public Set<String> getSkipAttrNameList() {
        return skipAttrNameList;
    }

    public void resetSkipInclAttrNameList() {
        skipAttrNameList.clear();
        inclAttrNameList.clear();
    }
    
    public void addInclAttribs(String[] names) {
        List<String> nms = Arrays.asList(names);
        inclAttrNameList.addAll(nms);
        skipAttrNameList.clear();
    }
    
    public void addSkipAttribs(String[] names) {
        List<String> nms = Arrays.asList(names);
        skipAttrNameList.addAll(nms);
    }

    public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	protected AnSql() {
    }
    
    public List<Object> getParameters() {
        return parameters;
    }

    public List<String> getAttribNameList() {
        return attribNameList;
    }

    public int getType() {
        return type;
    }

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
            if (!getSkipAttrNameList().contains(propertyName)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public void startSqlDelete() throws Exception {
        reset();
        type = TYPE_DELETE;
    }
    
    public void startSqlInsert(T objectToInsert) throws Exception {
        reset();
        setNativeObject(objectToInsert);
        type = TYPE_INSERT;
        insertParamClause = new StringBuilder();
        Map<String, AnAttrib> om = getAttribList();
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

    public void startSqlUpdate(T objectToUpdate) throws Exception {
        reset();
        setNativeObject(objectToUpdate);
        type = TYPE_UPDATE;
        Map<String, AnAttrib> om = getAttribList();
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

    public String getColumnName(AnAttrib attrib) {
    	return attrib.getColumnOrAttribName();
    }

    public AnSql<?> startSqlCreate() {
        reset();
        type = TYPE_CREATE;
        Map<String, AnAttrib> cm = 
        getAttribList();
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

    public String getAliasedColumn(String columnName) {
        return alias + "." + columnName;
    }

    public void startSqlSelect() {
        reset();
        type = TYPE_SELECT;
        alias = tableName + "0";
        Map<String, AnAttrib> cm = getAttribList();
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

    public void addWhere(String condition, Object param) {
        if (param != null) {
            addWhere(condition);
            parameters.add(param);
        }
    }

    public void addWhere(String condition) {
        condition = ensureFirstCondition(condition);
        isWhere = true;
        addSql(condition);
    }

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
