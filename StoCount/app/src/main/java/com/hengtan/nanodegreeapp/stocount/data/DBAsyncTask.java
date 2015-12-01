package com.hengtan.nanodegreeapp.stocount.data;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.hengtan.nanodegreeapp.stocount.R;
import com.hengtan.nanodegreeapp.stocount.data.Product;
import com.hengtan.nanodegreeapp.stocount.data.User;

/**
 * Created by hengtan on 28/11/2015.
 */
public class DBAsyncTask extends AsyncTask<Object,  Integer,  Integer> {

    public enum OperationType
    {
        SAVE(0), DELETE(1);

        private int value;

        private OperationType(int value)
        {
            this.value = value;
        }
    };


    public enum ObjectType
    {
        PRODUCT(0), USER(1), STOCK_PERIOD(2), PRODUCT_COUNT(3);

        private int value;

        private ObjectType(int value)
        {
            this.value = value;
        }
    };

    private Context mContext;
    private ContentResolver mContentResolver;
    private ObjectType mSaveType;
    private OperationType mOperationType;

    final private Integer SUCCESSFUL = 1;
    final private Integer FAILED = 0;

    public DBAsyncTask(Context c, ContentResolver contentResolver, ObjectType saveType, OperationType operationType) {
        this.mContext = c;
        this.mContentResolver = contentResolver;
        this.mSaveType = saveType;
        this.mOperationType = operationType;
    }

    @Override
    protected Integer doInBackground(Object... params) {

        try {
            // get all locations

            if(mOperationType == OperationType.SAVE) {
                if (mSaveType == ObjectType.PRODUCT)
                    ((Product) params[0]).SaveProduct(mContentResolver);
                else if (mSaveType == ObjectType.USER)
                    ((User) params[0]).SaveUser(mContentResolver);
                else if (mSaveType == ObjectType.STOCK_PERIOD)
                    ((StockPeriod) params[0]).SaveStockPeriod(mContentResolver);
                else if (mSaveType == ObjectType.PRODUCT_COUNT)
                    ((ProductCount) params[0]).SaveProductCount(mContentResolver);
            }
            else if(mOperationType == OperationType.DELETE)
            {
                if (mSaveType == ObjectType.PRODUCT)
                    ((Product) params[0]).DeleteProduct(mContentResolver);
            }

            return SUCCESSFUL;

        } catch (Exception ex) {

            String message = ex.getMessage();

        }

        return null;
    }


    protected void onPostExecute(Integer result) {

        if(result != null && result.equals(SUCCESSFUL)) {

            Toast.makeText(this.mContext, "Save Successful..........",
                    Toast.LENGTH_SHORT).show();

            if(mSaveType == ObjectType.PRODUCT && mOperationType == OperationType.SAVE)
            {
                EditText productCount = (EditText) ((Activity)mContext).getWindow().getDecorView().findViewById(R.id.et_productcount);
                TextInputLayout productCountTextInputLayout = (TextInputLayout) ((Activity)mContext).getWindow().getDecorView().findViewById(R.id.til_productcount);
                productCount.setVisibility(View.VISIBLE);
                productCountTextInputLayout.setVisibility(View.VISIBLE);
            }
        }
        else
        {
            Toast.makeText(this.mContext, "FAILED To Save ..........",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
