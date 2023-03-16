package com.example.studyApp;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.studyApp.StatusBar.CustomActivityLifecycleCallback;
import com.example.studyApp.demo.other.GreyEffect;
import com.example.studyApp.verify.MemoryDetectionCallback2;


public class CustomApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("CustomApplication","onCreate");
  //      HotfixTools.loadPatchApk(this);
        // PluginTool.loadPluginDex(this);
        registerComponentCallbacks(MemoryDetectionCallback2.getInstance());
        registerActivityLifecycleCallbacks(CustomActivityLifecycleCallback.getInstance());
        registerActivityLifecycleCallbacks(GreyEffect.INSTANCE);
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //  PrivacyInstrumentation.attach(this);
//        Reflection.unseal(base);
//        NetWorkHook.hookDnsCacheSizeAndTime(base);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d("CustomApplication","onConfigurationChanged");
    }



}
