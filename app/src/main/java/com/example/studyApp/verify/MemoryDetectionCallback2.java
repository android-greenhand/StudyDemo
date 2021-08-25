package com.example.studyApp.verify;


import android.app.Activity;
import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import android.util.Log;

import androidx.annotation.NonNull;


import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class MemoryDetectionCallback2 implements ComponentCallbacks2 {

    public static String TAG = MemoryDetectionCallback2.class.getName();

    public List<WeakReference<Activity>> mWeakReferenceArrayList = new ArrayList<>();

    private MemoryDetectionCallback2() {
    }

    public void addActivity(Activity activity) {
        WeakReference<Activity> weakReference = new WeakReference<>(activity);
        mWeakReferenceArrayList.add(weakReference);
    }

    private void finishActivity() {
        Iterator<WeakReference<Activity>> iterator = mWeakReferenceArrayList.listIterator();
        Log.d(TAG, "Size:" + mWeakReferenceArrayList.size());
        while (iterator.hasNext()) {
            WeakReference<Activity> activityWeakReference = iterator.next();
            Activity activity = activityWeakReference.get();

            if (activity == null) {
                iterator.remove();
                continue;
            }
            activity.finish();
            iterator.remove();
            Log.d(TAG, "activity已被销毁");

        }

    }

    @Override
    public void onTrimMemory(int level) {
        Log.d(TAG, "onTrimMemory" + level + "");
        if (level == ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW) {
            finishActivity();
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        Log.d(TAG, "onConfigurationChanged");
    }

    @Override
    public void onLowMemory() {
        Log.d(TAG, "onLowMemory");

    }


    private static final class Singleton {
        private static final MemoryDetectionCallback2 mInstance = new MemoryDetectionCallback2();
    }

    public static MemoryDetectionCallback2 getInstance() {
        return Singleton.mInstance;
    }

}





