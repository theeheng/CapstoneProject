package com.hengtan.nanodegreeapp.stocount.api;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import com.amazon.service.ecommerce.AWSECommerceClient;
import com.amazon.webservices.awsecommerceservice.Errors;
import com.amazon.webservices.awsecommerceservice.ImageSet;
import com.amazon.webservices.awsecommerceservice.ItemLookup;
import com.amazon.webservices.awsecommerceservice.ItemLookupRequest;
import com.amazon.webservices.awsecommerceservice.ItemLookupResponse;
import com.amazon.webservices.awsecommerceservice.ItemSearch;
import com.amazon.webservices.awsecommerceservice.ItemSearchRequest;
import com.amazon.webservices.awsecommerceservice.ItemSearchResponse;
import com.amazon.webservices.awsecommerceservice.Items;
import com.amazon.webservices.awsecommerceservice.Item;
import com.amazon.webservices.awsecommerceservice.client.AWSECommerceServicePortType_SOAPClient;
import com.hengtan.nanodegreeapp.stocount.DetailActivity;
import com.hengtan.nanodegreeapp.stocount.R;
import com.hengtan.nanodegreeapp.stocount.data.Product;
import com.hengtan.nanodegreeapp.stocount.search.SearchSuggestion;
import com.leansoft.nano.ws.SOAPServiceCallback;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by htan on 30/11/2015.
 */
public class AmazonApiCall extends BaseApiCall implements ApiCall {

    private String errorString;
    private String noBarcodeMatch;
    private String noNameMatch;
    private List<SearchSuggestion> searchResult = new ArrayList<SearchSuggestion>();

    @Override
    public List<SearchSuggestion> GetSuggestedItemName(final String query, final Context ctx) {
        Resources res = ctx.getResources();
        this.errorString = res.getString(R.string.api_search_error);
        this.noNameMatch = res.getString(R.string.api_search_no_product_found_name);
        // Get shared client
        AWSECommerceServicePortType_SOAPClient client = AWSECommerceClient.getSharedClient(res.getString(R.string.aws_accesskeyid), res.getString(R.string.aws_securekeyid));

        AWSECommerceClient.Client.setDebug(true);

        // Build request
        ItemSearch request = new ItemSearch();
        request.awsAccessKeyId = res.getString(R.string.aws_accesskeyid);
        request.associateTag = "tag"; // seems any tag is ok
        request.shared = new ItemSearchRequest();
        request.shared.searchIndex = "All";
        request.shared.responseGroup = new ArrayList<String>();
        request.shared.responseGroup.add("Images");
        request.shared.responseGroup.add("ItemAttributes");
        request.shared.responseGroup.add("EditorialReview");
        request.shared.keywords = query;

        // authenticate the request
        // http://docs.aws.amazon.com/AWSECommerceService/latest/DG/NotUsingWSSecurity.html
        AWSECommerceClient.authenticateRequest("ItemSearch");
        request.Signature = AWSECommerceClient.GetSignature();
        request.Timestamp = AWSECommerceClient.GetTimestamp();



        // make API call
        AWSECommerceClient.Client.itemSearch(request, new SOAPServiceCallback<ItemSearchResponse>() {

            @Override
            public void onSuccess(ItemSearchResponse responseObject) { // handle successful response

                // success handling logic
                if (responseObject.items != null && responseObject.items.size() > 0) {
                    Items items = responseObject.items.get(0);
                    if (items.item != null && items.item.size() > 0) {

                        /*Product prod = new Product(items.item.get(0));

                        prod.setBarcode(barcodeScanResult);
                        prod.setBarcodeFormat(barcodeFormatName);

                        Bundle bundle = new Bundle();
                        bundle.putParcelable(DetailActivity.PRODUCT_PARCELABLE, prod);
                        Intent intent = new Intent(ctx, DetailActivity.class);
                        intent.putExtra(DetailActivity.PRODUCT_PARCELABLE, bundle);
                        ctx.startActivity(intent);
                        */

                        int i = 0;

                        searchResult.clear();

                        for (Item s : items.item) {

                            SearchSuggestion ss = new SearchSuggestion();
                            ss.id = i;
                            ss.name = s.itemAttributes.title;
                            ss.additionalInfo = s.asin;

                            searchResult.add(ss);
                            i++;

                        }

                    } else {
                        DisplayToast(ctx, noNameMatch+query);
                    }
                } else { // response resident error
                    if (responseObject.operationRequest != null && responseObject.operationRequest.errors != null) {
                        Errors errors = responseObject.operationRequest.errors;
                        if (errors.error != null && errors.error.size() > 0) {
                            com.amazon.webservices.awsecommerceservice.errors.Error error = errors.error.get(0);
                            DisplayToast(ctx, error.message);
                        } else {
                            DisplayToast(ctx, noNameMatch+query);
                        }
                    } else {
                        DisplayToast(ctx, noNameMatch+query);
                    }
                }
            }

            @Override
            public void onFailure(Throwable error, String errorMessage) { // HTTP or parsing error

                //ALog.e(TAG, errorMessage);
                DisplayToast(ctx, errorString);
            }

            @Override
            public void onSOAPFault(Object soapFault) { // soap fault

                com.leansoft.nano.soap11.Fault fault = (com.leansoft.nano.soap11.Fault) soapFault;

                //ALog.e(TAG, fault.faultstring);
                DisplayToast(ctx, errorString);

            }

        });

        return searchResult;
    }

