package com.vals.a2ios.sqlighter.impl;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.vals.a2ios.sqlighter.intf.SQLighterDb;
import com.vals.a2ios.sqlighter.intf.SQLighterRs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Android implementation of SQLighter interfaces.
 *
 * See interface's java doc for more details
 */
public class SQLighterDbImpl implements SQLighterDb {
    private String dbName, dbPath;
    private boolean isOverwrite = false;
    private Context context;
    private SQLiteDatabase db;
    private boolean isOpen = false;
    private boolean isDeployed = false;
    private long stmtOpenCnt = 0;
    private long stmtCloseCnt = 0;
    private Map<Long, List<Object>> parameterMap = new HashMap<>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat(SQLighterDb.DATE_FORMAT);
    private boolean isDateNamedColumn = true;

    public class ResultSetImpl implements SQLighterRs {
        private Cursor cursor;

        public ResultSetImpl(Cursor cursor) {
            this.cursor = cursor;
        }

        @Override
        public boolean isNull(int index) {
            return cursor.isNull(index);
        }

        @Override
        public boolean hasNext() {
            return cursor.moveToNext();
        }

        @Override
        public Date getDate(int index) {
            if(cursor.isNull(index)) {
                return null;
            }
            StringBuffer sb = new StringBuffer(cursor.getString(index));
            try {
                Date date = dateFormat.parse(sb.toString());
                return date;
            } catch (ParseException e) {
                return null;
            }
        }

        @Override
        public Double getDouble(int index) {
            if(cursor.isNull(index)) {
                return null;
            }
            return cursor.getDouble(index);
        }

        @Override
        public Long getLong(int index) {
            if(cursor.isNull(index)) {
                return null;
            }
            return cursor.getLong(index);
        }

        @Override
        public Integer getInt(int index) {
            if(cursor.isNull(index)) {
                return null;
            }
            return cursor.getInt(index);
        }

        @Override
        public Number getNumber(int index) {
            if(cursor.isNull(index)) {
                return null;
            }
            return cursor.getDouble(index);
        }

        @Override
        public String getString(int index) {
            if(cursor.isNull(index)) {
                return null;
            }
            return cursor.getString(index);
        }

        @Override
        public byte[] getBlob(int index) {
            if(cursor.isNull(index)) {
                return null;
            }
            return cursor.getBlob(index);
        }

        @Override
        public void close() {
            cursor.close();
            stmtCloseCnt++;
        }

        @Override
        public int getColumnType(int index) {
            return cursor.getType(index);
        }

        @Override
        public String getColumnName(int index) {
            return cursor.getColumnName(index);
        }

        @Override
        public Object getObject(int index) {
            int columnType = getColumnType(index);
            if (columnType == Cursor.FIELD_TYPE_NULL) {
                return null;
            } else if (columnType == Cursor.FIELD_TYPE_INTEGER) {
                return getInt(index);
            } else if (columnType == Cursor.FIELD_TYPE_FLOAT) {
                return getDouble(index);
            } else if (columnType == Cursor.FIELD_TYPE_BLOB) {
                return getBlob(index);
            } else if (columnType == Cursor.FIELD_TYPE_STRING) {
                if (isDateNamedColumn && getColumnName(index) != null &&
                        getColumnName(index).toLowerCase().indexOf(SQLighterDb.DATE_HINT) != -1) {
                    return getDate(index);
                }
                return getString(index);
            }
            return null;
        }
    }

    @Override
    public void setIsDateNamedColumn(boolean isDateNamedColumn) {
        this.isDateNamedColumn = isDateNamedColumn;
    }

    @Override
    public boolean isDbFileDeployed() {
        File devicePath = new File(dbPath + dbName);
        return devicePath.exists();
    }

    @Override
    public void setOverwriteDb(boolean isOverwrite) {
        this.isOverwrite = isOverwrite;
    }

    @Override
    public void setDbName(String name) {
        this.dbName = name;
    }

    /**
     * Android specific method
     * @param path - location of db file
     */
    public void setDbPath(String path) {
        if (!path.endsWith("/")) {
            path += "/";
        }
        this.dbPath = path;
    }

    @Override
    public synchronized void openIfClosed() throws Exception {
        if(!isOpen) {
            if (context == null) {
                throw new Exception("Context object is null");
            }
            db = context.openOrCreateDatabase(dbName, Context.MODE_PRIVATE, null);
            isOpen = true;
        }
    }

    private void ensureInitialState(String deviceDbFileName) throws Exception {
        if (context == null) {
            throw new Exception("Context object is null");
        }
        File devicePath = new File(dbPath);
        if (!devicePath.exists()) {
            /**
             app is being started the very first time, no db path exists,
             the following commands will create an empty db file and path
             to the file
             */
            db = context.openOrCreateDatabase(deviceDbFileName, Context.MODE_PRIVATE, null);
            db.close();
            // the path has been created, but we will have to overwrite the db file with
            // our file from the assets (to be consistent with iOS), so let's delete it
            // first
            isOverwrite = true;
        }
    }

    @Override
    public boolean deleteDBFile() {
        File f = new File(getDeviceDbFileName());
        boolean result = f.delete();
        isDeployed = false;
        return result;
    }

    private String getDeviceDbFileName() {
        return dbPath + dbName;
    }

    @Override
    public synchronized void copyDbOnce() throws Exception {
        deployDbOnce();
    }

