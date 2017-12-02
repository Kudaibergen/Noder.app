package com.ka.noder.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class NoderDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "noder.db";
    private static final int CURRENT_DATABASE_VERSION = 1;

    NoderDatabase(Context context) {
        super(context, DATABASE_NAME, null, CURRENT_DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Contract.Notes.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Contract.Notes.TABLE_NAME);
        onCreate(db);
    }
}