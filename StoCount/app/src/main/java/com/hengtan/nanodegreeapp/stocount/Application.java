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
    private static String tescoApiSessionKey;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        InitialiseTescoSessionKey();
    }

    public static String getTescoApiSessionKey()
    {
        return tescoApiSessionKey;
    }

    public static Context getContext(){
        return context;
    }

    public static void InitialiseTescoSessionKey()
    {
        TescoApi testApi = new TescoApi();

        TescoService testService = testApi.getService();

        Resources res = context.getResources();

        testService.getSessionKey(res.getString(R.string.tesco_apiEmail), res.getString(R.string.tesco_apiPassword), res.getString(R.string.tesco_apiDeveloperKey), res.getString(R.string.tesco_apiApplicationKey), new retrofit.Callback<TescoSessionKey>() {
            @Override
            public void success(final TescoSessionKey result, Response response) {

                tescoApiSessionKey = result.getSessionKey();

                Toast.makeText(context, "sessionkey  : " + tescoApiSessionKey, Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(final RetrofitError error) {
                String msg = error.getMessage();
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();

            }
        });
    }
}
