package com.hengtan.nanodegreeapp.stocount;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.HashMap;
import java.util.Map;

import retrofit.RetrofitError;
import retrofit.client.Response;
import walmart.webapi.android.ItemList;
import walmart.webapi.android.WalmartApi;
import walmart.webapi.android.WalmartService;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        WalmartApi testApi = new WalmartApi();

        WalmartService testService = testApi.getService();

        Map<String, Object> params = new HashMap<String, Object>();

        params.put("apiKey","fdzf42nwrkg8cwu3uzvx7mrs");
        params.put("upc", "038000786129");

        testService.getProduct(params, new retrofit.Callback<ItemList>() {
            @Override
            public void success(final ItemList result, Response response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                       // if(result != null && result.items != null && result.items.items != null && result.items.items.size() > 0) {
                            String prodName = result.items.get(0).name;
                        String xxx = result.items.get(0).thumbnailImage;
                      //  }

                    }
                });
            }

            @Override
            public void failure(final RetrofitError error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        String msg = error.getMessage();

                    }
                });
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
}
