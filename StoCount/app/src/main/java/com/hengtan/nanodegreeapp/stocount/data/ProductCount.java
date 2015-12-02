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

import tesco.webapi.android.TescoProduct;
import walmart.webapi.android.WalmartItems;

/**
 * Created by htan on 30/11/2015.
 */
public class ProductCount implements Parcelable {

    private Integer mProductCountId;
    private Integer mStockPeriodId;
    private Integer mProductId;
    private Double mQuantity;
    private Date mCountDate;

    public final SimpleDateFormat DateFormat = new SimpleDateFormat("dd-MM-yyyy");

    private List<String> mParcelableString;

    public ProductCount()
    {

    }

    public ProductCount(Cursor cursor) {

        if(cursor.getInt(cursor.getColumnIndex(StoCountContract.ProductCountEntry._ID)) != 0) {

            this.mProductCountId = cursor.getInt(cursor.getColumnIndex(StoCountContract.ProductCountEntry._ID));
            this.mStockPeriodId = cursor.getInt(cursor.getColumnIndex(StoCountContract.ProductCountEntry.STOCK_PERIOD_ID));
            this.mProductId = cursor.getInt(cursor.getColumnIndex(StoCountContract.ProductCountEntry.PRODUCT_ID));
            this.mQuantity = cursor.getDouble(cursor.getColumnIndex(StoCountContract.ProductCountEntry.QUANTITY));

            try {
                this.mCountDate = DateFormat.parse(cursor.getString(cursor.getColumnIndex(StoCountContract.ProductCountEntry.COUNT_DATE)));
            } catch (Exception ex) {

            }
        }
    }

    private enum ProductCountIndex
    {
        PRODUCT_COUNT_ID(0), STOCK_PERIOD_ID(1), PRODUCT_ID(2), QUANTITY(3), COUNT_DATE(4);

        private int value;

        private ProductCountIndex(int value)
        {
            this.value = value;
        }
    };

    public ProductCount(Parcel in)
    {
        mParcelableString = new ArrayList<String>();
        in.readStringList(this.mParcelableString);

        if(!mParcelableString.get(ProductCountIndex.PRODUCT_COUNT_ID.ordinal()).isEmpty())
        {
            this.mProductCountId = Integer.parseInt(mParcelableString.get(ProductCountIndex.PRODUCT_COUNT_ID.ordinal()));
        }
        else
        {
            this.mProductCountId = null;
        }

        if(!mParcelableString.get(ProductCountIndex.STOCK_PERIOD_ID.ordinal()).isEmpty())
        {
            this.mStockPeriodId = Integer.parseInt(mParcelableString.get(ProductCountIndex.STOCK_PERIOD_ID.ordinal()));
        }
        else
        {
            this.mStockPeriodId = null;
        }

        if(!mParcelableString.get(ProductCountIndex.PRODUCT_ID.ordinal()).isEmpty())
        {
            this.mProductId = Integer.parseInt(mParcelableString.get(ProductCountIndex.PRODUCT_ID.ordinal()));
        }
        else
        {
            this.mProductId = null;
        }

        if(!mParcelableString.get(ProductCountIndex.QUANTITY.ordinal()).isEmpty())
        {
            this.mQuantity = Double.parseDouble(mParcelableString.get(ProductCountIndex.QUANTITY.ordinal()));
        }
        else
        {
            this.mQuantity = null;
        }

        try {

            if(!mParcelableString.get(ProductCountIndex.COUNT_DATE.ordinal()).isEmpty())
                this.mCountDate = DateFormat.parse(mParcelableString.get(ProductCountIndex.COUNT_DATE.ordinal()));
            else
                this.mCountDate = null;

        }catch (Exception ex)
        {

        }
    }

    public Integer getProductCountId()
    {
        return this.mProductCountId;
    }

    public void setProductCountId(Integer productCountId)
    {
        this.mProductCountId = productCountId;
    }

    public Integer getStockPeriodId()
    {
        return this.mStockPeriodId;
    }

    public void setStockPeriodId(Integer stockPeriodId)
    {
        this.mStockPeriodId = stockPeriodId;
    }

    public Integer getProductId()
    {
        return this.mProductId;
    }

    public void setProductId(Integer productId)
    {
        this.mProductId = productId;
    }

    public Double getQuantity()
    {
        return this.mQuantity;
    }

    public void setQuantity(Double quantity)
    {
        this.mQuantity = quantity;
    }

    public Date getCountDate()
    {
        return this.mCountDate;
    }

    public void setCountDate(Date countDate)
    {
        this.mCountDate = countDate;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {

        ArrayList<String> values = new ArrayList<String>();

        if(this.mProductCountId == null)
        {
            values.add(ProductCountIndex.PRODUCT_COUNT_ID.ordinal(),"");
        }
        else
        {
            values.add(ProductCountIndex.PRODUCT_COUNT_ID.ordinal(),this.mProductCountId.toString());
        }

        if(this.mStockPeriodId == null)
        {
            values.add(ProductCountIndex.STOCK_PERIOD_ID.ordinal(),"");
        }
        else
        {
            values.add(ProductCountIndex.STOCK_PERIOD_ID.ordinal(),this.mStockPeriodId.toString());
        }

        if(this.mProductId == null)
        {
            values.add(ProductCountIndex.PRODUCT_ID.ordinal(),"");
        }
        else
        {
            values.add(ProductCountIndex.PRODUCT_ID.ordinal(),this.mProductId.toString());
        }

        if(this.mQuantity == null)
        {
            values.add(ProductCountIndex.QUANTITY.ordinal(),"");
        }
        else
        {
            values.add(ProductCountIndex.QUANTITY.ordinal(),this.mQuantity.toString());
        }

        values.add(ProductCountIndex.COUNT_DATE.ordinal(), (this.mCountDate != null) ? DateFormat.format(this.mCountDate) : "");

        dest.writeStringList(values);
    }

    public static final Parcelable.Creator<ProductCount> CREATOR
            = new Parcelable.Creator<ProductCount>() {

        public ProductCount createFromParcel(Parcel in) {
            return new ProductCount(in);
        }

        public ProductCount[] newArray(int size) {
            return new ProductCount[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public void SaveProductCount(ContentResolver contentResolver) {
        ContentValues values = new ContentValues();

        if(this.mStockPeriodId != null)
            values.put(StoCountContract.ProductCountEntry.STOCK_PERIOD_ID, this.mStockPeriodId);

        if(this.mProductId != null)
            values.put(StoCountContract.ProductCountEntry.PRODUCT_ID, this.mProductId);

        if(this.mQuantity != null)
            values.put(StoCountContract.ProductCountEntry.QUANTITY, this.mQuantity);
        else if(this.getProductCountId() != null && this.mQuantity == null)
            values.put(StoCountContract.ProductCountEntry.QUANTITY, "");

        if(mCountDate !=  null)
            values.put(StoCountContract.ProductCountEntry.COUNT_DATE, DateFormat.format(this.mCountDate));

        if(this.mProductCountId != null)
        {
            contentResolver.update(StoCountContract.ProductCountEntry.CONTENT_URI, values, StoCountContract.ProductCountEntry._ID + " = ? ", new String[] { this.mProductCountId.toString() } );
        }
        else
        {
            Uri result = contentResolver.insert(StoCountContract.ProductCountEntry.CONTENT_URI, values);
            mProductCountId = Integer.parseInt(result.getLastPathSegment());
        }

    }
}