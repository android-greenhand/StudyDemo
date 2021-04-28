package com.example.studyApp;

import android.app.Application;

import com.example.studyApp.StatusBar.CustomActivityLifecycleCallback;
import com.example.studyApp.demo.other.GreyEffect;

public class CustomApplication extends Application {

    @Override

    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(CustomActivityLifecycleCallback.getInstance());
        registerActivityLifecycleCallbacks(GreyEffect.INSTANCE);
    }

}
