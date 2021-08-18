package com.example.studyApp.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public class PluginTool {

    private static String dexPath = Environment.getExternalStorageDirectory().toString() + "/download_test/" + "plugin-debug.apk";

    public static void loadPluginDex(Context context) {

        try {
            Class baseDexClassLoaderClass = Class.forName("dalvik.system.BaseDexClassLoader");
            Class dexPathListClass = Class.forName("dalvik.system.DexPathList");
       //     Class elementClass = Class.forName("dalvik.system.Element");

            Field pathListFiled = baseDexClassLoaderClass.getDeclaredField("pathList");
            Field elementFiled = dexPathListClass.getDeclaredField("dexElements");

            pathListFiled.setAccessible(true);
            elementFiled.setAccessible(true);

            /**
             * 宿主
             */
            ClassLoader hostClassLoader = context.getClassLoader();
            Object hostPathList = pathListFiled.get(hostClassLoader);
            Object[] hostDexElements = (Object[]) elementFiled.get(hostPathList);


            /**
             * 插件
             */

            DexClassLoader pluginClassLoader = new DexClassLoader(dexPath,
                    context.getCacheDir().getAbsolutePath(), null, context.getClassLoader());
            Object pluginPathList = pathListFiled.get(pluginClassLoader);
            Object[] pluginDexElements = (Object[]) elementFiled.get(pluginPathList);


            /**
             * 宿主 + 插件 = newDexElements
             *
             */

            //生成数组对象
            Object[] newDexElements = (Object[]) Array.newInstance(hostDexElements.getClass().getComponentType(), hostDexElements.length + pluginDexElements.length);
            System.arraycopy(hostDexElements, 0, newDexElements, 0, hostDexElements.length);
            System.arraycopy(pluginDexElements, 0, newDexElements, hostDexElements.length, pluginDexElements.length);


            /**
             *  newDexElements 设置给宿主
             */
            elementFiled.set(hostPathList, newDexElements);

        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            Log.e("PluginTool loadPluginDex",e.toString());
        }
    }

    public static String getStringFromPlugin(Context context) {
        ClassLoader classLoader = context.getClassLoader();
        try {
            Class<?> PluginAppClassTest = classLoader.loadClass("com.example.plugin.PluginAppClassTest");
            Method getPluginAppClassTest = PluginAppClassTest.getMethod("getPluginAppClassTest", null);
            return (String) getPluginAppClassTest.invoke(null, null);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            Log.e("PluginTool getStringFromPlugin",e.toString());
        }

        return null;
    }


}
