package com.hengtan.nanodegreeapp.stocount;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.GridViewPager;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class DetailActivity extends Activity {

    private Resources res;
    private GridViewPager pager;
    public static final int SPEECH_REQUEST_CODE = 0;
    private ProductDetailGridPagerAdapter gridPagerAdapter;
    private CustomFragment customFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        res = getResources();
        pager = (GridViewPager) findViewById(R.id.pager);
        customFragment = new CustomFragment();

        Intent intent = getIntent();

        final int prodId = intent.getIntExtra("prodId", 0);
        final String prodName = intent.getStringExtra("prodName");
        final String prodAdditionalInfo = intent.getStringExtra("prodInfo");
        final int prodCountId = intent.getIntExtra("prodCountId", 0);
        final Double currentCount = intent.getDoubleExtra("prodCurrentCount", 0);

        byte[] bytes = intent.getByteArrayExtra("prodImage");
        final Bitmap prodImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        //final Bitmap prodImage = (Bitmap) intent.getParcelableExtra("prodImage");

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                pager = (GridViewPager) stub.findViewById(R.id.pager);
                pager.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                    @Override
                    public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                        // Adjust page margins:
                        //   A little extra horizontal spacing between pages looks a bit
                        //   less crowded on a round display.
                        final boolean round = insets.isRound();
                        int rowMargin = res.getDimensionPixelOffset(R.dimen.page_row_margin);
                        int colMargin = res.getDimensionPixelOffset(round ?
                                R.dimen.page_column_margin_round : R.dimen.page_column_margin);
                        pager.setPageMargins(rowMargin, colMargin);

                        // GridViewPager relies on insets to properly handle
                        // layout for round displays. They must be explicitly
                        // applied since this listener has taken them over.
                        pager.onApplyWindowInsets(insets);
                        return insets;
                    }
                });

                gridPagerAdapter = new ProductDetailGridPagerAdapter(DetailActivity.this, getFragmentManager(), customFragment, prodId, prodName, prodAdditionalInfo,  prodCountId, currentCount, prodImage);

                pager.setAdapter(gridPagerAdapter);
                DotsPageIndicator dotsPageIndicator = (DotsPageIndicator) findViewById(R.id.page_indicator);
                dotsPageIndicator.setPager(pager);
            }
        });
    }

    @Override
    public void onResume() {

        if(gridPagerAdapter != null && customFragment != null) {
            gridPagerAdapter.UpdateQuantity(customFragment.getProductId(), customFragment.getCurrentCount());
        }

        super.onResume();
    }
}
