package com.hengtan.nanodegreeapp.stocount.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by saj on 22/12/14.
 */
public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION_0 = 0;
    private static final int DATABASE_VERSION_1 = 1;
    public static final int CURRENT_DATABASE_VERSION = DATABASE_VERSION_1;
    public static final String DATABASE_NAME = "stocount.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, CURRENT_DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_USER_TABLE = "CREATE TABLE " + StoCountContract.UserEntry.TABLE_NAME + " ("+
                StoCountContract.UserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                StoCountContract.UserEntry.DISPLAY_NAME + " TEXT NOT NULL," +
                StoCountContract.UserEntry.EMAIL + " TEXT ," +
                StoCountContract.UserEntry.PHOTO_URL + " TEXT ," +
                StoCountContract.UserEntry.GOOGLE_ID + " TEXT NOT NULL, " +
                "UNIQUE ("+ StoCountContract.UserEntry.GOOGLE_ID +") ON CONFLICT IGNORE)";

        final String SQL_CREATE_PRODUCT_TABLE = "CREATE TABLE " + StoCountContract.ProductEntry.TABLE_NAME + " ("+
                StoCountContract.ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                StoCountContract.ProductEntry.PRODUCT_NAME + " TEXT," +
                StoCountContract.ProductEntry.DESCRIPTION + " TEXT," +
                StoCountContract.ProductEntry.THUMBNAIL_IMAGE + " TEXT," +
                StoCountContract.ProductEntry.LARGE_IMAGE + " TEXT," +
                StoCountContract.ProductEntry.ADDITIONAL_INFO + " TEXT," +
                StoCountContract.ProductEntry.BARCODE + " TEXT," +
                StoCountContract.ProductEntry.BARCODE_FORMAT + " TEXT," +
                StoCountContract.ProductEntry.DELETED + " BOOLEAN," +
                " FOREIGN KEY (" + StoCountContract.ProductEntry._ID + ") REFERENCES " +
                StoCountContract.ProductCountEntry.TABLE_NAME + " (" + StoCountContract.ProductCountEntry._ID + "))";

        final String SQL_CREATE_STOCK_PERIOD_TABLE = "CREATE TABLE " + StoCountContract.StockPeriodEntry.TABLE_NAME + " ("+
                StoCountContract.StockPeriodEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                StoCountContract.StockPeriodEntry.START_DATE + " DATE," +
                StoCountContract.StockPeriodEntry.END_DATE + " DATE," +
                " FOREIGN KEY (" + StoCountContract.StockPeriodEntry._ID + ") REFERENCES " +
                StoCountContract.ProductCountEntry.TABLE_NAME + " (" + StoCountContract.ProductCountEntry._ID + "))";

        final String SQL_CREATE_PRODUCT_COUNT_TABLE = "CREATE TABLE " + StoCountContract.ProductCountEntry.TABLE_NAME + " ("+
                StoCountContract.ProductCountEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                StoCountContract.ProductCountEntry.STOCK_PERIOD_ID + " INTEGER," +
                StoCountContract.ProductCountEntry.PRODUCT_ID + " INTEGER," +
                StoCountContract.ProductCountEntry.QUANTITY + " DOUBLE," +
                StoCountContract.ProductCountEntry.COUNT_DATE + " DATETIME, " +
                "UNIQUE ("+ StoCountContract.ProductCountEntry.STOCK_PERIOD_ID + ", "+ StoCountContract.ProductCountEntry.PRODUCT_ID  +") ON CONFLICT IGNORE)";

        Log.d("sql-statments", SQL_CREATE_USER_TABLE);
        Log.d("sql-statments", SQL_CREATE_PRODUCT_TABLE);
        Log.d("sql-statments", SQL_CREATE_STOCK_PERIOD_TABLE);
        Log.d("sql-statments", SQL_CREATE_PRODUCT_COUNT_TABLE);

        db.execSQL(SQL_CREATE_USER_TABLE);
        db.execSQL(SQL_CREATE_PRODUCT_TABLE);
        db.execSQL(SQL_CREATE_STOCK_PERIOD_TABLE);
        db.execSQL(SQL_CREATE_PRODUCT_COUNT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch(oldVersion) {
            case DATABASE_VERSION_0:
                //db.execSQL(DATABASE_CREATE_color);
                // we want both updates, so no break statement here...
            case DATABASE_VERSION_1:
                //db.execSQL(DATABASE_CREATE_someothertable);
        }
    }
}
