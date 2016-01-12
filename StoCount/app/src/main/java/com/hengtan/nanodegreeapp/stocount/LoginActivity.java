package com.hengtan.nanodegreeapp.stocount;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.hengtan.nanodegreeapp.stocount.data.StoCountContract;
import com.hengtan.nanodegreeapp.stocount.data.StockPeriod;
import com.hengtan.nanodegreeapp.stocount.data.User;

import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, LoaderManager.LoaderCallbacks<Cursor> {

    private final String TAG = LoginActivity.class.getSimpleName();

    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;

    @InjectView(R.id.sign_in_button)
    protected SignInButton signInButton;

    private ProgressDialog mProgressDialog;

    private GoogleSignInAccount mAccount;

    private User mUser;

    private StockPeriod mStockPeriod;

    private boolean mIsOffline = false;

    // Identifies a particular Loader being used in this component
    private static final int USER_LOADER = 0;

    private static final int STOCK_PERIOD_LOADER = 1;

    private static final int OFFLINE_USER_LOADER = 2;

    private static final int UNKNOWN_LOGN_STATUS = 12501;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                //.enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addOnConnectionFailedListener(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Customize sign-in button. The sign-in button can be displayed in
        // multiple sizes and color schemes. It can also be contextually
        // rendered based on the requested scopes. For example. a red button may
        // be displayed when Google+ scopes are requested, but a white button
        // may be displayed when only basic profile is requested. Try adding the
        // Scopes.PLUS_LOGIN scope to the GoogleSignInOptions to see the
        // difference.

        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setColorScheme(SignInButton.COLOR_DARK);
        signInButton.setScopes(gso.getScopeArray());
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            showProgressDialog();
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                    hideProgressDialog();
                }
            }, 15, TimeUnit.SECONDS);

        }
    }

    @OnClick(R.id.sign_in_button)
    protected void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {

            mIsOffline = false;

            // Signed in successfully, show authenticated UI.
            mAccount = result.getSignInAccount();

            getLoaderManager().restartLoader(USER_LOADER, null, this);
        }
        else if (result.getStatus().getStatusCode() == UNKNOWN_LOGN_STATUS || result.getStatus().getStatusCode() == ConnectionResult.INTERRUPTED  || result.getStatus().getStatusCode() == ConnectionResult.TIMEOUT || result.getStatus().getStatusCode() == ConnectionResult.NETWORK_ERROR)
        {
            if(!Utilities.IsConnectedToInternet(this))
            {
                mIsOffline = true;
                getLoaderManager().restartLoader(OFFLINE_USER_LOADER, null, this);
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Toast.makeText(this,"onConnectionFailed:" + connectionResult ,Toast.LENGTH_SHORT);

        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        /*
         * Takes action based on the ID of the Loader that's being created
         */
        switch (id) {
            case USER_LOADER:
                // Returns a new CursorLoader
                return new CursorLoader(
                        this,
                        StoCountContract.UserEntry.CONTENT_URI,
                        null,
                        StoCountContract.UserEntry.GOOGLE_ID + " = ? ",
                        new String[]{mAccount.getId()},
                        null
                );
            case OFFLINE_USER_LOADER:
                // Returns a new CursorLoader
                return new CursorLoader(
                        this,
                        StoCountContract.UserEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null
                );
            case STOCK_PERIOD_LOADER:
                // Returns a new CursorLoader
                return new CursorLoader(
                        this,
                        StoCountContract.StockPeriodEntry.CONTENT_URI,
                        null,
                        StoCountContract.StockPeriodEntry.END_DATE + " IS NULL ",
                        null,
                        null
                );
            default:
                // An invalid id was passed in
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        switch (loader.getId()) {
            case USER_LOADER:
            case OFFLINE_USER_LOADER:

                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    mUser = new User(cursor);

                    //Load current stock period
                    getLoaderManager().restartLoader(STOCK_PERIOD_LOADER, null, this);

                } else if (mIsOffline){
                    Resources res = getResources();
                    Toast.makeText(this, res.getString(R.string.offline_firstime_text) , Toast.LENGTH_SHORT).show();

                } else {
                    mUser = new User(mAccount);
                    mUser.SaveUser(getContentResolver());

                    //Load current stock period
                    getLoaderManager().restartLoader(STOCK_PERIOD_LOADER, null, this);

                }

                break;

            case STOCK_PERIOD_LOADER:

                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    mStockPeriod = new StockPeriod(cursor);

                    Application.setCurrentStockPeriod(mStockPeriod);

                    Intent intent = new Intent(this, HomeActivity.class);
                    this.startActivity(intent);

                } else if (mUser != null) {

                    Application.setCurrentLoginUser(mUser);

                    Intent intent = new Intent(this, StockPeriodActivity.class);
                    this.startActivity(intent);
                }

                break;
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
