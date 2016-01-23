package com.hengtan.nanodegreeapp.stocount;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.internal.view.menu.ActionMenuItemView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.hengtan.nanodegreeapp.stocount.data.DbHelper;
import com.hengtan.nanodegreeapp.stocount.data.DbImportExport;
import com.hengtan.nanodegreeapp.stocount.data.Product;
import com.hengtan.nanodegreeapp.stocount.data.ProductCount;
import com.hengtan.nanodegreeapp.stocount.data.StoCountContract;
import com.hengtan.nanodegreeapp.stocount.data.StockPeriod;
import com.hengtan.nanodegreeapp.stocount.wearable.ProductWearService;
import com.hengtan.nanodegreeapp.stocount.wearable.WearListenerService;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements SearchView.OnSuggestionListener, LoaderManager.LoaderCallbacks<Cursor>, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = HomeActivity.class.getSimpleName();

    @InjectView(R.id.scanButton)
    protected Button btnScan;

    @InjectView(R.id.searchButton)
    protected Button btnSearch;

    @InjectView(R.id.viewButton)
    protected Button btnView;

    @InjectView(R.id.closeButton)
    protected Button btnClose;

    @InjectView(R.id.stockPeriodDate)
    protected TextView txtStockPeriodDate;

    @InjectView(R.id.voiceButton)
    protected Button btnVoice;

    private SearchView searchView;

    public static final int RESULT_SETTINGS = 1;

    // Identifies a particular Loader being used in this component
    private static final int PRODUCT_ID_LOADER = 0;

    private static final int PRODUCT_BARCODE_LOADER = 1;

    private static final int STOCK_PERIOD_LOADER = 2;

    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;

    private String mBarcodeResult;
    private String mBarcodeFormat;

    private String mStockPeriodStartDateStr;
    private String mErrorBarcodeStr;
    private String mCancelledBarcodeStr;
    private String mScannedBarcodeStr;
    private String mSearchCriteriaBarcodeStr;
    private String mSearchCriteriaProductIdStr;
    private String mNoProductFoundStr;
    private String mBackupSuccessfulStr;
    private String mBackupErrorStr;
    private String mRestoreSuccessfulStr;
    private String mRestoreErrorStr;

    private Integer mSearchResultId;

    private GoogleApiClient mGoogleApiClient;

    private StockPeriod mStockPeriod;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.inject(this);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mStockPeriod = Application.getCurrentStockPeriod();

        Resources res = getResources();

        mStockPeriodStartDateStr = res.getString(R.string.cd_stock_period_start_date);
        mErrorBarcodeStr = res.getString(R.string.error_barcode_text);
        mCancelledBarcodeStr = res.getString(R.string.cancelled_barcode_text);
        mScannedBarcodeStr = res.getString(R.string.scanned_barcode_log_text);
        mSearchCriteriaBarcodeStr = res.getString(R.string.search_criteria_barcode);
        mSearchCriteriaProductIdStr = res.getString(R.string.search_criteria_productid);
        mNoProductFoundStr =  res.getString(R.string.no_product_found_toast_text);
        mBackupSuccessfulStr =  res.getString(R.string.backup_successful);
        mRestoreSuccessfulStr =  res.getString(R.string.restore_successful);
        mBackupErrorStr =  res.getString(R.string.backup_error);
        mRestoreErrorStr =  res.getString(R.string.restore_error);

        if (mStockPeriod != null) {
            SetStockPeriodText(mStockPeriod);
        }

    }

    private void SetStockPeriodText(StockPeriod stckPeriod) {
        txtStockPeriodDate.setText(mStockPeriodStartDateStr + " " + stckPeriod.DateFormat.format(stckPeriod.getStartDate()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnSuggestionListener(this);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case R.id.action_logout:
                Application.Logout(mGoogleApiClient, this);
                return true;
            case R.id.action_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivityForResult(i, RESULT_SETTINGS);
                return true;
            case R.id.action_backup:
                if(DbImportExport.exportDb(this)) {
                    Toast.makeText(this, mBackupSuccessfulStr, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(this, mBackupErrorStr, Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_restore:
                int dbVersion = DbImportExport.restoreDb(this);

                if(dbVersion == DbImportExport.INVALID_DB_VERSION && dbVersion == DbImportExport.DB_FILE_NOT_EXIST && dbVersion == DbImportExport.RESTORE_DB_EXCEPTION) {

                    Toast.makeText(this, String.format(mRestoreErrorStr, dbVersion), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(this, String.format(mRestoreSuccessfulStr, dbVersion) , Toast.LENGTH_SHORT).show();

                    //Load current stock period
                    getLoaderManager().restartLoader(STOCK_PERIOD_LOADER, null, this);
                }
                return true;
            case R.id.action_send:
                DbImportExport.sendDBFile(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.scanButton)
    public void onScanBtnClick(View v) {
        try {
            IntentIntegrator intentIntegrator = new IntentIntegrator(HomeActivity.this);
            //intentIntegrator.setOrientationLocked(false);
            intentIntegrator.initiateScan();
        } catch (Exception ex) {
            Log.e(TAG, mErrorBarcodeStr + ex.getMessage());
        }
    }

    @OnClick(R.id.searchButton)
    public void onSearchBtnClick(View v) {

        View actionSearch = findViewById(R.id.action_search);

        if(actionSearch instanceof ActionMenuItemView)
        {
            ((ActionMenuItemView)actionSearch).callOnClick();
        }
    }

    @OnClick(R.id.voiceButton)
    public void onVoiceBtnClick(View v) {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        // Specify the calling package to identify your application
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass()
                .getPackage().getName());

        // Display an hint to the user about what he should say.
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say: {Product Name} QUANTITY {Stock Count Value} UPDATE"
                .toString());

        // Given an hint to the recognizer about what the user is going to say
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                Locale.getDefault());

        int noOfMatches = 10;
        // Specify how many results you want to receive. The results will be
        // sorted where the first result is the one with higher confidence.

        //intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, noOfMatches);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }


    @OnClick(R.id.viewButton)
    public void onViewBtnClick(View v) {

        Intent intent = new Intent(this, ProductListActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.closeButton)
    public void onCloseBtnClick(View v) {
        Intent intent = new Intent(this, StockPeriodActivity.class);
        intent.putExtra(StockPeriodActivity.IS_CLOSE_STOCK_EXTRA, true);
        this.startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (requestCode == IntentIntegrator.REQUEST_CODE && result != null) {

            mBarcodeResult = result.getContents();
            mSearchResultId = null;

            if (mBarcodeResult == null) {
                Log.d(TAG, mCancelledBarcodeStr);
                Toast.makeText(this, mCancelledBarcodeStr, Toast.LENGTH_LONG).show();
            } else {
                Log.d(TAG, mScannedBarcodeStr + mBarcodeResult);

                mBarcodeFormat = result.getFormatName(); // EAN_13 | UPC_A
                String formatTypeResult = result.getType(); // PRODUCT | ISBN
                searchProductFromDB();
            }
        } else if (requestCode == this.VOICE_RECOGNITION_REQUEST_CODE) {
            //If Voice recognition is successful then it returns RESULT_OK
            if (resultCode == RESULT_OK) {

                ArrayList<String> textMatchList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                if (!textMatchList.isEmpty()) {

                    if (textMatchList.size() == 1) {
                        voiceProductSearch(textMatchList.get(0));
                    } else {
                        if (textMatchList.size() > 1) {
                            AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                                    HomeActivity.this);
                            builderSingle.setIcon(android.R.drawable.ic_menu_search);
                            builderSingle.setTitle("Select a correct suggestion:-");
                            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                    HomeActivity.this,
                                    android.R.layout.select_dialog_singlechoice, textMatchList);

                            builderSingle.setNegativeButton("cancel",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });

                            builderSingle.setAdapter(arrayAdapter,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String strName = arrayAdapter.getItem(which);
                                            voiceProductSearch(strName);
                                        }
                                    });
                            builderSingle.show();
                        }
                    }

                    // If first Match contains the 'search' word
                    // Then start web search.

                    // if (textMatchList.get(0).contains("search")) {

                    //String searchQuery = textMatchList.get(0).replace("search", " ");
                    //Intent search = new Intent(Intent.ACTION_WEB_SEARCH);
                    //search.putExtra(SearchManager.QUERY, searchQuery);
                    //startActivity(search);
                    //  } else {
                    // populate the Matches
                    //     mlvTextMatches
                    //             .setAdapter(new ArrayAdapter<String>(this,
                    //                     android.R.layout.simple_list_item_1,
                    //                     textMatchList));
                    // }

                }
                //Result code for various error.
            } else if (resultCode == RecognizerIntent.RESULT_AUDIO_ERROR) {
                showToastMessage("Audio Error");
            } else if (resultCode == RecognizerIntent.RESULT_CLIENT_ERROR) {
                showToastMessage("Client Error");
            } else if (resultCode == RecognizerIntent.RESULT_NETWORK_ERROR) {
                showToastMessage("Network Error");
            } else if (resultCode == RecognizerIntent.RESULT_NO_MATCH) {
                showToastMessage("No Match");
            } else if (resultCode == RecognizerIntent.RESULT_SERVER_ERROR) {
                showToastMessage("Server Error");
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void voiceProductSearch(String voiceText)
    {
        String productName = voiceText.toLowerCase().replace(" qty "," quantity ").replace(" quantities "," quantity ").replace("updates","update");
        String quantityVoice = null;
        boolean updateVoice = false;

        showToastMessage(productName);

        if(productName.toLowerCase().contains(" quantity "))
        {
            if(productName.length() > 6 && productName.substring(productName.length()-6,productName.length()).toLowerCase().equals("update"))
            {
                updateVoice = true;
            }

            String[] voiceResult = productName.toLowerCase().replace("update","").split(" quantity ");

            productName = voiceResult[0];
            quantityVoice = voiceResult[1];
        }

    }

    void showToastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        return false;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        if(searchView != null) {
            CursorAdapter c = searchView.getSuggestionsAdapter();

            Cursor cur = c.getCursor();
            cur.moveToPosition(position);
            mSearchResultId = cur.getInt(0);
            mBarcodeResult = null;
            searchProductFromDB();

            return true;
        }
        else
        {
            return false;
        }
    }

    private void searchProductFromDB()
    {
        Cursor cursor = null;

        if(mBarcodeResult != null && !mBarcodeResult.isEmpty()) {

            getLoaderManager().restartLoader(PRODUCT_BARCODE_LOADER, null, this);

        }
        else if (mSearchResultId != null) {

            getLoaderManager().restartLoader(PRODUCT_ID_LOADER, null, this);

        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        /*
         * Takes action based on the ID of the Loader that's being created
         */
        switch (id) {
            case PRODUCT_BARCODE_LOADER:
                // Returns a new CursorLoader
                return new CursorLoader(
                        this,
                        StoCountContract.ProductEntry.buildCurrentProductUri(mStockPeriod.getStockPeriodId()),
                        null,
                        StoCountContract.ProductEntry.BARCODE + " = ? AND " + StoCountContract.ProductEntry.BARCODE_FORMAT + " = ? ",
                        new String[]{mBarcodeResult, mBarcodeFormat},
                        null
                );

            case PRODUCT_ID_LOADER:
                // Returns a new CursorLoader
                return new CursorLoader(
                        this,
                        StoCountContract.ProductEntry.buildCurrentProductUri(mStockPeriod.getStockPeriodId()),
                        null,
                        StoCountContract.ProductEntry.TABLE_NAME + "." + StoCountContract.ProductEntry._ID +" = ?",
                        new String[] {mSearchResultId.toString()},
                        null
                );
            case STOCK_PERIOD_LOADER:
                // Returns a new CursorLoader
                return new CursorLoader(
                        this,
                        StoCountContract.StockPeriodEntry.CONTENT_URI,
                        null,
                        StoCountContract.StockPeriodEntry.END_DATE + " IS NULL ",
                        null,
                        null
                );
            default:
                // An invalid id was passed in
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if(cursor != null && cursor.getCount() > 0) {

            cursor.moveToFirst();

            if(loader.getId() == PRODUCT_BARCODE_LOADER || loader.getId() == PRODUCT_ID_LOADER) {

                Product prod = new Product(cursor);
                ProductCount prodCount = new ProductCount(cursor);

                Bundle bundle = new Bundle();
                bundle.putParcelable(DetailActivity.PRODUCT_PARCELABLE, prod);

                if(prodCount.getProductCountId() != null)
                    bundle.putParcelable(DetailActivity.PRODUCT_COUNT_PARCELABLE, prodCount);

                Intent intent = new Intent(this, DetailActivity.class);
                intent.putExtra(DetailActivity.PRODUCT_PARCELABLE, bundle);
                intent.putExtra(DetailActivity.IS_STOCK_ENTRY_EXTRA, true);
                this.startActivity(intent);
            }
            else if(loader.getId() == STOCK_PERIOD_LOADER )
            {
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    mStockPeriod = new StockPeriod(cursor);
                    SetStockPeriodText(mStockPeriod);
                    Application.setCurrentStockPeriod(mStockPeriod);
                }
            }
        }
        else {
            String searchCriteria = "";

            if(loader.getId() == PRODUCT_BARCODE_LOADER)
                searchCriteria = mSearchCriteriaBarcodeStr + " " +mBarcodeResult;
            else if(loader.getId() == PRODUCT_ID_LOADER)
                searchCriteria = mSearchCriteriaProductIdStr + " " +mSearchResultId.toString();
            Toast.makeText(this, mNoProductFoundStr + " " +searchCriteria, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
