package com.hengtan.nanodegreeapp.stocount.wearable;

import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.hengtan.nanodegreeapp.stocount.Application;
import com.hengtan.nanodegreeapp.stocount.data.DBAsyncCallBack;
import com.hengtan.nanodegreeapp.stocount.data.DBAsyncTask;
import com.hengtan.nanodegreeapp.stocount.data.ProductCount;
import com.hengtan.nanodegreeapp.stocount.data.StockPeriod;

import java.util.ArrayList;
import java.util.Date;

public class WearListenerService extends com.google.android.gms.wearable.WearableListenerService implements DBAsyncCallBack {

    private static final String TAG = "WearListenerService";
    public static final String ACTION_DATA_UPDATED = "com.example.android.sunshine.app.ACTION_DATA_UPDATED";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.v(TAG, "Message received on phone  from " + messageEvent.getSourceNodeId());

        if (messageEvent.getPath().equals("/stocount-wearable-message-path")) {
            final String message = new String(messageEvent.getData());
            Log.v("myTag", "Message path received on phone is: " + messageEvent.getPath());
            Log.v("myTag", "Message received on phone is: " + message);

            startService(new Intent(ACTION_DATA_UPDATED).setClass(this, ProductWearService.class));

        } else {
            super.onMessageReceived(messageEvent);
        }
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d(TAG, "onDataChanged(): " + dataEvents);

        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                String path = event.getDataItem().getUri().getPath();
                if (ProductWearService.WEARABLE_DATA_PATH.equals(path)) {
                    DataMapItem dataMapItem = DataMapItem
                            .fromDataItem(event.getDataItem());

                    DataMap dataMap = dataMapItem.getDataMap().getDataMap("stockCountDataMap");
                    int prodId = dataMap.getInt("prodId");
                    Integer productCountId = dataMap.getInt("prodCountId");
                    Double quantity = dataMap.getDouble("prodQuantity");
                    Log.d(TAG, "DataMap :  " + prodId + " , " + productCountId + " , " + quantity);

                    StockPeriod currentStockPeriod = Application.getCurrentStockPeriod();

                    DBAsyncTask saveProductAsyncTask = new DBAsyncTask(getContentResolver(), DBAsyncTask.ObjectType.PRODUCT_COUNT_ONLY, DBAsyncTask.OperationType.SAVE, this);

                    ProductCount mProductCount = new ProductCount();

                    if(productCountId != 0)
                    {
                        mProductCount.setProductCountId(productCountId);
                    }

                    mProductCount.setStockPeriodId(currentStockPeriod.getStockPeriodId());
                    mProductCount.setProductId(prodId);
                    mProductCount.setQuantity(quantity);
                    mProductCount.setCountDate(new Date());

                    saveProductAsyncTask.execute(mProductCount);

                }

            }  else {
                Log.d(TAG, "Unknown data event type: " + event.getType());
            }
        }
    }

    @Override
    public void CallBackOnSuccessfull() {
        Log.d(TAG, "Successful save stock count");
    }

    @Override
    public void CallBackOnFail() {
        Log.d(TAG, "Failed save stock count ");
    }
}