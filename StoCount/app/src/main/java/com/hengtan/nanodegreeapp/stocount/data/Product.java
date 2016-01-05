package com.hengtan.nanodegreeapp.stocount.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.amazon.webservices.awsecommerceservice.ImageSet;
import com.amazon.webservices.awsecommerceservice.Item;
import com.hengtan.nanodegreeapp.stocount.R;

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
    private boolean mDeleted;

    private List<String> mParcelableString;

    public Product(Resources res)
    {
        this.mProductId = null;
        this.mName = res.getString(R.string.product_default_name);
        this.mDescription = res.getString(R.string.product_default_description);
        this.mAdditionalInfo = res.getString(R.string.product_default_additional_info);
        this.mThumbnailImage = "";
        this.mLargeImage = "";
        this.mBarcode = "";
        this.mBarcodeFormat = "";
        this.mDeleted = false;
    }

    public Product(Cursor cursor) {

        this.mProductId = cursor.getInt(cursor.getColumnIndex(StoCountContract.ProductEntry._ID));
        this.mName = cursor.getString(cursor.getColumnIndex(StoCountContract.ProductEntry.PRODUCT_NAME));
        this.mDescription = cursor.getString(cursor.getColumnIndex(StoCountContract.ProductEntry.DESCRIPTION));
        this.mAdditionalInfo = cursor.getString(cursor.getColumnIndex(StoCountContract.ProductEntry.ADDITIONAL_INFO));
        this.mThumbnailImage = cursor.getString(cursor.getColumnIndex(StoCountContract.ProductEntry.THUMBNAIL_IMAGE));
        this.mLargeImage = cursor.getString(cursor.getColumnIndex(StoCountContract.ProductEntry.LARGE_IMAGE));
        this.mBarcode = cursor.getString(cursor.getColumnIndex(StoCountContract.ProductEntry.BARCODE));
        this.mBarcodeFormat = cursor.getString(cursor.getColumnIndex(StoCountContract.ProductEntry.BARCODE_FORMAT));
        this.mDeleted = cursor.getInt(cursor.getColumnIndex(StoCountContract.ProductEntry.DELETED)) == 1;
    }

    private enum ProductIndex
    {
        PRODUCT_ID(0), PRODUCT_NAME(1), PRODUCT_DESCRIPTION(2), PRODUCT_THUMBNAILIMAGE(3), PRODUCT_LARGEIMAGE(4), PRODUCT_CATEGORY(5), PRODUCT_BARCODE(6), PRODUCT_BARCODEFORMAT(7), PRODUCT_DELETED(8);

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
        this.mBarcodeFormat = "EAN_13";
        this.mDeleted = false;
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
        this.mBarcodeFormat = "UPC_A";
        this.mDeleted = false;
    }

    public Product(Item amazonItem)
    {
        this.mProductId = null;
        this.mName = amazonItem.itemAttributes.title;

        //String name =
        //String description = null;
        //String thumbnailUrl = null;

        if(amazonItem.editorialReviews !=  null && amazonItem.editorialReviews.editorialReview != null && amazonItem.editorialReviews.editorialReview.size() > 0) {
            this.mDescription = amazonItem.editorialReviews.editorialReview.get(0).content;
        }
        else
        {
            this.mDescription = "";
        }

        if(amazonItem.imageSets !=  null && amazonItem.imageSets.size()  > 0 && amazonItem.imageSets.get(0).imageSet.size() > 0 && amazonItem.imageSets.get(0).imageSet.get(0).thumbnailImage != null) {

            for(ImageSet imgset : amazonItem.imageSets.get(0).imageSet) {
                if (imgset.category.equals("primary")) {
                    this.mThumbnailImage = imgset.mediumImage.url; //imgset.thumbnailImage.url;
                    this.mLargeImage = imgset.largeImage.url;
                }
            }
        }
        else
        {
            this.mThumbnailImage = ""; //imgset.thumbnailImage.url;
            this.mLargeImage = "";
        }

        if(amazonItem.itemAttributes != null && amazonItem.itemAttributes.category != null)
        {
            this.mAdditionalInfo = "";
            for(String str : amazonItem.itemAttributes.category)
            {
                if(this.mAdditionalInfo.isEmpty())
                {
                    this.mAdditionalInfo = str;
                }
                else
                {
                    this.mAdditionalInfo = this.mAdditionalInfo + ", " + str;
                }
            }
        }

        this.mDeleted = false;

        //UpdateUI(name, description, thumbnailUrl);
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
        this.mDeleted = Boolean.parseBoolean(mParcelableString.get(ProductIndex.PRODUCT_DELETED.ordinal()));
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
        values.add(ProductIndex.PRODUCT_DELETED.ordinal(),Boolean.toString(this.mDeleted));
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
        values.put(StoCountContract.ProductEntry.DELETED, (this.mDeleted) ? 1 : 0);

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

        ContentValues values = new ContentValues();
        values.put(StoCountContract.ProductEntry.DELETED, 1);

        return contentResolver.update(StoCountContract.ProductEntry.CONTENT_URI, values, StoCountContract.ProductEntry._ID + " = ? ", new String[]{this.mProductId.toString()});

        /*return contentResolver.delete(
                StoCountContract.ProductEntry.CONTENT_URI,
                StoCountContract.ProductEntry._ID + " = ? ",
                new String[] { this.mProductId.toString() }
        );
        */
    }

    public boolean IsAddingNewProduct()
    {
        return this.mProductId == null;
    }
}
