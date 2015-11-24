package com.hengtan.nanodegreeapp.stocount;

import android.content.Context;
import android.content.res.Resources;
import android.widget.Toast;

import retrofit.RetrofitError;
import retrofit.client.Response;
import tesco.webapi.android.TescoApi;
import tesco.webapi.android.TescoService;
import tesco.webapi.android.TescoSessionKey;

/**
 * Created by htan on 17/11/2015.
 */
public class Application extends android.app.Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

    }

    public static Context getContext(){
        return context;
    }


}
