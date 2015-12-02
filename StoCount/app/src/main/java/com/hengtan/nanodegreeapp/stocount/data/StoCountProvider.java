package com.hengtan.nanodegreeapp.stocount.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by saj on 24/12/14.
 */
public class StoCountProvider extends ContentProvider {

    private static final int USER_ID = 100;
    private static final int USER = 101;

    private static final int PRODUCT_ID = 200;
    private static final int PRODUCT = 201;

    private static final int STOCK_PERIOD_ID = 300;
    private static final int STOCK_PERIOD = 301;

    private static final int PRODUCT_COUNT_ID = 400;
    private static final int PRODUCT_COUNT = 401;

    private static final int PRODUCT_FULL = 500;
    //private static final int PRODUCT_FULLDETAIL = 501;

    private static final UriMatcher uriMatcher = buildUriMatcher();

    private DbHelper dbHelper;

    private static final SQLiteQueryBuilder productFull;

    static{
        productFull = new SQLiteQueryBuilder();
        productFull.setTables(
                StoCountContract.ProductEntry.TABLE_NAME + " LEFT OUTER JOIN " +
                StoCountContract.ProductCountEntry.TABLE_NAME + " ON (" + StoCountContract.ProductEntry.TABLE_NAME+"."+StoCountContract.ProductEntry._ID + " = "+ StoCountContract.ProductCountEntry.TABLE_NAME + "." + StoCountContract.ProductCountEntry.PRODUCT_ID  +")");
    }


