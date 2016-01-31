/*
 * Copyright (C) 2014 The Android Open Source Project
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

package com.hengtan.nanodegreeapp.stocount;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CustomFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private static final String TAG = "CustomFragment";
    private Button inputBtn;
    private int productId;
    private Integer productCountId;
    private double currentCount;
    private GoogleApiClient mGoogleApiClient;
    DataMap mSendDataMap;

    public CustomFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View fragmentRootView = inflater.inflate(R.layout.custom_fragment, container, false);

        mGoogleApiClient =  new GoogleApiClient.Builder(getActivity())
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();

        Bundle bundle=getArguments();

        //here is your list array
        productId=bundle.getInt("productId");
        productCountId = bundle.getInt("productCountId");
        currentCount = bundle.getDouble("prodCurrentCount");

        inputBtn = (Button) fragmentRootView.findViewById(R.id.input_button);
        inputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displaySpeechRecognizer();
            }
        });
        return fragmentRootView;

    }

    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say: {Numeric Quantity}"
                .toString());


        // Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, DetailActivity.SPEECH_REQUEST_CODE);
    }

    // This callback is invoked when the Speech Recognizer returns.
    // This is where you process the intent and extract the speech text from the intent.
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == DetailActivity.SPEECH_REQUEST_CODE && resultCode == DetailActivity.RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            String spokenText = results.get(0).replace(" ","");

            try {

                Double quantity = Double.parseDouble(spokenText);

                mSendDataMap = new DataMap();
                mSendDataMap.putInt("prodId", productId);
                mSendDataMap.putInt("prodCountId", productCountId);
                mSendDataMap.putDouble("prodQuantity", quantity);

                Log.v(TAG, "calling : SendToDataLayerThread");

                new SendToDataLayerThread(ListenerService.DEVICE_DATA_PATH, mSendDataMap).start();

                currentCount = quantity;

            }
            catch (Exception ex)
            {
                Toast.makeText(getActivity(), "unable to update quantity", Toast.LENGTH_SHORT).show();
            }
            // Do something with spokenText
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected(): Successfully connected to Google API client");
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "onConnectionSuspended(): Connection to Google API client was suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.e(TAG, "onConnectionFailed(): Failed to connect, with result: " + result);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume : ");
        //mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        //mGoogleApiClient.disconnect();
    }

    public double getCurrentCount()
    {
        return currentCount;
    }

    public int getProductId()
    {
        return productId;
    }

    class SendToDataLayerThread extends Thread {
        String path;
        DataMap dataMap;

        // Constructor for sending data objects to the data layer
        SendToDataLayerThread(String p, DataMap data) {
            path = p;
            dataMap = data;
        }

        public void run() {

            Log.v(TAG, "SendToDataLayerThread run DataMap: " + dataMap);
            PutDataMapRequest dataMapRequest = PutDataMapRequest.create(path);
            dataMapRequest.getDataMap().putDataMap("stockCountDataMap", dataMap);
            dataMapRequest.getDataMap().putLong("time", new Date().getTime());
            PutDataRequest request = dataMapRequest.asPutDataRequest();

            DataApi.DataItemResult result = Wearable.DataApi.putDataItem(mGoogleApiClient, request).await();

            if (result.getStatus().isSuccess()) {
                Log.v(TAG, "DataMap: " + dataMap + " sent successful");
            } else {
                // Log an error
                Log.v(TAG, "ERROR: failed to send DataMap");
            }
        }
    }
}
