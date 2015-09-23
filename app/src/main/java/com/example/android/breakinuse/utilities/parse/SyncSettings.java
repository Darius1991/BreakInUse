package com.example.android.breakinuse.utilities.parse;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.android.breakinuse.NewsFeedActivity;
import com.example.android.breakinuse.R;
import com.example.android.breakinuse.utilities.Utility;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SyncSettings {

    private final String TAG = SyncSettings.class.getName();

    public void modifySettingsInCloud(final Context context, final String userEmailID){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Settings");
        query.whereEqualTo("userEmailID", userEmailID);
        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> list, ParseException e) {

                if (e == null) {

                    if (list.size() > 0) {

                        ParseObject settings = list.get(0);

                        Boolean isNotificationsEnabled = Utility.getIsNotificationsEnabled(context);
                        Set<String> favouriteTopicsSet = Utility.getFavouriteTopicsSet(context);

                        settings.put("userEmailID", userEmailID);
                        settings.put("isNotificationsEnabled", isNotificationsEnabled);
                        settings.remove("favouriteTopics");
                        settings.saveInBackground();
                        settings.addAllUnique("favouriteTopics", favouriteTopicsSet);
                        settings.saveInBackground();

                    }

                } else {

                    // TODO HandleError

                }

            }

        });

    }

    public void uploadSettingsToCloud(Context context, String userEmailID){

        ParseObject settings = new ParseObject("Settings");
        Boolean isNotificationsEnabled = Utility.getIsNotificationsEnabled(context);
        Set<String> favouriteTopicsSet = Utility.getFavouriteTopicsSet(context);

        settings.put("userEmailID", userEmailID);
        settings.put("isNotificationsEnabled", isNotificationsEnabled);
        settings.addAll("favouriteTopics", favouriteTopicsSet);
        settings.saveInBackground();
        launchHomeActivity(context);

    }

    public void downloadSettingsFromCloud(final Context context, String userEmailID){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Settings");
        query.whereEqualTo("userEmailID", userEmailID);
        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> list, ParseException e) {

                if (e == null) {
                    if (list.size() > 0) {

                        ParseObject settings = list.get(0);
                        Boolean isNotificationsEnabled = settings.getBoolean("isNotificationsEnabled");
                        List<String> favouriteTopicsList = settings.getList("favouriteTopics");
                        Set<String> favouriteTopicsSet = new HashSet<>(favouriteTopicsList);

                        SharedPreferences sharedPreferences = PreferenceManager.
                                getDefaultSharedPreferences(context);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(context.getString(R.string.preferences_notifications_key),
                                isNotificationsEnabled);
                        editor.putStringSet(context.getString(R.string.preferences_topics_key),
                                favouriteTopicsSet);
                        editor.apply();
                        launchHomeActivity(context);
                    }

                } else {

                    // TODO HandleError

                }

                 }
        });

    }

    private void launchHomeActivity(Context context){

        Intent intent = new Intent(context,NewsFeedActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

    }

}