    @Override
    public synchronized void deployDbOnce() throws Exception {
        if(!isDeployed) {
            isDeployed = true;
            String deviceDbFileName = getDeviceDbFileName();
            ensureInitialState(deviceDbFileName);
            File f = new File(deviceDbFileName);
            boolean isDeviceDbFileExists = f.exists();
            if(!isDeviceDbFileExists || isOverwrite) {
                if (isDeviceDbFileExists) {
                    // we are here to overwrite the target file,
                    // so, let's delete it
                    f.delete();
                }
                // Open assets (initial) db as the input stream
                InputStream assetsDbFileIs = context.getAssets().open(dbName);
                // open empty target file as the output stream
                OutputStream myOutput = new FileOutputStream(deviceDbFileName);
                // copy
                byte[] buffer = new byte[1024];
                int length;
                while ((length = assetsDbFileIs.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }
                // close streams
                myOutput.flush();
                myOutput.close();
                assetsDbFileIs.close();
            }
        }
    }

    private List<Object> getParameterList() {
        Long threadId = Thread.currentThread().getId();
        List<Object> parameterList = parameterMap.get(threadId);
        if(parameterList == null) {
            parameterList = new LinkedList<Object>();
            parameterMap.put(threadId, parameterList);
        }
        return parameterList;
    }

    private void clearParameterList () {
        Long threadId = Thread.currentThread().getId();
        if(parameterMap.containsKey(threadId)) {
            parameterMap.remove(threadId);
        }
    }

    @Override
    public void setContext(Object context) {
        this.context = (Context)context;
    }

    @Override
    public void addParam(double d) {
        getParameterList().add(new Double(d).toString());
    }

    @Override
    public void addParam(long l) {
        getParameterList().add(new Long(l).toString());
    }

    @Override
    public void addParam(String s) {
        getParameterList().add(s);
    }

    @Override
    public void addParam(byte[] blob) {
        getParameterList().add(blob);
    }

    @Override
    public void addParamNull() {
        getParameterList().add(null);
    }

    @Override
    public void addParamObj(Object o) {
        getParameterList().add(o);
    }

    private String dateToString(Date date) {
        if (date != null) {
            return dateFormat.format(date);
        }
        return null;
    }

    private String[] collectionToArray(Collection<Object> collection) {
        int i = 0;
        String[] array = new String[collection.size()];
        for (Object object: collection) {
            if (object == null) {
                array[i] = null;
            } else if (object instanceof String) {
                array[i] = (String)object;
            } else {
                array[i] = object.toString();
            }
            i++;
        }
        return array;
    }

    @Override
    public void addParam(Date date) {
        getParameterList().add(date);
    }

    @Override
    public synchronized SQLighterRs executeSelect(String selectQuery) throws Exception {
        try {
            String[] sp = collectionToArray(getParameterList());
            getParameterList().clear();
            Cursor cursor = db.rawQuery(selectQuery, sp);
            stmtOpenCnt++;
            SQLighterRs rs = new ResultSetImpl(cursor);
            return rs;
        } catch (Throwable t) {
            getParameterList().clear();
            throw new Exception(t.getMessage(), t);
        }
    }

    @Override
    public synchronized Long executeChange(String update) throws Exception  {
        Long changeId = null;
        try {
            SQLiteStatement stmt = db.compileStatement(update);
            stmtOpenCnt++;
            bindParams(stmt);
            if (update.trim().toLowerCase().startsWith("insert")) {
                changeId = stmt.executeInsert();
            } else {
                Integer rowCnt;
                rowCnt = stmt.executeUpdateDelete();
                if(rowCnt != null) {
                    changeId = rowCnt.longValue();
                }
            }
            stmt.close();
            stmtCloseCnt++;
        } catch (Throwable t) {
            getParameterList().clear();
            throw new Exception(t.getMessage(), t);
        }
        return changeId;
    }

    private synchronized void bindParams(SQLiteStatement stmt) {
        int i = 1;
        for (Object o: getParameterList()) {
            if(o == null) {
                stmt.bindNull(i);
            } else if (o instanceof Double) {
                stmt.bindDouble(i, (Double)o);
            } else if (o instanceof Float) {
                stmt.bindDouble(i, ((Float) o).doubleValue());
            } else if (o instanceof String) {
                stmt.bindString(i, (String)o);
            } else if (o instanceof Integer) {
                stmt.bindLong(i, ((Integer)o).longValue());
            } else if (o instanceof Long) {
                stmt.bindLong(i, (Long)o);
            } else if (o instanceof Short) {
                stmt.bindLong(i, ((Short)o).longValue());
            } else if  (o instanceof byte[]) {
                stmt.bindBlob(i, (byte[])o);
            } else if (o instanceof Date) {
                Date d = (Date)o;
                StringBuilder sb = new StringBuilder(dateFormat.format(d));
                stmt.bindString(i, sb.toString());
            }
            i++;
        }
        // parameterList.clear();
        clearParameterList();
    }

    @Override
    public void beginTransaction() throws Exception {
        this.executeChange("begin transaction");
    }

    @Override
    public void commitTransaction() throws Exception {
        this.executeChange("commit");
    }

    @Override
    public void rollbackTransaction() throws Exception {
        this.executeChange("rollback");
    }

    @Override
    public synchronized void close() {
        if(isOpen) {
            db.close();
            isOpen = false;
        }
    }

    public Date getDateWithoutMillis(Date date) {
        if(date != null) {
            date.setTime(date.getTime() / 1000);
            date.setTime(date.getTime() * 1000);
            return date;
        }
        return null;
    }

    public long getStatementBalance() {
        return stmtOpenCnt - stmtCloseCnt;
    }
}
