package com.hengtan.nanodegreeapp.stocount;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.internal.view.menu.ActionMenuItem;
import android.support.v7.internal.view.menu.ActionMenuItemView;
import android.support.v7.widget.SearchView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.amazon.webservices.awsecommerceservice.ImageSet;
import com.bumptech.glide.Glide;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import tesco.webapi.android.TescoApi;
import tesco.webapi.android.TescoProductSearch;
import tesco.webapi.android.TescoService;
import tesco.webapi.android.TescoSessionKey;
import walmart.webapi.android.WalmartApi;
import walmart.webapi.android.WalmartItemList;
import walmart.webapi.android.WalmartService;

import com.amazon.service.ecommerce.AWSECommerceClient;
import com.amazon.webservices.awsecommerceservice.Errors;
import com.amazon.webservices.awsecommerceservice.ItemLookup;
import com.amazon.webservices.awsecommerceservice.ItemLookupRequest;
import com.amazon.webservices.awsecommerceservice.ItemLookupResponse;
import com.amazon.webservices.awsecommerceservice.Items;
import com.amazon.webservices.awsecommerceservice.client.AWSECommerceServicePortType_SOAPClient;
import com.hengtan.nanodegreeapp.stocount.data.StoCountContract;
import com.leansoft.nano.log.ALog;
import com.leansoft.nano.ws.SOAPServiceCallback;

import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements SearchView.OnSuggestionListener {

    private static final String TAG = HomeActivity.class.getSimpleName();

    @InjectView(R.id.scanButton)
    protected Button btnScan;

    @InjectView(R.id.searchButton)
    protected Button btnSearch;

    @InjectView(R.id.viewButton)
    protected Button btnView;

    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.inject(this);

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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.scanButton)
    public void onScanBtnClick(View v) {
        // This is the callback method that the system will invoke when your button is
        // clicked. You might do this by launching another app or by including the
        //functionality directly in this app.
        // Hint: Use a Try/Catch block to handle the Intent dispatch gracefully, if you
        // are using an external app.
        //when you're done, remove the toast below.
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

        ActionMenuItemView actionSearch = (ActionMenuItemView) findViewById(R.id.action_search);
        actionSearch.callOnClick();
        //super.onSearchRequested();
    }

    @OnClick(R.id.viewButton)
    public void onViewBtnClick(View v) {
        Intent intent = new Intent(this, ProductListActivity.class);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {

            final String barcodeScanResult = result.getContents();

            if (barcodeScanResult == null) {
                Log.d(TAG, "Cancelled scan");
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Log.d(TAG, "Scanned : " + barcodeScanResult);

                String formatNameResult = result.getFormatName();
                String formatTypeResult = result.getType();

                //SearchProductFromTescoAPI(null, barcodeScanResult);

                Cursor cursor = getContentResolver().query(
                        StoCountContract.ProductEntry.CONTENT_URI,
                        null, // leaving "columns" null just returns all the columns.
                        StoCountContract.ProductEntry.BARCODE+" = ? ", // cols for "where" clause
                        new String[] { barcodeScanResult }, // values for "where" clause
                        null  // sort order
                );

                if(cursor.getCount() > 0) {

                    cursor.moveToFirst();

                    Product prod = new Product(cursor);

                    Bundle bundle = new Bundle();
                    bundle.putParcelable(DetailActivity.PRODUCT_PARCELABLE, prod);
                    Intent intent = new Intent(this, DetailActivity.class);
                    intent.putExtra(DetailActivity.PRODUCT_PARCELABLE, bundle);
                    this.startActivity(intent);
                }
                else
                {
                    Toast.makeText(this, "No product found for barcode: "+barcodeScanResult, Toast.LENGTH_LONG).show();
                }


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
            int suggestionItemId = cur.getInt(0);

            //SearchProductFromWalmartAPI(null, Integer.toString(suggestionItemId));

            //SearchProductFromTescoAPI(null, Integer.toString(suggestionItemId));

            return true;
        }
        else
        {
            return false;
        }
    }
}
