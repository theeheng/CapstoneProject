package com.hengtan.nanodegreeapp.stocount.api;

import android.content.Context;
import android.content.res.Resources;
import android.widget.Toast;

import com.amazon.service.ecommerce.AWSECommerceClient;
import com.amazon.webservices.awsecommerceservice.Errors;
import com.amazon.webservices.awsecommerceservice.ImageSet;
import com.amazon.webservices.awsecommerceservice.ItemLookup;
import com.amazon.webservices.awsecommerceservice.ItemLookupRequest;
import com.amazon.webservices.awsecommerceservice.ItemLookupResponse;
import com.amazon.webservices.awsecommerceservice.Items;
import com.amazon.webservices.awsecommerceservice.client.AWSECommerceServicePortType_SOAPClient;
import com.hengtan.nanodegreeapp.stocount.R;
import com.hengtan.nanodegreeapp.stocount.SearchSuggestion;
import com.leansoft.nano.log.ALog;
import com.leansoft.nano.ws.SOAPServiceCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by htan on 30/11/2015.
 */
public class AmazonApiCall implements ApiCall {
    @Override
    public List<SearchSuggestion> GetSuggestedItemName(String query, Context ctx) {
        return null;
    }

    @Override
    public void SearchProduct(String barcodeScanResult, String barcodeFormatName, String itemId, final Context ctx)
    {
        Resources res = ctx.getResources();

        // Get shared client
        AWSECommerceServicePortType_SOAPClient client = AWSECommerceClient.getSharedClient(res.getString(R.string.aws_accesskeyid), res.getString(R.string.aws_securekeyid));

        client.setDebug(true);

        // Build request
        ItemLookup request = new ItemLookup();
        request.associateTag = "tag"; // seems any tag is ok
        request.shared = new ItemLookupRequest();
        request.shared.searchIndex = "All";
        request.shared.responseGroup = new ArrayList<String>();
        request.shared.responseGroup.add("Images");
        request.shared.responseGroup.add("ItemAttributes");
        request.shared.responseGroup.add("EditorialReview");
        List<String> itemLookup = new ArrayList<String>();
        itemLookup.add(barcodeScanResult);
        request.shared.itemId = itemLookup;

        if(barcodeFormatName.indexOf("EAN")> -1) {

            if(barcodeFormatName.indexOf("ISBN") > -1) {
                request.shared.idType = "ISBN";
            }
            else {
                request.shared.idType = "EAN";
            }
        }else if(barcodeFormatName.indexOf("UPC")> -1) {
            request.shared.idType = "UPC";
        }

        // authenticate the request
        // http://docs.aws.amazon.com/AWSECommerceService/latest/DG/NotUsingWSSecurity.html
        AWSECommerceClient.authenticateRequest("ItemLookup");

        // make API call
        client.itemLookup(request, new SOAPServiceCallback<ItemLookupResponse>() {

            @Override
            public void onSuccess(ItemLookupResponse responseObject) { // handle successful response

                // success handling logic
                if (responseObject.items != null && responseObject.items.size() > 0) {
                    Items items = responseObject.items.get(0);
                    if (items.item != null && items.item.size() > 0) {

                        String name = items.item.get(0).itemAttributes.title;
                        String description = null;
                        String thumbnailUrl = null;

                        if(items.item.get(0).editorialReviews !=  null && items.item.get(0).editorialReviews.editorialReview != null && items.item.get(0).editorialReviews.editorialReview.size() > 0) {
                            description = items.item.get(0).editorialReviews.editorialReview.get(0).content;
                        }

                        if(items.item.get(0).imageSets !=  null && items.item.get(0).imageSets.size()  > 0 && items.item.get(0).imageSets.get(0).imageSet.size() > 0 && items.item.get(0).imageSets.get(0).imageSet.get(0).thumbnailImage != null) {

                            for(ImageSet imgset : items.item.get(0).imageSets.get(0).imageSet)
                                if(imgset.category.equals("primary"))
                                {
                                    thumbnailUrl = imgset.mediumImage.url; //imgset.thumbnailImage.url;
                                }
                        }

                        //UpdateUI(name, description, thumbnailUrl);

                    } else {
                        Toast.makeText(ctx, "No result", Toast.LENGTH_LONG).show();
                    }
                } else { // response resident error
                    if (responseObject.operationRequest != null && responseObject.operationRequest.errors != null) {
                        Errors errors = responseObject.operationRequest.errors;
                        if (errors.error != null && errors.error.size() > 0) {
                            com.amazon.webservices.awsecommerceservice.errors.Error error = errors.error.get(0);
                            Toast.makeText(ctx, error.message, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ctx, "No result", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(ctx, "No result", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Throwable error, String errorMessage) { // HTTP or parsing error

                //ALog.e(TAG, errorMessage);
                Toast.makeText(ctx, errorMessage, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSOAPFault(Object soapFault) { // soap fault

                com.leansoft.nano.soap11.Fault fault = (com.leansoft.nano.soap11.Fault) soapFault;

                //ALog.e(TAG, fault.faultstring);

                Toast.makeText(ctx, fault.faultstring, Toast.LENGTH_LONG).show();

            }

        });
    }
}
