package com.vals.a2ios.sqlighter.intf;

import java.util.Date;

/**
 * General interface for managing SQL select statement's result set
 */
public interface SQLighterRs {
    /**
     *
     * @return - true if resultSet has more records
     */
    public boolean hasNext();

    /**
     * Returns corresponding column value
     * @param index - 0 based index
     * @return Number object or null
     */
    public Double getDouble(int index);
    /**
     * Returns corresponding column value
     * @param index - 0 based index
     * @return Number object or null
     */
    public Long getLong(int index);
    /**
     * Returns corresponding column value
     * @param index - 0 based index
     * @return Number object or null
     */
    public Number getNumber(int index);
    /**
     * Returns corresponding column value
     * @param index - 0 based index
     * @return String object or null
     */
    public String getString(int index);
    /**
     * Returns corresponding column value
     * @param index - 0 based index
     * @return byte array or null
     */
    public byte[] getBlob(int index);
    /**
     * Returns corresponding column value
     * @param index - 0 based index
     * @return Number object or null
     */
    public Integer getInt(int index);
    /**
     *
     * @param index
     * @return
     */
    public Date getDate(int index);

    /**
     * Identifies the actual type of result set's
     * column and returns it as an Object.
     *
     * If TEXT column name contains '_date', the result
     * will be attempted as java.util.Date according to
     * isDateNamedColumn
     *
     * @param index - o based column index
     * @return
     */
    public Object getObject(int index);

    /**
     * Checks if column value is SQL NULL
     * @param index
     * @return
     */
    public boolean isNull(int index);

    /**
     * Returned column types are compliant to
     * Cursor get column type.
     *
     * @param index
     * @return
     */
    public int getColumnType(int index);

    /**
     * @param index
     * @return column name
     */
    public String getColumnName(int index);

    /**
     * Close result set
     */
    public void close();
}
