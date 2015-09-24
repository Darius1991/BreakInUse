package com.example.android.breakinuse;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.parse.SignUpCallback;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = LoginActivity.class.getName();
    private static String LOGIN_METHOD ;
    private Context mContext;
    private static final String LOGGEDIN_EMAIL = "Logged in through Email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mContext = this;

        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.loginActivity_toolBar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

        findViewById(R.id.sign_up_button_email).setOnClickListener(this);
        findViewById(R.id.sign_in_button_email).setOnClickListener(this);
        findViewById(R.id.new_to_breakInUse).setOnClickListener(this);
        findViewById(R.id.existing_user_breakInUse).setOnClickListener(this);
        findViewById(R.id.forgotPassword_breakInUse).setOnClickListener(this);
        findViewById(R.id.resetPassword_button_email).setOnClickListener(this);

        findViewById(R.id.sign_up_button_email).setVisibility(View.GONE);
        findViewById(R.id.existing_user_breakInUse).setVisibility(View.GONE);
        findViewById(R.id.resetPassword_button_email).setVisibility(View.GONE);

        TextView textView = (TextView) findViewById(R.id.new_to_breakInUse);
        SpannableString content = new SpannableString(textView.getText());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textView.setText(content);

        textView = (TextView) findViewById(R.id.existing_user_breakInUse);
        content = new SpannableString(textView.getText());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textView.setText(content);

        textView = (TextView) findViewById(R.id.forgotPassword_breakInUse);
        content = new SpannableString(textView.getText());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textView.setText(content);

        LOGIN_METHOD = (this.getSharedPreferences(getString(R.string.preferences_key), Context.MODE_PRIVATE))
                                .getString(getString(R.string.login_method_key), getString(R.string.logged_out));

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

        if (id == android.R.id.home){

            super.onBackPressed();
            return true;

        }

        if (id == R.id.action_settings) {

            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        if (!Utility.isNetworkAvailable(mContext)){
            Utility.makeToast(mContext,
                    "We are not able to detect an internet connection. Please resolve this before trying to login again.",
                    Toast.LENGTH_LONG);
            return;
        }

        int viewId = v.getId();

        if (viewId == R.id.sign_in_button_email){

            onSignInClicked("Email");

        } else if (viewId == R.id.sign_up_button_email){

            onSignUpClicked();

        } else if (viewId == R.id.new_to_breakInUse){

            findViewById(R.id.sign_up_button_email).setVisibility(View.VISIBLE);
            findViewById(R.id.existing_user_breakInUse).setVisibility(View.VISIBLE);
            findViewById(R.id.new_to_breakInUse).setVisibility(View.GONE);
            findViewById(R.id.sign_in_button_email).setVisibility(View.GONE);
            findViewById(R.id.forgotPassword_breakInUse).setVisibility(View.VISIBLE);
            findViewById(R.id.resetPassword_button_email).setVisibility(View.GONE);
            findViewById(R.id.sign_in_email_password).setVisibility(View.VISIBLE);
            EditText editText = (EditText) findViewById(R.id.sign_in_email_password);
            editText.setHint("Set your password");

        } else if (viewId == R.id.existing_user_breakInUse){

            findViewById(R.id.sign_up_button_email).setVisibility(View.GONE);
            findViewById(R.id.existing_user_breakInUse).setVisibility(View.GONE);
            findViewById(R.id.new_to_breakInUse).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_in_button_email).setVisibility(View.VISIBLE);
            findViewById(R.id.forgotPassword_breakInUse).setVisibility(View.VISIBLE);
            findViewById(R.id.resetPassword_button_email).setVisibility(View.GONE);
            findViewById(R.id.sign_in_email_password).setVisibility(View.VISIBLE);

        } else if (viewId == R.id.forgotPassword_breakInUse){

            findViewById(R.id.sign_up_button_email).setVisibility(View.GONE);
            findViewById(R.id.existing_user_breakInUse).setVisibility(View.VISIBLE);
            findViewById(R.id.new_to_breakInUse).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_in_button_email).setVisibility(View.GONE);
            findViewById(R.id.forgotPassword_breakInUse).setVisibility(View.GONE);
            findViewById(R.id.sign_in_email_password).setVisibility(View.GONE);
            findViewById(R.id.resetPassword_button_email).setVisibility(View.VISIBLE);


        } else if (viewId == R.id.resetPassword_button_email){

            onResetPasswordClicked();

        }

    }

    private void onResetPasswordClicked() {

        if (!Utility.isNetworkAvailable(mContext)){

            Utility.makeToast(mContext,
                    "We are not able to detect an internet connection. Please resolve this before trying to signout again.",
                    Toast.LENGTH_LONG);
            return;

        }

        EditText editTextEmail = (EditText)findViewById(R.id.sign_in_email_emailID);
        final String userEmailID = editTextEmail.getText().toString().trim();
        if (userEmailID.length() == 0){

            Utility.makeToast(mContext,
                    "The email ID entered is not valid.",Toast.LENGTH_SHORT);
            return;

        }else if (!Patterns.EMAIL_ADDRESS.matcher(userEmailID).matches()){

            Utility.makeToast(mContext,
                    "The email ID entered is not valid.",Toast.LENGTH_SHORT);
            return;

        }

        ParseUser.requestPasswordResetInBackground(userEmailID, new RequestPasswordResetCallback() {

            public void done(ParseException e) {

                if (e == null) {

                    Utility.makeToast(mContext, "An email containing reset-password link was sent to the email ID. Please reset your password through the sent link",
                                        Toast.LENGTH_SHORT);

                } else {

                    //TODO handleError

                }

            }

        });

    }

    private void onSignInClicked(String signInMethod) {


        if (!Utility.isNetworkAvailable(mContext)){

            Utility.makeToast(mContext,
                    "We are not able to detect an internet connection. Please resolve this before trying to signout again.",
                    Toast.LENGTH_LONG);
            return;

        }

        if (signInMethod.equals("Email")){

            EditText editTextPassword = (EditText)findViewById(R.id.sign_in_email_password);
            String password = editTextPassword.getText().toString();
            if (password.length() <5){

                Utility.makeToast(mContext,
                        "The password should have at least 5 characters.",Toast.LENGTH_SHORT);
                return;

            }

            EditText editTextEmail = (EditText)findViewById(R.id.sign_in_email_emailID);
            final String userEmailID = editTextEmail.getText().toString().trim();
            if (userEmailID.length() == 0){

                Utility.makeToast(mContext,
                        "The email ID entered is not valid.",Toast.LENGTH_SHORT);
                return;

            }else if (!Patterns.EMAIL_ADDRESS.matcher(userEmailID).matches()){

                Utility.makeToast(mContext,
                        "The email ID entered is not valid.",Toast.LENGTH_SHORT);
                return;

            }

            ParseUser.logInInBackground(userEmailID, password, new LogInCallback() {

                public void done(ParseUser user, ParseException e) {

                    if (user != null) {

                        saveSharedPreferences(LOGGEDIN_EMAIL);
                        Utility.makeToast(mContext,
                                "You are signed in.", Toast.LENGTH_SHORT);

                        SyncSettings preferencesUpdater = new SyncSettings();
                        preferencesUpdater.downloadSettingsFromCloud(mContext,
                                userEmailID);

                    } else {

                        Utility.makeToast(mContext,
                                "We are facing trouble signing in. Please try again later.", Toast.LENGTH_SHORT);

                    }

                }

            });

        }

    }

    private void onSignUpClicked(){

        if (!Utility.isNetworkAvailable(mContext)){

            Utility.makeToast(mContext,
                    "We are not able to detect an internet connection. Please resolve this before trying to signout again.",
                    Toast.LENGTH_LONG);
            return;

        }

        EditText editTextPassword = (EditText)findViewById(R.id.sign_in_email_password);
        String password = editTextPassword.getText().toString();
        if (password.length() <5){

            Utility.makeToast(mContext,
                    "The password should have at least 5 characters.",Toast.LENGTH_SHORT);
            return;

        }

        EditText editTextEmail = (EditText)findViewById(R.id.sign_in_email_emailID);
        final String userEmailID = editTextEmail.getText().toString().trim();
        if (userEmailID.length() == 0){

            Utility.makeToast(mContext,
                    "The email ID entered is not valid.",Toast.LENGTH_SHORT);
            return;

        } else if (!Patterns.EMAIL_ADDRESS.matcher(userEmailID).matches()){

            Utility.makeToast(mContext,
                    "The email ID entered is not valid.",Toast.LENGTH_SHORT);
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
                    Utility.makeToast(mContext, "You are signed in.", Toast.LENGTH_SHORT);

                    SyncSettings preferencesUpdater = new SyncSettings();
                    preferencesUpdater.uploadSettingsToCloud(mContext, userEmailID);

                } else {

                    Utility.makeToast(mContext,
                            "We are facing trouble signing in. Please try again later.", Toast.LENGTH_SHORT);

                }

            }

        });

    }

    private void saveSharedPreferences(String loginMethodValue){

        LOGIN_METHOD = loginMethodValue;
        SharedPreferences sharedPreferences = mContext
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
