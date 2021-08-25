package com.example.plugin;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Environment;
import android.util.Log;
import android.view.ContextThemeWrapper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class PluginLoadUtil {

    private static Context PluginLoadUtilContext;
    private static String dexPath = Environment.getExternalStorageDirectory().toString() + "/download_test/" + "plugin-debug.apk";


    public static Context getContext(Context context) {
        if (PluginLoadUtilContext != null) {
            return PluginLoadUtilContext;
        }
        try {
            PluginLoadUtilContext = new ContextThemeWrapper(context, null);
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPathMethod = AssetManager.class.getDeclaredMethod("addAssetPath",String.class);
            addAssetPathMethod.setAccessible(true);
            addAssetPathMethod.invoke(assetManager, dexPath);

            //这里传入Activity的Context 会循环调用
            Resources resources = context.getResources();
            Resources customResources = new Resources(assetManager, resources.getDisplayMetrics(), resources.getConfiguration());

            Field mResourcesFiled = PluginLoadUtilContext.getClass().getDeclaredField("mResources");
            mResourcesFiled.setAccessible(true);
            mResourcesFiled.set(PluginLoadUtilContext, customResources);

            return PluginLoadUtilContext;
        } catch (Exception e) {
            Log.e("PluginLoadUtil",e.toString());
            e.printStackTrace();
        }

        return null;
    }
}
