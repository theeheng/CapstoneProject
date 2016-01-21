package com.hengtan.nanodegreeapp.stocount.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.hengtan.nanodegreeapp.stocount.DetailActivity;
import com.hengtan.nanodegreeapp.stocount.R;
import com.hengtan.nanodegreeapp.stocount.data.Product;
import com.hengtan.nanodegreeapp.stocount.data.StoCountContract;
import com.hengtan.nanodegreeapp.stocount.data.StockPeriod;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    public static final String LOG_TAG = StackRemoteViewsFactory.class.getSimpleName();
    private int mProductCount = 0;
    private List<Product> mWidgetItems = new ArrayList<Product>();
    private List<String> mWidgetItemStockCounts = new ArrayList<String>();
    private Context mContext;
    private int mAppWidgetId;
    private static int counter = 0;
    public StackRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    private void setupWidgetData()
    {
        mProductCount = 0;
        StockPeriod stockPeriod = null;

        Cursor cursor = mContext.getContentResolver().query(
                StoCountContract.StockPeriodEntry.CONTENT_URI,   // The content URI of the words table
                null,                        // The columns to return for each row
                StoCountContract.StockPeriodEntry.END_DATE+" is null",                    // Selection criteria
                null,                     // Selection criteria
                null);

        if(cursor != null) {
            if(cursor.moveToFirst())
            {
                stockPeriod = new StockPeriod(cursor);
            }

            cursor.close();
        }

        if(stockPeriod != null) {
            cursor = mContext.getContentResolver().query(
                    StoCountContract.ProductEntry.buildCurrentProductUri(stockPeriod.getStockPeriodId()),   // The content URI of the words table
                    null,                        // The columns to return for each row
                    null,                    // Selection criteria
                    null,                     // Selection criteria
                    null);

            if (cursor != null) {
                while (cursor.moveToNext()) {

                    mWidgetItems.add(new Product(cursor));
                    mWidgetItemStockCounts.add(cursor.getString(cursor.getColumnIndex(StoCountContract.ProductCountEntry.QUANTITY)));
                    mProductCount++;
                }

                cursor.close();
            }
        }
        counter ++;
    }

    public void onCreate() {
    }

    public void onDestroy() {
        mWidgetItems.clear();
    }

    public int getCount() {
        return mProductCount;
    }

    public RemoteViews getViewAt(int position) {

        if(mWidgetItems.size() > position) {

            RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);
            rv.setTextViewText(R.id.txt_data, mWidgetItems.get(position).getName());
            rv.setContentDescription(R.id.txt_data, mWidgetItems.get(position).getName());
            rv.setTextViewText(R.id.txt_datainfo, mWidgetItems.get(position).getAdditionalInfo());
            rv.setContentDescription(R.id.txt_datainfo, mWidgetItems.get(position).getAdditionalInfo());
            rv.setTextViewText(R.id.txt_datacount, mWidgetItemStockCounts.get(position));
            rv.setContentDescription(R.id.txt_datacount, mWidgetItemStockCounts.get(position));

            try {

                String imagePath = mWidgetItems.get(position).getThumbnailImage();
                Bitmap bmp = null;

                if(imagePath != null && (!imagePath.isEmpty()) && imagePath.indexOf("http") > -1)
                {
                    URL url = new URL(imagePath);
                    bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                }
                else if(imagePath != null && (!imagePath.isEmpty()))
                {
                    Uri url = Uri.fromFile(new File(imagePath));
                    bmp = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(url));
                }

                if(bmp != null) {
                    rv.setImageViewBitmap(R.id.img_data, bmp);
                }

            }
            catch (Exception ex)
            {
                rv.setImageViewResource(R.id.img_data, android.R.drawable.ic_menu_gallery);
                String err = ex.getMessage();
            }
            //rv.setImageViewUri(R.id.img_data, Uri.parse(mWidgetItems.get(position).getThumbnailImage()));

            //Decorative Graphic Content Description to null
            rv.setContentDescription(R.id.img_data, null);

            Bundle extras = new Bundle();
            extras.putParcelable(DetailActivity.PRODUCT_PARCELABLE, mWidgetItems.get(position));
            Intent fillInIntent = new Intent();
            fillInIntent.putExtra(DetailActivity.PRODUCT_PARCELABLE, extras);
            rv.setOnClickFillInIntent(R.id.widget_linear_layout, fillInIntent);

            // Return the remote views object.
            return rv;
        }
        else
        {
            return null;
        }
    }

    public RemoteViews getLoadingView() {
        return null;
    }

    public int getViewTypeCount() {
        return 1;
    }

    public long getItemId(int position) {
        return position;
    }

    public boolean hasStableIds() {
        return true;
    }

    public void onDataSetChanged() {
        setupWidgetData();
    }
}