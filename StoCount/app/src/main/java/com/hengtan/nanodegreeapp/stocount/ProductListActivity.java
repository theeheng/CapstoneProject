package com.hengtan.nanodegreeapp.stocount;

import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;

import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.internal.view.menu.ActionMenuItemView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazon.service.ecommerce.AWSECommerceClient;
import com.amazon.webservices.awsecommerceservice.Errors;
import com.amazon.webservices.awsecommerceservice.ImageSet;
import com.amazon.webservices.awsecommerceservice.ItemLookup;
import com.amazon.webservices.awsecommerceservice.ItemLookupRequest;
import com.amazon.webservices.awsecommerceservice.ItemLookupResponse;
import com.amazon.webservices.awsecommerceservice.Items;
import com.amazon.webservices.awsecommerceservice.client.AWSECommerceServicePortType_SOAPClient;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.hengtan.nanodegreeapp.stocount.data.StoCountContract;
import com.hengtan.nanodegreeapp.stocount.data.StoCountProvider;
import com.hudomju.swipe.OnItemClickListener;
import com.hudomju.swipe.SwipeToDismissTouchListener;
import com.hudomju.swipe.SwipeableItemClickListener;
import com.hudomju.swipe.adapter.RecyclerViewAdapter;
import com.leansoft.nano.log.ALog;
import com.leansoft.nano.ws.SOAPServiceCallback;

import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import tesco.webapi.android.TescoApi;
import tesco.webapi.android.TescoProductSearch;
import tesco.webapi.android.TescoService;
import walmart.webapi.android.WalmartApi;
import walmart.webapi.android.WalmartItemList;
import walmart.webapi.android.WalmartService;
import android.app.LoaderManager;

import static android.widget.Toast.LENGTH_SHORT;

