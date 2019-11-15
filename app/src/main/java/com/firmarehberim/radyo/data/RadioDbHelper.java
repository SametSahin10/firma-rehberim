package com.firmarehberim.radyo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RadioDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "radios.db";
    private static final int DATABASE_VERSION = 1;
    public static final String LOG_TAG = "RadioDbHelper";

    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
    }

    public RadioDbHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("CREATE TABLE favourite_radios (_id INTEGER PRIMARY KEY AUTOINCREMENT, id INTEGER NOT NULL, city_id INTEGER NOT NULL, town_id INTEGER NOT NULL, neighbourhood_id INTEGER NOT NULL, category_id TEXT NOT NULL, user_id INTEGER NOT NULL, name TEXT NOT NULL, category TEXT NOT NULL, icon_url TEXT NOT NULL, stream_link TEXT NOT NULL, shareable_link TEXT NOT NULL, hit INTEGER NOT NULL, num_of_online_listeners INTEGER NOT NULL, is_being_buffered INTEGER NOT NULL DEFAULT 0, is_Liked INTEGER NOT NULL DEFAULT 0);");
    }
}
