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
     * Get Spotify catalog information about playlists that match a keyword string.
     *
     * @param q        The search query's keywords (and optional field filters and operators), for example "roadhouse+blues"
     * @param callback Callback method
     * @see <a href="https://developer.spotify.com/web-api/search-item/">Search for an Item</a>
     */

    @GET("/items")
    void getProduct(@QueryMap Map<String, Object> options, Callback<ItemList> callback);

    /**
     * Get Spotify catalog information about playlists that match a keyword string.
     *
     * @param q        The search query's keywords (and optional field filters and operators), for example "roadhouse+blues"
     * @param callback Callback method
     * @see <a href="https://developer.spotify.com/web-api/search-item/">Search for an Item</a>
     */

    @GET("/search")
    void searchProduct(@QueryMap Map<String, Object> options, Callback<ItemList> callback);
}

