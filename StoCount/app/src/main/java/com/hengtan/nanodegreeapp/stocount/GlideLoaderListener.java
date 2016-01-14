package com.hengtan.nanodegreeapp.stocount;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.Locale;

public class GlideLoaderListener<T, R> implements RequestListener<T, R> {

    Context ctx;
    int defaultImg;
    ImageView imgView;

    public GlideLoaderListener(Context c, int img, ImageView imgView)
    {
        this.ctx = c;
        this.defaultImg = img;
        this.imgView = imgView;
    }
    @Override
    public boolean onException(Exception e, Object model, Target target, boolean isFirstResource) {
        android.util.Log.d("GLIDE LoggingListener", String.format(Locale.ROOT,
                "onException(%s, %s, %s, %s)", e, model, target, isFirstResource), e);

        Glide.with(ctx).load(defaultImg).fitCenter().into(target);

        return true;
    }

    @Override
    public boolean onResourceReady(Object resource, Object model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
        android.util.Log.d("GLIDE LoggingListener", String.format(Locale.ROOT,
                "onResourceReady(%s, %s, %s, %s, %s)", resource, model, target, isFromMemoryCache, isFirstResource));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && imgView != null) {
            scheduleStartPostponedTransition(imgView, ((Activity) this.ctx));
        }

        return false;
    }

    private void scheduleStartPostponedTransition(final View sharedElement, final Activity ctx) {
        sharedElement.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        sharedElement.getViewTreeObserver().removeOnPreDrawListener(this);
                        ctx.startPostponedEnterTransition();
                        return true;
                    }
                });
    }
}
