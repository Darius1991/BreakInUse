package com.example.android.breakinuse.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.example.android.breakinuse.R;
import com.example.android.breakinuse.dataSync.syncAdapter.BreakInUseSyncAdapter;
import com.parse.ParseUser;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
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

                if (ParseUser.getCurrentUser() != null){

                    ParseUser.logOut();

                }
                saveLoginMethodPreference(context, LOGGEDOUT);
                makeToast(context, "You are now signed out.", Toast.LENGTH_SHORT);

                break;

            case LOGGEDIN_FACEBOOK:

                if (ParseUser.getCurrentUser() != null){

                    ParseUser.logOut();

                }
                saveLoginMethodPreference(context, LOGGEDOUT);
                makeToast(context, "You are now signed out.", Toast.LENGTH_SHORT);
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

    public static String getYesterdayDate(){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = simpleDateFormat.format(new Date());
        String yesterdayDate = null;
        Calendar calendar = Calendar.getInstance();

        try {

            calendar.setTime(simpleDateFormat.parse(currentDate));
            calendar.add(Calendar.DATE, -1);
            yesterdayDate = simpleDateFormat.format(calendar.getTime());

        } catch (ParseException e) {

            Log.d(TAG, "Yesterday's date being returned as null.");
            e.printStackTrace();

        }

        return yesterdayDate;
    }

    public static void updateNewsFeed(Context context){

        BreakInUseSyncAdapter.syncImmediately(context);

    }

    public static class NewsArticleWithNewsFeedID{

        public JSONObject newsArticle;
        public int newsFeedID;

        public NewsArticleWithNewsFeedID(JSONObject tempNewsArticle, int tempNewsFeedID){

            this.newsArticle = tempNewsArticle;
            this.newsFeedID = tempNewsFeedID;

        }

    }

    public static String getImageURLFromMainHTML(String imageHTMLSource){

        StringBuilder imageSource = new StringBuilder(imageHTMLSource);
        int startIndex = imageSource.indexOf("http");
        int endIndex = imageSource.indexOf(".jpg");
        if (startIndex == -1){

            return "http://vignette3.wikia.nocookie.net/wiisportsresortwalkthrough/images/6/60/No_Image_Available.png";

        }
        if (endIndex == -1){

            endIndex = imageSource.indexOf(".jpeg");
            if (endIndex == -1){

                return "http://vignette3.wikia.nocookie.net/wiisportsresortwalkthrough/images/6/60/No_Image_Available.png";

            } else {

                imageSource = new StringBuilder(imageSource.substring(startIndex,endIndex+5));

            }

        }

        return imageSource.toString();

    }

}
