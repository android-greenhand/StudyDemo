package com.example.studyApp.StatusBar;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;


public class CustomActivityLifecycleCallback implements Application.ActivityLifecycleCallbacks {
    String[] mActivityS = {
            StatusBarActivity.class.getName()
    };

    private CustomActivityLifecycleCallback() {
        StatusBarUtils.registerActivity(StatusBarUtils.HASH_MAP_KEY_1, mActivityS);
    }

    private static final class Singleton {
        private static final CustomActivityLifecycleCallback mInstance = new CustomActivityLifecycleCallback();
    }

    public static CustomActivityLifecycleCallback getInstance() {
        return Singleton.mInstance;
    }


    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        StatusBarUtils.toggleStatusBar(StatusBarUtils.HASH_MAP_KEY_1, activity);
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}
