package com.hengtan.nanodegreeapp.stocount;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.internal.view.menu.ActionMenuItemView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
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
import com.hengtan.nanodegreeapp.stocount.data.DbImportExport;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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

    @InjectView(R.id.stockPeriodSpinner)
    protected Spinner mStockPeriodSpinner;

    @InjectView(R.id.recycler_view)
    protected RecyclerView mRecyclerView;

    @InjectView(R.id.progressBarHolder)
    protected FrameLayout progressBarHolder;

    private WebView mWebView;

    private SearchView searchView;

    //public static final int RESULT_SETTINGS = 1;

    private ProductListAdapter adapter;

    private StockPeriodSpinnerAdapter spinnerAdapter;

    // Identifies a particular Loader being used in this component
    private static final int PRODUCT_LOADER = 0;

    private static final int PREVIOUS_STOCK_LOADER = 1;

    private static final String SELECTED_STOCKPERIOD_KEY = "SELECTEDSTOCKPERIODKEY";


    private String mApiCode;

    private ApiCall mApiCall;

    private StockPeriod mCurrentStockPeriod;

    private StockPeriod mSelectedStockPeriod;

    private MenuItem mSearchItem;

    private static final int EMPTY_VIEW = 10;

    private String mErrorBarcodeStr;
    private String mCancelledBarcodeStr;
    private String mScannedBarcodeStr;

    private SwipeToDismissTouchListener<RecyclerViewAdapter> mTouchListener;

    private boolean mIsTouchListenerRemoved = true;
    private boolean mShowSearchItem = false;
    private int mPreviousSelectedStockPeriodPosition = -1;
    private int mSavedSelectedStockPeriodPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_list_activity);
        ButterKnife.inject(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mApiCode = Application.GetApiCodeFromPreference();
        mApiCall = Application.GetApiCallFromPreference(mApiCode);
        mCurrentStockPeriod = Application.getCurrentStockPeriod();

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_STOCKPERIOD_KEY)) {
            mSavedSelectedStockPeriodPosition = savedInstanceState.getInt(SELECTED_STOCKPERIOD_KEY,-1);
            mPreviousSelectedStockPeriodPosition = -1;
        }

        init();

        hideShowFab();

        getLoaderManager().restartLoader(PREVIOUS_STOCK_LOADER, null, this);

        Resources res = getResources();

        mErrorBarcodeStr = res.getString(R.string.error_barcode_text);
        mCancelledBarcodeStr = res.getString(R.string.cancelled_barcode_text);
        mScannedBarcodeStr = res.getString(R.string.scanned_barcode_log_text);
    }

    @OnClick(R.id.fabManualButton)
    public void onManualClick(View v) {

        famProductListButton.collapse();

        mShowSearchItem = false;
        invalidateOptionsMenu();

        Product prod = new Product(getResources());
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
            if(mShowSearchItem == false) {
                mShowSearchItem = true;
                invalidateOptionsMenu();
            }
            else {
                ((ActionMenuItemView) actionSearch).callOnClick();
            }
        }
    }

    @OnClick(R.id.fabBarcodeButton)
    public void onBarcodeClick(View v) {
        famProductListButton.collapse();

        mShowSearchItem = false;
        invalidateOptionsMenu();

        try {
            IntentIntegrator intentIntegrator = new IntentIntegrator(ProductListActivity.this);
            //intentIntegrator.setOrientationLocked(false);
            intentIntegrator.initiateScan();
        } catch (Exception ex) {
            Log.e(TAG, mErrorBarcodeStr + ex.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.product_list_menu, menu);

        mSearchItem = menu.findItem(R.id.action_search);

        //if(!Utilities.IsConnectedToInternet(this))
        //{
        //    mSearchItem.setVisible(false);
        //}

        MenuItem printMenuItem = menu.findItem(R.id.action_print);

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            printMenuItem.setVisible(false);
        }

        searchView = (SearchView) MenuItemCompat.getActionView(mSearchItem);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        //searchView.setIconified(false);

        searchView.setOnSuggestionListener(this);
        /*searchView.setOnQueryTextListener(
                new

        );*/

        if(!mShowSearchItem) {
            mSearchItem.setEnabled(false);
        } else
        {
            mSearchItem.expandActionView();
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_print:
                PrintStockSheet();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void PrintStockSheet() {
        // Create a WebView object specifically for printing
        WebView webView = new WebView(this);
        webView.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.i(TAG, "page finished loading " + url);
                createPrintJob(view);
                mWebView = null;
            }
        });

        String currentHeaderFormatString = "Current Period Starting From %1$s";
        String previousHeaderFormatString = "Previous Stock Period From %1$s To %2$s";
        String row = "<tr><td><img src=\"%1$s\" width=100 height=100/></td><td>%2$s</td><td>%3$s</td><td>%4$s</td></tr>";
        String header = "";

        if(mSelectedStockPeriod.getEndDate() == null)
        {
            header = String.format(currentHeaderFormatString,mSelectedStockPeriod.DateFormat.format(mSelectedStockPeriod.getStartDate()));
        }
        else
        {
            header = String.format(previousHeaderFormatString,mSelectedStockPeriod.DateFormat.format(mSelectedStockPeriod.getStartDate()), mSelectedStockPeriod.DateFormat.format(mSelectedStockPeriod.getEndDate()));
        }
        
        // Generate an HTML document on the fly:
        String htmlDocument = "<html><body><h1>"+header+"</h1><p> ";

        htmlDocument += "<table><th colspan=2>Product Name</th><th>Additional Info</th><th>Stock Count</th>";

        if(adapter.mProductCursor != null)
        {
            if(adapter.mProductCursor.moveToFirst()) {
                do {
                        htmlDocument += String.format(row,
                                adapter.mProductCursor.getString(adapter.mProductCursor.getColumnIndex(StoCountContract.ProductEntry.THUMBNAIL_IMAGE)),
                                adapter.mProductCursor.getString(adapter.mProductCursor.getColumnIndex(StoCountContract.ProductEntry.PRODUCT_NAME)),
                                adapter.mProductCursor.getString(adapter.mProductCursor.getColumnIndex(StoCountContract.ProductEntry.ADDITIONAL_INFO)),
                                adapter.mProductCursor.getString(adapter.mProductCursor.getColumnIndex(StoCountContract.ProductCountEntry.QUANTITY))

                        );

                }while(adapter.mProductCursor.moveToNext());
            }
        }

        htmlDocument += "</table></p></body></html>";
        String imageDirectory ="file:///"+ Environment.getExternalStorageDirectory()+"/"+ DbImportExport.DEFAULT_IMAGE_BACKUP_DIRECTORY;

        webView.loadDataWithBaseURL(imageDirectory, htmlDocument, "text/HTML", "UTF-8", null);

        // Keep a reference to WebView object until you pass the PrintDocumentAdapter
        // to the PrintManager
        mWebView = webView;

    }

    private void createPrintJob(WebView webView) {

        // Get a PrintManager instance
        PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);

        // Get a print adapter instance
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter();

        // Create a print job with name and adapter instance
        String jobName = getString(R.string.app_name) + " Document";
        PrintJob printJob = printManager.print(jobName, printAdapter,new PrintAttributes.Builder().build());

        // Save the job object for later status checking
        //mPrintJobs.add(printJob);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {

            final String barcodeScanResult = result.getContents();

            if (barcodeScanResult == null) {
                Log.d(TAG, mCancelledBarcodeStr);
                Toast.makeText(this, mCancelledBarcodeStr, Toast.LENGTH_LONG).show();
            } else {
                Log.d(TAG, mScannedBarcodeStr + barcodeScanResult);

                String formatNameResult = result.getFormatName();
                String formatTypeResult = result.getType();

                mApiCall.SearchProduct(barcodeScanResult, formatNameResult, null, this);

                //SearchProductFromAmazonApi(barcodeScanResult, formatNameResult, formatTypeResult);
                //SearchProductFromWalmartAPI(barcodeScanResult,null);
            }
        } else {
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

        if(mSelectedStockPeriod != null && mSelectedStockPeriod.getStockPeriodId() == mCurrentStockPeriod.getStockPeriodId()) {
            getLoaderManager().restartLoader(PRODUCT_LOADER, null, this);
        }
    }
    
    private void init() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        adapter = new ProductListAdapter(this, mCurrentStockPeriod);
        mRecyclerView.setAdapter(adapter);

        mTouchListener =
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

                            }
                        });

        mRecyclerView.addOnItemTouchListener(new SwipeableItemClickListener(this,
                new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (view.getId() == R.id.txt_delete) {
                            mTouchListener.processPendingDismisses();
                        } else if (view.getId() == R.id.txt_undo) {
                            mTouchListener.undoPendingDismiss();
                        } else { // R.id.txt_data
                            adapter.onItemclicked(view, position);
                        }
                    }
                }));

        spinnerAdapter = new StockPeriodSpinnerAdapter(this, mCurrentStockPeriod);

        mStockPeriodSpinner.setAdapter(spinnerAdapter);

        mStockPeriodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * Called when a new item was selected (in the Spinner)
             */
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int pos, long id) {
                mSelectedStockPeriod = (StockPeriod) parent.getItemAtPosition(pos);

                if (mSelectedStockPeriod != null) {
                    if (mSelectedStockPeriod.getStockPeriodId() == mCurrentStockPeriod.getStockPeriodId()) {
                        famProductListButton.setVisibility(View.VISIBLE);
                    } else {
                        if (mShowSearchItem) {
                            mShowSearchItem = false;
                            invalidateOptionsMenu();
                        }

                        famProductListButton.setVisibility(View.GONE);
                    }

                    if (mPreviousSelectedStockPeriodPosition != pos) {

                        //ProgressBarHelper.ShowProgressBar(progressBarHolder);
                        mPreviousSelectedStockPeriodPosition = pos;
                        getLoaderManager().restartLoader(PRODUCT_LOADER, null, ProductListActivity.this);
                    }
                }
            }


            public void onNothingSelected(AdapterView parent) {
                // Do nothing.
            }
        });
    }

    private void removeRecyclerViewTouchListener()
    {
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        mRecyclerView.setOnScrollListener(null);


        mIsTouchListenerRemoved = true;
    }

    private void setRecyclerViewTouchListener()
    {
        mRecyclerView.setOnTouchListener(mTouchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        mRecyclerView.setOnScrollListener((RecyclerView.OnScrollListener) mTouchListener.makeScrollListener());

        mIsTouchListenerRemoved = false;
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

                if(mSelectedStockPeriod == null)
                {
                    mSelectedStockPeriod = Application.getCurrentStockPeriod();
                }

                if(mCurrentStockPeriod.getStockPeriodId() == mSelectedStockPeriod.getStockPeriodId()) {
                    return new CursorLoader(
                            this,
                            StoCountContract.ProductEntry.buildCurrentProductUri(mSelectedStockPeriod.getStockPeriodId()),
                            null,
                            null,
                            null,
                            null
                    );
                }
                else
                {
                    return new CursorLoader(
                            this,
                            StoCountContract.ProductEntry.buildPreviousProductUri(mSelectedStockPeriod.getStockPeriodId()),
                            null,
                            null,
                            null,
                            null
                    );
                }

            case PREVIOUS_STOCK_LOADER:
                return new CursorLoader(
                        this,
                        StoCountContract.StockPeriodEntry.PREVIOUS_CONTENT_URI,
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

        switch (loader.getId())
        {
            case PRODUCT_LOADER:

                if(data.getCount() == 0 && mCurrentStockPeriod.getStockPeriodId() != mSelectedStockPeriod.getStockPeriodId() && mIsTouchListenerRemoved == false) {
                    removeRecyclerViewTouchListener();
                }
                else if(data.getCount() > 0 && mSelectedStockPeriod !=null && mCurrentStockPeriod.getStockPeriodId() == mSelectedStockPeriod.getStockPeriodId() && mIsTouchListenerRemoved == true)
                {
                    setRecyclerViewTouchListener();
                }

                //ProgressBarHelper.HideProgressBar(progressBarHolder);

                adapter.swapCursor(data, mSelectedStockPeriod);
                adapter.notifyDataSetChanged();

                break;
            case PREVIOUS_STOCK_LOADER:
                spinnerAdapter.swapCursor(data);
                spinnerAdapter.notifyDataSetChanged();

                if(mSavedSelectedStockPeriodPosition != -1)
                {
                    //fix recycler view not refresh when user select index == 0
                    if(mSavedSelectedStockPeriodPosition == 0 && mStockPeriodSpinner.getSelectedItemPosition() ==0)
                    {
                        //ProgressBarHelper.ShowProgressBar(progressBarHolder);
                        mPreviousSelectedStockPeriodPosition = mSavedSelectedStockPeriodPosition;
                        mSelectedStockPeriod = (StockPeriod) spinnerAdapter.getItem(mSavedSelectedStockPeriodPosition);
                        getLoaderManager().restartLoader(PRODUCT_LOADER, null, ProductListActivity.this);
                    }
                    else {
                        mStockPeriodSpinner.setSelection(mSavedSelectedStockPeriodPosition);
                    }
                }
                else {
                    mStockPeriodSpinner.setSelection(spinnerAdapter.getCount() - 1);
                }

                break;
        }
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

    @Override
    public void onSaveInstanceState(Bundle outState) {

        if (mPreviousSelectedStockPeriodPosition != -1) {
            outState.putInt(SELECTED_STOCKPERIOD_KEY, mPreviousSelectedStockPeriodPosition);
        }
        super.onSaveInstanceState(outState);
    }

    static class ProductListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Cursor mProductCursor;
        private Context mContext;
        private StockPeriod mCurrentStockPeriod;
        private StockPeriod mSelectedStockPeriod;
        private GlideLoaderListener<String, GlideDrawable> mGlideListener;

        ProductListAdapter(Context context, StockPeriod stockPeriod) {
            this.mProductCursor = null;
            this.mContext = context;
            this.mCurrentStockPeriod = stockPeriod;
            this.mGlideListener = new GlideLoaderListener<String, GlideDrawable>(mContext, android.R.drawable.ic_menu_gallery, null);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int position) {

            View v;

            if (position == EMPTY_VIEW) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_view, parent, false);

                SetEmpytViewText(v);

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
                myViewHolder.dataTextView.setContentDescription(mProductCursor.getString(mProductCursor.getColumnIndex(StoCountContract.ProductEntry.PRODUCT_NAME)));

                myViewHolder.dataTextInfoView.setText(mProductCursor.getString(mProductCursor.getColumnIndex(StoCountContract.ProductEntry.ADDITIONAL_INFO)));
                myViewHolder.dataTextInfoView.setContentDescription(mProductCursor.getString(mProductCursor.getColumnIndex(StoCountContract.ProductEntry.ADDITIONAL_INFO)));

                myViewHolder.dataTextCount.setText(mProductCursor.getString(mProductCursor.getColumnIndex(StoCountContract.ProductCountEntry.QUANTITY)));
                myViewHolder.dataTextCount.setContentDescription(mProductCursor.getString(mProductCursor.getColumnIndex(StoCountContract.ProductCountEntry.QUANTITY)));

                String imageUrl = mProductCursor.getString(mProductCursor.getColumnIndex(StoCountContract.ProductEntry.THUMBNAIL_IMAGE));

                if(imageUrl.isEmpty())
                {
                    Glide.with(this.mContext).load(android.R.drawable.ic_menu_gallery).fitCenter().into(myViewHolder.dataImageView);
                }
                else {
                    Glide.with(this.mContext).load(imageUrl).listener(this.mGlideListener).fitCenter().into(myViewHolder.dataImageView);
                }
            }
            else if(position == 0 && holder.itemView.findViewById(R.id.empty_view) != null)
            {
                SetEmpytViewText(holder.itemView);
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

            if(mCurrentStockPeriod.getStockPeriodId() == mSelectedStockPeriod.getStockPeriodId()) {
                mProductCursor.moveToPosition(position);

                Product prod = new Product(mProductCursor);

                DBAsyncTask deleteAsyncTask = new DBAsyncTask(mContext.getContentResolver(), DBAsyncTask.ObjectType.PRODUCT, DBAsyncTask.OperationType.DELETE, null);
                deleteAsyncTask.execute(prod);

                ((Activity) mContext).getLoaderManager().restartLoader(PRODUCT_LOADER, null, (ProductListActivity) mContext);

                notifyItemRemoved(position);
            }
            else
            {
                Resources res = mContext.getResources();
                Toast.makeText(mContext, res.getString(R.string.unable_delete_previous_stock_toast), Toast.LENGTH_LONG).show();
            }

        }

        public void swapCursor(Cursor cursor, StockPeriod stockPeriod)
        {
            this.mProductCursor = cursor;
            this.mSelectedStockPeriod = stockPeriod;
        }

        public void onItemclicked(View view, int position)
        {
            if(mProductCursor != null) {

                if(position ==0 && mProductCursor.getCount() ==0)
                {
                    //Do nothing because user is tapping default empty view for list
                }
                else {
                    mProductCursor.moveToPosition(position);

                    Product prod = new Product(mProductCursor);
                    ProductCount prodCount = new ProductCount(mProductCursor);

                    Bundle bundle = new Bundle();
                    bundle.putParcelable(DetailActivity.PRODUCT_PARCELABLE, prod);

                    if (prodCount.getProductCountId() != null)
                        bundle.putParcelable(DetailActivity.PRODUCT_COUNT_PARCELABLE, prodCount);

                    Intent intent = new Intent(mContext, DetailActivity.class);
                    intent.putExtra(DetailActivity.PRODUCT_PARCELABLE, bundle);


                    if (mCurrentStockPeriod.getStockPeriodId() != mSelectedStockPeriod.getStockPeriodId()) {
                        intent.putExtra(DetailActivity.IS_PREVIOUS_STOCK_PERIOD, true);
                    } else {
                        intent.putExtra(DetailActivity.IS_PREVIOUS_STOCK_PERIOD, false);
                    }

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                        ImageView productImage = null;

                        if(view instanceof ImageView)
                        {
                            productImage = (ImageView) view;
                        }
                        else
                        {
                            if(view.getParent() != null && view.getParent().getParent() != null)
                            {
                                View temp = ((View)view.getParent().getParent()).findViewById(R.id.img_data);

                                if(temp instanceof ImageView)
                                {
                                    productImage = (ImageView) temp;
                                }
                            }
                        }

                        if(productImage != null)
                        {
                            productImage.setTransitionName("photo");
                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(((Activity)mContext), productImage, "photo");
                            mContext.startActivity(intent, options.toBundle());

                        }
                        else
                        {
                            mContext.startActivity(intent);
                        }
                       }
                    else
                    {
                        mContext.startActivity(intent);
                    }
                }
            }
        }

        private void SetEmpytViewText(View v)
        {
            TextView emptyViewText = (TextView) v.findViewById(R.id.empty_view);
            Resources res = mContext.getResources();

            if(mSelectedStockPeriod != null && mCurrentStockPeriod.getStockPeriodId() != mSelectedStockPeriod.getStockPeriodId())
            {
                emptyViewText.setText(res.getString(R.string.cd_empty_view_no_count_list_item));
            }
            else
            {
                emptyViewText.setText(res.getString(R.string.cd_empty_view_list_item));
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


class StockPeriodSpinnerAdapter extends BaseAdapter
{
    private Cursor mStockPeriodCursor;
    private Context mContext;
    private StockPeriod mCurrentPeriod;
    private LayoutInflater mInflater;
    private String mPreviouStockDateText;
    private String mCurrentStockDateText;

    StockPeriodSpinnerAdapter(Context context, StockPeriod stockPeriod) {
        this.mStockPeriodCursor = null;
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mCurrentPeriod = stockPeriod;

        Resources res = this.mContext.getResources();
        mPreviouStockDateText = res.getString(R.string.preivous_stock_period_spinner_item_text);
        mCurrentStockDateText = res.getString(R.string.current_stock_period_spinner_item_text);

    }
    public void swapCursor(Cursor cursor)
    {
        this.mStockPeriodCursor = cursor;
    }


    @Override
    public int getCount() {
        if(mStockPeriodCursor != null)
        {
            return mStockPeriodCursor.getCount() + 1;
        }
        else
            return 1;
    }

    @Override
    public Object getItem(int i) {

        if (mStockPeriodCursor != null) {

            if(mStockPeriodCursor.getCount() > i) {
                mStockPeriodCursor.moveToPosition(i);
                return new StockPeriod(mStockPeriodCursor);
            }
            else
            {
                return mCurrentPeriod;
            }
        }
        else
            return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder=null;

        if(view==null ||  view.getTag() == null ) {
            view = mInflater.inflate(R.layout.stock_period_spinner_item,viewGroup,false);

            holder = new ViewHolder();

            holder.txtStockPeriodDate=(TextView)view.findViewById(R.id.stockPeriodSpinnerItemDate);

            if(i==0)
            {
                view.setTag(holder);
            }

        }
        else
        {
            holder = (ViewHolder) view.getTag();
        }

        if (mStockPeriodCursor != null) {
            if(mStockPeriodCursor.getCount() > i) {
                mStockPeriodCursor.moveToPosition(i);
                StockPeriod sp = new StockPeriod(mStockPeriodCursor);
                holder.txtStockPeriodDate.setText(String.format(mPreviouStockDateText,sp.DateFormat.format(sp.getStartDate()), sp.DateFormat.format(sp.getEndDate())));
            }
            else
            {
                holder.txtStockPeriodDate.setText(mCurrentStockDateText+" "+mCurrentPeriod.DateFormat.format(mCurrentPeriod.getStartDate()));
            }
        }

        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        View view = super.getDropDownView(position, convertView, parent);
        TextView textView = (TextView) view.findViewById(R.id.stockPeriodSpinnerItemDate);
        textView.setPadding(16,0,0,0);
        return view;
    }
}

class ViewHolder
{
    TextView txtStockPeriodDate;
}

