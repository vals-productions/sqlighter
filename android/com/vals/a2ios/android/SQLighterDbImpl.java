package com.vals.a2ios.android;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.vals.a2ios.sqlighter.SQLighterDb;
import com.vals.a2ios.sqlighter.SQLighterRs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * Android implementation of SQLighter interfaces
 */
public class SQLighterDbImpl implements SQLighterDb {

    public class ResultSetImpl implements SQLighterRs {
        private Cursor cursor;

        public ResultSetImpl(Cursor cursor) {
            this.cursor = cursor;
        }

        @Override
        public boolean hasNext() {
            return cursor.moveToNext();
        }

        @Override
        public Number getDouble(int index) {
            if(cursor.isNull(index)) {
                return null;
            }
            return cursor.getDouble(index);
        }

        @Override
        public Number getLong(int index) {
            if(cursor.isNull(index)) {
                return null;
            }
            return cursor.getLong(index);
        }

        @Override
        public Number getInt(int index) {
            if(cursor.isNull(index)) {
                return null;
            }
            return cursor.getInt(index);
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

        public void close() {
            cursor.close();
       }
    }

    private String dbName, dbPath;
    private boolean isOverwrite = false;
    private Context context;
    private SQLiteDatabase db;
    private boolean isOpen = false;
    private boolean isDbCopied = false;

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void setOverwriteDb(boolean isOverwrite) {
        this.isOverwrite = isOverwrite;
    }

    @Override
    public void setDbName(String name) {
        this.dbName = name;
    }

    @Override
    public void setDbPath(String path) {
        this.dbPath = path;
    }

    @Override
    public void openIfClosed() {
        if(!isOpen) {
            db = context.openOrCreateDatabase(dbName, Context.MODE_PRIVATE, null);
            isOpen = true;
        }
    }

    @Override
    public void copyDbOnce()  throws Exception {
        if (!isDbCopied) {
            isDbCopied = true;
        } else {
            return;
        }
        //Open your local db as the input stream
        InputStream myInput = context.getAssets().open(dbName);

        // Path to the just created empty db
        String outFileName = dbPath + dbName;

        File f = new File(outFileName);

        boolean isFileExists = f.exists();

        if(!isFileExists || isOverwrite) {
            if (isFileExists) {
                f.delete();
            }
            //Open the empty db as the output stream
            OutputStream myOutput = new FileOutputStream(outFileName);

            //transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            //Close the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();
        }

    }

    @Override
    public void setContext(Object context) {
        this.context = (Context)context;
    }

    private List<Object> pl = new LinkedList<>();
    public void addParam(double d) {
        pl.add(new Double(d).toString());
    }
    public void addParam(long l) {
        pl.add(new Long(l).toString());
    }
//    public void addParam(int i) {
//        pl.add(new Integer(i).toString());
//    }
    public void addParam(String s) {
        pl.add(s);
    }
    public void addParam(byte[] blob) {
        pl.add(blob);
    }
    @Override
    public SQLighterRs executeSelect(String selectQuery) {
        String[] sp = pl.toArray(new String[pl.size()]);
        pl.clear();
        Cursor cursor = db.rawQuery(selectQuery, sp);
        return new ResultSetImpl(cursor);
    }
    public void executeChange(String update) {
        SQLiteStatement stmt = db.compileStatement(update);
        bindParams(stmt);
        stmt.executeUpdateDelete();
        stmt.close();
    }
    private void bindParams(SQLiteStatement stmt) {
        int i = 1;
        for (Object o: pl) {
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
        pl.clear();
    }
}
