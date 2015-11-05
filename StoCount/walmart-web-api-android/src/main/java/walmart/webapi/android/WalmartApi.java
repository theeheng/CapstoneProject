package walmart.webapi.android;


import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.android.MainThreadExecutor;

/**
 * Creates and configures a REST adapter for Tesco Web API.
 *
 * Basic usage:
 * TescoApi wrapper = new TescoApi();
 *
 * Setting access token is optional for certain endpoints
 * so if you know you'll only use the ones that don't require authorisation
 * you can skip this step:
 * wrapper.setAccessToken(authenticationResponse.getAccessToken());
 *
 * TescoService tesco = wrapper.getService();
 *
 * Product product = tesco.getProduct("2dIGnmEIy1WZIcZCFSj6i8");
 */
public class WalmartApi {

    /**
     * Main Walmart Web API endpoint
     */
    public static final String WALMART_WEB_API_ENDPOINT = "http://api.walmartlabs.com/v1";

    /**
     * The request interceptor that will add the header with OAuth
     * token to every request made with the wrapper.
     */
    private class WebApiAuthenticator implements RequestInterceptor {
        @Override
        public void intercept(RequestFacade request) {
            if (mAccessToken != null) {
                request.addHeader("Authorization", "Bearer " + mAccessToken);
            }
        }
    }

    private final WalmartService mWalmartService;

    private String mAccessToken;

    /**
     * Create instance of SpotifyApi with given executors.
     *
     * @param httpExecutor     executor for http request. Cannot be null.
     * @param callbackExecutor executor for callbacks. If null is passed than the same
     *                         thread that created the instance is used.
     */
    public WalmartApi(Executor httpExecutor, Executor callbackExecutor) {
        mWalmartService = init(httpExecutor, callbackExecutor);
    }

    private WalmartService init(Executor httpExecutor, Executor callbackExecutor) {

        final RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .setExecutors(httpExecutor, callbackExecutor)
                .setEndpoint(WALMART_WEB_API_ENDPOINT)
                .setRequestInterceptor(new WebApiAuthenticator())
                .build();

        return restAdapter.create(WalmartService.class);
    }

    /**
     * New instance of WalmartApi,
     * with single thread executor both for http and callbacks.
     */
    public WalmartApi() {
        Executor httpExecutor = Executors.newSingleThreadExecutor();
        MainThreadExecutor callbackExecutor = new MainThreadExecutor();
        mWalmartService = init(httpExecutor, callbackExecutor);
    }

    /**
     * Sets access token on the wrapper.
     * Use to set or update token with the new value.
     * If you want to remove token set it to null.
     *
     * @param accessToken The token to set on the wrapper.
     * @return The instance of the wrapper.
     */
    public WalmartApi setAccessToken(String accessToken) {
        mAccessToken = accessToken;
        return this;
    }

    /**
     * @return The WalmartApi instance
     */
    public WalmartService getService() {
        return mWalmartService;
    }
}