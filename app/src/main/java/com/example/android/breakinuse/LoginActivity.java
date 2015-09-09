package com.example.android.breakinuse;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.breakinuse.utilities.parse.SyncSettings;
import com.example.android.breakinuse.utilities.Utility;
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
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.Arrays;

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

    private GoogleApiClient mGoogleApiClient;
    private CallbackManager mCallbackManager = CallbackManager.Factory.create();

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
        findViewById(R.id.sign_up_button_email).setOnClickListener(this);
        findViewById(R.id.sign_in_button_email).setOnClickListener(this);
        findViewById(R.id.new_to_breakInUse).setOnClickListener(this);
        findViewById(R.id.existing_user_breakInUse).setOnClickListener(this);

        findViewById(R.id.sign_up_button_email).setVisibility(View.GONE);
        findViewById(R.id.existing_user_breakInUse).setVisibility(View.GONE);

        TextView textView = (TextView) findViewById(R.id.new_to_breakInUse);
        SpannableString content = new SpannableString(textView.getText());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textView.setText(content);

        textView = (TextView) findViewById(R.id.existing_user_breakInUse);
        content = new SpannableString(textView.getText());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textView.setText(content);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .build();

        LOGIN_METHOD = (getApplicationContext()
                .getSharedPreferences(getString(R.string.preferences_key), Context.MODE_PRIVATE))
                .getString(getString(R.string.login_method_key), getString(R.string.logged_out));

        LoginButton loginButton = (LoginButton) findViewById(R.id.sign_in_button_facebook);
        loginButton.setReadPermissions(Arrays.asList("email", "public_profile", "user_birthday"));

        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {

                saveSharedPreferences(LOGGEDIN_FACEBOOK);
                Utility.makeToast(getApplicationContext(),
                        "You have been signed in with Facebook.", Toast.LENGTH_SHORT);
                launchHomeActivity();
            }

            @Override
            public void onCancel() {
                Utility.makeToast(getApplicationContext(),
                        "The sign-in attempt was cancelled.",Toast.LENGTH_SHORT);
            }

            @Override
            public void onError(FacebookException exception) {
                Utility.makeToast(getApplicationContext(),
                        "We are facing trouble signing in. Please try again later.", Toast.LENGTH_SHORT);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login_activity, menu);
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

        if (LOGIN_METHOD.equals(LOGGEDOUT)){

            mShouldResolve = false;
            saveSharedPreferences(LOGGEDIN_GOOGLE);
            Utility.makeToast(getApplicationContext(),
                    "You are signed in with Google.", Toast.LENGTH_SHORT);
            launchHomeActivity();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        // TODO Handle Error
    }

    @Override
    public void onClick(View v) {

        if (!Utility.isNetworkAvailable(getApplicationContext())){
            Utility.makeToast(getApplicationContext(),
                    "We are not able to detect an internet connection. Please resolve this before trying to login again.",
                    Toast.LENGTH_LONG);
            return;
        }

        int viewId = v.getId();

        if (viewId == R.id.sign_in_button_google) {

            onSignInClicked("Google");

        } else if (viewId == R.id.sign_in_button_email){

            onSignInClicked("Email");

        } else if (viewId == R.id.sign_up_button_email){

            onSignUpClicked();

        } else if (viewId == R.id.new_to_breakInUse){

            findViewById(R.id.sign_up_button_email).setVisibility(View.VISIBLE);
            findViewById(R.id.existing_user_breakInUse).setVisibility(View.VISIBLE);
            findViewById(R.id.new_to_breakInUse).setVisibility(View.GONE);
            findViewById(R.id.sign_in_button_email).setVisibility(View.GONE);
            findViewById(R.id.sign_in_button_facebook).setVisibility(View.GONE);
            findViewById(R.id.sign_in_button_google).setVisibility(View.GONE);
            EditText editText = (EditText) findViewById(R.id.sign_in_email_password);
            editText.setHint("Set your password");

        } else if (viewId == R.id.existing_user_breakInUse){

            findViewById(R.id.sign_up_button_email).setVisibility(View.GONE);
            findViewById(R.id.existing_user_breakInUse).setVisibility(View.GONE);
            findViewById(R.id.new_to_breakInUse).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_in_button_email).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_in_button_email).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_in_button_facebook).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_in_button_google).setVisibility(View.VISIBLE);

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

                Utility.makeToast(getApplicationContext(),
                        "We are facing trouble signing in. Please try again later.", Toast.LENGTH_SHORT);

            }
        } else {

            Utility.makeToast(getApplicationContext(),
                    "You have been signed out.", Toast.LENGTH_SHORT);

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

            EditText editTextPassword = (EditText)findViewById(R.id.sign_in_email_password);
            String password = editTextPassword.getText().toString();
            if (password.length() <5){

                Utility.makeToast(getApplicationContext(),
                        "The password should have at least 5 characters.",Toast.LENGTH_SHORT);
                return;

            }
            EditText editTextEmail = (EditText)findViewById(R.id.sign_in_email_emailID);
            final String userEmailID = editTextEmail.getText().toString().trim();
            if (userEmailID.length() == 0){

                return;

            }

            ParseUser.logInInBackground(userEmailID,password, new LogInCallback() {
                public void done(ParseUser user, ParseException e) {
                    if (user != null) {

                        saveSharedPreferences(LOGGEDIN_EMAIL);
                        Utility.makeToast(getApplicationContext(),
                                "You are signed in.", Toast.LENGTH_SHORT);

                        SyncSettings preferencesUpdater = new SyncSettings();
                        preferencesUpdater.downloadSettingsFromCloud(getApplicationContext(),
                                                                        userEmailID);

                    } else {

                        Utility.makeToast(getApplicationContext(),
                                "We are facing trouble signing in. Please try again later.", Toast.LENGTH_SHORT);

                    }
                }
            });

        }
    }

    private void onSignUpClicked(){

        EditText editTextPassword = (EditText)findViewById(R.id.sign_in_email_password);
        String password = editTextPassword.getText().toString();
        if (password.length() <5){
            Utility.makeToast(getApplicationContext(),
                    "The password should have at least 5 characters.",Toast.LENGTH_SHORT);
            return;
        }

        EditText editTextEmail = (EditText)findViewById(R.id.sign_in_email_emailID);
        final String userEmailID = editTextEmail.getText().toString().trim();
        if (userEmailID.length() == 0){
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(userEmailID).matches()){
            return;
        }

        ParseUser user = new ParseUser();
        user.setUsername(userEmailID);
        user.setPassword(password);
        user.setEmail(userEmailID);
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    saveSharedPreferences(LOGGEDIN_EMAIL);
                    Utility.makeToast(getApplicationContext(), "You are signed in.", Toast.LENGTH_SHORT);

                    SyncSettings preferencesUpdater = new SyncSettings();
                    preferencesUpdater.uploadSettingsToCloud(getApplicationContext(), userEmailID);
                } else {
                    Utility.makeToast(getApplicationContext(),
                            "We are facing trouble signing in. Please try again later.", Toast.LENGTH_SHORT);
                }
            }
        });

    }

    private void saveSharedPreferences(String loginMethodValue){

        LOGIN_METHOD = loginMethodValue;
        SharedPreferences sharedPreferences = getApplicationContext()
                .getSharedPreferences(getString(R.string.preferences_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.login_method_key), LOGIN_METHOD);
        editor.apply();

    }

    private void launchHomeActivity(){

        Intent intent = NavUtils.getParentActivityIntent(this);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        NavUtils.navigateUpTo(this, intent);

    }

}
