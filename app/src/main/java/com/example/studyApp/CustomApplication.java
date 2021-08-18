package com.example.studyApp;

import android.app.Application;
import android.content.Context;

import com.example.studyApp.StatusBar.CustomActivityLifecycleCallback;
import com.example.studyApp.demo.other.GreyEffect;
import com.example.studyApp.utils.PluginTool;

public class CustomApplication extends Application {

    @Override

    public void onCreate() {
        super.onCreate();
        PluginTool.loadPluginDex(this);
        registerActivityLifecycleCallbacks(CustomActivityLifecycleCallback.getInstance());
        registerActivityLifecycleCallbacks(GreyEffect.INSTANCE);
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        PrivacyInstrumentation.attach(this);
    }
}
