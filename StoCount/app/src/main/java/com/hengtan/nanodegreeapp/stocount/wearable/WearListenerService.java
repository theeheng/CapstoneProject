package com.hengtan.nanodegreeapp.stocount.wearable;

import android.content.Intent;
import android.util.Log;
import com.google.android.gms.wearable.MessageEvent;

public class WearListenerService extends com.google.android.gms.wearable.WearableListenerService {

    public static final String ACTION_DATA_UPDATED = "com.example.android.sunshine.app.ACTION_DATA_UPDATED";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.v("myTag", "Message received on phone  from " + messageEvent.getSourceNodeId());

        if (messageEvent.getPath().equals("/message_path")) {
            final String message = new String(messageEvent.getData());
            Log.v("myTag", "Message path received on phone is: " + messageEvent.getPath());
            Log.v("myTag", "Message received on phone is: " + message);

            startService(new Intent(ACTION_DATA_UPDATED).setClass(this, ProductWearService.class));

        } else {
            super.onMessageReceived(messageEvent);
        }
    }
    }