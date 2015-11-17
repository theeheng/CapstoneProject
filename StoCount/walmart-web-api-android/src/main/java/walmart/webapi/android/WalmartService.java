package walmart.webapi.android;

import java.util.Map;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.QueryMap;

/**
 * Created by htan on 05/11/2015.
 */
public interface WalmartService {


    /**
     * Get item detail information that match barcode string or item id.
     *
     * @param options  The item query's option include: apikey , upc (upc barcode, ex: 885909456017), itemid (ex: 21805445)
     * @param callback Callback method
     * @see <a href="https://developer.walmartlabs.com/docs/read/Product_Lookup_API__new">Product Lookup API</a>
     */

    @GET("/items")
    void getProduct(@QueryMap Map<String, Object> options, Callback<WalmartItemList> callback);

    /**
     * Get item that matches text or keyword string.
     *
     * @param options  The search query's options include: apikey, query (ex: "ipod"), sort (ex: "price"), ord (ex:"asc")
     * @param callback Callback method
     * @see <a href="https://developer.walmartlabs.com/docs/read/Search_API">Search API</a>
     */

    @GET("/search")
    void searchProduct(@QueryMap Map<String, Object> options, Callback<WalmartItemList> callback);

    @GET("/search")
    WalmartItemList searchProduct(@QueryMap Map<String, Object> options);
}

