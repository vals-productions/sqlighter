package com.vals.a2ios.amfibian.intf;

import com.vals.a2ios.amfibian.intf.AnAttrib;

import java.util.List;
import java.util.Set;

/**
 * Created by vsayenko on 1/8/16.
 *
 * AmfibiaN SQL generation interface. AnSql builds queries based on AnObject's attributes.
 *
 */
public interface AnSql<T> extends AnObject<T> {
    /**
     * Initiate SQL select query generation. 
     */
    void startSqlSelect();

    /**
     * Initiate SQL delete query generation.
     * @throws Exception
     */
    void startSqlDelete() throws Exception;

    /**
     * Initiate SQL insert query generation
     * @param objectToInsert - the object to insert.
     * @throws Exception
     */
    void startSqlInsert(T objectToInsert) throws Exception;

    /**
     * Initiate SQL update query generation.
     * @param objectToUpdate - this object's values will be applied to "set ..."  clause of the query.
     * @throws Exception
     */
    void startSqlUpdate(T objectToUpdate) throws Exception;

    /**
     * Adds where condition to previously initiated SELECT, UPDATE or DELETE query.
     * @param condition - " and/or [sql condition]" format.
     * @param param - param to bind. If param == null, the condition is being skipped.
     */
    void addWhere(String condition, Object param);

    /**
     *Adds parameterless where condition to previously initiated SELECT, UPDATE or DELETE query.
     * @param condition
     */
    void addWhere(String condition);

    /**
     * Adds custom SQL to previously initiated SELECT, UPDATE or DELETE statement.
     * @param sql
     */
    void addSql(String sql);

    /**
     * Returns current SQL query string. May be used for verification purposes or in conjunction with whatever SQL engine you use.
     * @return
     */
    String getQueryString();

    /**
     * 
     * @return
     */
    Set<String> getSkipAttrNameList();

    /**
     * resets values set by either Incl or Skip lists
     */
    void resetSkipInclAttrNameList();

    /**
     * limits SELECT SQL query select clause to attributes listed in the call to this method.
     * @param names
     */
    void addInclAttribs(String... names);

    /**
     *  limits SELECT SQL query select clause to all  attributes less atributes listed in the call to this method.
     * @param names
     */
    void addSkipAttribs(String... names);

    /**
     *
     * @return
     */
    String getTableName();

    /**
     * Sets DB table name to use with queries.
     * @param tableName
     */
    void setTableName(String tableName);

    /**
     * Gives the final list of parameters associated with the query throuh "addWhere" statements.
     * @return
     */
    List<Object> getParameters();

    /**
     * // TODO: remove from interface for now.
     * @return
     */
    List<String> getAttribNameList();

    /**
     * TODO: remove from interface.
     * @return
     */
    int getType();

    /**
     * TODO: remove from interface.
     * @param type
     */
    void setType(int type);

    /**
     * // TODO: remove from interface for now.
     * @param attrib
     * @return
     */
    String getColumnName(AnAttrib attrib);

    /**
     * Initiates CREATE table SQL statement.
     * @return
     */
    AnSql<?> startSqlCreate();

    /**
     * // TODO: remove from interface for now
     * @param columnJavaClass
     * @return
     */
    String getSqlTypeForClass(Class<?> columnJavaClass);

    /**
     * // TODO: remove from interface for now.
     * @param columnName
     * @return
     */
    String getAliasedColumn(String columnName);

}