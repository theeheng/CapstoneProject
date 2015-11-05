package tesco.webapi.android;

import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by hengtan on 04/11/2015.
 */
public class TescoService {

    /**
     * Get a playlist owned by a Spotify user.
     *
     * @param userId     The user's Spotify user ID.
     * @param playlistId The Spotify ID for the playlist.
     * @param callback   Callback method
     * @see <a href="https://developer.spotify.com/web-api/get-playlist/">Get a Playlist</a>
     */
    //@GET("/users/{user_id}/playlists/{playlist_id}")
    //void getPlaylist(@Path("user_id") String userId, @Path("playlist_id") String playlistId, Callback<Playlist> callback);

    /**
     * Get Spotify catalog information about playlists that match a keyword string.
     *
     * @param q        The search query's keywords (and optional field filters and operators), for example "roadhouse+blues"
     * @param callback Callback method
     * @see <a href="https://developer.spotify.com/web-api/search-item/">Search for an Item</a>
     */
    //@GET("/search?type=playlist")
    //void searchPlaylists(@Query("q") String q, Callback<PlaylistsPager> callback);

}
