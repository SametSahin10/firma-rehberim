package com.firmarehberim.canliradyo.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.firmarehberim.canliradyo.data.RadioContract.RadioEntry;

public class RadioDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "radios.db";
    // DATABASE_VERSION is set to 2 to trigger onUpgrade()
    private static final int DATABASE_VERSION = 2;
    public static final String LOG_TAG = RadioDbHelper.class.getSimpleName();

    public RadioDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(LOG_TAG, "DATABASE_VERSION:" + DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase sqliteDatabase) {
        Log.d(LOG_TAG, "onCreate() called");
        Log.d(LOG_TAG, "creating database");
        sqliteDatabase.execSQL("CREATE TABLE " + RadioEntry.TABLE_NAME + "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                RadioEntry.COLUMN_ID + " INTEGER NOT NULL," +
                                RadioEntry.COLUMN_CITY_ID + " INTEGER NOT NULL," +
                                RadioEntry.COLUMN_TOWN_ID + " INTEGER NOT NULL," +
                                RadioEntry.COLUMN_NEIGHBOURHOOD_ID + " INTEGER NOT NULL," +
                                RadioEntry.COLUMN_CATEGORY_ID + " TEXT NOT NULL," +
                                RadioEntry.COLUMN_USER_ID + " INTEGER NOT NULL," +
                                RadioEntry.COLUMN_NAME + " TEXT NOT NULL," +
                                RadioEntry.COLUMN_CATEGORY + " TEXT NOT NULL," +
                                RadioEntry.COLUMN_ICON_URL + " TEXT NOT NULL," +
                                RadioEntry.COLUMN_STREAM_LINK + " TEXT NOT NULL," +
                                RadioEntry.COLUMN_IS_IN_HLS_FORMAT + " INTEGER NOT NULL DEFAULT 0," +
                                RadioEntry.COLUMN_SHAREABLE_LINK + " TEXT NOT NULL," +
                                RadioEntry.COLUMN_HIT + " INTEGER NOT NULL," +
                                RadioEntry.COLUMN_NUM_OF_ONLINE_LISTENERS + " INTEGER NOT NULL DEFAULT 0," +
                                RadioEntry.COLUMN_IS_BEING_BUFFERED + " INTEGER NOT NULL DEFAULT 0," +
                                RadioEntry.COLUMN_IS_LIKED + " INTEGER NOT NULL DEFAULT 0);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(LOG_TAG, "onUpgrade() called");
        Log.d(LOG_TAG, "oldVersion: " + oldVersion);
        Log.d(LOG_TAG, "newVersion: " + newVersion);

        db.execSQL("ALTER TABLE "
                + RadioEntry.TABLE_NAME
                + " ADD COLUMN " + RadioEntry.COLUMN_IS_IN_HLS_FORMAT + " INTEGER DEFAULT 0");
    }
}
