package com.example.studyApp.demo.hotfix;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.PathClassLoader;

public class HotfixTools {

    private static String dexPath = Environment.getExternalStorageDirectory().toString() + "/download_test/" + "hot_fix.dex";

    public static void loadPatchApk(Application application) {
        PathClassLoader pathClassLoader = (PathClassLoader) application.getClassLoader();

        Class pathClassLoaderClass = pathClassLoader.getClass();
        try {

            Class baseDexClassLoader = pathClassLoaderClass.getSuperclass();
            Field pathListField = baseDexClassLoader.getDeclaredField("pathList");
            pathListField.setAccessible(true);
            Object pathList = pathListField.get( (BaseDexClassLoader)pathClassLoader);
            Class dexPathListClass = Class.forName("dalvik.system.DexPathList");
            Field dexElementsField = dexPathListClass.getDeclaredField("dexElements");
            dexElementsField.setAccessible(true);
            Object[] hostDexElements = (Object[]) dexElementsField.get(pathList);
            Method makeDexElementsMethod = dexPathListClass.getDeclaredMethod("makePathElements", List.class, File.class, List.class);
            makeDexElementsMethod.setAccessible(true);

            List<File> fileList = Arrays.asList(new File(dexPath));
            List<IOException> ioExceptionList = new ArrayList<>();
            // 此方法是静态
            Object[] hotFixDexElements = (Object[]) makeDexElementsMethod.invoke(null, fileList, application.getCacheDir(), ioExceptionList);

            Object[] newDexElements = (Object[]) Array.newInstance(hotFixDexElements.getClass().getComponentType(), hostDexElements.length + hotFixDexElements.length);

            System.arraycopy(hotFixDexElements, 0, newDexElements, 0, hotFixDexElements.length);
            System.arraycopy(hostDexElements, 0, newDexElements, hotFixDexElements.length, hostDexElements.length);
            dexElementsField.set(pathList, newDexElements);

        } catch (Exception e) {
            Log.e("HotfixTools", e.toString());
        }


    }


}
