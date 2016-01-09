package com.vals.a2ios.amfibian.intf;

import com.vals.a2ios.amfibian.intf.AnAttrib;

import java.util.List;
import java.util.Set;

/**
 * Created by vsayenko on 1/8/16.
 */
public interface AnSql<T> extends AnObject<T> {

    Set<String> getSkipAttrNameList();

    void resetSkipInclAttrNameList();

    void addInclAttribs(String[] names);

    void addSkipAttribs(String[] names);

    String getTableName();

    void setTableName(String tableName);

    List<Object> getParameters();

    List<String> getAttribNameList();

    int getType();

    void setType(int type);

    void startSqlDelete() throws Exception;

    void startSqlInsert(T objectToInsert) throws Exception;

    void startSqlUpdate(T objectToUpdate) throws Exception;

    String getColumnName(AnAttrib attrib);

    AnSql<?> startSqlCreate();

    String getSqlTypeForClass(Class<?> columnJavaClass);

    String getAliasedColumn(String columnName);

    void startSqlSelect();

    void addWhere(String condition, Object param);

    void addWhere(String condition);

    void addSql(String sql);

    String getQueryString();
}
