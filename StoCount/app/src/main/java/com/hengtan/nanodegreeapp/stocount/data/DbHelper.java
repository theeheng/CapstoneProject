package com.hengtan.nanodegreeapp.stocount.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by saj on 22/12/14.
 */
public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "stocount.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_BOOK_TABLE = "CREATE TABLE " + StoCountContract.BookEntry.TABLE_NAME + " ("+
                StoCountContract.BookEntry._ID + " INTEGER PRIMARY KEY," +
                StoCountContract.BookEntry.TITLE + " TEXT NOT NULL," +
                StoCountContract.BookEntry.SUBTITLE + " TEXT ," +
                StoCountContract.BookEntry.DESC + " TEXT ," +
                StoCountContract.BookEntry.IMAGE_URL + " TEXT, " +
                "UNIQUE ("+ StoCountContract.BookEntry._ID +") ON CONFLICT IGNORE)";

        final String SQL_CREATE_AUTHOR_TABLE = "CREATE TABLE " + StoCountContract.AuthorEntry.TABLE_NAME + " ("+
                StoCountContract.AuthorEntry._ID + " INTEGER," +
                StoCountContract.AuthorEntry.AUTHOR + " TEXT," +
                " FOREIGN KEY (" + StoCountContract.AuthorEntry._ID + ") REFERENCES " +
                StoCountContract.BookEntry.TABLE_NAME + " (" + StoCountContract.BookEntry._ID + "))";

        final String SQL_CREATE_CATEGORY_TABLE = "CREATE TABLE " + StoCountContract.CategoryEntry.TABLE_NAME + " ("+
                StoCountContract.CategoryEntry._ID + " INTEGER," +
                StoCountContract.CategoryEntry.CATEGORY + " TEXT," +
                " FOREIGN KEY (" + StoCountContract.CategoryEntry._ID + ") REFERENCES " +
                StoCountContract.BookEntry.TABLE_NAME + " (" + StoCountContract.BookEntry._ID + "))";


        Log.d("sql-statments", SQL_CREATE_BOOK_TABLE);
        Log.d("sql-statments", SQL_CREATE_AUTHOR_TABLE);
        Log.d("sql-statments", SQL_CREATE_CATEGORY_TABLE);

        db.execSQL(SQL_CREATE_BOOK_TABLE);
        db.execSQL(SQL_CREATE_AUTHOR_TABLE);
        db.execSQL(SQL_CREATE_CATEGORY_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
