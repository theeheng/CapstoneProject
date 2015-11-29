package com.hengtan.nanodegreeapp.stocount;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

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

    public static void Logout(GoogleApiClient googleApiClient, final Activity activity)
    {
        try {
            Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            Intent intent = new Intent(activity, LoginActivity.class);
                            activity.startActivity(intent);
                        }
                    });
        }
        catch (Exception ex)
        {
            String msg = ex.getMessage();
        }
    }

}
