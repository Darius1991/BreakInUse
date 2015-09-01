package com.example.android.breakinuse.Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.example.android.breakinuse.R;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Utility {

    private static final String TAG = Utility.class.getName();

    public static boolean logOut(Context context){

        if (!isNetworkAvailable(context)){

            makeToast(context,
                    "We are not able to detect an internet connection. Please resolve this before trying to signout again.",
                    Toast.LENGTH_LONG);
            return false;

        }

        String LOGIN_METHOD = (context
                .getSharedPreferences(context.getString(R.string.preferences_key), Context.MODE_PRIVATE))
                .getString(context.getString(R.string.login_method_key), context.getString(R.string.logged_out));
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

                    if (ParseUser.getCurrentUser() != null){

                        ParseUser.logOut();

                    }
                    saveLoginMethodPreference(context, LOGGEDOUT);
                    makeToast(context,"You are now signed out.",Toast.LENGTH_SHORT);

                }
                break;

            case LOGGEDIN_GOOGLE:

                googleApiClient.connect();
                if (googleApiClient.isConnected()){

                    Plus.AccountApi.clearDefaultAccount(googleApiClient);

                    if (ParseUser.getCurrentUser() != null){

                        ParseUser.logOut();

                    }
                    saveLoginMethodPreference(context, LOGGEDOUT);
                    makeToast(context, "You are now signed out.", Toast.LENGTH_SHORT);

                } else {

                    if (ParseUser.getCurrentUser() != null){

                        ParseUser.logOut();

                    }
                    saveLoginMethodPreference(context, LOGGEDOUT);
                    makeToast(context,"You are now signed out.",Toast.LENGTH_SHORT);

                }
                break;

            case LOGGEDIN_FACEBOOK:
                if (AccessToken.getCurrentAccessToken() != null){

                    LoginManager.getInstance().logOut();
                    if (ParseUser.getCurrentUser() != null){

                        ParseUser.logOut();

                    }
                    saveLoginMethodPreference(context, LOGGEDOUT);
                    makeToast(context, "You are now signed out.", Toast.LENGTH_SHORT);

                } else {

                    if (ParseUser.getCurrentUser() != null){

                        ParseUser.logOut();

                    }
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
        editor.putString(context.getString(R.string.login_method_key), value);
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

    public static class NewsFeedItem{

        public String webURL = null;
        public String apiURL = null;
        public String webTitle = null;
        public String trailText = null;
        public String articleID = null;
        public String sectionID = null;

    }

    public static Boolean isUserLoggedIn(Context context){

        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preferences_key),
                Context.MODE_PRIVATE);
        String LOGIN_METHOD = sharedPreferences.getString(context.getString(R.string.login_method_key),
                                context.getString(R.string.logged_out));
        return (!LOGIN_METHOD.equals(context.getString(R.string.logged_out)));

    }

    public static Set<String> getDefaultFavouriteTopicsSet (Context context){

        String[] defaultFavouriteTopicsArray = context
                .getResources().getStringArray(R.array.preferences_topics_entryValues);
        List<String> defaultFavouriteTopicsList = Arrays.asList(defaultFavouriteTopicsArray);
        Set<String> defaultFavouriteTopicsSet = new HashSet<>(defaultFavouriteTopicsList);
        return defaultFavouriteTopicsSet;

    }

    public static Set<String> getFavouriteTopicsSet (Context context){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> defaultFavouriteTopicsSet = getDefaultFavouriteTopicsSet(context);
        Set<String> favouriteTopicsSet = sharedPreferences.getStringSet(
                context.getString(R.string.preferences_topics_key),defaultFavouriteTopicsSet);
        return favouriteTopicsSet;

    }

    public static Boolean getIsNotificationsEnabled(Context context){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean isNotificationsEnabled = sharedPreferences.getBoolean(
                context.getString(R.string.preferences_notifications_key), true);
        return isNotificationsEnabled;

    }

    public static String getCurrentDate(){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = simpleDateFormat.format(new Date());
        return currentDate;

    }



}
