package com.hengtan.nanodegreeapp.stocount;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;

/**
 * Created by htan on 17/06/2015.
 */
public final class ProgressBarHelper {

    public static void ShowProgressBar(FrameLayout progressBarHolder)
    {
        AlphaAnimation inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        progressBarHolder.setAnimation(inAnimation);
        progressBarHolder.setVisibility(View.VISIBLE);
    }

    public static void HideProgressBar(FrameLayout progressBarHolder)
    {
        AlphaAnimation outAnimation = new AlphaAnimation(1f, 0f);
        outAnimation.setDuration(200);
        progressBarHolder.setAnimation(outAnimation);
        progressBarHolder.setVisibility(View.GONE);
    }
}