public class ProductListActivity extends AppCompatActivity implements SearchView.OnSuggestionListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = ProductListActivity.class.getSimpleName();

    @InjectView(R.id.famProductListButton)
    protected FloatingActionsMenu famProductListButton;

    @InjectView(R.id.fabSearchButton)
    protected FloatingActionButton fabSearchButton;

    @InjectView(R.id.fabBarcodeButton)
    protected FloatingActionButton fabBarcodeButton;

    @InjectView(R.id.recycler_view)
    protected RecyclerView mRecyclerView;

    private SearchView searchView;

    private MyBaseAdapter adapter;

    // Identifies a particular Loader being used in this component
    private static final int URL_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_list_activity);
        ButterKnife.inject(this);
        init();
        getLoaderManager().restartLoader(URL_LOADER, null, this);

        /*Cursor cursor = getContentResolver().query(
                StoCountContract.ProductEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        */

    }

    @OnClick(R.id.fabSearchButton)
    public void onSearchClick(View v) {
        famProductListButton.collapse();
        ActionMenuItemView actionSearch = (ActionMenuItemView) findViewById(R.id.action_search);
        actionSearch.callOnClick();
    }

    @OnClick(R.id.fabBarcodeButton)
    public void onBarcodeClick(View v) {
        famProductListButton.collapse();
        try {
            IntentIntegrator intentIntegrator = new IntentIntegrator(ProductListActivity.this);
            //intentIntegrator.setOrientationLocked(false);
            intentIntegrator.initiateScan();
        } catch (Exception ex) {
            Log.e(TAG, "Error loading barcode scanning :" + ex.getMessage());
        }
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

        //searchView.setIconified(false);

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

                SearchProductFromTescoAPI(barcodeScanResult);

                //SearchProductFromAmazonApi(barcodeScanResult, formatNameResult, formatTypeResult);

                // SearchProductFromWalmartAPI(barcodeScanResult,null);
            }
        } else {
            Log.d(TAG, "Weird");
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        getLoaderManager().restartLoader(URL_LOADER, null, this);
    }
    
    private void init() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        adapter = new MyBaseAdapter(this);
        mRecyclerView.setAdapter(adapter);
        final SwipeToDismissTouchListener<RecyclerViewAdapter> touchListener =
                new SwipeToDismissTouchListener<>(
                        new RecyclerViewAdapter(mRecyclerView),
                        new SwipeToDismissTouchListener.DismissCallbacks<RecyclerViewAdapter>() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(RecyclerViewAdapter view, int position) {

                                adapter.remove(position);

                                //put delete product code here
                                //
                                //
                                //
                                //
                            }
                        });

        mRecyclerView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        mRecyclerView.setOnScrollListener((RecyclerView.OnScrollListener) touchListener.makeScrollListener());
        mRecyclerView.addOnItemTouchListener(new SwipeableItemClickListener(this,
                new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (view.getId() == R.id.txt_delete) {
                            touchListener.processPendingDismisses();
                        } else if (view.getId() == R.id.txt_undo) {
                            touchListener.undoPendingDismiss();
                        } else { // R.id.txt_data
                            adapter.onItemclicked(position);
                        }
                    }
                }));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

     /*
     * Takes action based on the ID of the Loader that's being created
     */
        switch (id) {
            case URL_LOADER:
                // Returns a new CursorLoader
                return new CursorLoader(
                        this,
                        StoCountContract.ProductEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null
                );
            default:
                // An invalid id was passed in
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        adapter.swapCursor(data);
        adapter.notifyDataSetChanged();
        //if (position != ListView.INVALID_POSITION) {
        //    bookList.smoothScrollToPosition(position);
        //}
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
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

            SearchProductFromTescoAPI(Integer.toString(suggestionItemId));

            return true;
        }
        else
        {
            return false;
        }
    }

    public void SearchProductFromTescoAPI(final String searchText)
    {
        TescoApi testApi = new TescoApi();

        final TescoService testService = testApi.getService();


        testService.productSearch(searchText, new retrofit.Callback<TescoProductSearch>() {
            @Override
            public void success(final TescoProductSearch result, Response response) {


                if (result != null && result.getStatusCode() != null && result.getStatusCode() == 0 && result.getTotalProductCount() != null && result.getTotalProductCount() > 0 && result.getProducts() != null && result.getProducts().size() > 0)
                {
                    final Product prod = new Product(result.getProducts().get(0));

                    TescoApi descriptionApi = new TescoApi();
                    TescoService descriptionService = descriptionApi.getService();

                    testService.productExtendedInfo(result.getProducts().get(0).getProductId(), new retrofit.Callback<Response>() {

                        @Override
                        public void success(final Response result, Response response) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    String test = new String(((TypedByteArray) result.getBody()).getBytes());

                                    String divTag = "<div class=\"content\">";
                                    String divEndTag = "</div>" ;

                                    if(test.indexOf(divTag) > -1) {

                                        int divIndex = test.indexOf(divTag) + divTag.length();

                                        if(test.substring(divIndex).indexOf(divEndTag) > -1) {
                                            int divEndIndex = test.substring(divIndex).indexOf(divEndTag) + divIndex;
                                            prod.setDescription(Jsoup.parse(test.substring(divIndex, divEndIndex)).text().replaceAll("\\<.*?\\>", ""));
                                        }
                                    }
                                    Bundle bundle = new Bundle();
                                    bundle.putParcelable(DetailActivity.PRODUCT_PARCELABLE, prod);
                                    Intent intent = new Intent(ProductListActivity.this, DetailActivity.class);
                                    intent.putExtra(DetailActivity.PRODUCT_PARCELABLE, bundle);
                                    startActivity(intent);

                                }
                            });
                        }

                        @Override
                        public void failure(final RetrofitError error) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String msg = error.getMessage();
                                    Toast.makeText(ProductListActivity.this, msg, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });

                } else {
                    Toast.makeText(ProductListActivity.this, "Product not found for : " + searchText, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void failure(final RetrofitError error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String msg = error.getMessage();
                        Toast.makeText(ProductListActivity.this, msg, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
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

                        if (result != null && result.items != null && result.items.size() > 0) {
                            String name = result.items.get(0).name;
                            String description = (result.items.get(0).shortDescription == null) ? result.items.get(0).longDescription : result.items.get(0).shortDescription;

                            //UpdateUI(name, description, result.items.get(0).largeImage);

                        } else {
                            Toast.makeText(ProductListActivity.this, "Product not found for : " + searchCriteria, Toast.LENGTH_LONG).show();
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
                        Toast.makeText(ProductListActivity.this, msg, Toast.LENGTH_LONG).show();
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

                        //UpdateUI(name, description, thumbnailUrl);

                    } else {
                        Toast.makeText(ProductListActivity.this, "No result", Toast.LENGTH_LONG).show();
                    }
                } else { // response resident error
                    if (responseObject.operationRequest != null && responseObject.operationRequest.errors != null) {
                        Errors errors = responseObject.operationRequest.errors;
                        if (errors.error != null && errors.error.size() > 0) {
                            com.amazon.webservices.awsecommerceservice.errors.Error error = errors.error.get(0);
                            Toast.makeText(ProductListActivity.this, error.message, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ProductListActivity.this, "No result", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(ProductListActivity.this, "No result", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Throwable error, String errorMessage) { // HTTP or parsing error

                ALog.e(TAG, errorMessage);
                Toast.makeText(ProductListActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSOAPFault(Object soapFault) { // soap fault

                com.leansoft.nano.soap11.Fault fault = (com.leansoft.nano.soap11.Fault) soapFault;

                ALog.e(TAG, fault.faultstring);

                Toast.makeText(ProductListActivity.this, fault.faultstring, Toast.LENGTH_LONG).show();

            }

        });
    }

    static class MyBaseAdapter extends RecyclerView.Adapter<MyBaseAdapter.MyViewHolder> {

        private Cursor mProductCursor;
        private Context mContext;

        //private final List<String> mDataSet = new ArrayList<>();

        MyBaseAdapter(Context context) {

            this.mProductCursor = null;
            this.mContext = context;
            /*
            for (int i = 0; i < SIZE; i++)
                mDataSet.add(i, "This is row number " + i);
                */
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int position) {
            return new MyViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {

            if(mProductCursor != null) {
                mProductCursor.moveToPosition(position);
                holder.dataTextView.setText(mProductCursor.getString(mProductCursor.getColumnIndex(StoCountContract.ProductEntry.PRODUCT_NAME)));
            }
        }

        @Override
        public int getItemCount() {
            if(mProductCursor != null)
                return mProductCursor.getCount();
            else
                return 0;
        }

        public void remove(int position) {
            //mProductCursor..remove(position);
            //notifyItemRemoved(position);
        }

        public void swapCursor(Cursor cursor)
        {
            this.mProductCursor = cursor;
        }

        public void onItemclicked(int position)
        {
            if(mProductCursor != null) {
                mProductCursor.moveToPosition(position);

                Product prod = new Product(mProductCursor);

                Bundle bundle = new Bundle();
                bundle.putParcelable(DetailActivity.PRODUCT_PARCELABLE, prod);
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra(DetailActivity.PRODUCT_PARCELABLE, bundle);
                mContext.startActivity(intent);
            }
        }
        static class MyViewHolder extends RecyclerView.ViewHolder {

            TextView dataTextView;
            MyViewHolder(View view) {
                super(view);
                dataTextView = ((TextView) view.findViewById(R.id.txt_data));
            }
        }
    }

}
