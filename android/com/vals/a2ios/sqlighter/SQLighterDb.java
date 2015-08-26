package com.vals.a2ios.sqlighter;

/**
 * General interface for managing SQLite database
 *
 */
public interface SQLighterDb {
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
    public void setDbPath(String path);

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
     * Copy sqlite project source database file to the device
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
     * Execute SELECT statement  with previously (optionally) specified parameters and return result set
     * @param selectQuery
     * @return
     */
    public SQLighterRs executeSelect(String selectQuery);

    /**
     * Execute UPDATE/INSERT/DELETE with previously (optionally) specified parameters
     * @param insert
     */
    public void executeChange(String insert);
}
