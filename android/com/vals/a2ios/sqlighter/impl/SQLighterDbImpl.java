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
import java.util.LinkedList;
import java.util.List;

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
    private boolean isDbCopied = false;
    private List<Object> parameterList = new LinkedList<Object>();

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
       }
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
        this.dbPath = path;
    }

    @Override
    public void openIfClosed() throws Exception {
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
    public void copyDbOnce() throws Exception {
        if (!isDbCopied) {
            isDbCopied = true;
            String deviceDbFileName = dbPath + dbName;
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

    @Override
    public void setContext(Object context) {
        this.context = (Context)context;
    }

    @Override
    public void addParam(double d) {
        parameterList.add(new Double(d).toString());
    }

    @Override
    public void addParam(long l) {
        parameterList.add(new Long(l).toString());
    }

    @Override
    public void addParam(String s) {
        parameterList.add(s);
    }

    @Override
    public void addParam(byte[] blob) {
        parameterList.add(blob);
    }

    @Override
    public void addParamNull() {
        parameterList.add(null);
    }

    @Override
    public void addParamObj(Object o) {
        parameterList.add(o);
    }

    @Override
    public SQLighterRs executeSelect(String selectQuery) {
        String[] sp = parameterList.toArray(new String[parameterList.size()]);
        parameterList.clear();
        Cursor cursor = db.rawQuery(selectQuery, sp);
        return new ResultSetImpl(cursor);
    }

    @Override
    public void executeChange(String update) {
        SQLiteStatement stmt = db.compileStatement(update);
        bindParams(stmt);
        stmt.executeUpdateDelete();
        stmt.close();
    }

    private void bindParams(SQLiteStatement stmt) {
        int i = 1;
        for (Object o: parameterList) {
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
            }
            i++;
        }
        parameterList.clear();
    }

    @Override
    public void beginTransaction() {
        this.executeChange("begin transaction");
    }

    @Override
    public void commitTransaction() {
        this.executeChange("commit");
    }

    @Override
    public void rollbackTransaction() {
        this.executeChange("rollback");
    }
}
