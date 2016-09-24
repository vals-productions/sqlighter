package com.vals.a2ios.amfibian.intf;

import com.vals.a2ios.sqlighter.intf.SQLighterDb;

import org.json.JSONObject;

import java.util.Collection;

/**
 * Created by vsayenko on 1/8/16.
 *
 * AmfibiaN ORM class.
 *
 * This class facilitates business object persistence and
 * retrieval operations by extending AnSqlImpl query generation
 * capabilities.
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

    void setIncubator(AnIncubator incubator);

    /**
     * Executes SQL select query and returns results placed
     * in provided collection.
     *
     * Closes result set.
     *
     * @throws Exception
     */
    Collection<T> getRecords(Collection<T> collectionToUse) throws Exception;

    Collection<JSONObject> getJSONObjectRecords(Collection<T> collectionToUse) throws Exception;

    Collection<JSONObject> getJSONObjectRecords() throws Exception;

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
     * @return Last inserted row id in case of "insert" statement,
     * affected row count in case of update/delete statements. Also
     * depends on whether the table has INTEGER PRIMARY KEY column
     * or not...
     *
     * Check SQLite docs on row id information.
     *
     * @throws Exception
     */
    Long apply() throws Exception;

    /**
     * Assign db engine
     * @param sqlighterDb
     */
    void setSqlighterDb(SQLighterDb sqlighterDb);

    /**
     *
     * @return assigned DB engine
     */
    SQLighterDb getSqlighterDb();

    /**
     * Will select associations of entit(y/ies) by attribName, and
     * assign associations to respective entit(y/ies). Extra Sql will
     * be optionally appended to association retrieval query. It might
     * contain filter or order by condition.
     */
    void fetch(T entity, String attribName, String extraSql) throws Exception;
    void fetch(Collection<T> entities, String attribName, String extraSql) throws Exception;
    void fetch(T entity, String attribName) throws Exception;
    void fetch(Collection<T> entities, String attribName) throws Exception;

}
