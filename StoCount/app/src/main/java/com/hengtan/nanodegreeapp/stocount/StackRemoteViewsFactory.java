package com.hengtan.nanodegreeapp.stocount;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.AppWidgetTarget;
import com.hengtan.nanodegreeapp.stocount.data.StoCountContract;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    public static final String LOG_TAG = StackRemoteViewsFactory.class.getSimpleName();
    private int mProductCount = 0;
    private List<Product> mWidgetItems = new ArrayList<Product>();
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

        Cursor mCursor = mContext.getContentResolver().query(
                StoCountContract.ProductEntry.CONTENT_URI,   // The content URI of the words table
                null,                        // The columns to return for each row
                null,                    // Selection criteria
                null,                     // Selection criteria
                null);

        if(mCursor != null) {
            while(mCursor.moveToNext()) {

                mWidgetItems.add(new Product(mCursor));

                mProductCount++;
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
            rv.setTextViewText(R.id.txt_datacount, "5");
            rv.setContentDescription(R.id.txt_datacount, "5");

            try {
                URL url = new URL(mWidgetItems.get(position).getThumbnailImage());
                Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                rv.setImageViewBitmap(R.id.img_data, bmp);
            }
            catch (Exception ex)
            {
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