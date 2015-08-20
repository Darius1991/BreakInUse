package com.example.android.breakinuse.Utilities.Parse;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseCrashReporting;
import com.parse.ParseFacebookUtils;

public class OnStartApplication extends Application {
    @Override
    public void onCreate() {

        super.onCreate();
        final int FacebookLoginRequestCodeOffset = 28 ;
        ParseCrashReporting.enable(this);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
        ParseFacebookUtils.initialize(this,FacebookLoginRequestCodeOffset);

    }
}
