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
import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
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
import com.hengtan.nanodegreeapp.stocount.data.StoCountContract;
import com.hengtan.nanodegreeapp.stocount.data.StockPeriod;

import java.io.ByteArrayOutputStream;


public class ProductWearService extends IntentService implements
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient googleClient;

    private static final String WEARABLE_DATA_PATH = "/message_path";

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

    public ProductWearService() {
        super("ProductWearService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        boolean dataUpdated = intent != null &&
                WearListenerService.ACTION_DATA_UPDATED.equals(intent.getAction());
        Log.w("action", intent.getAction());
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

    @Override
    public void onConnected(Bundle bundle) {
        String message = null;
        //Requires a new thread to avoid blocking the UI
        Log.w("action", "sending message");
        // Create a DataMap object and send it to the data layer
        // Get today's data from the ContentProvider

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
        if (!data.moveToFirst()) {
            data.close();
            return;
        }


        // Extract the weather data from the Cursor
        String productName = data.getString(data.getColumnIndex(StoCountContract.ProductEntry.PRODUCT_NAME));
        String mThumbnailImage = data.getString(data.getColumnIndex(StoCountContract.ProductEntry.THUMBNAIL_IMAGE));

        data.close();

        DataMap dataMap = new DataMap();

        Bitmap bitmap = null;
        Asset asset = null;
        try
        {
            bitmap=Glide.with(this).load(mThumbnailImage).asBitmap().into(100,100).get();
            asset = createAssetFromBitmap(bitmap);
        }catch (Exception ex)
        {

        }


        dataMap.putString("prodName", productName);

        //Requires a new thread to avoid blocking the UI

        //message=formattedMaxTemperature+";"+formattedMinTemperature+";"+weatherId;
        //new SendMessageToDataLayerThread(WEARABLE_DATA_PATH, message).start();

        new SendToDataLayerThread(WEARABLE_DATA_PATH, dataMap, asset).start();

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
        DataMap dataMap;
        Asset asset;

        // Constructor for sending data objects to the data layer
        SendToDataLayerThread(String p, DataMap data, Asset ast) {
            path = p;
            dataMap = data;
            asset = ast;
        }

        public void run() {
            NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleClient).await();
            if (nodes != null && nodes.getNodes() != null) {
                for (Node node : nodes.getNodes()) {
                    Log.v("myTag", "DataMap: " + dataMap);
                    // Construct a DataRequest and send over the data layer
                    PutDataMapRequest putDMR = PutDataMapRequest.create(path);
                    putDMR.getDataMap().putDataMap("map",dataMap);

                    if(asset != null) {
                   //     putDMR.getDataMap().putAsset("prodImage", asset);
                    }

                    PutDataRequest request = putDMR.asPutDataRequest();

                    DataApi.DataItemResult result = Wearable.DataApi.putDataItem(googleClient, request).await();

                    if (result.getStatus().isSuccess()) {
                        Log.v("myTag", "DataMap: " + dataMap + " sent to: " + node.getDisplayName() + ", id:" + node.getId());
                    } else {
                        // Log an error
                        Log.v("myTag", "ERROR: failed to send DataMap");
                    }
                }
            }else{
                Log.v("myTag", "ERROR: no nodes connected");
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
}
