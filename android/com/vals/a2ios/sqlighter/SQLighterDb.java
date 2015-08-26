package com.vals.a2ios.sqlighter;

/**
 * General interface for managing SQLite database
 */
public interface SQLighterDb {
    /**
     *
     * @param name - sqlite database file name
     */
    public void setDbName(String name);

    /**
     *
     * @param path - path to source sqlite database file in the project path
     *             (typically - assets directory on android)
     */
    public void setDbPath(String path);

    /**
     * Android only
     * @param context - Context object
     */
    public void setContext(Object context);

    /**
     * If yes, copyDbOnce will overwrite device's sqlite database with the source database file
     * @param isOverwrite
     */
    public void setOverwriteDb(boolean isOverwrite);

    /**
     * Open database if it was not opened yet
     * @throws Exception
     */
    public void openIfClosed() throws Exception;

    /**
     * Copy sqlite project source database file to the device
     * @throws Exception
     */
    public void copyDbOnce() throws Exception;

    /**
     * Bind the param for the statement that will be executed next
     * @param s
     */
    public void addParam(String s);
    /**
     * Bind the param for the statement that will be executed next
     * @param s
     */
    public void addParam(double s);
    /**
     * Bind the param for the statement that will be executed next
     * @param s
     */
    public void addParam(long s);
//    /**
//     * Bind the param for the statement that will be executed next
//     * @param s
//     */
//    public void addParam(int s);
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
