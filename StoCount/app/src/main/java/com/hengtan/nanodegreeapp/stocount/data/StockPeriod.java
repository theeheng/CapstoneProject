package com.hengtan.nanodegreeapp.stocount.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by hengtan on 29/11/2015.
 */
public class StockPeriod implements Parcelable, Cloneable {

    private Integer mStockPeriodId;
    private Date mStartDate;
    private Date mEndDate;

    private List<String> mParcelableString;

    public final SimpleDateFormat DateFormat = new SimpleDateFormat("dd-MM-yyyy");

    public StockPeriod()
    {}

    public StockPeriod(Cursor cursor) {

        try {
            this.mStockPeriodId = cursor.getInt(cursor.getColumnIndex(StoCountContract.StockPeriodEntry._ID));
            this.mStartDate = DateFormat.parse(cursor.getString(cursor.getColumnIndex(StoCountContract.StockPeriodEntry.START_DATE)));
            this.mEndDate = DateFormat.parse(cursor.getString(cursor.getColumnIndex(StoCountContract.StockPeriodEntry.END_DATE)));
        }
        catch (Exception ex)
        {

        }

    }

    private enum StockPeriodIndex
    {
        STOCK_PERIOD_ID(0), START_DATE(1), END_DATE(2);

        private int value;

        private StockPeriodIndex(int value)
        {
            this.value = value;
        }
    };



    public StockPeriod(Parcel in)
    {
        mParcelableString = new ArrayList<String>();
        in.readStringList(this.mParcelableString);

        if(!mParcelableString.get(StockPeriodIndex.STOCK_PERIOD_ID.ordinal()).isEmpty())
        {
            this.mStockPeriodId = Integer.parseInt(mParcelableString.get(StockPeriodIndex.STOCK_PERIOD_ID.ordinal()));
        }
        else
        {
            this.mStockPeriodId = null;
        }

        try {

            if(!mParcelableString.get(StockPeriodIndex.START_DATE.ordinal()).isEmpty())
                this.mStartDate = DateFormat.parse(mParcelableString.get(StockPeriodIndex.START_DATE.ordinal()));
            else
                this.mStartDate = null;

            if(!mParcelableString.get(StockPeriodIndex.END_DATE.ordinal()).isEmpty())
                this.mEndDate = DateFormat.parse(mParcelableString.get(StockPeriodIndex.END_DATE.ordinal()));
            else
                this.mEndDate = null;

        }catch (Exception ex)
        {

        }

    }

    public Date getStartDate()
    {
        return this.mStartDate;
    }

    public void setStartDate(Date startDate)
    {
        this.mStartDate = startDate;
    }

    public Date getEndDate()
    {
        return this.mEndDate;
    }

    public void setEndDate(Date endDate)
    {
        this.mEndDate = endDate;
    }

    public Integer getStockPeriodId()
    {
        return this.mStockPeriodId;
    }

    public void setStockPeriodId(Integer stockPeriodId)
    {
        this.mStockPeriodId = stockPeriodId;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        ArrayList<String> values = new ArrayList<String>();

        if(this.mStockPeriodId == null)
        {
            values.add(StockPeriodIndex.STOCK_PERIOD_ID.ordinal(),"");
        }
        else
        {
            values.add(StockPeriodIndex.STOCK_PERIOD_ID.ordinal(),this.mStockPeriodId.toString());
        }
        values.add(StockPeriodIndex.START_DATE.ordinal(), (this.mStartDate != null) ? DateFormat.format(this.mStartDate) : "");
        values.add(StockPeriodIndex.END_DATE.ordinal(), (this.mEndDate != null) ? DateFormat.format(this.mEndDate): "");


        dest.writeStringList(values);
    }

    public static final Parcelable.Creator<StockPeriod> CREATOR
            = new Parcelable.Creator<StockPeriod>() {

        public StockPeriod createFromParcel(Parcel in) {
            return new StockPeriod(in);
        }

        public StockPeriod[] newArray(int size) { return new StockPeriod[size]; }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public void SaveStockPeriod(ContentResolver contentResolver) {
        ContentValues values = new ContentValues();

        if(mStartDate != null)
            values.put(StoCountContract.StockPeriodEntry.START_DATE, DateFormat.format(this.mStartDate));

        if(mEndDate != null)
            values.put(StoCountContract.StockPeriodEntry.END_DATE, DateFormat.format(this.mEndDate));

        if(this.mStockPeriodId != null)
        {
            contentResolver.update(StoCountContract.StockPeriodEntry.CONTENT_URI, values, StoCountContract.StockPeriodEntry._ID + " = ? ", new String[] { this.mStockPeriodId.toString() } );
        }
        else
        {
            Uri result = contentResolver.insert(StoCountContract.StockPeriodEntry.CONTENT_URI, values);
            mStockPeriodId = Integer.parseInt(result.getLastPathSegment());
        }

    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

