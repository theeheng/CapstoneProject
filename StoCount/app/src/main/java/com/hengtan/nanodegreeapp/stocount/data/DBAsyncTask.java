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
        PRODUCT(0), USER(1), STOCK_PERIOD(2), PRODUCT_COUNT(3), CLOSE_STOCK_PERIOD(4);

        private int value;

        private ObjectType(int value)
        {
            this.value = value;
        }
    };

    private ContentResolver mContentResolver;
    private ObjectType mObjectType;
    private OperationType mOperationType;
    private DBAsyncCallBack mDBAsyncCallBack;

    final private Integer SUCCESSFUL = 1;
    final private Integer FAILED = 0;

    public DBAsyncTask(ContentResolver contentResolver, ObjectType saveType, OperationType operationType, DBAsyncCallBack dbAsyncCallBack) {
        this.mContentResolver = contentResolver;
        this.mObjectType = saveType;
        this.mOperationType = operationType;
        this.mDBAsyncCallBack = dbAsyncCallBack;
    }

    public void setObjectType(ObjectType objType)
    {
        this.mObjectType = objType;
    }

    public void setOperationType(OperationType opType)
    {
        this.mOperationType = opType;
    }

    @Override
    protected Integer doInBackground(Object... params) {

        try {
            // get all locations

            if(mOperationType == OperationType.SAVE) {
                if (mObjectType == ObjectType.PRODUCT) {
                    ((Product) params[0]).SaveProduct(mContentResolver);
                }
                else if (mObjectType == ObjectType.USER) {
                    ((User) params[0]).SaveUser(mContentResolver);
                }
                else if (mObjectType == ObjectType.STOCK_PERIOD) {
                    ((StockPeriod) params[0]).SaveStockPeriod(mContentResolver);
                }
                else if (mObjectType == ObjectType.PRODUCT_COUNT) {
                    ((Product) params[0]).SaveProduct(mContentResolver);
                    ((ProductCount) params[1]).SaveProductCount(mContentResolver);
                }
                else if (mObjectType == ObjectType.CLOSE_STOCK_PERIOD) {
                    ((StockPeriod) params[0]).SaveStockPeriod(mContentResolver);
                    ((StockPeriod) params[1]).SaveStockPeriod(mContentResolver);
                }
            }
            else if(mOperationType == OperationType.DELETE)
            {
                if (mObjectType == ObjectType.PRODUCT)
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

            mDBAsyncCallBack.CallBackOnSuccessfull();
        }
        else
        {
            mDBAsyncCallBack.CallBackOnFail();
        }
    }
}
