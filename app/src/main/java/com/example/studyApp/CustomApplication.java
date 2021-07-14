package com.example.studyApp;

import android.app.Application;
import android.content.Context;

import com.example.studyApp.StatusBar.CustomActivityLifecycleCallback;
import com.example.studyApp.demo.other.GreyEffect;

public class CustomApplication extends Application {

    @Override

    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(CustomActivityLifecycleCallback.getInstance());
        registerActivityLifecycleCallbacks(GreyEffect.INSTANCE);
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        PrivacyInstrumentation.attach(this);
    }
}
