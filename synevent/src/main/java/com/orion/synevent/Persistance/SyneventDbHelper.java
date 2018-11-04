package com.orion.synevent.Persistance;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class SyneventDbHelper extends SQLiteOpenHelper{
    public static final int DATABASE_VERSION = 0;
    public static final String DATABASE_NAME = "Synevent.db";

    private static SyneventDbHelper sInstance;

    private SyneventDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Nos aseguramos de que solo haya una instancia para evitar errores.
     * Mas detalles:
     * http://www.androiddesignpatterns.com/2012/05/correctly-managing-your-sqlite-database.html
     */
    public static synchronized SyneventDbHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new SyneventDbHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseContract.Schedule.SQL_CREATE_SCHEDULE_TABLE);
        db.execSQL(DatabaseContract.Activity.SQL_CREATE_ACTIVITY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DatabaseContract.Activity.SQL_DELETE_ACTIVITY);
        db.execSQL(DatabaseContract.Schedule.SQL_DELETE_SCHEDULE);
        onCreate(db);
    }
}
