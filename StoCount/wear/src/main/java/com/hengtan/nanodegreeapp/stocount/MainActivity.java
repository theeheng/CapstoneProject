package com.hengtan.nanodegreeapp.stocount;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements WearableListView.ClickListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, DataApi.DataListener, NodeApi.NodeListener  {

    private static final String TAG = "MainActivity";
    private TextView mTextView;

    private DemoItemAdapter mAdapter;
    private List<DemoItem> mData = new ArrayList<DemoItem>();
    private WearableListView mListView;
    private GoogleApiClient mGoogleApiClient;

    BroadcastReceiver messageReceiver= new  BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle dataMap=intent.getExtras();

            String maxTemp = dataMap.getString("productName");
            String minTemp = dataMap.getString("minTemp");
            String value=dataMap.getString("weatherId");


        }
    };

    private void onProductUpdateLoaded() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      /* final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                mListView = (WearableListView) stub.findViewById(R.id.wearable_list);
            }
        });
*/

        mListView = (WearableListView) findViewById(R.id.wearable_list);

        mGoogleApiClient =  new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();

        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);

        //setupData();
        //setupListView();
    }

    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {
        DemoItem item = mData.get(viewHolder.getPosition());
        startActivity(item.getIntent());
    }

    @Override
    public void onTopEmptyRegionClick() {
        // For now, do nothing
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        Wearable.NodeApi.removeListener(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
    }

    public void setupData() {
       // mData.add(new DemoItem(getString(R.string.demo_item_bad_layout), new Intent(this, BadLayoutActivity.class)));
        mData.add(new DemoItem("Detail", new Intent(this, DetailActivity.class)));

    }

    public void setupListView() {

        mAdapter = new DemoItemAdapter(mData);
        mListView.setAdapter(mAdapter);

        // Note that this is NOT setting an OnClickListener, but a ClickListener
        mListView.setClickListener(this);
    }

    @Override
    public void onConnected(Bundle bundle) {

        Log.d(TAG, "onConnected(): Successfully connected to Google API client");
        Wearable.DataApi.addListener(mGoogleApiClient, this);
        Wearable.NodeApi.addListener(mGoogleApiClient, this);

        String messagePhone = "Hello phone\n Via the data layer";
        new SendToDataLayerThread("/stocount-wearable-message-path", messagePhone).start();

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
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d(TAG, "onDataChanged(): " + dataEvents);


        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        dataEvents.close();
        for (DataEvent event : events) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                String path = event.getDataItem().getUri().getPath();
                if (ListenerService.WEARABLE_DATA_PATH.equals(path)) {
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());

                    ArrayList<DataMap> arrayListDataMap = dataMapItem.getDataMap().getDataMapArrayList("stockDataMap");

                    if(arrayListDataMap != null && arrayListDataMap.size() > 0) {

                        mData.clear();

                        for (DataMap dm : arrayListDataMap) {


                            String productName = dm.getString("prodName");

                            Log.d(TAG, "DataMap  " + productName);

                            mData.add(new DemoItem(productName, new Intent(this, DetailActivity.class)));
                            //dm.getAsset("prodImage");
                        }

                        setupListView();
                    }
                    /*Asset photo = dataMapItem.getDataMap()
                            .getAsset(ListenerService.IMAGE_KEY);
                    final Bitmap bitmap = loadBitmapFromAsset(mGoogleApiClient, photo);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "Setting background image..");
                            //mLayout.setBackground(new BitmapDrawable(getResources(), bitmap));
                            image.setImageDrawable(new BitmapDrawable(getResources(), bitmap));
                            image.setVisibility(ImageView.VISIBLE);

                            loading.setVisibility(TextView.GONE);
                        }
                    });
                    */

                }

            }  else {
                Log.d(TAG, "Unknown data event type: " + event.getType());
            }
        }
    }

    /**
     * Extracts {@link android.graphics.Bitmap} data from the
     * {@link com.google.android.gms.wearable.Asset}
     */
    private Bitmap loadBitmapFromAsset(GoogleApiClient apiClient, Asset asset) {
        if (asset == null) {
            throw new IllegalArgumentException("Asset must be non-null");
        }

        InputStream assetInputStream = Wearable.DataApi.getFdForAsset(
                apiClient, asset).await().getInputStream();

        if (assetInputStream == null) {
            Log.w(TAG, "Requested an unknown Asset.");
            return null;
        }
        return BitmapFactory.decodeStream(assetInputStream);
    }

    @Override
    public void onPeerConnected(Node node) {
        Log.d(TAG, "node" + node.getId());
    }

    @Override
    public void onPeerDisconnected(Node node) {
        Log.d(TAG, "node Disconnected"+ node.getId());
    }


    class SendToDataLayerThread extends Thread {
        String path;
        String message;

        // Constructor to send a message to the data layer
        SendToDataLayerThread(String p, String msg) {
            path = p;
            message = msg;
        }

        public void run() {
            NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
            for (Node node : nodes.getNodes()) {
                MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), path, message.getBytes()).await();
                if (result.getStatus().isSuccess()) {
                    Log.v("myTag", "Message: {" + message + "} sent to: " + node.getDisplayName() + " , Id: " + node.getId());
                } else {
                    // Log an error
                    Log.v("myTag", "ERROR: failed to send Message");
                }
            }
        }
    }
    public static class DemoItem {
        private String mName;
        private Intent mIntent;

        public DemoItem(String name, Intent intent) {
            mName = name;
            mIntent = intent;
        }

        public String getName() {
            return mName;
        }

        public Intent getIntent() {
            return mIntent;
        }
    }
}
