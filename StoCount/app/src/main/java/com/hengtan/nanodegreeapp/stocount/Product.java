package com.hengtan.nanodegreeapp.stocount;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.hengtan.nanodegreeapp.stocount.data.StoCountContract;

import java.util.ArrayList;
import java.util.List;

import tesco.webapi.android.TescoProduct;
import tesco.webapi.android.TescoProductSearch;

/**
 * Created by htan on 24/11/2015.
 */
public class Product implements Parcelable {

    private String mName;
    private String mDescription;
    private String mThumbnailImage;
    private String mLargeImage;
    private String mCategory;
    private String mBarcode;
    private String mBarcodeFormat;

    private List<String> mParcelableString;

    private enum ProductIndex
    {
        PRODUCT_NAME(0), PRODUCT_DESCRIPTION(1), PRODUCT_THUMBNAILIMAGE(2), PRODUCT_LARGEIMAGE(3), PRODUCT_CATEGORY(4), PRODUCT_BARCODE(5), PRODUCT_BARCODEFORMAT(6);

        private int value;

        private ProductIndex(int value)
        {
            this.value = value;
        }
    };

    public Product(TescoProduct tescoProduct)
    {
        this.mName = tescoProduct.getName();
        this.mDescription = tescoProduct.getExtendedDescription();
        this.mThumbnailImage = tescoProduct.getImagePath();

        if(this.mThumbnailImage != null && !this.mThumbnailImage.isEmpty()) {
            this.mLargeImage = this.mThumbnailImage.replace("90x90","540x540"); //90x90 225x225 540x540
        }

        this.mCategory = tescoProduct.getUnitType();
        this.mBarcode = tescoProduct.getEANBarcode();
        this.mBarcodeFormat = "EAN13";
    }

    public Product(Parcel in)
    {
        mParcelableString = new ArrayList<String>();
        in.readStringList(this.mParcelableString);

        this.mName = mParcelableString.get(ProductIndex.PRODUCT_NAME.ordinal());
        this.mDescription = mParcelableString.get(ProductIndex.PRODUCT_DESCRIPTION.ordinal());
        this.mThumbnailImage = mParcelableString.get(ProductIndex.PRODUCT_THUMBNAILIMAGE.ordinal());
        this.mLargeImage = mParcelableString.get(ProductIndex.PRODUCT_LARGEIMAGE.ordinal());
        this.mCategory = mParcelableString.get(ProductIndex.PRODUCT_CATEGORY.ordinal());
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

    public String getCategory()
    {
        return this.mCategory;
    }

    public void setCategory(String category)
    {
        this.mCategory = category;
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

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        ArrayList<String> values = new ArrayList<String>();

        values.add(ProductIndex.PRODUCT_NAME.ordinal(),this.mName);
        values.add(ProductIndex.PRODUCT_DESCRIPTION.ordinal(),this.mDescription);
        values.add(ProductIndex.PRODUCT_THUMBNAILIMAGE.ordinal(),this.mThumbnailImage);
        values.add(ProductIndex.PRODUCT_LARGEIMAGE.ordinal(),this.mLargeImage);
        values.add(ProductIndex.PRODUCT_CATEGORY.ordinal(),this.mCategory);
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
        //values.put(StoCountContract.ProductEntry._ID, ean);
        values.put(StoCountContract.ProductEntry.PRODUCT_NAME, this.mName);
        values.put(StoCountContract.ProductEntry.DESCRIPTION, this.mDescription);
        values.put(StoCountContract.ProductEntry.THUMBNAIL_IMAGE, this.mThumbnailImage);
        values.put(StoCountContract.ProductEntry.LARGE_IMAGE, this.mLargeImage);
        values.put(StoCountContract.ProductEntry.CATEGORY, this.mCategory);
        values.put(StoCountContract.ProductEntry.BARCODE, this.mBarcode);
        values.put(StoCountContract.ProductEntry.BARCODE_FORMAT, this.mBarcodeFormat);
        contentResolver.insert(StoCountContract.ProductEntry.CONTENT_URI, values);
    }
}
