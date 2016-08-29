package com.vals.a2ios.sqlighter.intf;

import java.util.Date;

/**
 * General interface for managing SQLite database
 *
 * NOTE: SQLite does not have dedicated Date type.
 * Sqlighter provides Date implementation based on
 * TEXT data column type by saving/retrieving
 * date formatted strings. This implementation is optional
 * and you may use your own date type implementation by
 * storing time as number, your own string format or whatever.
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
     * Android sepcific, iOS impementation is not
     * doing anything.
     *
     * @param path
     */
    public void setDbPath(String path);

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
     *
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
     *
     * @deprecated - see deployDBOnce. No functionality change,
     * just naming consistency.
     */
    public void copyDbOnce() throws Exception;

    /**
     * Check this doc for detailed info:
     *
     * https://github.com/vals-productions/sqlighter#sqlighterdbcopydbonce
     *
     * @throws Exception in case of input\output exceptions
     */
    public void deployDbOnce() throws Exception;

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
     * @param statementString
     * @return last inserted row id in case of "insert" statement, affected row count in
     * case of update/delete statements
     *
     * Check SQLite docs on row id information.
     */
    public Long executeChange(String statementString) throws Exception;

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
     * Lets you delete your database file from the device in case you need this.
     */
    public boolean deleteDBFile();

    /**
     * @param isDateNamedColumn - if true, then Any string type column having "date"
     * in its name will be attempted to be parsed as a Date when processing a result set.
     * Otherwise, will be treated as a string.
     */
    public void setIsDateNamedColumn(boolean isDateNamedColumn);

    /**
     * Date is stored in database without milliseconds.
     *
     * This method might be useful to be able to eliminate
     * milliseconds from java.util.Date for comparison
     * purposes.
     *
     * @param date
     * @return
     */
    public Date getDateWithoutMillis(Date date);

    /**
     * Returns created / closed statements balance. You
     * can check the balance once in a while to make sure
     * there's no resource leaks.
     *
     * @return
     */
    public long getStatementBalance();

    /**
     * OPTIONAL Date implementation
     *
     * Override default date column hint.
     *
     * This is mostly needed when invoking
     * SQLighterRs.getObject(...) on the column.
     *
     * Column names containing the hint (case insens.)
     * will be considered date columns, and the method
     * above would return Date instead of the String.
     */
    public void setDateColumnNameHint(String hint);

    /**
     * OPTIONAL Date implementation
     *
     * Be sure to check the timeZone name/id is
     * supported on both - Android and iOS.
     *
     * Timezone specification through this setter in
     * conjunction with TimeZone inclusion in Date Format
     * String for database storage lets you manipulate
     * data objects created in different timezones. Sqlighter
     * will convert date's string representation to reflect
     * time zone specified in this method when persisting the date
     * as string into the database. Assuming dateFormatString also
     * containd timezone information, will let sqlighter evaluate
     * date's string representation and initialize Date object
     * correctly.
     *
     * Typically it is suggested to persist all dates in
     * UTC.
     *
     * @param timeZone i.e. UTC
     */
    public void setTimeZone(String timeZone);

    /**
     *
     * OPTIONAL Date implementation
     *
     * Make sure the date format will be equivalently
     * supported at iOS side as well.
     *
     * @param dateFormatString
     */
    void setDateFormatString(String dateFormatString);

}
