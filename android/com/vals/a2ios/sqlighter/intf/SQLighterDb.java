package com.vals.a2ios.sqlighter.intf;

import java.util.Date;

/**
 * General interface for managing SQLite database
 *
 */
public interface SQLighterDb {
    public static final String DATE_HINT = "_date";
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * Checks if Db file is already in place
     */
    public boolean isDbFileDeployed();

    /**
     * Specifies database file name
     *
     * @param name - sqlite database file name
     */
    public void setDbName(String name);

    /**
     * Specifies path to
     * @param path - path to source sqlite database file on the device
     * <pre>
     *  Android - "/data/data/<<YOUR PROJECT path>>/databases/"
     *  iOS - not used
     * </pre>
     */
    // public void setDbPath(String path);

    /**
     * Android only. Important as openIfClosed will call
     * <pre>
     *    context.openOrCreateDatabase(dbName, Context.MODE_PRIVATE, null);
     * </pre>
     * @param context - Context object
     */
    public void setContext(Object context);

    /**
     * If yes, copyDbOnce will overwrite device's sqlite database with the source (project's)
     * database file. May be useful during development, but shouldn't be useful for production.
     *
     * @param isOverwrite
     */
    public void setOverwriteDb(boolean isOverwrite);

    /**
     * Open database if it was not opened yet.
     *
     * @throws Exception
     */
    public void openIfClosed() throws Exception;

    /**
     * Check this doc for detailed info:
     *
     * https://github.com/vals-productions/sqlighter#sqlighterdbcopydbonce
     *
     * @throws Exception in case of input\output exceptions
     */
    public void copyDbOnce() throws Exception;

    /**
     * Bind the param for the statement that will be executed next
     * @param s
     */
    public void addParam(String s);
    /**
     * Bind the param for the statement that will be executed next
     * @param d
     */
    public void addParam(double d);
    /**
     * Bind the param for the statement that will be executed next
     * @param l
     */
    public void addParam(long l);
    /**
     * Bind NULL param for the statement that will be executed next
     *
     */
    public void addParamNull();
    /**
     * Bind the param for the statement that will be executed next
     * @param blob
     */
    public void addParam(byte[] blob);

    /**
     * Bind date paramß
     * @param date
     */
    public void addParam(Date date);
    /**
     * Bind object param
     *
     * @param o
     */
    public void addParamObj(Object o);

    /**
     * Execute SELECT statement  with previously (optionally) specified parameters and return result set
     * @param selectQuery
     * @return
     */
    public SQLighterRs executeSelect(String selectQuery) throws Exception;

    /**
     * Execute UPDATE/INSERT/DELETE/ALTER/CREATE with previously (optionally) specified parameters
     * @param insert
     * @return last inserted row id in case of "insert" statement. Check SQLite
     * docs on row id information.
     */
    public Long executeChange(String insert) throws Exception;

    /**
     * start transaction
     */
    public void beginTransaction() throws Exception;

    /**
     * commit transaction
     */
    public void commitTransaction() throws Exception;

    /**
     * rollback transaction
     */
    public void rollbackTransaction() throws Exception;

    /**
     * Close database
     */
    public void close();

    /**
     * @param isDateNamedColumn - if true, then Any string type column having "date"
     * in its name will be attempted to be parsed as a Date when processing a result set.
     * Otherwise, will be treated as a string.
     */
    public void setIsDateNamedColumn(boolean isDateNamedColumn);

}
