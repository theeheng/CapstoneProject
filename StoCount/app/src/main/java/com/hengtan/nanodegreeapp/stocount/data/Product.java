package com.hengtan.nanodegreeapp.stocount.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import tesco.webapi.android.TescoProduct;
import walmart.webapi.android.WalmartItems;

/**
 * Created by htan on 24/11/2015.
 */
public class Product implements Parcelable {

    private Integer mProductId;
    private String mName;
    private String mDescription;
    private String mThumbnailImage;
    private String mLargeImage;
    private String mAdditionalInfo;
    private String mBarcode;
    private String mBarcodeFormat;

    private List<String> mParcelableString;

    private ProductCount mProductCount;

    public Product(Cursor cursor) {
        InitialiseWithCursor(cursor, false);
    }

    public Product(Cursor cursor, boolean withProductCount) {
        InitialiseWithCursor(cursor, withProductCount);
    }

    public void InitialiseWithCursor(Cursor cursor, boolean withProductCount) {

        if(withProductCount) {
            this.mProductId = cursor.getInt(0);
            mProductCount = new ProductCount(cursor, true);
        }
        else
        {
            this.mProductId = cursor.getInt(cursor.getColumnIndex(StoCountContract.ProductEntry._ID));
        }


        this.mName = cursor.getString(cursor.getColumnIndex(StoCountContract.ProductEntry.PRODUCT_NAME));
        this.mDescription = cursor.getString(cursor.getColumnIndex(StoCountContract.ProductEntry.DESCRIPTION));
        this.mAdditionalInfo = cursor.getString(cursor.getColumnIndex(StoCountContract.ProductEntry.ADDITIONAL_INFO));
        this.mThumbnailImage = cursor.getString(cursor.getColumnIndex(StoCountContract.ProductEntry.THUMBNAIL_IMAGE));
        this.mLargeImage = cursor.getString(cursor.getColumnIndex(StoCountContract.ProductEntry.LARGE_IMAGE));
        this.mBarcode = cursor.getString(cursor.getColumnIndex(StoCountContract.ProductEntry.BARCODE));
        this.mBarcodeFormat = cursor.getString(cursor.getColumnIndex(StoCountContract.ProductEntry.BARCODE_FORMAT));


    }

    private enum ProductIndex
    {
        PRODUCT_ID(0), PRODUCT_NAME(1), PRODUCT_DESCRIPTION(2), PRODUCT_THUMBNAILIMAGE(3), PRODUCT_LARGEIMAGE(4), PRODUCT_CATEGORY(5), PRODUCT_BARCODE(6), PRODUCT_BARCODEFORMAT(7);

        private int value;

        private ProductIndex(int value)
        {
            this.value = value;
        }
    };

    public Product(TescoProduct tescoProduct)
    {
        this.mProductId = null;
        this.mName = tescoProduct.getName();
        this.mDescription = tescoProduct.getExtendedDescription();
        this.mThumbnailImage = tescoProduct.getImagePath();

        if(this.mThumbnailImage != null && !this.mThumbnailImage.isEmpty()) {
            this.mLargeImage = this.mThumbnailImage.replace("90x90","540x540"); //90x90 225x225 540x540
        }

        this.mAdditionalInfo = tescoProduct.getPriceDescription();
        this.mBarcode = tescoProduct.getEANBarcode();
        this.mBarcodeFormat = "EAN13";
    }

    public Product(WalmartItems walmartProduct)
    {
        this.mProductId = null;
        this.mName = walmartProduct.name;
        this.mDescription = (walmartProduct.shortDescription == null) ? walmartProduct.longDescription : walmartProduct.shortDescription;
        this.mThumbnailImage = walmartProduct.thumbnailImage;
        this.mLargeImage = walmartProduct.largeImage;
        this.mAdditionalInfo = walmartProduct.categoryPath;
        this.mBarcode = walmartProduct.upc;
        this.mBarcodeFormat = "UPC";
    }


    public Product(Parcel in)
    {
        mParcelableString = new ArrayList<String>();
        in.readStringList(this.mParcelableString);

        if(!mParcelableString.get(ProductIndex.PRODUCT_ID.ordinal()).isEmpty())
        {
            this.mProductId = Integer.parseInt(mParcelableString.get(ProductIndex.PRODUCT_ID.ordinal()));
        }
        else
        {
            this.mProductId = null;
        }

        this.mName = mParcelableString.get(ProductIndex.PRODUCT_NAME.ordinal());
        this.mDescription = mParcelableString.get(ProductIndex.PRODUCT_DESCRIPTION.ordinal());
        this.mThumbnailImage = mParcelableString.get(ProductIndex.PRODUCT_THUMBNAILIMAGE.ordinal());
        this.mLargeImage = mParcelableString.get(ProductIndex.PRODUCT_LARGEIMAGE.ordinal());
        this.mAdditionalInfo = mParcelableString.get(ProductIndex.PRODUCT_CATEGORY.ordinal());
        this.mBarcode = mParcelableString.get(ProductIndex.PRODUCT_BARCODE.ordinal());
        this.mBarcodeFormat = mParcelableString.get(ProductIndex.PRODUCT_BARCODEFORMAT.ordinal());
    }

