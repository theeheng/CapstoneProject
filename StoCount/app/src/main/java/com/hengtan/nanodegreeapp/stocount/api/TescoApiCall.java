package com.hengtan.nanodegreeapp.stocount.api;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.hengtan.nanodegreeapp.stocount.DetailActivity;
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
import tesco.webapi.android.TescoProduct;
import tesco.webapi.android.TescoProductSearch;
import tesco.webapi.android.TescoService;

/**
 * Created by htan on 30/11/2015.
 */
public class TescoApiCall extends BaseApiCall implements ApiCall {

    public List<SearchSuggestion> GetSuggestedItemName(String query, Context ctx)
    {
        //previousQuery = query;

        TescoApi testApi = new TescoApi();

        TescoService testService = testApi.getService();

        Map<String, Object> params = new HashMap<String, Object>();

        try {
            TescoProductSearch result = testService.productSearch(query);

            if (result != null && result.getStatusCode() != null && result.getStatusCode() == 0 && result.getTotalProductCount() != null && result.getTotalProductCount() > 0 && result.getProducts() != null && result.getProducts().size() > 0) {

                List<SearchSuggestion> searchResult = new ArrayList<SearchSuggestion>();

                for (TescoProduct s : result.getProducts()) {

                    SearchSuggestion ss = new SearchSuggestion();
                    ss.id = Integer.parseInt(s.getProductId());
                    ss.name = s.getName();
                    ss.additionalInfo = s.getPriceDescription();

                    searchResult.add(ss);
                }

                return searchResult;

            } else {
                //Toast.makeText(LoginActivity.this, "Product not found for name: ", Toast.LENGTH_SHORT).show();
            }
        }
        catch(Exception ex)
        {
            String err = ex.getMessage();
        }

        return null;
    }


    public void SearchProduct(String barcodeScanResult, String barcodeFormatName, String itemId, final Context ctx)
    {
        TescoApi testApi = new TescoApi();

        final TescoService testService = testApi.getService();

        final String searchText = (barcodeScanResult != null && !barcodeScanResult.isEmpty()) ? barcodeScanResult : itemId ;

        testService.productSearch(searchText, new retrofit.Callback<TescoProductSearch>() {
            @Override
            public void success(final TescoProductSearch result, Response response) {


                if (result != null && result.getStatusCode() != null && result.getStatusCode() == 0 && result.getTotalProductCount() != null && result.getTotalProductCount() > 0 && result.getProducts() != null && result.getProducts().size() > 0)
                {
                    final Product prod = new Product(result.getProducts().get(0));

                    TescoApi descriptionApi = new TescoApi();
                    TescoService descriptionService = descriptionApi.getService();

                    testService.productExtendedInfo(result.getProducts().get(0).getProductId(), new retrofit.Callback<Response>() {

                        @Override
                        public void success(final Response result, Response response) {


                                    String test = new String(((TypedByteArray) result.getBody()).getBytes());

                                    String divTag = "<div class=\"content\">";
                                    String divEndTag = "</div>";

                                    if (test.indexOf(divTag) > -1) {

                                        int divIndex = test.indexOf(divTag) + divTag.length();

                                        if (test.substring(divIndex).indexOf(divEndTag) > -1) {
                                            int divEndIndex = test.substring(divIndex).indexOf(divEndTag) + divIndex;
                                            prod.setDescription(Jsoup.parse(test.substring(divIndex, divEndIndex)).text().replaceAll("\\<.*?\\>", ""));
                                        }
                                    }
                                    Bundle bundle = new Bundle();
                                    bundle.putParcelable(DetailActivity.PRODUCT_PARCELABLE, prod);
                                    Intent intent = new Intent(ctx, DetailActivity.class);
                                    intent.putExtra(DetailActivity.PRODUCT_PARCELABLE, bundle);
                                    ctx.startActivity(intent);

                        }

                        @Override
                        public void failure(final RetrofitError error) {

                                    String msg = error.getMessage();
                                    DisplayToast(ctx, msg);
                        }
                    });

                } else {
                    DisplayToast(ctx, "Product not found for : " + searchText);
                }
            }

            @Override
            public void failure(final RetrofitError error) {

                        String msg = error.getMessage();
                        DisplayToast(ctx, msg);
            }
        });
    }
}
