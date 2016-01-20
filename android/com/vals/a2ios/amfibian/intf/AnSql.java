package com.vals.a2ios.amfibian.intf;

import com.vals.a2ios.amfibian.intf.AnAttrib;

import java.util.List;
import java.util.Set;

/**
 * Created by vsayenko on 1/8/16.
 *
 * AmfibiaN SQL generation interface
 *
 */
public interface AnSql<T> extends AnObject<T> {
    /**
     *
     */
    void startSqlSelect();

    /**
     *
     * @throws Exception
     */
    void startSqlDelete() throws Exception;

    /**
     *
     * @param objectToInsert
     * @throws Exception
     */
    void startSqlInsert(T objectToInsert) throws Exception;

    /**
     *
     * @param objectToUpdate
     * @throws Exception
     */
    void startSqlUpdate(T objectToUpdate) throws Exception;

    /**
     *
     * @param condition
     * @param param
     */
    void addWhere(String condition, Object param);

    /**
     *
     * @param condition
     */
    void addWhere(String condition);

    /**
     *
     * @param sql
     */
    void addSql(String sql);

    /**
     *
     * @return
     */
    String getQueryString();

    /**
     *
     * @return
     */
    Set<String> getSkipAttrNameList();

    /**
     *
     */
    void resetSkipInclAttrNameList();

    /**
     *
     * @param names
     */
    void addInclAttribs(String... names);

    /**
     *
     * @param names
     */
    void addSkipAttribs(String... names);

    /**
     *
     * @return
     */
    String getTableName();

    /**
     *
     * @param tableName
     */
    void setTableName(String tableName);

    /**
     *
     * @return
     */
    List<Object> getParameters();

    /**
     *
     * @return
     */
    List<String> getAttribNameList();

    /**
     *
     * @return
     */
    int getType();

    /**
     *
     * @param type
     */
    void setType(int type);

    /**
     *
     * @param attrib
     * @return
     */
    String getColumnName(AnAttrib attrib);

    /**
     *
     * @return
     */
    AnSql<?> startSqlCreate();

    /**
     *
     * @param columnJavaClass
     * @return
     */
    String getSqlTypeForClass(Class<?> columnJavaClass);

    /**
     *
     * @param columnName
     * @return
     */
    String getAliasedColumn(String columnName);

}
