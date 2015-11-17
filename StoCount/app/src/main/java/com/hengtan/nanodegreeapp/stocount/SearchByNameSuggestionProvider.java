package com.hengtan.nanodegreeapp.stocount;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tesco.webapi.android.TescoApi;
import tesco.webapi.android.TescoProduct;
import tesco.webapi.android.TescoProductSearch;
import tesco.webapi.android.TescoService;
import walmart.webapi.android.WalmartItemList;
import walmart.webapi.android.WalmartItems;
import walmart.webapi.android.WalmartApi;
import walmart.webapi.android.WalmartService;

/**
 * Created by htan on 06/11/2015.
 */
public class SearchByNameSuggestionProvider  extends ContentProvider {
    private static final String tag = "SuggestUrlProvider";
    public static String AUTHORITY = "com.hengtan.nanodegreeapp.stocount.SearchByNameSuggestionProvider";

    private static final int SEARCH_SUGGEST = 0;
    private static final int SHORTCUT_REFRESH = 1;
    private static final UriMatcher sURIMatcher = buildUriMatcher();

    List<SearchSuggestion> searchResult = null;
    private String previousQuery;

    private static final String[] COLUMNS = {
            "_id",  // must include this column
            SearchManager.SUGGEST_COLUMN_TEXT_1,
            SearchManager.SUGGEST_COLUMN_TEXT_2,
            SearchManager.SUGGEST_COLUMN_INTENT_DATA,
            SearchManager.SUGGEST_COLUMN_INTENT_ACTION,
            SearchManager.SUGGEST_COLUMN_SHORTCUT_ID
    };

    private static UriMatcher buildUriMatcher()
    {
        UriMatcher matcher =
                new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(AUTHORITY,
                SearchManager.SUGGEST_URI_PATH_QUERY,
                SEARCH_SUGGEST);
        matcher.addURI(AUTHORITY,
                SearchManager.SUGGEST_URI_PATH_QUERY +
                        "/*",
                SEARCH_SUGGEST);
        matcher.addURI(AUTHORITY,
                SearchManager.SUGGEST_URI_PATH_SHORTCUT,
                SHORTCUT_REFRESH);
        matcher.addURI(AUTHORITY,
                SearchManager.SUGGEST_URI_PATH_SHORTCUT +
                        "/*",
                SHORTCUT_REFRESH);
        return matcher;
    }

    @Override
    public boolean onCreate() {

        //lets not do anything in particular
        Log.d(tag, "onCreate called");
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection,
                        String selection, String[] selectionArgs,
                        String sortOrder)
    {
        Log.d(tag,"query called with uri:" + uri);
        Log.d(tag,"selection:" + selection);

        String query = selectionArgs[0];
        Log.d(tag,"query:" + query);

        switch (sURIMatcher.match(uri)) {
            case SEARCH_SUGGEST:
                Log.d(tag,"search suggest called");
                return getSuggestions(query);
            case SHORTCUT_REFRESH:
                Log.d(tag,"shortcut refresh called");
                return null;
            default:
                throw new IllegalArgumentException("Unknown URL " + uri);
        }
    }

    private Cursor getSuggestions(String query)
    {
        //List<StockCountItemSearchSuggestion> result;
        //List<String> result = new ArrayList<String>();

        if (query == null) return null;
        else if(query.length() <= 2) {
            return null;
        }
        else if(!query.equals(previousQuery) || (query.equals(previousQuery) &&  (searchResult == null || (searchResult != null && searchResult.size() == 0))))
        {
            //result = getSuggestedItemNameFromDB(query);
            getSuggestedItemNameFromTescoAPI(query);
            //result.add("Orange juice");
            //result.add("Apple juice");
            //result.add("Mango juice");
        }

        return createCursor();

    }

    private Cursor createCursor()
    {
        MatrixCursor cursor = null;

        if(searchResult != null) {

            cursor = new MatrixCursor(COLUMNS);

            for(SearchSuggestion s : searchResult) {
                cursor.addRow(createRow(s));
            }
        }

        return cursor;
    }

