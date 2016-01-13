package com.hengtan.nanodegreeapp.stocount.data;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.hengtan.nanodegreeapp.stocount.Application;
import com.hengtan.nanodegreeapp.stocount.SettingsActivity;

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
    public static final String DEFAULT_BACKUP_DIRECTORY = "StoCountBackup";
    public static final File DATABASE_EXTERNAL_DIRECTORY = new File(Environment.getExternalStorageDirectory(),DEFAULT_BACKUP_DIRECTORY);

    /** File path of Db to be imported **/
    protected static final File IMPORT_EXTERNAL_DIRECTORY_FILE = new File(DATABASE_EXTERNAL_DIRECTORY,DbHelper.DATABASE_NAME);

    public static final String DATABASE_TABLE = "entryTable";

    /** Contains: /data/data/com.example.app/databases/example.db **/

    private static final File DATA_DIRECTORY_DATABASE =
            new File(Environment.getDataDirectory() +
                    "/data/" + Application.GetPackageName() +
                    "/databases/" + DbHelper.DATABASE_NAME );

    private static String GetBackupDirectoryFromPreference(Context ctx)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(SettingsActivity.BACKUP_DIRECTORY_KEY, null);
    }

    /** Saves the application database to the
     * export directory under MyDb.db **/
    public static boolean exportDb(Context ctx){

        String preferenceBackupPath = GetBackupDirectoryFromPreference(ctx);

        File dbFile = DATA_DIRECTORY_DATABASE;
        File exportDir = null;

        if(preferenceBackupPath != null && (!preferenceBackupPath.isEmpty()))
            exportDir = new File(preferenceBackupPath);
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
    public static boolean restoreDb(Context ctx){

        String preferenceBackupPath = GetBackupDirectoryFromPreference(ctx);

        File importFile = null;

        if(preferenceBackupPath != null && (!preferenceBackupPath.isEmpty()))
            importFile = new File(preferenceBackupPath, DbHelper.DATABASE_NAME);
        else
            importFile = IMPORT_EXTERNAL_DIRECTORY_FILE;

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

    public static void sendDBFile(Activity context) {

        String preferenceBackupPath = GetBackupDirectoryFromPreference(context);

        File backedupFile = null;

        if(preferenceBackupPath != null && (!preferenceBackupPath.isEmpty()))
            backedupFile = new File(preferenceBackupPath, DbHelper.DATABASE_NAME);
        else
            backedupFile = IMPORT_EXTERNAL_DIRECTORY_FILE;

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"email@example.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "subject here");
        intent.putExtra(Intent.EXTRA_TEXT, "body text");

        if (!backedupFile.exists() || !backedupFile.canRead()) {
            Toast.makeText(context, "Attachment Error", Toast.LENGTH_SHORT).show();
            context.finish();
            return;
        }
        Uri uri = Uri.fromFile(backedupFile);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType("Message/rfc822");
        context.startActivity(Intent.createChooser(intent, "Send DB file..."));
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
