package com.example.android.breakinuse;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
                                GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

    private boolean mIsResolving = false;
    private boolean mShouldResolve = false;
    private static final int RC_SIGN_IN = 0;
    private static final String TAG = LoginActivity.class.getName();
    private static String LOGIN_METHOD ;
    private static final String LOGGEDIN_GOOGLE = "Logged in with Google";
    private static final String LOGGEDIN_FACEBOOK = "Logged in with Facebok";
    private static final String LOGGEDIN_EMAIL = "Logged in with Email";
    private static final String LOGGEDOUT = "Logged Out";

    private GoogleApiClient mGoogleApiClient;
    private CallbackManager mCallbackManager = CallbackManager.Factory.create();

    @Override
    protected void onStart() {
        super.onStart();
        LOGIN_METHOD = LOGGEDOUT;
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_login);

        findViewById(R.id.sign_in_button_google).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .build();

        LoginButton loginButton = (LoginButton) findViewById(R.id.sign_in_button_facebook);
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                LOGIN_METHOD = LOGGEDIN_FACEBOOK;
                // TODO showSignedInUI();
            }

            @Override
            public void onCancel() {
                // TODO
            }

            @Override
            public void onError(FacebookException exception) {
                // TODO showSignedOutUI();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {

        Log.d(TAG, "onConnected:" + bundle);
        mShouldResolve = false;
        LOGIN_METHOD = LOGGEDIN_GOOGLE;
        // TODO showSignedInUI();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.sign_in_button_google) {
            onSignInClicked();
        }

        if (v.getId() == R.id.sign_out_button) {
            onSignOutClicked();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.d(TAG, "onConnectionFailed:" + connectionResult);

        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIsResolving = true;
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "Could not resolve ConnectionResult.", e);
                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }
            } else {
                // Could not resolve the connection result, show the user an
                // error dialog.
                // TODO showErrorDialog(connectionResult);
            }
        } else {
            // Show the signed-out UI
            // TODO showSignedOutUI();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);

        if (requestCode == RC_SIGN_IN) {

            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
            }
            mIsResolving = false;
            mGoogleApiClient.connect();
        }
    }

    private void onSignInClicked() {
        mShouldResolve = true;
        mGoogleApiClient.connect();
    }

    private void onSignOutClicked() {

        if (LOGIN_METHOD.equals(LOGGEDIN_GOOGLE)){

            if (mGoogleApiClient.isConnected()) {
                Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                mGoogleApiClient.disconnect();
                LOGIN_METHOD = LOGGEDOUT;
                //TODO showSignedOutUI();
            }

        } else if (LOGIN_METHOD.equals(LOGGEDIN_EMAIL)){
            LOGIN_METHOD = LOGGEDOUT;
            //TODO showSignedOutUI();
        }

    }
}
