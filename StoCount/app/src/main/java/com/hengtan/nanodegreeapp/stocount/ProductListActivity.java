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

    @InjectView(R.id.fabManualButton)
    protected FloatingActionButton fabManualButton;

    @InjectView(R.id.fabSearchButton)
    protected FloatingActionButton fabSearchButton;

    @InjectView(R.id.fabBarcodeButton)
    protected FloatingActionButton fabBarcodeButton;

    @InjectView(R.id.recycler_view)
    protected RecyclerView mRecyclerView;

    private SearchView searchView;

    public static final int RESULT_SETTINGS = 1;

    private ProductListAdapter adapter;

    // Identifies a particular Loader being used in this component
    private static final int PRODUCT_LOADER = 0;

    private GoogleApiClient mGoogleApiClient;

    private String mApiCode;

    private ApiCall mApiCall;

    private StockPeriod mStockPeriod;

    private MenuItem mSearchItem;

    private static final int EMPTY_VIEW = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_list_activity);
        ButterKnife.inject(this);
        mApiCode = Application.GetApiCodeFromPreference();
        mApiCall = Application.GetApiCallFromPreference(mApiCode);
        mStockPeriod = Application.getCurrentStockPeriod();

        init();

        hideShowFab();

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

    @OnClick(R.id.fabManualButton)
    public void onManualClick(View v) {
        Product prod = new Product();
        Bundle bundle = new Bundle();
        bundle.putParcelable(DetailActivity.PRODUCT_PARCELABLE, prod);
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.PRODUCT_PARCELABLE, bundle);
        this.startActivity(intent);
    }

    @OnClick(R.id.fabSearchButton)
    public void onSearchClick(View v) {
        famProductListButton.collapse();

        View actionSearch = findViewById(R.id.action_search);

        if(actionSearch instanceof ActionMenuItemView)
        {
            ((ActionMenuItemView)actionSearch).callOnClick();
        }
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

        mSearchItem = menu.findItem(R.id.action_search);

        if(!Utilities.IsConnectedToInternet(this))
        {
            mSearchItem.setVisible(false);
        }

        searchView = (SearchView) MenuItemCompat.getActionView(mSearchItem);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

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
        hideShowFab();
        RefreshApiPreference();
        getLoaderManager().restartLoader(PRODUCT_LOADER, null, this);
    }
    
    private void init() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        adapter = new ProductListAdapter(this, mStockPeriod);
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

    private void hideShowFab()
    {
        if(Utilities.IsConnectedToInternet(this))
        {
            fabSearchButton.setVisibility(View.VISIBLE);
            fabBarcodeButton.setVisibility(View.VISIBLE);

            if(mSearchItem != null)
            {
                mSearchItem.setVisible(true);
            }
        }
        else
        {
            fabSearchButton.setVisibility(View.GONE);
            fabBarcodeButton.setVisibility(View.GONE);

            if(mSearchItem != null)
            {
                mSearchItem.setVisible(false);
            }
        }
    }

    private void RefreshApiPreference()
    {
        String tmpApiPreferenceCode = Application.GetApiCodeFromPreference();

        if(!mApiCode.equals(tmpApiPreferenceCode))
        {
            mApiCode = tmpApiPreferenceCode;
            mApiCall = Application.GetApiCallFromPreference(tmpApiPreferenceCode);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

     /*
     * Takes action based on the ID of the Loader that's being created
     */
        switch (id) {
            case PRODUCT_LOADER:
                // Returns a new CursorLoader

                if(mStockPeriod == null)
                {
                    mStockPeriod = Application.getCurrentStockPeriod();
                }
                return new CursorLoader(
                        this,
                        StoCountContract.ProductEntry.buildFullProductUri(mStockPeriod.getStockPeriodId()),
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

    static class ProductListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Cursor mProductCursor;
        private Context mContext;
        private StockPeriod mStockPeriod;

        ProductListAdapter(Context context, StockPeriod stockPeriod) {
            this.mProductCursor = null;
            this.mContext = context;
            this.mStockPeriod = stockPeriod;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int position) {

            View v;

            if (position == EMPTY_VIEW) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_view, parent, false);
                EmptyViewHolder evh = new EmptyViewHolder(v);
                return evh;
            }

            return new MyViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            if(mProductCursor != null && holder instanceof MyViewHolder) {

                MyViewHolder myViewHolder = (MyViewHolder) holder;

                mProductCursor.moveToPosition(position);
                myViewHolder.dataTextView.setText(mProductCursor.getString(mProductCursor.getColumnIndex(StoCountContract.ProductEntry.PRODUCT_NAME)));
                myViewHolder.dataTextInfoView.setText(mProductCursor.getString(mProductCursor.getColumnIndex(StoCountContract.ProductEntry.ADDITIONAL_INFO)));
                myViewHolder.dataTextCount.setText(mProductCursor.getString(mProductCursor.getColumnIndex(StoCountContract.ProductCountEntry.QUANTITY)));

                String imageUrl = mProductCursor.getString(mProductCursor.getColumnIndex(StoCountContract.ProductEntry.THUMBNAIL_IMAGE));

                if(imageUrl.isEmpty())
                {
                    Glide.with(this.mContext).load(android.R.drawable.ic_menu_gallery).fitCenter().into(myViewHolder.dataImageView);
                }
                else {
                    Glide.with(this.mContext).load(imageUrl).fitCenter().into(myViewHolder.dataImageView);
                }
            }
        }

        @Override
        public int getItemCount() {
            if(mProductCursor != null)
            {
                if(mProductCursor.getCount() == 0)
                    return 1;
                else
                    return mProductCursor.getCount();
            }
            else
                return 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (mProductCursor != null && mProductCursor.getCount() == 0) {
                return EMPTY_VIEW;
            }
            return super.getItemViewType(position);
        }

        public void remove(int position) {

            mProductCursor.moveToPosition(position);

            Product prod = new Product(mProductCursor);

            DBAsyncTask deleteAsyncTask = new DBAsyncTask(mContext.getContentResolver(), DBAsyncTask.ObjectType.PRODUCT, DBAsyncTask.OperationType.DELETE, null);
            deleteAsyncTask.execute(prod);

            ((Activity)mContext).getLoaderManager().restartLoader(PRODUCT_LOADER, null, (ProductListActivity)mContext);

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

                Product prod = new Product(mProductCursor);
                ProductCount prodCount = new ProductCount(mProductCursor);

                Bundle bundle = new Bundle();
                bundle.putParcelable(DetailActivity.PRODUCT_PARCELABLE, prod);

                if(prodCount.getProductCountId() != null)
                    bundle.putParcelable(DetailActivity.PRODUCT_COUNT_PARCELABLE, prodCount);

                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra(DetailActivity.PRODUCT_PARCELABLE, bundle);
                mContext.startActivity(intent);

            }
        }

        static class EmptyViewHolder extends RecyclerView.ViewHolder {
            public EmptyViewHolder(View itemView) {
                super(itemView);
            }
        }

        static class MyViewHolder extends RecyclerView.ViewHolder {

            ImageView dataImageView;
            TextView dataTextView;
            TextView dataTextInfoView;
            TextView dataTextCount;

            MyViewHolder(View view) {
                super(view);
                dataTextView = ((TextView) view.findViewById(R.id.txt_data));
                dataTextInfoView = ((TextView) view.findViewById(R.id.txt_datainfo));
                dataTextCount = ((TextView) view.findViewById(R.id.txt_datacount));
                dataImageView = ((ImageView) view.findViewById(R.id.img_data));
            }
        }
    }

}
