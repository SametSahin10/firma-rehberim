package net.dijitalbeyin.firma_rehberim.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import net.dijitalbeyin.firma_rehberim.data.CompanyContract.CompanyEntry;

public class CompanyDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "companies.db";
    private static final int DATABASE_VERSION = 1;

    public CompanyDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_COMPANIES_TABLE = "CREATE TABLE " + CompanyEntry.TABLE_NAME + " ("
                + CompanyEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CompanyEntry.COLUMN_COMPANY_NAME + " TEXT, "
                + CompanyEntry.COLUMN_AUTHORITATIVE_NAME + " TEXT, "
                + CompanyEntry.COLUMN_CALL_STATUS + " INTEGER DEFAULT 1, "
                + CompanyEntry.COLUMN_DATE_INFO + " TEXT);";
        db.execSQL(SQL_CREATE_COMPANIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
