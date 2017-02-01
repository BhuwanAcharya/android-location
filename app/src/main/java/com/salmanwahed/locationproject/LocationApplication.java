package com.salmanwahed.locationproject;

import android.app.Application;
import android.content.SharedPreferences;

/**
 * Created by salman on 17-Dec-16.
 */

public class LocationApplication extends Application {
    private SharedPreferences sharedPreferences;
    private static LocationApplication application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        sharedPreferences = getSharedPreferences(getString(R.string.app_preference), MODE_PRIVATE);
    }

    public static synchronized LocationApplication getInstance(){
        return application;
    }

    public SharedPreferences getSharedPreferences(){
        return sharedPreferences;
    }
}
