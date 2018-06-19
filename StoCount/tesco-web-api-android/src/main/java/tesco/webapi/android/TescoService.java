package tesco.webapi.android;

import java.util.Map;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;

/**
 * Created by hengtan on 04/11/2015.
 */
public interface TescoService {

    /**
     * Get item detail information that match barcode string or item id.
     *
     * @param options  The item query's option include: apikey , upc (upc barcode, ex: 885909456017), itemid (ex: 21805445)
     * @param callback Callback method
     * @see <a href="https://secure.techfortesco.com/tescoapiweb/wiki/login.html">LOGIN</a>
     */

    @GET("/groceryapi/RESTService.aspx?command=LOGIN")
    void getSessionKey(@Query(encodeValue = false, value = "email") String email, @Query("password") String password, @Query("developerkey") String developerKey, @Query("applicationKey") String applicationKey , Callback<TescoSessionKey> callback);

    @GET("/groceryapi/RESTService.aspx?command=LOGIN")
    TescoSessionKey getSessionKey(@Query(encodeValue = false, value = "email") String email, @Query("password") String password, @Query("developerkey") String developerKey, @Query("applicationKey") String applicationKey);

    /**
     * Get item that matches text or keyword string.
     *
     * @param options  The search query's options include: apikey, query (ex: "ipod"), sort (ex: "price"), ord (ex:"asc")
     * @param callback Callback method
     * @see <a href="https://secure.techfortesco.com/tescoapiweb/wiki/productsearch.html">PRODUCT SEARCH</a>
     */

    @GET("/grocery/products/?offset=0&limit=20")
    void productSearch(@Query("query") String searchText, Callback<TescoProductSearch> callback);

    @GET("/grocery/products/?offset=0&limit=20")
    TescoProductSearch productSearch(@Query("query") String searchText);

    @GET("/product/")
    void productBarcodeSearch(@Query("gtin") String ean13Barcode, @Query("tpnc") String itemId, Callback<TescoProductSearch> callback);

}
