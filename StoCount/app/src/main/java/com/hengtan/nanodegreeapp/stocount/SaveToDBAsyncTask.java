package com.hengtan.nanodegreeapp.stocount;

import android.content.ContentResolver;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.List;

/**
 * Created by hengtan on 28/11/2015.
 */
public class SaveToDBAsyncTask extends AsyncTask<Object,  Integer,  Integer> {

    public enum SaveType
    {
        PRODUCT(0), USER(1), STOCK_PERIOD(2);

        private int value;

        private SaveType(int value)
        {
            this.value = value;
        }
    };

    private Context mContext;
    private ContentResolver mContentResolver;
    private SaveType mSaveType;

    final private Integer SUCCESSFUL = 1;
    final private Integer FAILED = 0;

    public SaveToDBAsyncTask(Context c, ContentResolver contentResolver, SaveType saveType) {
        this.mContext = c;
        this.mContentResolver = contentResolver;
        this.mSaveType = saveType;
    }

    @Override
    protected Integer doInBackground(Object... params) {

        try {
            // get all locations

            if(mSaveType == SaveType.PRODUCT)
                ((Product)params[0]).SaveProduct(mContentResolver);
            else if(mSaveType == SaveType.USER)
                ((User)params[0]).SaveUser(mContentResolver);


            return SUCCESSFUL;

        } catch (Exception ex) {

            String message = ex.getMessage();

        }

        return null;
    }


    protected void onPostExecute(Integer result) {

        if(result != null && result.equals(SUCCESSFUL)) {

            Toast.makeText(this.mContext, "Update Product Detail Successful..........",
                    Toast.LENGTH_SHORT).show();

        }
        else
        {
            Toast.makeText(this.mContext, "FAILED To Product Detail ..........",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
