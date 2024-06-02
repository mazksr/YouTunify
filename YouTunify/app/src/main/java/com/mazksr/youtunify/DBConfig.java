package com.mazksr.youtunify;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBConfig extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Songs.db";
    private static final int DATABASE_VERSION = 1;
    public DBConfig(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE songs (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, artist TEXT, song_id TEXT, cover BLOB)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
