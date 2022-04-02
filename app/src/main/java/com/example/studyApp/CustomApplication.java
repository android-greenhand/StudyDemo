package com.example.studyApp;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.example.studyApp.StatusBar.CustomActivityLifecycleCallback;
import com.example.studyApp.demo.hotfix.HotfixTools;
import com.example.studyApp.demo.other.GreyEffect;
import com.example.studyApp.network.NetWorkHook;
import com.example.studyApp.verify.MemoryDetectionCallback2;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import me.weishu.reflection.Reflection;


public class CustomApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
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

}
