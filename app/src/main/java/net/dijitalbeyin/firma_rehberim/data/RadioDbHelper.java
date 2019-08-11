package net.dijitalbeyin.firma_rehberim.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static net.dijitalbeyin.firma_rehberim.data.RadioContract.*;

public class RadioDbHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = RadioDbHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "radios.db";
    private static final int DATABASE_VERSION = 1;

    public RadioDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_FAVOURITE_RADIOS_TABLE = "CREATE TABLE " + RadioEntry.TABLE_NAME + " ("
                + RadioEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + RadioEntry.COLUMN_RADIO_ID + " INTEGER NOT NULL, "
                + RadioEntry.COLUMN_CITY_ID + " INTEGER NOT NULL, "
                + RadioEntry.COLUMN_TOWN_ID + " INTEGER NOT NULL, "
                + RadioEntry.COLUMN_NEIGHBOURHOOD_ID + " INTEGER NOT NULL, "
                + RadioEntry.COLUMN_CATEGORY_ID + " TEXT NOT NULL, "
                + RadioEntry.COLUMN_USER_ID + " INTEGER NOT NULL, "
                + RadioEntry.COLUMN_RADIO_NAME + " TEXT NOT NULL, "
                + RadioEntry.COLUMN_RADIO_CATEGORY + " TEXT NOT NULL, "
                + RadioEntry.COLUMN_RADIO_ICON_URL + " TEXT NOT NULL, "
                + RadioEntry.COLUMN_RADIO_STREAM_LINK + " TEXT NOT NULL, "
                + RadioEntry.COLUMN_RADIO_SHAREABLE_LINK + " TEXT NOT NULL, "
                + RadioEntry.COLUMN_RADIO_HIT + " INTEGER NOT NULL, "
                + RadioEntry.COLUMN_NUM_OF_ONLINE_LISTENERS + " INTEGER NOT NULL, "
                + RadioEntry.COLUMN_RADIO_IS_BEING_BUFFERED + " INTEGER NOT NULL DEFAULT 0, "
                + RadioEntry.COLUMN_RADIO_IS_LIKED + " INTEGER NOT NULL DEFAULT 0);";
        db.execSQL(SQL_CREATE_FAVOURITE_RADIOS_TABLE);

        //  private int radioId;
        //    private int cityId;
        //    private int townId; //ilceId
        //    private int neighbourhoodId; //mahalleId
        //    private String categoryId;
        //    private int userId;
        //    private String radioName;
        //    private String category;
        //    private String radioIconUrl;
        //    private String streamLink;
        //    private String shareableLink;
        //    private int hit;
        //    private int numOfOnlineListeners;
        //    private boolean isBeingBuffered;
        //    private boolean isLiked;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
