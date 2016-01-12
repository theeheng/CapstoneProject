package com.hengtan.nanodegreeapp.stocount;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.Locale;

public class GlideLoaderListener<T, R> implements RequestListener<T, R> {

    Context ctx;
    int defaultImg;

    public GlideLoaderListener(Context c, int img)
    {
        this.ctx = c;
        defaultImg = img;
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
        android.util.Log.d("GLIDE LoggingListener" , String.format(Locale.ROOT,
                "onResourceReady(%s, %s, %s, %s, %s)", resource, model, target, isFromMemoryCache, isFirstResource));
        return false;
    }
}
