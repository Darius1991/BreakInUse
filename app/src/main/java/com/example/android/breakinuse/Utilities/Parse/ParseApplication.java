package com.example.android.breakinuse.Utilities.Parse;

import android.app.Application;
import com.parse.Parse;
import com.parse.ParseCrashReporting;

public class ParseApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        ParseCrashReporting.enable(this);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
    }
}
