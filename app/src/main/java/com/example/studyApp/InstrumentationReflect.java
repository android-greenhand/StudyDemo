package com.example.studyApp;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.MainThread;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

class InstrumentationReflect {

    private Object mActivityThread;
    private Field mInstrumentation;
    private Instrumentation mBase;
    private List<IDelayHook> mDelayedBiz = new ArrayList<>();

    protected InstrumentationReflect() {
        try {
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            Method sCurrentActivityThread = activityThread.getDeclaredMethod("currentActivityThread");
            sCurrentActivityThread.setAccessible(true);
            //获取ActivityThread 对象
            mActivityThread = sCurrentActivityThread.invoke(activityThread);
            //获取 Instrumentation 对象
            mInstrumentation = activityThread.getDeclaredField("mInstrumentation");
            mInstrumentation.setAccessible(true);
            mBase = (Instrumentation) mInstrumentation.get(mActivityThread);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /**
     * 置换
     *
     * @param instrumentation 目标实例
     * @param hooks           需要延迟处理的业务逻辑
     */
    public void invoke(Instrumentation instrumentation, IDelayHook... hooks) {
        try {
            mInstrumentation.set(mActivityThread, instrumentation);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (hooks == null) {
            return;
        }
        for (IDelayHook h : hooks) {
            if (h == null) continue;
            Log.i("Instrumentation", "add new delayed hook @" + h.getClass().getCanonicalName());
            mDelayedBiz.add(h);
            h.invoke();
        }
    }

    /**
     * 重置
     */
    public void revoke() {
        try {
            mInstrumentation.set(mActivityThread, mBase);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mDelayedBiz == null) {
            return;
        }
        for (IDelayHook h : mDelayedBiz) {
            if (h == null) continue;
            long start = System.currentTimeMillis();
            h.revoke();
            long duration = System.currentTimeMillis() - start;
            String clzName = h.getClass().getCanonicalName();
            Log.i("Instrumentation", "delayed hook from " + clzName + " is executed: " + duration + " ms");

        }
    }

    /**
     * 分发
     */
    public Activity newActivity(ClassLoader cl, String className, Intent intent) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        return mBase.newActivity(cl, className, intent);
    }

    /**
     * 业务拦截接口
     */
    public static interface IDelayHook {
        /**
         * 拦截开始，执行个性化配置
         */
        @MainThread
        void invoke();

        /**
         * 可以开始执行被延迟的业务
         */
        @MainThread
        void revoke();
    }
}
