package com.example.android.breakinuse.utilities.parse;

import android.app.Application;
import com.parse.Parse;
import com.parse.ParseCrashReporting;

public class OnStartApplication extends Application {

    @Override
    public void onCreate() {

        super.onCreate();
        ParseCrashReporting.enable(this);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this);

    }

}