    @Override
    public void SearchProduct(final String barcodeScanResult, final String barcodeFormatName, String itemId, final Context ctx)
    {
        Resources res = ctx.getResources();
        this.errorString = res.getString(R.string.api_search_error);
        this.noBarcodeMatch = res.getString(R.string.api_search_no_product_found_barcode);

        // Get shared client
        AWSECommerceClient.getSharedClient(res.getString(R.string.aws_accesskeyid), res.getString(R.string.aws_securekeyid));

        AWSECommerceClient.Client.setDebug(true);

        // Build request
        ItemLookup request = new ItemLookup();
        request.awsAccessKeyId = res.getString(R.string.aws_accesskeyid);
        request.associateTag = "tag"; // seems any tag is ok
        request.shared = new ItemLookupRequest();
        request.shared.responseGroup = new ArrayList<String>();
        request.shared.responseGroup.add("Images");
        request.shared.responseGroup.add("ItemAttributes");
        request.shared.responseGroup.add("EditorialReview");
        List<String> itemLookup = new ArrayList<String>();

        if(barcodeScanResult != null && (!barcodeScanResult.isEmpty()))
        {
            itemLookup.add(barcodeScanResult);
            request.shared.itemId = itemLookup;

            if (barcodeFormatName.indexOf("EAN") > -1) {
                request.shared.idType = "EAN";
            } else if (barcodeFormatName.indexOf("UPC") > -1) {
                request.shared.idType = "UPC";
            }

            request.shared.searchIndex = "All";
        }
        else if (itemId != null && (!itemId.isEmpty())) {
            itemLookup.add(itemId);
            request.shared.itemId = itemLookup;
            request.shared.idType = "ASIN";
            request.shared.searchIndex = "";
        }

        // authenticate the request
        // http://docs.aws.amazon.com/AWSECommerceService/latest/DG/NotUsingWSSecurity.html
        AWSECommerceClient.authenticateRequest("ItemLookup");
        request.Signature = AWSECommerceClient.GetSignature();
        request.Timestamp = AWSECommerceClient.GetTimestamp();

        // make API call
        AWSECommerceClient.Client.itemLookup(request, new SOAPServiceCallback<ItemLookupResponse>() {

            @Override
            public void onSuccess(ItemLookupResponse responseObject) { // handle successful response

                // success handling logic
                if (responseObject.items != null && responseObject.items.size() > 0) {
                    Items items = responseObject.items.get(0);
                    if (items.item != null && items.item.size() > 0) {

                        Product prod = new Product(items.item.get(0));

                        prod.setBarcode(barcodeScanResult);
                        prod.setBarcodeFormat(barcodeFormatName);

                        Bundle bundle = new Bundle();
                        bundle.putParcelable(DetailActivity.PRODUCT_PARCELABLE, prod);
                        Intent intent = new Intent(ctx, DetailActivity.class);
                        intent.putExtra(DetailActivity.PRODUCT_PARCELABLE, bundle);
                        ctx.startActivity(intent);

                    } else {
                        DisplayToast(ctx, noBarcodeMatch+barcodeScanResult);
                    }
                } else { // response resident error
                    if (responseObject.operationRequest != null && responseObject.operationRequest.errors != null) {
                        Errors errors = responseObject.operationRequest.errors;
                        if (errors.error != null && errors.error.size() > 0) {
                            com.amazon.webservices.awsecommerceservice.errors.Error error = errors.error.get(0);
                            DisplayToast(ctx, error.message);
                        } else {
                            DisplayToast(ctx, noBarcodeMatch+barcodeScanResult);
                        }
                    } else {
                        DisplayToast(ctx, noBarcodeMatch+barcodeScanResult);
                    }
                }
            }

            @Override
            public void onFailure(Throwable error, String errorMessage) { // HTTP or parsing error

                //ALog.e(TAG, errorMessage);
                DisplayToast(ctx, errorString);
            }

            @Override
            public void onSOAPFault(Object soapFault) { // soap fault

                com.leansoft.nano.soap11.Fault fault = (com.leansoft.nano.soap11.Fault) soapFault;

                //ALog.e(TAG, fault.faultstring);

                DisplayToast(ctx, errorString);

            }

        });
    }
}
