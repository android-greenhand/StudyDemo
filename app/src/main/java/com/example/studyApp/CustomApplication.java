package com.example.studyApp;

import android.app.Application;

import com.example.studyApp.StatusBar.CustomActivityLifecycleCallback;

public class CustomApplication extends Application {

    @Override

    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(CustomActivityLifecycleCallback.getInstance());
    }

}
