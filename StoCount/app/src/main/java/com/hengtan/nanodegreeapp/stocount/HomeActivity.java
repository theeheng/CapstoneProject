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
import com.leansoft.nano.log.ALog;
import com.leansoft.nano.ws.SOAPServiceCallback;

import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements SearchView.OnSuggestionListener {

    private static final String TAG = "INTENT_TO_SCAN_ACTIVITY";

    @InjectView(R.id.helloWorldTextView)
    protected EditText txtView;

    @InjectView(R.id.scanButton)
    protected Button btnScan;

    @InjectView(R.id.searchButton)
    protected Button btnSearch;

    @InjectView(R.id.image)
    protected ImageView mImageView;

    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.inject(this);


       // txtView.setInputType(InputType.TYPE_NULL);
       // txtView.setBackground(null);

        /*final Intent queryIntent = getIntent();
        final String queryAction = queryIntent.getAction();

        if (Intent.ACTION_SEARCH.equals(queryAction))
        {
            String test = queryIntent.getStringExtra(SearchManager.QUERY);
        }
        else if(Intent.ACTION_VIEW.equals(queryAction))
        {
            String itemId = queryIntent.getData().getLastPathSegment();
            //SearchProductFromWalmartAPI(null, itemId);
            SearchProductFromTescoAPI(null,itemId);
        }
        else {
            Log.d(TAG,"Create intent NOT from search");
        }
        */

        Glide.with(this).load(R.mipmap.no_image).fitCenter().into(mImageView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setIconified(false);

        searchView.setOnSuggestionListener(this);
        /*searchView.setOnQueryTextListener(
                new

        );*/
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

        //ActionMenuItemView actionSearch = (ActionMenuItemView) findViewById(R.id.action_search);
        //actionSearch.callOnClick();
        Intent intent = new Intent(this, TextInputLayoutActivity.class);
        startActivity(intent);
        //super.onSearchRequested();
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

                SearchProductFromAmazonApi(barcodeScanResult, formatNameResult, formatTypeResult);

               // SearchProductFromWalmartAPI(barcodeScanResult,null);
            }
        } else {
            Log.d(TAG, "Weird");
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void SearchProductFromWalmartAPI(String barcodeScanResult, String itemId)
    {
        WalmartApi testApi = new WalmartApi();

        WalmartService testService = testApi.getService();

        Map<String, Object> params = new HashMap<String, Object>();

        Resources res = getResources();

        params.put("apiKey", res.getString(R.string.walmart_apiKey));

        if(barcodeScanResult != null) {
            params.put("upc", barcodeScanResult);
        }
        else if(itemId != null)
        {
            params.put("itemId", itemId);
            barcodeScanResult = itemId;
        }

        final String searchCriteria = barcodeScanResult;

        testService.getProduct(params, new retrofit.Callback<WalmartItemList>() {
            @Override
            public void success(final WalmartItemList result, Response response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if(result != null && result.items != null && result.items.size() > 0) {
                            String name = result.items.get(0).name;
                            String description = (result.items.get(0).shortDescription == null) ? result.items.get(0).longDescription : result.items.get(0).shortDescription;

                            UpdateUI(name, description, result.items.get(0).largeImage);

                        }
                        else {
                            Toast.makeText(HomeActivity.this, "Product not found for : " + searchCriteria, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

            @Override
            public void failure(final RetrofitError error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String msg = error.getMessage();
                        Toast.makeText(HomeActivity.this, msg , Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    public void SearchProductFromTescoAPI(String barcodeScanResult, final String itemId)
    {
        TescoApi testApi = new TescoApi();

        TescoService testService = testApi.getService();


        testService.productSearch(itemId, Application.getTescoApiSessionKey(), new retrofit.Callback<TescoProductSearch>() {
            @Override
            public void success(final TescoProductSearch result, Response response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (result != null && result.getStatusCode() != null && result.getStatusCode() == 0 && result.getTotalProductCount() != null && result.getTotalProductCount() > 0 && result.getProducts() != null && result.getProducts().size() > 0)
                        {
                            String name = result.getProducts().get(0).getName();
                            String description = result.getProducts().get(0).getExtendedDescription();

                            UpdateUI(name, description, result.getProducts().get(0).getImagePath().replace("90x90","225x225")); //540x540

                        } else {
                            Toast.makeText(HomeActivity.this, "Product not found for : " + itemId, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

            @Override
            public void failure(final RetrofitError error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String msg = error.getMessage();
                        Toast.makeText(HomeActivity.this, msg, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    public void SearchProductFromAmazonApi(String barcodeScanResult, String formatName, String formatType)
    {
        Resources res = getResources();

        // Get shared client
        AWSECommerceServicePortType_SOAPClient client = AWSECommerceClient.getSharedClient(res.getString(R.string.aws_accesskeyid), res.getString(R.string.aws_securekeyid));

        client.setDebug(true);

        // Build request
        ItemLookup request = new ItemLookup();
        request.associateTag = "tag"; // seems any tag is ok
        request.shared = new ItemLookupRequest();
        request.shared.searchIndex = "All";
        request.shared.responseGroup = new ArrayList<String>();
        request.shared.responseGroup.add("Images");
        request.shared.responseGroup.add("ItemAttributes");
        request.shared.responseGroup.add("EditorialReview");
        List<String> itemLookup = new ArrayList<String>();
        itemLookup.add(barcodeScanResult);
        request.shared.itemId = itemLookup;

        if(formatName.indexOf("EAN")> -1) {

            if(formatType.indexOf("ISBN") > -1) {
                request.shared.idType = "ISBN";
            }
            else {
                request.shared.idType = "EAN";
            }
        }else if(formatName.indexOf("UPC")> -1) {
            request.shared.idType = "UPC";
        }


        // authenticate the request
        // http://docs.aws.amazon.com/AWSECommerceService/latest/DG/NotUsingWSSecurity.html
        AWSECommerceClient.authenticateRequest("ItemLookup");

        // make API call
        client.itemLookup(request, new SOAPServiceCallback<ItemLookupResponse>() {

            @Override
            public void onSuccess(ItemLookupResponse responseObject) { // handle successful response

                // success handling logic
                if (responseObject.items != null && responseObject.items.size() > 0) {
                    Items items = responseObject.items.get(0);
                    if (items.item != null && items.item.size() > 0) {

                        String name = items.item.get(0).itemAttributes.title;
                        String description = null;
                        String thumbnailUrl = null;

                        if(items.item.get(0).editorialReviews !=  null && items.item.get(0).editorialReviews.editorialReview != null && items.item.get(0).editorialReviews.editorialReview.size() > 0) {
                            description = items.item.get(0).editorialReviews.editorialReview.get(0).content;
                        }

                        if(items.item.get(0).imageSets !=  null && items.item.get(0).imageSets.size()  > 0 && items.item.get(0).imageSets.get(0).imageSet.size() > 0 && items.item.get(0).imageSets.get(0).imageSet.get(0).thumbnailImage != null) {

                            for(ImageSet imgset : items.item.get(0).imageSets.get(0).imageSet)
                                if(imgset.category.equals("primary"))
                                {
                                    thumbnailUrl = imgset.mediumImage.url; //imgset.thumbnailImage.url;
                                }
                        }

                        UpdateUI(name, description, thumbnailUrl);

                    } else {
                        Toast.makeText(HomeActivity.this, "No result", Toast.LENGTH_LONG).show();
                    }
                } else { // response resident error
                    if (responseObject.operationRequest != null && responseObject.operationRequest.errors != null) {
                        Errors errors = responseObject.operationRequest.errors;
                        if (errors.error != null && errors.error.size() > 0) {
                            com.amazon.webservices.awsecommerceservice.errors.Error error = errors.error.get(0);
                            Toast.makeText(HomeActivity.this, error.message, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(HomeActivity.this, "No result", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(HomeActivity.this, "No result", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Throwable error, String errorMessage) { // HTTP or parsing error


                ALog.e(TAG, errorMessage);
                Toast.makeText(HomeActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSOAPFault(Object soapFault) { // soap fault


                com.leansoft.nano.soap11.Fault fault = (com.leansoft.nano.soap11.Fault) soapFault;

                ALog.e(TAG, fault.faultstring);

                Toast.makeText(HomeActivity.this, fault.faultstring, Toast.LENGTH_LONG).show();

            }

        });
    }

    public void UpdateUI(String name, String description, String thumbnailUrl)
    {
        if(description != null) {
            description = Jsoup.parse(description).text().replaceAll("\\<.*?\\>", "");

            txtView.setText(name + " - " + description);

            txtView.setMovementMethod(new ScrollingMovementMethod());
            //txtView.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        }
        else
        {
            txtView.setText(name);
            //txtView.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            //txtView.setClickable(true);
            //txtView.setFocusable(true);
        }

        if(thumbnailUrl != null && (!thumbnailUrl.isEmpty()))
        {
            Glide.with(this).load(thumbnailUrl).fitCenter().into(mImageView);
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

            SearchProductFromTescoAPI(null, Integer.toString(suggestionItemId));

            return true;
        }
        else
        {
            return false;
        }
    }
}
