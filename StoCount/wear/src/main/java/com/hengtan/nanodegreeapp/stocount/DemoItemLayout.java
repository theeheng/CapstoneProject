package com.hengtan.nanodegreeapp.stocount;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.wearable.view.WearableListView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DemoItemLayout extends LinearLayout implements WearableListView.OnCenterProximityListener {

    private static final float ALPHA_MAX = 1.0f;
    private static final float ALPHA_MIN = 0.7f;
    private static final float SCALE_MAX = 1.0f;
    private static final float SCALE_MIN = 0.7f;
    private static final int ANIMATION_DURATION = 100;

    private int mColorBlue;
    private int mColorGreen;

    private ImageView mCircle;
    private TextView mText;

    public DemoItemLayout(Context context) {
        super(context);
        init();
    }

    public DemoItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DemoItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_demo_item_layout_content, this, true);
        mCircle = (ImageView) findViewById(R.id.circle);
        mText = (TextView) findViewById(R.id.name);
        mColorBlue = getResources().getColor(R.color.demo_blue);
        mColorGreen = getResources().getColor(R.color.demo_green);

        // Initialize view with non-center position values
        mCircle.setScaleX(SCALE_MIN);
        mCircle.setScaleY(SCALE_MIN);
        mText.setAlpha(ALPHA_MIN);
    }

    // WearableListView.Item methods //

    @Override
    public void onCenterPosition(boolean animate) {
        ((GradientDrawable) mCircle.getDrawable()).setColor(mColorBlue);
        if (animate) {
            mCircle.animate()
                    .scaleX(SCALE_MAX)
                    .scaleY(SCALE_MAX)
                    .setDuration(ANIMATION_DURATION)
                    .start();
            mText.animate()
                    .alpha(ALPHA_MAX)
                    .setDuration(ANIMATION_DURATION)
                    .start();
        } else {
            mCircle.setScaleX(SCALE_MAX);
            mCircle.setScaleY(SCALE_MAX);
            mText.setAlpha(ALPHA_MAX);
        }
    }

    @Override
    public void onNonCenterPosition(boolean animate) {
        ((GradientDrawable) mCircle.getDrawable()).setColor(mColorGreen);
        if (animate) {
            mCircle.animate()
                    .scaleX(SCALE_MIN)
                    .scaleY(SCALE_MIN)
                    .setDuration(ANIMATION_DURATION)
                    .start();
            mText.animate()
                    .alpha(ALPHA_MIN)
                    .setDuration(ANIMATION_DURATION)
                    .start();
        } else {
            mCircle.setScaleX(SCALE_MIN);
            mCircle.setScaleY(SCALE_MIN);
            mText.setAlpha(ALPHA_MIN);
        }
    }
}
