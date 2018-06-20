package com.hengtan.nanodegreeapp.stocount.api;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Toast;

import com.hengtan.nanodegreeapp.stocount.Application;
import com.hengtan.nanodegreeapp.stocount.DetailActivity;
import com.hengtan.nanodegreeapp.stocount.R;
import com.hengtan.nanodegreeapp.stocount.data.Product;
import com.hengtan.nanodegreeapp.stocount.search.SearchSuggestion;

import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import tesco.webapi.android.TescoApi;
import tesco.webapi.android.TescoBarcodeProduct;
import tesco.webapi.android.TescoProduct;
import tesco.webapi.android.TescoProductSearch;
import tesco.webapi.android.TescoService;

/**
 * Created by htan on 30/11/2015.
 */
public class TescoApiCall extends BaseApiCall implements ApiCall {

    public List<SearchSuggestion> GetSuggestedItemName(String query, Context ctx)
    {
        Application.SetAPISearchInProgress(true);
        //previousQuery = query;
        Resources res = ctx.getResources();
        TescoApi testApi = new TescoApi();

        testApi.setAccessToken(res.getString(R.string.tesco_apiApplicationKey));

        TescoService testService = testApi.getService();

        Map<String, Object> params = new HashMap<String, Object>();

        try {
            TescoProductSearch result = testService.productSearch(query);

            if (result != null && result.getProducts() != null && result.getProducts().size() > 0) {

                List<SearchSuggestion> searchResult = new ArrayList<SearchSuggestion>();

                for (TescoProduct s : result.getProducts()) {

                    SearchSuggestion ss = new SearchSuggestion();
                    ss.id = Integer.parseInt(s.getProductId());
                    ss.name = s.getName();
                    ss.additionalInfo = (s.getDescription() != null && s.getDescription().size() > 0) ? s.getDescription().get(0) : "";

                    searchResult.add(ss);
                }

                Application.SetAPISearchInProgress(false);
                return searchResult;

            } else {
                //Toast.makeText(LoginActivity.this, "Product not found for name: ", Toast.LENGTH_SHORT).show();
            }

            Application.SetAPISearchInProgress(false);
        }
        catch(Exception ex)
        {
            Application.SetAPISearchInProgress(false);
            String err = ex.getMessage();
        }

        return null;
    }


    public void SearchProduct(final String barcodeScanResult, String barcodeFormatName, String itemId, final Context ctx)
    {
        final Resources res = ctx.getResources();
        final String noBarcodeMatch = res.getString(R.string.api_search_no_product_found_barcode);

        TescoApi testApi = new TescoApi();
        testApi.setAccessToken(res.getString(R.string.tesco_apiApplicationKey));

        final TescoService testService = testApi.getService();


            testService.productBarcodeSearch(barcodeScanResult, itemId,  new retrofit.Callback<TescoProductSearch>() {

                @Override
                public void success(final TescoProductSearch result, Response response) {

                    if (result != null && result.getBarcodeProduct() != null && result.getBarcodeProduct().size() > 0) {
                        GetProductDetail(testService, result.getBarcodeProduct().get(0), ctx);

                    } else {
                        DisplayToast(ctx, noBarcodeMatch + barcodeScanResult);
                    }

                }

                @Override
                public void failure(final RetrofitError error) {

                    String msg = error.getMessage();
                    DisplayToast(ctx, msg);
                }
            });
    }

    private void GetProductDetail(TescoService testService, final TescoBarcodeProduct prod, final Context ctx)
    {
        testService.productSearch(prod.getProductId(), new retrofit.Callback<TescoProductSearch>() {

            @Override
            public void success(final TescoProductSearch result, Response response) {


                if (result != null && result.getProducts() != null && result.getProducts().size() > 0) {

                /*String test = new String(((TypedByteArray) result.getBody()).getBytes());

                String divTag = "<div class=\"content\">";
                String divEndTag = "</div>";

                if (test.indexOf(divTag) > -1) {

                    int divIndex = test.indexOf(divTag) + divTag.length();

                    if (test.substring(divIndex).indexOf(divEndTag) > -1) {
                        int divEndIndex = test.substring(divIndex).indexOf(divEndTag) + divIndex;
                        prod.setDescription(Jsoup.parse(test.substring(divIndex, divEndIndex)).text().replaceAll("\\<.*?\\>", ""));
                    }
                }*/

                    TescoProduct temp = result.getProducts().get(0);
                    //Toast.makeText(ctx, "code: " + prod.getEANBarcode(), Toast.LENGTH_LONG).show();
                    temp.setEANBarcode(prod.getEANBarcode());
                    temp.setExtendedDescription((temp.getDescription() != null && temp.getDescription().size() > 0) ? temp.getDescription().get(0) : "");

                    Bundle bundle = new Bundle();
                    bundle.putParcelable(DetailActivity.PRODUCT_PARCELABLE, new Product(temp));
                    Intent intent = new Intent(ctx, DetailActivity.class);
                    intent.putExtra(DetailActivity.PRODUCT_PARCELABLE, bundle);
                    ctx.startActivity(intent);
                }
            }

            @Override
            public void failure ( final RetrofitError error){

                String msg = error.getMessage();
                DisplayToast(ctx, msg);
            }
        });
    }
}
