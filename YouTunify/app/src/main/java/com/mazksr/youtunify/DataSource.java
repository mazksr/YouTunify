package com.mazksr.youtunify;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;

public class DataSource {
    public static ArrayList<Songs> songs = new ArrayList<>();
    public static ArrayList<Songs> searchResult = new ArrayList<>();

    private static DBConfig dbHelper;

    public static void getData(Context context) {
        songs.clear();
        dbHelper = new DBConfig(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("songs", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String title = (cursor.getString(1));
                String artist = (cursor.getString(2));
                String songId = (cursor.getString(3));
                byte[] cover = (cursor.getBlob(4));
                songs.add(new Songs(id, title, artist, cover, songId));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
    }

    public static boolean addSong(Context context, Songs song) {
        dbHelper = new DBConfig(context);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", song.getTitle());
        values.put("artist", song.getArtist());
        values.put("song_id", song.getSongId());
        values.put("cover", song.getCover());

        long newRowId = db.insert("songs", null, values);

        db.close();

        if (newRowId != -1) {
            getData(context);
            return true;
        } else {
            return false;
        }
    }

    public static boolean removeSong(Context context, int id) {
        dbHelper = new DBConfig(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long newRowId = db.delete("songs", "id = ?", new String[]{String.valueOf(id)});
        db.close();
        if (newRowId != -1) {
            getData(context);
            return true;
        } else {
            return false;
        }
    }

    public static void searchData(Context context, String search) {
        searchResult.clear();
        dbHelper = new DBConfig(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM songs WHERE title LIKE '%" + search + "%'", null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String title = (cursor.getString(1));
                String artist = (cursor.getString(2));
                String songId = (cursor.getString(3));
                byte[] cover = (cursor.getBlob(4));
                searchResult.add(new Songs(id, title, artist, cover, songId));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
    }


}
