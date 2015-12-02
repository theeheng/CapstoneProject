package com.hengtan.nanodegreeapp.stocount;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.hengtan.nanodegreeapp.stocount.api.ApiCall;
import com.hengtan.nanodegreeapp.stocount.data.DBAsyncTask;
import com.hengtan.nanodegreeapp.stocount.data.Product;
import com.hengtan.nanodegreeapp.stocount.data.ProductCount;
import com.hengtan.nanodegreeapp.stocount.data.StoCountContract;
import com.hengtan.nanodegreeapp.stocount.data.StockPeriod;
import com.hudomju.swipe.OnItemClickListener;
import com.hudomju.swipe.SwipeToDismissTouchListener;
import com.hudomju.swipe.SwipeableItemClickListener;
import com.hudomju.swipe.adapter.RecyclerViewAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import android.app.LoaderManager;

public class ProductListActivity extends AppCompatActivity implements SearchView.OnSuggestionListener, LoaderManager.LoaderCallbacks<Cursor>, GoogleApiClient.OnConnectionFailedListener {

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
    private static final int PRODUCT_LOADER = 0;

    private static final int PRODUCT_COUNT_LOADER = 1;

    private GoogleApiClient mGoogleApiClient;

    private ApiCall mApiCall;

    private StockPeriod mStockPeriod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_list_activity);
        ButterKnife.inject(this);

        mApiCall = Application.GetApiCallFromPreference();
        mStockPeriod = Application.getCurrentStockPeriod();

        init();

        getLoaderManager().restartLoader(PRODUCT_LOADER, null, this);

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
        if (id == R.id.action_logout) {
            Application.Logout(mGoogleApiClient, this);
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

                mApiCall.SearchProduct(barcodeScanResult, formatNameResult, null, this);

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
        getLoaderManager().restartLoader(PRODUCT_LOADER, null, this);
    }
    
    private void init() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        adapter = new MyBaseAdapter(this, mStockPeriod);
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
            case PRODUCT_LOADER:
                // Returns a new CursorLoader
                return new CursorLoader(
                        this,
                        StoCountContract.ProductEntry.buildFullProductUri(1),
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
        //adapter.swapCursor(null);
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

            mApiCall.SearchProduct(null, null, Integer.toString(suggestionItemId), this);
            //SearchProductFromWalmartAPI(null, Integer.toString(suggestionItemId));

            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    static class MyBaseAdapter extends RecyclerView.Adapter<MyBaseAdapter.MyViewHolder> implements LoaderManager.LoaderCallbacks<Cursor> {

        private Cursor mProductCursor;
        private Context mContext;
        private StockPeriod mStockPeriod;
        private Product mProduct;
        private ProductCount mProductCount;

        MyBaseAdapter(Context context, StockPeriod stockPeriod) {
            this.mProductCursor = null;
            this.mContext = context;
            this.mStockPeriod = stockPeriod;
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
                holder.dataTextInfoView.setText(mProductCursor.getString(mProductCursor.getColumnIndex(StoCountContract.ProductEntry.ADDITIONAL_INFO)));
                Glide.with(this.mContext).load(mProductCursor.getString(mProductCursor.getColumnIndex(StoCountContract.ProductEntry.THUMBNAIL_IMAGE))).fitCenter().into(holder.dataImageView);
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

            mProductCursor.moveToPosition(position);

            Product prod = new Product(mProductCursor);

            DBAsyncTask deleteAsyncTask = new DBAsyncTask(mContext, mContext.getContentResolver(), DBAsyncTask.ObjectType.PRODUCT, DBAsyncTask.OperationType.DELETE);
            deleteAsyncTask.execute(prod);

            ((Activity)mContext).getLoaderManager().restartLoader(PRODUCT_LOADER, null, this);

            notifyItemRemoved(position);

        }

        public void swapCursor(Cursor cursor)
        {
            this.mProductCursor = cursor;
        }

        public void onItemclicked(int position)
        {
            if(mProductCursor != null) {
                mProductCursor.moveToPosition(position);

                mProduct = new Product(mProductCursor, true);

                Bundle bundle = new Bundle();
                bundle.putParcelable(DetailActivity.PRODUCT_PARCELABLE, mProduct);

                if(mProduct.getProductCount() != null && mProduct.getProductCount().getProductId() == 0)
                    bundle.putParcelable(DetailActivity.PRODUCT_COUNT_PARCELABLE,mProduct.getProductCount());

                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra(DetailActivity.PRODUCT_PARCELABLE, bundle);
                mContext.startActivity(intent);

                //((Activity)mContext).getLoaderManager().restartLoader(PRODUCT_COUNT_LOADER, null, this);

                //ProductCount prodCount = null;

                //Cursor countCursor;

                /*countCursor= mContext.getContentResolver().query(
                        StoCountContract.ProductCountEntry.CONTENT_URI,
                        null, // leaving "columns" null just returns all the columns.
                        StoCountContract.ProductCountEntry.STOCK_PERIOD_ID + " = ? AND "+StoCountContract.ProductCountEntry.PRODUCT_ID + " = ? ",
                        new String[] {mStockPeriod.getStockPeriodId().toString(), prod.getProductId().toString()},
                        null  // sort order
                );
                */

            }
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
            /*
             * Takes action based on the ID of the Loader that's being created
             */
            switch (id) {
                case PRODUCT_LOADER:
                    // Returns a new CursorLoader
                    return new CursorLoader(
                            mContext,
                            StoCountContract.ProductEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null
                    );
                case PRODUCT_COUNT_LOADER:
                    // Returns a new CursorLoader
                    return new CursorLoader(
                            mContext,
                            StoCountContract.ProductCountEntry.CONTENT_URI,
                            null,
                            StoCountContract.ProductCountEntry.STOCK_PERIOD_ID + " = ? AND "+StoCountContract.ProductCountEntry.PRODUCT_ID + " = ? ",
                            new String[] {mStockPeriod.getStockPeriodId().toString(), mProduct.getProductId().toString()},
                            null
                    );

                default:
                    // An invalid id was passed in
                    return null;
            }
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

            switch(loader.getId())
            {
                case PRODUCT_LOADER :

                    swapCursor(cursor);
                    notifyDataSetChanged();

                    break;

                case PRODUCT_COUNT_LOADER :

                    if(cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        mProductCount = new ProductCount(cursor);
                    }
                    else
                    {
                        mProductCount = null;
                    }

                    Bundle bundle = new Bundle();
                    bundle.putParcelable(DetailActivity.PRODUCT_PARCELABLE, mProduct);

                    if(mProductCount != null)
                        bundle.putParcelable(DetailActivity.PRODUCT_COUNT_PARCELABLE,mProductCount);

                    Intent intent = new Intent(mContext, DetailActivity.class);
                    intent.putExtra(DetailActivity.PRODUCT_PARCELABLE, bundle);
                    mContext.startActivity(intent);

                    break;
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }

        static class MyViewHolder extends RecyclerView.ViewHolder {

            ImageView dataImageView;
            TextView dataTextView;
            TextView dataTextInfoView;

            MyViewHolder(View view) {
                super(view);
                dataTextView = ((TextView) view.findViewById(R.id.txt_data));
                dataTextInfoView = ((TextView) view.findViewById(R.id.txt_datainfo));
                dataImageView =  ((ImageView) view.findViewById(R.id.img_data));
            }
        }
    }

}