    public String getName()
    {
        return this.mName;
    }

    public void setName(String name)
    {
        this.mName = name;
    }

    public String getDescription()
    {
        return this.mDescription;
    }

    public void setDescription(String description)
    {
        this.mDescription = description;
    }

    public String getThumbnailImage()
    {
        return this.mThumbnailImage;
    }

    public void setThumbnailImage(String thumbnailImage)
    {
        this.mThumbnailImage = thumbnailImage;
    }

    public String getLargeImage()
    {
        return this.mLargeImage;
    }

    public void setLargeImage(String largeImage)
    {
        this.mLargeImage = largeImage;
    }

    public String getAdditionalInfo()
    {
        return this.mAdditionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo)
    {
        this.mAdditionalInfo = additionalInfo;
    }

    public String getBarcode()
    {
        return this.mBarcode;
    }

    public void setBarcode(String barcode)
    {
        this.mBarcode = barcode;
    }

    public String getBarcodeFormat()
    {
        return this.mBarcodeFormat;
    }

    public void setBarcodeFormat(String barcodeFormat)
    {
        this.mBarcodeFormat = barcodeFormat;
    }

    public Integer getProductId()
    {
        return this.mProductId;
    }

    public void setProductId(Integer productId)
    {
        this.mProductId = productId;
    }

    public ProductCount getProductCount()
    {
        return mProductCount;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        ArrayList<String> values = new ArrayList<String>();

        if(this.mProductId == null)
        {
            values.add(ProductIndex.PRODUCT_ID.ordinal(),"");
        }
        else
        {
            values.add(ProductIndex.PRODUCT_ID.ordinal(),this.mProductId.toString());
        }
        values.add(ProductIndex.PRODUCT_NAME.ordinal(),this.mName);
        values.add(ProductIndex.PRODUCT_DESCRIPTION.ordinal(),this.mDescription);
        values.add(ProductIndex.PRODUCT_THUMBNAILIMAGE.ordinal(),this.mThumbnailImage);
        values.add(ProductIndex.PRODUCT_LARGEIMAGE.ordinal(),this.mLargeImage);
        values.add(ProductIndex.PRODUCT_CATEGORY.ordinal(),this.mAdditionalInfo);
        values.add(ProductIndex.PRODUCT_BARCODE.ordinal(),this.mBarcode);
        values.add(ProductIndex.PRODUCT_BARCODEFORMAT.ordinal(),this.mBarcodeFormat);

        dest.writeStringList(values);
    }

    public static final Parcelable.Creator<Product> CREATOR
            = new Parcelable.Creator<Product>() {

        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public void SaveProduct(ContentResolver contentResolver) {
        ContentValues values = new ContentValues();

        values.put(StoCountContract.ProductEntry.PRODUCT_NAME, this.mName);
        values.put(StoCountContract.ProductEntry.DESCRIPTION, this.mDescription);
        values.put(StoCountContract.ProductEntry.THUMBNAIL_IMAGE, this.mThumbnailImage);
        values.put(StoCountContract.ProductEntry.LARGE_IMAGE, this.mLargeImage);
        values.put(StoCountContract.ProductEntry.ADDITIONAL_INFO, this.mAdditionalInfo);
        values.put(StoCountContract.ProductEntry.BARCODE, this.mBarcode);
        values.put(StoCountContract.ProductEntry.BARCODE_FORMAT, this.mBarcodeFormat);

        if(IsAddingNewProduct())
        {
            Uri result = contentResolver.insert(StoCountContract.ProductEntry.CONTENT_URI, values);
            mProductId = Integer.parseInt(result.getLastPathSegment());
        }
        else
        {
            contentResolver.update(StoCountContract.ProductEntry.CONTENT_URI, values, StoCountContract.ProductEntry._ID + " = ? ", new String[] { this.mProductId.toString() } );
        }
    }

    public int DeleteProduct(ContentResolver contentResolver) {

        return contentResolver.delete(
                StoCountContract.ProductEntry.CONTENT_URI,
                StoCountContract.ProductEntry._ID + " = ? ",
                new String[] { this.mProductId.toString() }
        );
    }

    public boolean IsAddingNewProduct()
    {
        return this.mProductId == null;
    }
}
