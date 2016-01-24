/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hengtan.nanodegreeapp.stocount.wearable;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.hengtan.nanodegreeapp.stocount.Application;
import com.hengtan.nanodegreeapp.stocount.DetailActivity;
import com.hengtan.nanodegreeapp.stocount.data.Product;
import com.hengtan.nanodegreeapp.stocount.data.ProductCount;
import com.hengtan.nanodegreeapp.stocount.data.StoCountContract;
import com.hengtan.nanodegreeapp.stocount.data.StockPeriod;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class ProductWearService extends IntentService implements
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient googleClient;

    private static final String WEARABLE_DATA_PATH = "/stocount-wearable-data-path";

    /*private static final String[] FORECAST_COLUMNS = {
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP
    };
    */

    // these indices must match the projection
    private static final int INDEX_WEATHER_ID = 0;
    private static final int INDEX_SHORT_DESC = 1;
    private static final int INDEX_MAX_TEMP = 2;
    private static final int INDEX_MIN_TEMP = 3;

    private boolean isConnected = false;

    public ProductWearService() {
        super("ProductWearService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        boolean dataUpdated = intent != null &&
                WearListenerService.ACTION_DATA_UPDATED.equals(intent.getAction());
        Log.w("ProductWearService", "action: " + intent.getAction());
        if (dataUpdated ) {
            Log.w("action", "client created");
            // Build a new GoogleApiClient that includes the Wearable API
            googleClient = new GoogleApiClient.Builder(this)
                    .addApi(Wearable.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            googleClient.connect();




        }
    }

    private void sendMessage() {

        Log.w("ProductWearService", "sendMessage");

        StockPeriod currentStockPeriod = Application.getCurrentStockPeriod();

        Cursor data = getContentResolver().query(
                StoCountContract.ProductEntry.buildCurrentProductUri(currentStockPeriod.getStockPeriodId()),
                null,
                null,
                null,
                null
        );

        if (data == null) {
            return;
        }

        ArrayList<DataMap> arrayListDataMap= new ArrayList<DataMap>();

        if(data.moveToFirst()){

            int totalRows = data.getCount();
            int index = 0;
            do {

                // Extract the weather data from the Cursor

                Product prod = new Product(data);
                ProductCount prodCount = new ProductCount(data);

                String imagePath = prod.getThumbnailImage();

                DataMap dataMap = new DataMap();

                dataMap.putInt("prodIndex", index);
                dataMap.putString("prodName", prod.getName());
                dataMap.putString("prodInfo", prod.getAdditionalInfo());

                if (prodCount.getProductCountId() != null) {
                    dataMap.putInt("prodCountId", prodCount.getProductCountId());
                    dataMap.putDouble("prodQuantity", prodCount.getQuantity());
                }
                    try {

                    if (imagePath != null && (!imagePath.isEmpty()) && imagePath.indexOf("http") > -1) {
                        URL url = new URL(imagePath);

                        Glide.with(this).load(imagePath).asBitmap().into(new BitmapTarget<Bitmap>(dataMap, arrayListDataMap, index, totalRows));
                        //bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                    } else if (imagePath != null && (!imagePath.isEmpty())) {
                        Uri url = Uri.fromFile(new File(imagePath));
                        Bitmap bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(url));
                        Asset asset = createAssetFromBitmap(bmp);
                        dataMap.putAsset("prodImage", asset);
                        arrayListDataMap.add(dataMap);
                    }

                    if(arrayListDataMap.size() == totalRows)
                    {
                        //new SendMessageToDataLayerThread(WEARABLE_DATA_PATH, message).start();

                        new SendToDataLayerThread(WEARABLE_DATA_PATH, arrayListDataMap).start();

                    }

                    index++;
                }
                catch (Exception ex)
                {
                        String errorMesage = ex.getMessage();

                }

            }while(data.moveToNext());

        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        String message = null;
        //Requires a new thread to avoid blocking the UI
        Log.w("ProductWearService", "google api connected");
        // Create a DataMap object and send it to the data layer
        // Get today's data from the ContentProvider
        isConnected = true;
        sendMessage();
    }

    private Asset createAssetFromBitmap(Bitmap bitmap) {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        return Asset.createFromBytes(byteStream.toByteArray());
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.w("action", "connection suspended message " + i);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.w("action", "connection suspended failed " + connectionResult.toString());
    }


    class SendToDataLayerThread extends Thread {
        String path;
        ArrayList<DataMap> dataMap;

        // Constructor for sending data objects to the data layer
        SendToDataLayerThread(String p, ArrayList<DataMap> data) {
            path = p;
            dataMap = data;
        }

        public void run() {


            PutDataMapRequest dataMapRequest = PutDataMapRequest.create(path);
            dataMapRequest.getDataMap().putDataMapArrayList("stockDataMap", dataMap);
            dataMapRequest.getDataMap().putLong("time", new Date().getTime());
            PutDataRequest request = dataMapRequest.asPutDataRequest();

            DataApi.DataItemResult result = Wearable.DataApi.putDataItem(googleClient, request).await();

            if (result.getStatus().isSuccess()) {
                Log.v("myTag", "DataMap: " + dataMap + " sent successful");
            } else {
                // Log an error
                Log.v("myTag", "ERROR: failed to send DataMap");
            }
        }
    }

    @Override
    public void onDestroy() {
        if (null != googleClient && googleClient.isConnected()) {
            googleClient.disconnect();
        }
        super.onDestroy();
    }


    class SendMessageToDataLayerThread extends Thread {
        String path;
        String message;

        // Constructor to send a message to the data layer
        SendMessageToDataLayerThread(String p, String msg) {
            path = p;
            message = msg;
        }

        public void run() {
            NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleClient).await();
            if (nodes != null && nodes.getNodes() != null) {
                for (Node node : nodes.getNodes()) {
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(googleClient, node.getId(), path, message.getBytes()).await();
                    if (result.getStatus().isSuccess()) {
                        Log.v("myTag", "Message: {" + message + "} sent to: " + node.getDisplayName() + ", id:" + node.getId());
                    } else {
                        // Log an error
                        Log.v("myTag", "ERROR: failed to send Message");
                    }
                }
            }else{
                Log.v("myTag message", "ERROR: no nodes connected");
            }
        }
    }

    class BitmapTarget<Bitmap> extends SimpleTarget<Bitmap>
    {
        ArrayList<DataMap> arrayListDataMap;
        DataMap dataMap;
        int index;
        int totalRows;

        public BitmapTarget(DataMap dm, ArrayList<DataMap> listDM, int index, int totalRows)
        {
            this.dataMap = dm;
            this.arrayListDataMap = listDM;
            this.index = index;
            this.totalRows = totalRows;

        }

        @Override
        public void onResourceReady (Bitmap resource, GlideAnimation < ? super Bitmap > glideAnimation)
        {
            Asset asset = createAssetFromBitmap((android.graphics.Bitmap) resource);
            dataMap.putAsset("prodImage", asset);
            this.arrayListDataMap.add(dataMap);


            if(this.arrayListDataMap.size() == this.totalRows)
            {
                //new SendMessageToDataLayerThread(WEARABLE_DATA_PATH, message).start();

                new SendToDataLayerThread(WEARABLE_DATA_PATH, this.arrayListDataMap).start();

            }
        }
    }
}
