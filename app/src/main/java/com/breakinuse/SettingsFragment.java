package com.breakinuse;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.PreferenceFragment;

import com.breakinuse.utilities.Utility;
import com.breakinuse.utilities.parse.SyncSettings;
import com.parse.ParseUser;

import java.util.Set;

public class SettingsFragment extends PreferenceFragment implements
                                                            SharedPreferences.OnSharedPreferenceChangeListener{

    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mContext = getActivity();
        addPreferencesFromResource(R.xml.preferences);

        MultiSelectListPreference topicsPreference = (MultiSelectListPreference) findPreference(getString(R.string.preferences_topics_key));
        Set<String> favouriteTopics = topicsPreference.getValues();
        topicsPreference.setSummary(favouriteTopics.size() + " topics are currently followed.");

    }

    @Override
    public void onResume() {

        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {

        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals(getString(R.string.preferences_notifications_key))){

            if (Utility.isUserLoggedIn(mContext)){

                SyncSettings preferencesUpdater = new SyncSettings();
                ParseUser currentUser = ParseUser.getCurrentUser();

                if (currentUser != null){

                    preferencesUpdater.modifySettingsInCloud(mContext,currentUser.getEmail());

                }

            }

        } else if (key.equals(getString(R.string.preferences_topics_key))) {

            MultiSelectListPreference topicsPreference = (MultiSelectListPreference) findPreference(key);
            Set<String> favouriteTopics = topicsPreference.getValues();
            topicsPreference.setSummary(favouriteTopics.size() + " topics are currently followed.");

            if (Utility.isUserLoggedIn(mContext)){

                SyncSettings preferencesUpdater = new SyncSettings();
                ParseUser currentUser = ParseUser.getCurrentUser();

                if (currentUser != null){

                    preferencesUpdater.modifySettingsInCloud(mContext,currentUser.getEmail());

                }

            }

        }

    }

}
