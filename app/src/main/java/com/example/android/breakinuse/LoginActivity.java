package com.example.android.breakinuse;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
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
    private static final String LOGGEDIN_GOOGLE = "Logged in through Google";
    private static final String LOGGEDIN_FACEBOOK = "Logged in through FB";
    private static final String LOGGEDIN_EMAIL = "Logged in through Email";
    private static final String LOGGEDOUT = "Logged Out";
    private AccessTokenTracker mAccessTokenTracker;

    private GoogleApiClient mGoogleApiClient;
    private CallbackManager mCallbackManager = CallbackManager.Factory.create();

    @Override
    protected void onStart() {
        super.onStart();
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
        findViewById(R.id.sign_in_button_facebook).setOnClickListener(this);
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
                findViewById(R.id.sign_in_email_emailID).setVisibility(View.GONE);
                findViewById(R.id.sign_in_email_password).setVisibility(View.GONE);
                findViewById(R.id.sign_in_button_email).setVisibility(View.GONE);
                findViewById(R.id.sign_up_button_email).setVisibility(View.GONE);
                findViewById(R.id.sign_out_button).setVisibility(View.GONE);
                findViewById(R.id.sign_in_button_google).setVisibility(View.GONE);
                findViewById(R.id.sign_out_button).setVisibility(View.GONE);

                SharedPreferences sharedPreferences = getApplicationContext()
                        .getSharedPreferences(getString(R.string.preferences_key), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.login_method), LOGIN_METHOD);
                editor.apply();

                mAccessTokenTracker.startTracking();
                // TODO showSignedInUI();
            }

            @Override
            public void onCancel() {
                // TODO
            }

            @Override
            public void onError(FacebookException exception) {
                // TODO showError();
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
    public void onResume(){
        super.onResume();

        LOGIN_METHOD = (getApplicationContext()
                .getSharedPreferences(getString(R.string.preferences_key), Context.MODE_PRIVATE))
                .getString(getString(R.string.login_method),getString(R.string.logged_out));

        mAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    findViewById(R.id.sign_in_email_emailID).setVisibility(View.VISIBLE);
                    findViewById(R.id.sign_in_email_password).setVisibility(View.VISIBLE);
                    findViewById(R.id.sign_in_button_email).setVisibility(View.VISIBLE);
                    findViewById(R.id.sign_up_button_email).setVisibility(View.VISIBLE);
                    findViewById(R.id.sign_out_button).setVisibility(View.GONE);
                    findViewById(R.id.sign_in_button_google).setVisibility(View.VISIBLE);
                    LOGIN_METHOD = LOGGEDOUT;
                    SharedPreferences sharedPreferences = getApplicationContext()
                            .getSharedPreferences(getString(R.string.preferences_key), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(getString(R.string.login_method), LOGIN_METHOD);
                    editor.apply();
                    mAccessTokenTracker.stopTracking();
                }
            }
        };

        if (LOGIN_METHOD.equals(LOGGEDIN_GOOGLE)){

            findViewById(R.id.sign_in_email_emailID).setVisibility(View.GONE);
            findViewById(R.id.sign_in_email_password).setVisibility(View.GONE);
            findViewById(R.id.sign_in_button_email).setVisibility(View.GONE);
            findViewById(R.id.sign_up_button_email).setVisibility(View.GONE);
            findViewById(R.id.sign_in_button_google).setVisibility(View.GONE);
            findViewById(R.id.sign_in_button_facebook).setVisibility(View.GONE);

        } else if (LOGIN_METHOD.equals(LOGGEDIN_FACEBOOK)){

            findViewById(R.id.sign_in_email_emailID).setVisibility(View.GONE);
            findViewById(R.id.sign_in_email_password).setVisibility(View.GONE);
            findViewById(R.id.sign_in_button_email).setVisibility(View.GONE);
            findViewById(R.id.sign_up_button_email).setVisibility(View.GONE);
            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
            findViewById(R.id.sign_in_button_google).setVisibility(View.GONE);

        } else if (LOGIN_METHOD.equals(LOGGEDIN_EMAIL)){

            findViewById(R.id.sign_in_email_emailID).setVisibility(View.GONE);
            findViewById(R.id.sign_in_email_password).setVisibility(View.GONE);
            findViewById(R.id.sign_in_button_email).setVisibility(View.GONE);
            findViewById(R.id.sign_up_button_email).setVisibility(View.GONE);
            findViewById(R.id.sign_in_button_google).setVisibility(View.GONE);
            findViewById(R.id.sign_in_button_facebook).setVisibility(View.GONE);

        } else if (LOGIN_METHOD.equals(LOGGEDOUT)){

            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
        }


    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if (mAccessTokenTracker.isTracking()){
            mAccessTokenTracker.stopTracking();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

        if (LOGIN_METHOD.equals(LOGGEDOUT)){

            mShouldResolve = false;
            LOGIN_METHOD = LOGGEDIN_GOOGLE;
            findViewById(R.id.sign_in_email_emailID).setVisibility(View.GONE);
            findViewById(R.id.sign_in_email_password).setVisibility(View.GONE);
            findViewById(R.id.sign_in_button_email).setVisibility(View.GONE);
            findViewById(R.id.sign_up_button_email).setVisibility(View.GONE);
            findViewById(R.id.sign_in_button_google).setVisibility(View.GONE);
            findViewById(R.id.sign_in_button_facebook).setVisibility(View.GONE);
            findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);

            SharedPreferences sharedPreferences = getApplicationContext()
                    .getSharedPreferences(getString(R.string.preferences_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(getString(R.string.login_method), LOGIN_METHOD);
            editor.apply();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.sign_in_button_google) {
            onSignInClicked("Google");
        } else if (v.getId() == R.id.sign_in_button_email){
            onSignInClicked("Email");
        } else if ((v.getId() == R.id.sign_out_button) && (LOGIN_METHOD.equals(LOGGEDIN_GOOGLE))) {
            onSignOutClicked();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIsResolving = true;
                } catch (IntentSender.SendIntentException e) {
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

        if (requestCode == RC_SIGN_IN) {
            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
            }
            mIsResolving = false;
            mGoogleApiClient.connect();
        }
    }

    private void onSignInClicked(String signInMethod) {
        if (signInMethod.equals("Google")){
            mShouldResolve = true;
            mGoogleApiClient.connect();
        } else if (signInMethod.equals("Email")){
            //TODO Sign In Using Parse
        }
    }

    private void onSignOutClicked() {

        if (LOGIN_METHOD.equals(LOGGEDIN_GOOGLE)){
            if (mGoogleApiClient.isConnected()) {

                Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                mGoogleApiClient.disconnect();
                LOGIN_METHOD = LOGGEDOUT;
                findViewById(R.id.sign_in_email_emailID).setVisibility(View.VISIBLE);
                findViewById(R.id.sign_in_email_password).setVisibility(View.VISIBLE);
                findViewById(R.id.sign_in_button_email).setVisibility(View.VISIBLE);
                findViewById(R.id.sign_up_button_email).setVisibility(View.VISIBLE);
                findViewById(R.id.sign_in_button_google).setVisibility(View.VISIBLE);
                findViewById(R.id.sign_in_button_facebook).setVisibility(View.VISIBLE);
                findViewById(R.id.sign_out_button).setVisibility(View.GONE);
                //TODO showSignedOutUI();
            }

        } else if (LOGIN_METHOD.equals(LOGGEDIN_EMAIL)){
            LOGIN_METHOD = LOGGEDOUT;
            findViewById(R.id.sign_in_email_emailID).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_in_email_password).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_in_button_email).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_up_button_email).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_in_button_google).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_in_button_facebook).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
            //TODO showSignedOutUI();
        }
    }
}
