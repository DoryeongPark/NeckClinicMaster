package com.mokdoryeong.team7.mokdoryeong;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DbOpenHelper {

    private static final String DATABASE_NAME = "CervicalDataList";
    private static final int DATABASE_VERSION = 1;
    public static SQLiteDatabase mDB;
    private DatabaseHelper mDbHelper;
    private Context context;

    public DbOpenHelper(Context context) {
        this.context = context;
        Log.d("Database", "DbOpenHelper is successfully created");
    }

    public DbOpenHelper open() throws SQLException {
        mDbHelper = new DatabaseHelper(context);
        mDB = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDB.close();
    }

    public Cursor getAllColumns() {
        return mDB.query(Databases.CreateDB._TABLENAME, null, null, null, null, null, null);
    }

    private int getIndex(String startTime) {
        Cursor c = mDB.rawQuery("SELECT " + Databases.CreateDB.ID + " FROM " + Databases.CreateDB._TABLENAME
                + " WHERE " + Databases.CreateDB.STARTTIME + "='" + startTime + "';", null);
        if (c.moveToNext()) {
            return c.getInt(c.getColumnIndex(Databases.CreateDB.ID));
        }
        return -1;
    }

    public boolean insertRecord(String startTime, String finishTime, String averageAngle, String cervicalRiskIndex) {
        ContentValues values = new ContentValues();
        values.put(Databases.CreateDB.STARTTIME, startTime);
        values.put(Databases.CreateDB.FINISHTIME, finishTime);
        values.put(Databases.CreateDB.AVERAGEANGLE, averageAngle);
        values.put(Databases.CreateDB.CERVICALRISKINDEX, cervicalRiskIndex);
        Log.d("Database", "Insert " + startTime + ", " + finishTime + ", " +
                averageAngle +", " + cervicalRiskIndex);
        return mDB.insert(Databases.CreateDB._TABLENAME, null, values) > 0;
    }

    public boolean deleteRecord(String startTime) {
        int id = getIndex(startTime);
        Log.d("Database", "Delete " + id);
        return mDB.delete(Databases.CreateDB._TABLENAME, "id=" + id, null) > 0;
    }

    public boolean updateRecord(String startTime, String finishTime, String averageAngle, String cervicalRiskIndex) {
        int id = getIndex(startTime);
        Log.d("DataBase", "Update record" + startTime + ", " + finishTime + ", " + averageAngle + ", " + cervicalRiskIndex);
        ContentValues values = new ContentValues();
        values.put(Databases.CreateDB.AVERAGEANGLE, averageAngle);
        values.put(Databases.CreateDB.CERVICALRISKINDEX, cervicalRiskIndex);
        return mDB.update(Databases.CreateDB._TABLENAME, values, "id=" + id, null) > 0;
    }

    private class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(Databases.CreateDB._CREATE);
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + Databases.CreateDB._TABLENAME);
            onCreate(db);
        }
    }

}

