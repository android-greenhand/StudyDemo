package com.example.studyApp;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;

import com.example.studyApp.demo.plugin.PluginTool;

import java.lang.reflect.Field;


/**
 * 解决隐私授权前置弹框
 */
public class PrivacyInstrumentation extends Instrumentation {

    private Application mApp;
    private InstrumentationReflect mReflect;


    protected PrivacyInstrumentation(InstrumentationReflect reflect) {
        mReflect = reflect;
        mReflect.invoke(this);
    }

    /**
     * 在哪里调用 super.callApplicationOnCreate();
     * application 的onCreate将会在哪里执行
     *
     * @param app
     */
    @Override
    public void callApplicationOnCreate(Application app) {
        mApp = app;
    }

    @Override
    public Activity newActivity(ClassLoader cl, String className, Intent intent)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        super.callApplicationOnCreate(mApp);
        return mReflect.newActivity(cl, className, intent);
    }


    @Override
    public void callActivityOnStart(Activity activity) {
        super.callActivityOnStart(activity);
        Log.d("gzpActivity",activity.getClass().getName());
        if(activity.getPackageName().equals("")){

//            try {
//             //   Field GrantPermissionsViewHandler = activity.getClass().getDeclaredField("GrantPermissionsViewHandler");
//              //  Field mRootView = GrantPermissionsViewHandler.getClass().getDeclaredField("mRootView");
//              //  activity.setContentView();
//            } catch (NoSuchFieldException e) {
//                e.printStackTrace();
//            }

        }
    }

    /**
     * 覆盖安装或者已经过框隐私确认框的用户，不再需要出发拦截流程
     */
    public static void attach(Context context) {
        new PrivacyInstrumentation(new InstrumentationReflect());
    }
}
