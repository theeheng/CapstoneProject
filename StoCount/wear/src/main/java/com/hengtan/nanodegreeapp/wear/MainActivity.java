package com.hengtan.nanodegreeapp.wear;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements WearableListView.ClickListener  {

    private TextView mTextView;

    private DemoItemAdapter mAdapter;
    private List<DemoItem> mData = new ArrayList<DemoItem>();
    private WearableListView mListView;

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
        setupData();
        setupListView();
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
