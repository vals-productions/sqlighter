package com.vals.a2ios.amfibian.intf;

import com.vals.a2ios.sqlighter.intf.SQLighterDb;

import java.util.Collection;

/**
 * Created by vsayenko on 1/8/16.
 */
public interface AnOrm<T> extends AnSql<T> {
    /**
     * Executes SQL select query and returns results.
     *
     * Closes result set.
     * @return
     * @throws Exception
     */
    Collection<T> getRecords() throws Exception;

    /**
     * Executes SQL select query and returns results placed
     * in provided collection.
     *
     * Closes result set.
     *
     * @throws Exception
     */
    Collection<T> getRecords(Collection<T> collectionToUse) throws Exception;

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
    T getSingleResult() throws Exception;

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
    T getFirstResultOrNull() throws Exception;

    /**
     * Executes INSERT/UPDATE/DELETE/CREATE type of queries.
     *
     * @return Last inserted row id in case of "insert" statement, affected row count in
     * case of update/delete statements. Check SQLite docs on row id information.
     *
     * @throws Exception
     */
    Long apply() throws Exception;

    void setSqlighterDb(SQLighterDb sqlighterDb);

    SQLighterDb getSqlighterDb();
}
