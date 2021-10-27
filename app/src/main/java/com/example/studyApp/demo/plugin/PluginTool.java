package com.example.studyApp.demo.plugin;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import dalvik.system.DexClassLoader;

public class PluginTool {

    private static String dexPath = Environment.getExternalStorageDirectory().toString() + "/download_test/" + "plugin-debug.apk";

    public static String PLUGIN_INTENT = "plugin_intent";

    public static String HOOK_FLAG = "HOOK_FLAG";

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
            Log.e("PluginTool loadPluginDex", e.toString());
        }
    }

    /**
     * 调用插件中的方法
     *
     * @param context
     * @return
     */
    public static String getStringFromPlugin(Context context) {
        ClassLoader classLoader = context.getClassLoader();
        try {
            Class<?> PluginAppClassTest = classLoader.loadClass("com.example.plugin.PluginAppClassTest");
            Method getPluginAppClassTest = PluginAppClassTest.getMethod("getPluginAppClassTest");
            return (String) getPluginAppClassTest.invoke(null);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            Log.e("PluginTool getStringFromPlugin", e.toString());
        }

        return null;
    }


    public static void hooKStartActivity(Context context) {
        try {
            Class<?> ActivityTaskManagerClass = Class.forName("android.app.ActivityTaskManager");
            Field iActivityTaskManagerSingletonFiled = ActivityTaskManagerClass.getDeclaredField("IActivityTaskManagerSingleton");
            iActivityTaskManagerSingletonFiled.setAccessible(true);
            Object iActivityTaskManagerSingleton = iActivityTaskManagerSingletonFiled.get(null);

            Class<?> SingletonClass = Class.forName("android.util.Singleton");
            Field mInstanceFiled = SingletonClass.getDeclaredField("mInstance");
            mInstanceFiled.setAccessible(true);
            Object mInstance = mInstanceFiled.get(iActivityTaskManagerSingleton);


            Class<?> IActivityTaskManagerClass = Class.forName("android.app.IActivityTaskManager");

            Object pluginProxyActivity = Proxy.newProxyInstance(context.getClassLoader(), new Class[]{IActivityTaskManagerClass}, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    Log.e("PluginTool invoke", "在 invoke 中");
                    if(method == null || mInstance == null){
                        return null;
                    }
                    if ("startActivity".equals(method.getName())) {

                        for (int i = 0; i < args.length; i++) {
                            if (args[i] instanceof Intent) {
                                Intent pluginIntent = (Intent) args[i];
                                if (!TextUtils.isEmpty(pluginIntent.getStringExtra(HOOK_FLAG))) {
                                    Intent hostProxyIntent = new Intent();
                                    //这样写会有问题 （PluginProxyActivity 没有在AndroidManifest.xml中注册 找不到），好奇怪
                                    //  hostProxyIntent.setComponent(new ComponentName("com.example.com.example.studyApp",PluginProxyActivity.class.getName()));

                                    hostProxyIntent.setComponent(new ComponentName(context.getPackageName(), PluginProxyActivity.class.getName()));
                                    hostProxyIntent.putExtra(PLUGIN_INTENT, pluginIntent);
                                    args[i] = hostProxyIntent;
                                }
                                break;
                            }
                        }
                    }
                    return method.invoke(mInstance, args);


                }
            });
            mInstanceFiled.set(iActivityTaskManagerSingleton, pluginProxyActivity);

        } catch (Exception e) {
            Log.e("PluginTool hooKStartActivity", e.toString());

        }
    }



    public static void hookInstrumentation(){

        try {
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Field mInstrumentationField = activityThreadClass.getDeclaredField("mInstrumentation");
            mInstrumentationField.setAccessible(true);
            Field sCurrentActivityThreadField = activityThreadClass.getDeclaredField("sCurrentActivityThread");
            sCurrentActivityThreadField.setAccessible(true);
            Object sCurrentActivityThread = sCurrentActivityThreadField.get(null);
            mInstrumentationField.set(sCurrentActivityThread,new PluginInstrumentation());

        } catch (Exception e) {
            Log.e("PluginTool hookInstrumentation", e.toString());
        }


    }


}
