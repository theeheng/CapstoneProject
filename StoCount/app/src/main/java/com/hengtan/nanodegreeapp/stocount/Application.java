package com.hengtan.nanodegreeapp.stocount;

import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.hengtan.nanodegreeapp.stocount.api.AmazonApiCall;
import com.hengtan.nanodegreeapp.stocount.api.ApiCall;
import com.hengtan.nanodegreeapp.stocount.api.TescoApiCall;
import com.hengtan.nanodegreeapp.stocount.api.WalmartApiCall;
import com.hengtan.nanodegreeapp.stocount.data.DbHelper;
import com.hengtan.nanodegreeapp.stocount.data.DbImportExport;
import com.hengtan.nanodegreeapp.stocount.data.StoCountContract;
import com.hengtan.nanodegreeapp.stocount.data.StockPeriod;
import com.hengtan.nanodegreeapp.stocount.data.User;
import com.hengtan.nanodegreeapp.stocount.search.SearchSuggestion;

import java.util.ArrayList;

/**
 * Created by htan on 17/11/2015.
 */
public class Application extends android.app.Application {

    private static Context context;
    private static User mCurrentLoginUser;
    private static StockPeriod mCurrentStockPeriod;
    private static String mDefaultApiCode = "TESCO";
    private static String LAST_GOOGLE_SIGN_IN_KEY = "LastGoogleSignIn";

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        Resources resources = context.getResources();
        mDefaultApiCode  = resources.getString(R.string.preference_tesco_api_code);
        InitializeBackupDirectoryPreference();
    }

    public static Context getContext(){
        return context;
    }

    public static void setCurrentLoginUser(User user)
    {
        mCurrentLoginUser =  user;
    }

    public static User getCurrentLoginUser() { return mCurrentLoginUser; }

    public static void setCurrentStockPeriod(StockPeriod stockPeriod)
    {
        mCurrentStockPeriod =  stockPeriod;
    }

    public static StockPeriod getCurrentStockPeriod()
    {
        if(mCurrentStockPeriod != null && mCurrentStockPeriod.getStockPeriodId() != null) {
            return mCurrentStockPeriod;
        }
        else {

            Cursor retCursor = getContext().getContentResolver().query(
                    StoCountContract.StockPeriodEntry.CONTENT_URI,
                    null,
                    StoCountContract.StockPeriodEntry.END_DATE + " IS NULL ",
                    null,
                    null
            );

            if (retCursor.getCount() > 0) {

                if (retCursor.moveToFirst()) {
                    mCurrentStockPeriod = new StockPeriod(retCursor);
                    return mCurrentStockPeriod;
                }
            }
        }

        return null;
    }

    public static ApiCall GetApiCallFromPreference(String preferenceApiCode)
    {
        if(preferenceApiCode.equals("TESCO"))
        {
            return (ApiCall) new TescoApiCall();
        }
        else if(preferenceApiCode.equals("WALMART"))
        {
            return (ApiCall) new WalmartApiCall();
        }
        else if(preferenceApiCode.equals("AMAZON"))
        {
            return (ApiCall) new AmazonApiCall();
        }
        else
        {
            return (ApiCall) new TescoApiCall();
        }
    }

    public static void Logout(GoogleApiClient googleApiClient, final Activity activity)
    {
        try {
            Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            mCurrentLoginUser = null;
                            Intent intent = new Intent(activity, LoginActivity.class);
                            activity.startActivity(intent);
                        }
                    });
        }
        catch (Exception ex)
        {
            String msg = ex.getMessage();
        }
    }

    public static String GetApiCodeFromPreference()
    {
        if(context != null)
        {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            String preferenceApiCode = sharedPrefs.getString(SettingsActivity.API_PREFERENCE_ID, mDefaultApiCode);

            if (preferenceApiCode.length() == 0) {
                return mDefaultApiCode;
            } else {
                return preferenceApiCode;
            }
        }
        else
        {
            return mDefaultApiCode;
        }
    }

    public static void InitializeBackupDirectoryPreference()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String initialValue = preferences.getString(SettingsActivity.BACKUP_DIRECTORY_KEY, null);

        if(initialValue == null)
        {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(SettingsActivity.BACKUP_DIRECTORY_KEY, DbImportExport.DATABASE_EXTERNAL_DIRECTORY.getPath());
            editor.commit();
        }
    }

    public static String GetPackageName()
    {
        return context.getPackageName();
    }

    public static String GetLastGoogleSignIn()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String lastGoogleSignIn = preferences.getString(LAST_GOOGLE_SIGN_IN_KEY, "");

        return lastGoogleSignIn;

    }

    public static void SetLastGoogleSignIn(String lastGoogleSignIn)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String initialValue = preferences.getString(LAST_GOOGLE_SIGN_IN_KEY, "");

        if(initialValue == null || (initialValue != null && initialValue.isEmpty()))
        {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(LAST_GOOGLE_SIGN_IN_KEY, lastGoogleSignIn);
            editor.commit();
        }
    }
}
