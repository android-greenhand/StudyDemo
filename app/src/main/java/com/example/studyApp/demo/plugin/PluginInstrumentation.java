package com.example.studyApp.demo.plugin;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;


/**
 *
 */
public class PluginInstrumentation extends Instrumentation {

    @Override
    public Activity newActivity(ClassLoader cl, String className, Intent intent)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException {


        try {
            if (intent != null) {
                Intent pluginIntent = (Intent) intent.getParcelableExtra(PluginTool.PLUGIN_INTENT);
                if (pluginIntent != null) {
                    ComponentName componentName = pluginIntent.getComponent();
                    if (null != componentName) {
                        Log.e("PluginTool  newActivity ", componentName.getClassName());

                        return super.newActivity(cl, componentName.getClassName(), pluginIntent);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("PluginTool  PluginInstrumentation ", e.toString());
        }

        return super.newActivity(cl, className, intent);
    }



    @Override
    public void callActivityOnResume(Activity activity) {
        super.callActivityOnResume(activity);
        Log.e("PluginTool  callActivityOnResume ", activity.getComponentName().getClassName());

    }
}
