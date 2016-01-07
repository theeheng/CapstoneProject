package com.hengtan.nanodegreeapp.stocount.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by hengtan on 06/01/2016.
 */
public class DbImportExport {

    public static final String TAG = DbImportExport.class.getName();


    /** Directory that files are to be read from and written to **/

    protected static final File DATABASE_EXTERNAL_DIRECTORY = new File(Environment.getExternalStorageDirectory(),"MyDirectory");
    //protected static final File DATABASE_INTERNAL_DIRECTORY = new File(Environment.getFilesDir(),"MyDirectory");


    /** File path of Db to be imported **/
    protected static final File IMPORT_EXTERNAL_DIRECTORY_FILE = new File(DATABASE_EXTERNAL_DIRECTORY,DbHelper.DATABASE_NAME);
    public static final String PACKAGE_NAME = "com.hengtan.nanodegreeapp.stocount";
    public static final String DATABASE_TABLE = "entryTable";

    /** Contains: /data/data/com.example.app/databases/example.db **/

    private static final File DATA_DIRECTORY_DATABASE =
            new File(Environment.getDataDirectory() +
                    "/data/" + PACKAGE_NAME +
                    "/databases/" + DbHelper.DATABASE_NAME );

    /** Saves the application database to the
     * export directory under MyDb.db **/
    public static  boolean exportDb(File internalFileDir){

        File dbFile = DATA_DIRECTORY_DATABASE;
        File exportDir = null;

        if( ! SdIsPresent() )
            exportDir = internalFileDir;
        else
            exportDir = DATABASE_EXTERNAL_DIRECTORY;

        String filename = DbHelper.DATABASE_NAME;

        File file = new File(exportDir, filename);

        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }

        try {
            file.createNewFile();
            copyFile(dbFile, file);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Replaces current database with the IMPORT_FILE if
     * import database is valid and of the correct type **/
    public static boolean restoreDb(File internalFile){

        File importFile = null;

        if( ! SdIsPresent() )
        {
            importFile = internalFile;
        }
        else
        {
            importFile = IMPORT_EXTERNAL_DIRECTORY_FILE;
        }

        File exportFile = DATA_DIRECTORY_DATABASE;

        if( ! checkDbIsValid(importFile) ) return false;

        if (!importFile.exists()) {
            Log.d(TAG, "File does not exist");
            return false;
        }

        try {
            exportFile.createNewFile();
            copyFile(importFile, exportFile);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Imports the file at IMPORT_FILE **/
    protected static boolean importIntoDb(Context ctx){
        if( ! SdIsPresent() ) return false;

        File importFile = IMPORT_EXTERNAL_DIRECTORY_FILE;

        if( ! checkDbIsValid(importFile) ) return false;

        try{
            SQLiteDatabase sqlDb = SQLiteDatabase.openDatabase
                    (importFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);

            Cursor cursor = sqlDb.query(true, StoCountContract.ProductCountEntry.TABLE_NAME,
                    null, null, null, null, null, null, null
            );

           // DbAdapter dbAdapter = new DbAdapter(ctx);
           // dbAdapter.open();

          //  final int titleColumn = cursor.getColumnIndexOrThrow("title");
          //  final int timestampColumn = cursor.getColumnIndexOrThrow("timestamp");


// Adds all items in cursor to current database
       //     cursor.moveToPosition(-1);
       //     while(cursor.moveToNext()){
       //         dbAdapter.createQuote(
       //                 cursor.getString(titleColumn),
       //                 cursor.getString(timestampColumn)
       //         );
       //     }

            sqlDb.close();
            cursor.close();
       //     dbAdapter.close();
        } catch( Exception e ){
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /** Given an SQLite database file, this checks if the file
     * is a valid SQLite database and that it contains all the
     * columns represented by DbAdapter.ALL_COLUMN_KEYS **/
    protected static boolean checkDbIsValid( File db ){
        try{
            SQLiteDatabase sqlDb = SQLiteDatabase.openDatabase
                    (db.getPath(), null, SQLiteDatabase.OPEN_READONLY);

            Cursor cursor = sqlDb.query(true, StoCountContract.ProductCountEntry.TABLE_NAME,
                    null, null, null, null, null, null, null
            );

// ALL_COLUMN_KEYS should be an array of keys of essential columns.

// Throws exception if any column is missing
         //   for( String s : DbAdapter.ALL_COLUMN_KEYS ){
         //       cursor.getColumnIndexOrThrow(s);
         //   }

            sqlDb.close();
            cursor.close();
        } catch( IllegalArgumentException e ) {
            Log.d(TAG, "Database valid but not the right type");
            e.printStackTrace();
            return false;
        } catch( SQLiteException e ) {
            Log.d(TAG, "Database file is invalid.");
            e.printStackTrace();
            return false;
        } catch( Exception e){
            Log.d(TAG, "checkDbIsValid encountered an exception");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private static void copyFile(File src, File dst) throws IOException {

        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();

        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }
    }

    /** Returns whether an SD card is present and writable **/
    public static boolean SdIsPresent() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }
}
