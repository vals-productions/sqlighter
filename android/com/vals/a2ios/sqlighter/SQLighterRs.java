package com.vals.a2ios.sqlighter;

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
     * @return
     */
    public Number getDouble(int index);
    /**
     * Returns corresponding column value
     * @param index - 0 based index
     * @return
     */
    public Number getLong(int index);
    /**
     * Returns corresponding column value
     * @param index - 0 based index
     * @return
     */
    public String getString(int index);
    /**
     * Returns corresponding column value
     * @param index - 0 based index
     * @return
     */
    public byte[] getBlob(int index);
    /**
     * Returns corresponding column value
     * @param index - 0 based index
     * @return
     */
    public Number getInt(int index);

    /**
     * Close result set
     */
    public void close();
}