    private Object[] createRow(SearchSuggestion suggestion)
    {
        return columnValuesOfQuery(suggestion.id, //suggestion.SiteItemId,
                "android.intent.action.VIEW",
                "http://com.hengtan.nanodegreeapp.stocount.SearchByNameSuggestionProvider/" +suggestion.id, //suggestion.SiteItemId,
                suggestion.name,
                suggestion.category);
    }

    private Object[] columnValuesOfQuery(int siteItemId,
                                         String intentAction,
                                         String url,
                                         String text1,
                                         String text2)
    {
        String[] colValues = new String[] {
                Integer.toString(siteItemId),     // _id
                text1,     // text1
                text2,     // text2
                url,
                // intent_data (included when clicking on item)
                intentAction, //action
                SearchManager.SUGGEST_NEVER_MAKE_SHORTCUT
        };

        return colValues;
    }

    private Cursor refreshShortcut(String shortcutId,
                                   String[] projection) {
        return null;
    }

    public String getType(Uri uri) {
        switch (sURIMatcher.match(uri)) {
            case SEARCH_SUGGEST:
                return SearchManager.SUGGEST_MIME_TYPE;
            case SHORTCUT_REFRESH:
                return SearchManager.SHORTCUT_MIME_TYPE;
            default:
                throw
                        new IllegalArgumentException("Unknown URL " + uri);
        }
    }

    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException();
    }

    public int delete(Uri uri, String selection,
                      String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    public int update(Uri uri, ContentValues values,
                      String selection,
                      String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    private void getSuggestedItemNameFromWalmartAPI(String query)
    {

        WalmartApi testApi = new WalmartApi();

        WalmartService testService = testApi.getService();

        Map<String, Object> params = new HashMap<String, Object>();

        Resources res = getContext().getResources();

        params.put("apiKey",res.getString(R.string.walmart_apiKey));
        params.put("format", "json");
        params.put("query", query);

        try {
            WalmartItemList result = testService.searchProduct(params);

            if (result != null && result.items != null && result.items.size() > 0) {

                searchResult = new ArrayList<SearchSuggestion>();

                for (WalmartItems s : result.items) {

                    SearchSuggestion ss = new SearchSuggestion();
                    ss.id = s.itemId;
                    ss.name = s.name;
                    ss.category = s.categoryPath;

                    searchResult.add(ss);
                }

            } else {
                //Toast.makeText(LoginActivity.this, "Product not found for name: ", Toast.LENGTH_LONG).show();
            }
        }
        catch(Exception ex)
        {
            String err = ex.getMessage();
        }
    }


    private void getSuggestedItemNameFromTescoAPI(String query)
    {

        previousQuery = query;

        TescoApi testApi = new TescoApi();

        TescoService testService = testApi.getService();

        Map<String, Object> params = new HashMap<String, Object>();

        try {
            TescoProductSearch result = testService.productSearch(query, Application.getTescoApiSessionKey());

            if (result != null && result.getStatusCode() != null && result.getStatusCode() == 0 && result.getTotalProductCount() != null && result.getTotalProductCount() > 0 && result.getProducts() != null && result.getProducts().size() > 0) {

                searchResult = new ArrayList<SearchSuggestion>();

                for (TescoProduct s : result.getProducts()) {

                    SearchSuggestion ss = new SearchSuggestion();
                    ss.id = Integer.parseInt(s.getProductId());
                    ss.name = s.getName();
                    ss.category = s.getUnitType();

                    searchResult.add(ss);
                }

            } else {
                //Toast.makeText(LoginActivity.this, "Product not found for name: ", Toast.LENGTH_LONG).show();
            }
        }
        catch(Exception ex)
        {
            String err = ex.getMessage();
        }
    }

}

class SearchSuggestion
{
    String name;
    String category;
    int id;
}
