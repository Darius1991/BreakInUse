package com.example.android.breakinuse.Utilities.Parse;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import com.example.android.breakinuse.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.parse.Parse;
import com.parse.ParseCrashReporting;

import static android.app.PendingIntent.getActivity;
import static android.content.Context.MODE_PRIVATE;

public class OnStartApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ParseCrashReporting.enable(this);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
    }
}
