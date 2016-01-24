package com.hengtan.nanodegreeapp.stocount;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
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

    private ProductItemAdapter mAdapter;
    private ArrayList<ProductItem> mData = new ArrayList<ProductItem>();
    private WearableListView mListView;
    private GoogleApiClient mGoogleApiClient;
    private Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        setContentView(R.layout.activity_main);

       final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mListView = (WearableListView) stub.findViewById(R.id.wearable_list);
                setupListView();
            }
        });


        mListView = (WearableListView) findViewById(R.id.wearable_list);

        mGoogleApiClient =  new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    }


    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {
        ProductItem item = mData.get(viewHolder.getPosition());
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("prodName", item.getName());
        intent.putExtra("prodInfo", item.getAdditionalInfo());

        Double currentCount = 0.0;

        if(item.getQuantity() != null)
        {
            currentCount = item.getQuantity();
        }

        intent.putExtra("prodCurrentCount", currentCount);

        Bundle bundle = new Bundle();
        bundle.putParcelable("prodImage", item.getThumbnail());
        intent.putExtra("prodImage", bundle);

        startActivity(intent);
    }

    @Override
    public void onTopEmptyRegionClick() {
        // For now, do nothing
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume : ");
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        Wearable.NodeApi.removeListener(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
    }

    public void setupListView() {

        mAdapter = new ProductItemAdapter(mData);
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

        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                String path = event.getDataItem().getUri().getPath();
                if (ListenerService.WEARABLE_DATA_PATH.equals(path)) {
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());

                    ArrayList<DataMap> arrayListDataMap = dataMapItem.getDataMap().getDataMapArrayList("stockDataMap");

                    if(arrayListDataMap != null && arrayListDataMap.size() > 0) {

                        mData.clear();

                        for(int i=0 ;i < arrayListDataMap.size() ; i++) {
                            mData.add(i , null);
                        }

                        for (DataMap dm : arrayListDataMap) {

                            int index = dm.getInt("prodIndex");

                            Asset imgAsset = dm.getAsset("prodImage");

                            Log.d(TAG, "DataMap  " + dm.getString("prodName"));

                            Bitmap thumbnail = loadBitmapFromAsset(mGoogleApiClient, imgAsset);

                            mData.set(index, new ProductItem(dm.getString("prodName"), dm.getString("prodInfo"), thumbnail, dm.getInt("prodCountId"), dm.getDouble("prodQuantity")));

                        }

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.swapItem(mData);
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                    }
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

    public static class ProductItem {
        private String mName;
        private Integer mProductCountId;
        private Double mQuantity;
        private Bitmap mThumbnail;
        private String mAdditionalInfo;

        public ProductItem(String name, String additionalInfo, Bitmap thumbnail, Integer productCountId, Double quantity) {
            mName = name;
            mAdditionalInfo = additionalInfo;
            mThumbnail = thumbnail;
            mProductCountId = productCountId;
            mQuantity = quantity;
        }

        public String getName() {
            return mName;
        }

        public String getAdditionalInfo() {
            return mAdditionalInfo;
        }

        public Bitmap getThumbnail() {
            return mThumbnail;
        }

        public Double getQuantity() { return mQuantity; }

        public Integer getProductCountId() { return mProductCountId; }

    }
}
