package com.vals.a2ios.sqlighter.intf;

/**
 * General interface for managing SQLite database
 *
 */
public interface SQLighterDb {

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
     * Your initial sqlite database file is stored somewhere within your project structure.
     * This file could contain no tables/information whatsoever, or, contain some
     * initial database structure/data of your project. This is all in developer's hands.
     * Let's call this file initial database file.
     *
     * The location of the initial database file in the project and on the device (emulator or
     * real device) should be different. Among other things will prevent database from being
     * overwritten during sequential application upgrades by the content of the initial
     * database file.
     *
     * So, there's a task of copying the file from the project into designated device's
     * location. This task should be done once since otherwise you'll keep overwriting
     * user data in the real application.
     *
     * Whenever the user starts your application the very first time, the database file
     * should be copied from its project location into designated device's location.
     *
     * This, also, should be done before you start using the database. Basically #copyDbOnce
     * takes the complexity of various checks out of your hands.
     *
     * #copyDbOnce works only once for SQLighterDb instance. This is just to prevent
     * erroneous database overrides during application runs.
     *
     * Once invoked, first, #copyDbOnce checks the destination location of the database file.
     * If it's already there, it will skip copying the database file.
     *
     * If the file is not there, the #copyDbOnce will copy the file.
     *
     * There's also #setOverwriteDb method, that lets you override default behavior of #copyDbOnce.
     * If called with true, it will override the destination database file even if the file is
     * there. Normally, this is necessary for your development process where you would like to
     * roll database back and start fresh until you develop and test some particular. Normally,
     * you do not want to call #setOverwriteDb with "true" in your production environment unless
     * you want the user to start fresh every time.
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
     * Execute UPDATE/INSERT/DELETE/ALTER/CREATE with previously (optionally) specified parameters
     * @param insert
     */
    public void executeChange(String insert);
}
