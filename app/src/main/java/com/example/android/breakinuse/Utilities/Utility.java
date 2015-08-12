package com.example.android.breakinuse.Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.example.android.breakinuse.R;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.parse.ParseUser;

public class Utility {

    private static final String TAG = Utility.class.getName();

    public static boolean logOut(Context context){

        if (!isNetworkAvailable(context)){
            makeToast(context,
                    "We are not able to detect an internet connection. Please resolve this begore trying to signout again.",
                    Toast.LENGTH_LONG);
            return false;
        }

        String LOGIN_METHOD = (context
                .getSharedPreferences(context.getString(R.string.preferences_key), Context.MODE_PRIVATE))
                .getString(context.getString(R.string.login_method), context.getString(R.string.logged_out));
        final String LOGGEDIN_GOOGLE = "Logged in through Google";
        final String LOGGEDIN_FACEBOOK = "Logged in through FB";
        final String LOGGEDIN_EMAIL = "Logged in through Email";
        final String LOGGEDOUT = "Logged Out";
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                                .addApi(Plus.API)
                                .addScope(new Scope(Scopes.PROFILE))
                                .build();

        switch (LOGIN_METHOD){
            case LOGGEDIN_EMAIL:
                if (ParseUser.getCurrentUser() != null){
                    ParseUser.logOut();
                    saveLoginMethodPreference(context, LOGGEDOUT);
                    makeToast(context, "You are now signed out.", Toast.LENGTH_SHORT);
                } else {
                    saveLoginMethodPreference(context, LOGGEDOUT);
                    makeToast(context,"You are now signed out.",Toast.LENGTH_SHORT);
                }
                break;
            case LOGGEDIN_GOOGLE:
                googleApiClient.connect();
                if (googleApiClient.isConnected()){
                    Plus.AccountApi.clearDefaultAccount(googleApiClient);
                    saveLoginMethodPreference(context, LOGGEDOUT);
                    makeToast(context, "You are now signed out.", Toast.LENGTH_SHORT);
                } else {
                    saveLoginMethodPreference(context, LOGGEDOUT);
                    makeToast(context,"You are now signed out.",Toast.LENGTH_SHORT);
                }
                break;
            case LOGGEDIN_FACEBOOK:
                if (AccessToken.getCurrentAccessToken() != null){
                    LoginManager.getInstance().logOut();
                    saveLoginMethodPreference(context, LOGGEDOUT);
                    makeToast(context, "You are now signed out.", Toast.LENGTH_SHORT);
                } else {
                    saveLoginMethodPreference(context, LOGGEDOUT);
                    makeToast(context,"You are now signed out.",Toast.LENGTH_SHORT);
                }
                break;
            case LOGGEDOUT:
                break;

        }
        return true;

    }

    public static void saveLoginMethodPreference(Context context, String value){
        SharedPreferences sharedPreferences = context
                .getSharedPreferences(context.getString(R.string.preferences_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.login_method), value);
        editor.apply();
    }

    public static void makeToast(Context context, String toastText, int toastDuration){
        Toast.makeText(context, toastText, toastDuration).show();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
