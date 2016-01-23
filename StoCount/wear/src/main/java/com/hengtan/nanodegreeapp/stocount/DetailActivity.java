package com.hengtan.nanodegreeapp.stocount;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.GridViewPager;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.view.WindowInsets;
import android.widget.TextView;

public class DetailActivity extends Activity {

    private Resources res;
    private GridViewPager pager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        res = getResources();
        pager = (GridViewPager) findViewById(R.id.pager);

        Intent intent = getIntent();

        final String prodName = intent.getStringExtra("prodName");
        final Double currentCount = intent.getDoubleExtra("prodCurrentCount", 0);

        Bundle b = intent.getBundleExtra("prodImage");

        final Bitmap prodImage = (Bitmap) b.get("prodImage");

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
                pager.setAdapter(new ProductDetailGridPagerAdapter(DetailActivity.this, getFragmentManager(), prodName, currentCount, prodImage));
                DotsPageIndicator dotsPageIndicator = (DotsPageIndicator) findViewById(R.id.page_indicator);
                dotsPageIndicator.setPager(pager);
            }
        });
    }
}