    private static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = StoCountContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, StoCountContract.PATH_USERS+"/#", USER_ID);
        matcher.addURI(authority, StoCountContract.PATH_PRODUCTS+"/#", PRODUCT_ID);
        matcher.addURI(authority, StoCountContract.PATH_STOCK_PERIODS+"/#", STOCK_PERIOD_ID);
        matcher.addURI(authority, StoCountContract.PATH_PRODUCT_COUNTS+"/#", PRODUCT_COUNT_ID);

        matcher.addURI(authority, StoCountContract.PATH_USERS, USER);
        matcher.addURI(authority, StoCountContract.PATH_PRODUCTS, PRODUCT);
        matcher.addURI(authority, StoCountContract.PATH_STOCK_PERIODS, STOCK_PERIOD);
        matcher.addURI(authority, StoCountContract.PATH_PRODUCT_COUNTS, PRODUCT_COUNT);

        matcher.addURI(authority, StoCountContract.PATH_FULLPRODUCT +"/#", PRODUCT_FULL);
        //matcher.addURI(authority, StoCountContract.PATH_FULLPRODUCT, PRODUCT_FULL);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(getContext());
        return true;

    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (uriMatcher.match(uri)) {
            case USER:
                retCursor=dbHelper.getReadableDatabase().query(
                        StoCountContract.UserEntry.TABLE_NAME,
                        projection,
                        selection,
                        selection==null? null : selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case PRODUCT:
                retCursor=dbHelper.getReadableDatabase().query(
                        StoCountContract.ProductEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case STOCK_PERIOD:
                retCursor=dbHelper.getReadableDatabase().query(
                        StoCountContract.StockPeriodEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case PRODUCT_COUNT:
                retCursor=dbHelper.getReadableDatabase().query(
                        StoCountContract.ProductCountEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case USER_ID:
                retCursor=dbHelper.getReadableDatabase().query(
                        StoCountContract.UserEntry.TABLE_NAME,
                        projection,
                        StoCountContract.UserEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case PRODUCT_ID:
                retCursor=dbHelper.getReadableDatabase().query(
                        StoCountContract.ProductEntry.TABLE_NAME,
                        projection,
                        StoCountContract.ProductEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case STOCK_PERIOD_ID:
                retCursor=dbHelper.getReadableDatabase().query(
                        StoCountContract.StockPeriodEntry.TABLE_NAME,
                        projection,
                        StoCountContract.StockPeriodEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case PRODUCT_COUNT_ID:
                retCursor=dbHelper.getReadableDatabase().query(
                        StoCountContract.ProductCountEntry.TABLE_NAME,
                        projection,
                        StoCountContract.ProductCountEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            /*case PRODUCT_FULLDETAIL:
                String[] bfd_projection ={
                    StoCountContract.BookEntry.TABLE_NAME + "." + StoCountContract.BookEntry.TITLE,
                    StoCountContract.BookEntry.TABLE_NAME + "." + StoCountContract.BookEntry.SUBTITLE,
                    StoCountContract.BookEntry.TABLE_NAME + "." + StoCountContract.BookEntry.IMAGE_URL,
                    StoCountContract.BookEntry.TABLE_NAME + "." + StoCountContract.BookEntry.DESC,
                    "group_concat(DISTINCT " + StoCountContract.AuthorEntry.TABLE_NAME+ "."+ StoCountContract.AuthorEntry.AUTHOR +") as " + StoCountContract.AuthorEntry.AUTHOR
                };
                retCursor = bookFull.query(dbHelper.getReadableDatabase(),
                        bfd_projection,
                        StoCountContract.BookEntry.TABLE_NAME + "." + StoCountContract.BookEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs,
                        StoCountContract.BookEntry.TABLE_NAME + "." + StoCountContract.BookEntry._ID,
                        null,
                        sortOrder);
                break;*/
            case PRODUCT_FULL:
                String[] bf_projection ={
                        StoCountContract.ProductEntry.TABLE_NAME + "." + StoCountContract.ProductEntry._ID,
                        StoCountContract.ProductEntry.TABLE_NAME + "." + StoCountContract.ProductEntry.PRODUCT_NAME,
                        StoCountContract.ProductEntry.TABLE_NAME + "." + StoCountContract.ProductEntry.DESCRIPTION,
                        StoCountContract.ProductEntry.TABLE_NAME + "." + StoCountContract.ProductEntry.ADDITIONAL_INFO,
                        StoCountContract.ProductEntry.TABLE_NAME + "." + StoCountContract.ProductEntry.THUMBNAIL_IMAGE,
                        StoCountContract.ProductEntry.TABLE_NAME + "." + StoCountContract.ProductEntry.LARGE_IMAGE,
                        StoCountContract.ProductEntry.TABLE_NAME + "." + StoCountContract.ProductEntry.BARCODE,
                        StoCountContract.ProductEntry.TABLE_NAME + "." + StoCountContract.ProductEntry.BARCODE_FORMAT,
                        StoCountContract.ProductCountEntry.TABLE_NAME + "." + StoCountContract.ProductCountEntry._ID,
                        StoCountContract.ProductCountEntry.TABLE_NAME + "." + StoCountContract.ProductCountEntry.STOCK_PERIOD_ID,
                        StoCountContract.ProductCountEntry.TABLE_NAME + "." + StoCountContract.ProductCountEntry.QUANTITY,
                        StoCountContract.ProductCountEntry.TABLE_NAME + "." + StoCountContract.ProductCountEntry.COUNT_DATE
                        //"group_concat(DISTINCT " + StoCountContract.ProductCountEntry.TABLE_NAME+ "."+ StoCountContract.ProductCountEntry.STOCK_PERIOD_ID + ") as " + StoCountContract.AuthorEntry.AUTHOR
                };

                String bf_selection = null;

                if(selection != null && !selection.isEmpty())
                {
                    bf_selection = "("+StoCountContract.ProductCountEntry.TABLE_NAME + "." + StoCountContract.ProductCountEntry.STOCK_PERIOD_ID + " = '" + ContentUris.parseId(uri) + "' OR "+StoCountContract.ProductCountEntry.TABLE_NAME + "." + StoCountContract.ProductCountEntry.STOCK_PERIOD_ID+" is null) AND "+selection;
                }
                else
                {
                    bf_selection = StoCountContract.ProductCountEntry.TABLE_NAME + "." + StoCountContract.ProductCountEntry.STOCK_PERIOD_ID + " = '" + ContentUris.parseId(uri) + "' OR "+StoCountContract.ProductCountEntry.TABLE_NAME + "." + StoCountContract.ProductCountEntry.STOCK_PERIOD_ID+" is null ";
                }

                retCursor = productFull.query(dbHelper.getReadableDatabase(),
                        bf_projection,
                        bf_selection,
                        selectionArgs,
                        StoCountContract.ProductEntry.TABLE_NAME + "." + StoCountContract.ProductEntry._ID,
                        null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }



    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);

        switch (match) {
            //case BOOK_FULLDETAIL:
            //    return StoCountContract.BookEntry.CONTENT_ITEM_TYPE;
            case USER_ID:
                return StoCountContract.UserEntry.CONTENT_ITEM_TYPE;
            case PRODUCT_ID:
                return StoCountContract.ProductEntry.CONTENT_ITEM_TYPE;
            case STOCK_PERIOD_ID:
                return StoCountContract.StockPeriodEntry.CONTENT_ITEM_TYPE;
            case PRODUCT_COUNT_ID:
                return StoCountContract.ProductCountEntry.CONTENT_ITEM_TYPE;
            case USER:
                return StoCountContract.UserEntry.CONTENT_TYPE;
            case PRODUCT:
                return StoCountContract.ProductEntry.CONTENT_TYPE;
            case STOCK_PERIOD:
                return StoCountContract.StockPeriodEntry.CONTENT_TYPE;
            case PRODUCT_COUNT:
                return StoCountContract.ProductCountEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case USER: {
                long _id = db.insert(StoCountContract.UserEntry.TABLE_NAME, null, values);
                if ( _id > 0 ){
                    returnUri = StoCountContract.UserEntry.buildUserUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                //getContext().getContentResolver().notifyChange(StoCountContract.BookEntry.buildFullBookUri(_id), null);
                break;
            }
            case PRODUCT:{
                long _id = db.insert(StoCountContract.ProductEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = StoCountContract.ProductEntry.buildProductUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case STOCK_PERIOD: {
                long _id = db.insert(StoCountContract.StockPeriodEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = StoCountContract.StockPeriodEntry.buildStockPeriodUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case PRODUCT_COUNT: {
                long _id = db.insert(StoCountContract.ProductCountEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = StoCountContract.ProductCountEntry.buildProductCountUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case USER:
                rowsDeleted = db.delete(
                        StoCountContract.UserEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT:
                rowsDeleted = db.delete(
                        StoCountContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case STOCK_PERIOD:
                rowsDeleted = db.delete(
                        StoCountContract.StockPeriodEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_COUNT:
                rowsDeleted = db.delete(
                        StoCountContract.ProductCountEntry.TABLE_NAME,
                        StoCountContract.ProductCountEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsUpdated;
        switch (match) {
            case USER:
                rowsUpdated = db.update(StoCountContract.UserEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case PRODUCT:
                rowsUpdated = db.update(StoCountContract.ProductEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case STOCK_PERIOD:
                rowsUpdated = db.update(StoCountContract.StockPeriodEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case PRODUCT_COUNT:
                rowsUpdated = db.update(StoCountContract.ProductCountEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}