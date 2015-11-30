package com.hengtan.nanodegreeapp.stocount.api;

import android.content.Context;

import com.hengtan.nanodegreeapp.stocount.search.SearchSuggestion;

import java.util.List;

/**
 * Created by htan on 30/11/2015.
 */
public interface ApiCall {

    List<SearchSuggestion> GetSuggestedItemName(String query, Context ctx);
    void SearchProduct(String barcodeScanResult, String barcodeFormatName, String itemId, Context ctx);
}
