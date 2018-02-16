package com.lfwl.smemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by mgh on 2017/11/25.
 */

public class DBAdapter {
    private static final String DB_NAME = "memo.db";
    private static final String DB_TABLE = "memo";
    private static final int DB_VERSION = 1;

    private static final String KEY_ID = "id";
    private static final String KEY_TEXT = "text";
    private static final String KEY_DATE = "date";

    private SQLiteDatabase db;
    private final Context context;
    private DBOpenHelper dbOpenHelper;

    private static class DBOpenHelper extends SQLiteOpenHelper {
        public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        private static final String DB_CREATE = "create table " + DB_TABLE + "(" + KEY_ID + " integer primary key autoincrement, "
                + KEY_TEXT + " text not null, " + KEY_DATE + " text not null );";

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
            onCreate(db);
        }
    };

    public DBAdapter(Context context) {
        this.context = context;
    }

    public void open() throws SQLiteException {
        dbOpenHelper = new DBOpenHelper(context, DB_NAME, null, DB_VERSION);
        try {
            db = dbOpenHelper.getWritableDatabase();
        } catch (SQLiteException e) {
            db = dbOpenHelper.getReadableDatabase();
        }
    }

    public void close() {
        if(db != null) {
            db.close();
            db = null;
        }
    }

    public long insert(Memo memo) {
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_TEXT, memo.text);
        newValues.put(KEY_DATE, memo.date);
        return db.insert(DB_TABLE, null, newValues);
    }

    public long deleteAllData() {
        return db.delete(DB_TABLE, null, null);
    }

    public long deleteOneData(long id) {
        return db.delete(DB_TABLE, KEY_ID + "=" + id, null);
    }

    public long updateOneData(long id, Memo memo) {
        ContentValues updateValues = new ContentValues();
        updateValues.put(KEY_TEXT, memo.text);
        updateValues.put(KEY_DATE, memo.date);
        return db.update(DB_TABLE, updateValues, KEY_ID + "=" + id, null);
    }

    private Memo[] ConvertToPeople(Cursor cursor) {
        int resultCounts = cursor.getCount();
        if(resultCounts == 0 || !cursor.moveToFirst()) {
            return null;
        }
        Memo[] memos = new Memo[resultCounts];
        for(int i = 0; i <resultCounts; i++) {
            memos[i] = new Memo();
            memos[i].id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
            memos[i].text = cursor.getString(cursor.getColumnIndex(KEY_TEXT));
            memos[i].date = cursor.getString(cursor.getColumnIndex(KEY_DATE));
            cursor.moveToNext();
        }
        return memos;
    }

    public Memo[] getAllDate() {
        Cursor results = db.query(DB_TABLE, new String[] { KEY_ID, KEY_TEXT, KEY_DATE }, null, null, null, null, null);
        return ConvertToPeople(results);
    }

    public Memo[] getOneDateByID(long id) {
        Cursor results = db.query(DB_TABLE, new String[] { KEY_ID, KEY_TEXT, KEY_DATE }, KEY_ID + "=" + id, null, null, null, null);
        return ConvertToPeople(results);
    }

}
