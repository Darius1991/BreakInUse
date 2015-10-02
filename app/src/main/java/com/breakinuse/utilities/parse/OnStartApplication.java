package com.breakinuse.utilities.parse;

import android.app.Application;
import com.crashlytics.android.Crashlytics;
import com.parse.Parse;
import com.parse.ParseCrashReporting;
import io.fabric.sdk.android.Fabric;

public class OnStartApplication extends Application {

    @Override
    public void onCreate() {

        super.onCreate();
        Fabric.with(this, new Crashlytics());
        ParseCrashReporting.enable(this);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this);

    }

}
