package com.hengtan.nanodegreeapp.stocount;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.jsoup.Jsoup;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RetrofitError;
import retrofit.client.Response;
import walmart.webapi.android.ItemList;
import walmart.webapi.android.WalmartApi;
import walmart.webapi.android.WalmartService;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "INTENT_TO_SCAN_ACTIVITY";

    @InjectView(R.id.helloWorldTextView)
    protected TextView txtView;

    @InjectView(R.id.scanButton)
    protected Button btnScan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.inject(this);

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This is the callback method that the system will invoke when your button is
                // clicked. You might do this by launching another app or by including the
                //functionality directly in this app.
                // Hint: Use a Try/Catch block to handle the Intent dispatch gracefully, if you
                // are using an external app.
                //when you're done, remove the toast below.
                try {
                    IntentIntegrator intentIntegrator = new IntentIntegrator(LoginActivity.this);
                    //intentIntegrator.setOrientationLocked(false);
                    intentIntegrator.initiateScan();
                } catch (Exception ex) {
                    Log.e(TAG, "Error loading barcode scanning :" + ex.getMessage());
                }
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {

            final String barcodeScanResult = result.getContents();

            if (barcodeScanResult == null) {
                Log.d(TAG, "Cancelled scan");
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Log.d(TAG, "Scanned : " + barcodeScanResult);

                WalmartApi testApi = new WalmartApi();

                WalmartService testService = testApi.getService();

                Map<String, Object> params = new HashMap<String, Object>();

                Resources res = getResources();

                params.put("apiKey",res.getString(R.string.apiKey));
                params.put("upc", barcodeScanResult);

                testService.getProduct(params, new retrofit.Callback<ItemList>() {
                    @Override
                    public void success(final ItemList result, Response response) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if(result != null && result.items != null && result.items.size() > 0) {
                                    String name = result.items.get(0).name;
                                    String description = (result.items.get(0).shortDescription == null) ? result.items.get(0).longDescription : result.items.get(0).shortDescription;

                                    if(description != null) {
                                        description = Jsoup.parse(description).text().replaceAll("\\<.*?\\>", "");

                                        txtView.setText(name + " - " + description);
                                    }
                                    else
                                    {
                                        txtView.setText(name);
                                    }
                                }
                                else {
                                    Toast.makeText(LoginActivity.this, "Product not found for barcode: " + barcodeScanResult, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }

                    @Override
                    public void failure(final RetrofitError error) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String msg = error.getMessage();
                                Toast.makeText(LoginActivity.this, msg , Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            }
        } else {
            Log.d(TAG, "Weird");
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
