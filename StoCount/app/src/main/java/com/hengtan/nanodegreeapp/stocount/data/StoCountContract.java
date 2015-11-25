package com.hengtan.nanodegreeapp.stocount.data;

/**
 * Created by saj on 22/12/14.
 */

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;


public class StoCountContract {

    public static final String CONTENT_AUTHORITY = "com.hengtan.nanodegreeapp.stocount";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_USERS = "users";
    public static final String PATH_PRODUCTS = "products";
    public static final String PATH_STOCK_PERIODS = "stockperiods";
    public static final String PATH_PRODUCT_COUNTS = "books";


    public static final class UserEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_USERS).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_USERS;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_USERS;

        public static final String TABLE_NAME = "users";

        public static final String DISPLAY_NAME = "displayname";

        public static final String EMAIL = "email";

        public static final String PHOTO_URL = "photourl";

        public static final String GOOGLE_ID = "googleid";

        public static Uri buildUserUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }


    public static final class ProductEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PRODUCTS).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        public static final String TABLE_NAME = "products";

        public static final String PRODUCT_NAME = "name";

        public static final String DESCRIPTION = "description";

        public static final String THUMBNAIL_IMAGE = "thumbnailimage";

        public static final String LARGE_IMAGE = "largeimage";

        public static final String ADDITIONAL_INFO = "additionalinfo";

        public static final String BARCODE = "barcode";

        public static final String BARCODE_FORMAT = "barcodeformat";

        public static Uri buildProductUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class StockPeriodEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_STOCK_PERIODS).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_STOCK_PERIODS;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_STOCK_PERIODS;

        public static final String TABLE_NAME = "stockperiods";

        public static final String START_DATE = "startdate";

        public static final String END_DATE = "enddate";

        public static Uri buildStockPeriodUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    public static final class ProductCountEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PRODUCT_COUNTS).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT_COUNTS;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT_COUNTS;

        public static final String TABLE_NAME = "productcounts";

        public static final String STOCK_PERIOD_ID = "stockperiodid";

        public static final String PRODUCT_ID = "productid";

        public static final String QUANTITY = "quantity";

        public static final String COUNT_DATE = "countdate";

        public static Uri buildProductCountUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }
}