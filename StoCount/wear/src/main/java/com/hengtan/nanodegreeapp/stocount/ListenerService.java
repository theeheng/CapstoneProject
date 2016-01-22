package com.hengtan.nanodegreeapp.stocount;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Wearable listener service for data layer messages
 * Created by shalini
 */
public class ListenerService extends com.google.android.gms.wearable.WearableListenerService {
    public static final String WEARABLE_MESSAGE_PATH = "/stocount-wearable-message-path";
    public static final String WEARABLE_DATA_PATH = "/stocount-wearable-data-path";
    private static final String TAG = "ListenerService";
    private static long TIMEOUT_MS = 60000;

    @Override
    public void onCreate() {

        Log.d("ListenerService", "onCreate : ");

        super.onCreate();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        Log.d("ListenerService", "onMessageReceived : ");

        if (messageEvent.getPath().equals(WEARABLE_MESSAGE_PATH)) {
            final String message = new String(messageEvent.getData());
            Log.v("myTag", "Message path received on watch is: " + messageEvent.getPath());
            Log.v("myTag", "Message received from node (" + messageEvent.getSourceNodeId() + ") on watch is: " + message);
            String[] values=message.split(";");
            Bundle bundle= new Bundle();

            bundle.putString("maxTemp", values[0]);
            bundle.putString("minTemp", values[1]);
            bundle.putString("weatherId", "" + values[2]);
            Intent messageIntent = new Intent();
            messageIntent.setAction(Intent.ACTION_SEND);
            messageIntent.putExtras(bundle);
            LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
        }
        else {
            super.onMessageReceived(messageEvent);
        }
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {

        Log.d(TAG, "onDataChanged: " + dataEvents);

        Log.d(TAG, "onDataChanged: " + dataEvents);
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        dataEvents.close();
    }

    /*
    public Bitmap loadBitmapFromAsset(Asset asset) {
        if (asset == null) {
            throw new IllegalArgumentException("Asset must be non-null");
        }
        ConnectionResult result =  mGoogleApiClient.blockingConnect(TIMEOUT_MS, TimeUnit.MILLISECONDS);
        if (!result.isSuccess()) {
            return null;
        }
        // convert asset into a file descriptor and block until it's ready
        InputStream assetInputStream = Wearable.DataApi.getFdForAsset(
                mGoogleApiClient, asset).await().getInputStream();
        mGoogleApiClient.disconnect();

        if (assetInputStream == null) {
            Log.w(TAG, "Requested an unknown Asset.");
            return null;
        }
        // decode the stream into a bitmap
        return BitmapFactory.decodeStream(assetInputStream);
    }*/
}
