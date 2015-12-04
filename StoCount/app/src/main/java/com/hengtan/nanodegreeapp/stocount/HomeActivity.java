package com.hengtan.nanodegreeapp.stocount;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
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

import com.hengtan.nanodegreeapp.stocount.data.Product;
import com.hengtan.nanodegreeapp.stocount.data.ProductCount;
import com.hengtan.nanodegreeapp.stocount.data.StoCountContract;
import com.hengtan.nanodegreeapp.stocount.data.StockPeriod;

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

    private SearchView searchView;

    public static final int RESULT_SETTINGS = 1;

    // Identifies a particular Loader being used in this component
    private static final int PRODUCT_ID_LOADER = 0;

    private static final int PRODUCT_BARCODE_LOADER = 1;

    private String mBarcodeResult;
    private String mBarcodeFormat;

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

        if (mStockPeriod != null) {

            txtStockPeriodDate.setText("Current Stock Period Starting : "+mStockPeriod.DateFormat.format(mStockPeriod.getStartDate()));

        }

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
            Log.e(TAG, "Error loading barcode scanning :" + ex.getMessage());
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
        if (result != null) {

            mBarcodeResult = result.getContents();
            mSearchResultId = null;

            if (mBarcodeResult == null) {
                Log.d(TAG, "Cancelled scan");
                Toast.makeText(this, "Cancelled Scanning Barcode", Toast.LENGTH_LONG).show();
            } else {
                Log.d(TAG, "Scanned : " + mBarcodeResult);

                mBarcodeFormat = result.getFormatName(); // EAN_13 | UPC_A
                String formatTypeResult = result.getType(); // PRODUCT | ISBN
                searchProductFromDB();
            }
        } else {
            Log.d(TAG, "Weird");
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
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
                        StoCountContract.ProductEntry.buildFullProductUri(mStockPeriod.getStockPeriodId()),
                        null,
                        StoCountContract.ProductEntry.BARCODE + " = ? AND " + StoCountContract.ProductEntry.BARCODE_FORMAT + " = ? ",
                        new String[]{mBarcodeResult, mBarcodeFormat},
                        null
                );

            case PRODUCT_ID_LOADER:
                // Returns a new CursorLoader
                return new CursorLoader(
                        this,
                        StoCountContract.ProductEntry.buildFullProductUri(mStockPeriod.getStockPeriodId()),
                        null,
                        StoCountContract.ProductEntry.TABLE_NAME + "." + StoCountContract.ProductEntry._ID +" = ?",
                        new String[] {mSearchResultId.toString()},
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

        }
        else {
            String searchCriteria = "";

            if(loader.getId() == PRODUCT_BARCODE_LOADER)
                searchCriteria = "barcode :" + mBarcodeResult;
            else if(loader.getId() == PRODUCT_ID_LOADER)
                searchCriteria = "productid :" + mSearchResultId.toString();
            Toast.makeText(this, "No product found for " + searchCriteria, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
