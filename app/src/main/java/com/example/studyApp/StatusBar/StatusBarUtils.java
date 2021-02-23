package com.example.studyApp.StatusBar;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.system.Os;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;


import androidx.annotation.IntRange;

import com.example.studyApp.R;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * 状态栏工具
 * 支持状态栏透明、半透明、背景颜色改变，字体设置
 * <p>
 * Android 4.4以下 不支持
 * Android 4.4 - ~
 * 支持4.4以上版本
 * <p>
 *
 * 状态栏透明半透明、背景颜色改变：
 * 5.0（API 21） 以上API提供单独设置状态栏颜色
 * 4.4-5.0（API 19 - API 21） 状态栏透明 + View（颜色）
 * 4.4（API 19） 以前状态栏永远黑底白字，不能改变
 *
 * 状态栏字体修改：
 * 6.0 (API 23) 提供API可以更改状态栏的字体为黑色字体
 * 4.4-5.0（API 19 - API 21)魅族和小米手机字体可以改成黑色字体
 * 4.4（API 19） 以前状态栏永远黑底白字，不能改变
 *
 */

@SuppressWarnings("deprecation")
public class StatusBarUtils {

    private static final String TAG = "StatusBarUtils";
    private static final int DEFAULT_STATUS_BAR_ALPHA = 112;
    private static int sStatusBarPadding;
    private static final int STATUS_BAR_VIEW_ID = R.id.custom_status_bar_view;
    private static final int TRANSLUCENT_VIEW_ID = R.id.custom_status_bar_translucent_view;
    private static HashMap<String, Set<String>> SHOW_STATUS_BAR_ACTIVITY = new HashMap<>();

    //自定义的hashMap key值，外部注册使用
    public static final String HASH_MAP_KEY_1 = "HASH_MAP_KEY_1";

    /**
     * 针对4.4-5.0的手机，修改状态栏颜色。
     * 布局一个等状态栏高度的view，设置背景颜色
     *
     * @param activity 状态栏对应的Activity
     * @param color    状态栏要设置的颜色
     * @return void
     */
    private static void setupStatusBarView(Activity activity, int color) {
        ViewGroup contentLayout = (ViewGroup) activity.getWindow().getDecorView();
        int statusBarHeight = getStatusBarHeight(activity);
        View statusBarView = new View(activity);
        statusBarView.setId(STATUS_BAR_VIEW_ID);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight);
        contentLayout.addView(statusBarView, lp);
        statusBarView.setBackgroundColor(color);
    }

    /**
     * 移除状态栏从对应的activity布局中
     *
     * @param activity
     * @return void
     */
    private static void removeStatusBarView(Activity activity) {
        ViewGroup contentLayout = (ViewGroup) activity.getWindow().getDecorView();
        View view = contentLayout.findViewById(STATUS_BAR_VIEW_ID);
        if (view == null) return;
        contentLayout.removeView(view);
    }

    /**
     * 获得状态栏高度
     *
     * @param context
     * @return int 返回状态栏的高度
     */
    public static int getStatusBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    /**
     * APP启动的时候，用LaunchActivity确定是否支持透明状态栏。
     *
     * @param activity
     * @return 不支持时，返回状态栏的高度。否则返回0
     */
    public static int initStatusBarPadding(Activity activity) {
        try {
            sStatusBarPadding = getStatusBarHeight(activity);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (miuiSetStatusBarLightMode(activity, true)) {
                    sStatusBarPadding = 0;
                } else if (flymeSetImmersedWindow(activity.getWindow(), true)) {
                    sStatusBarPadding = 0;
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    sStatusBarPadding = 0;
                }
                return sStatusBarPadding;
            }
        } catch (Exception e) {
        }
        return sStatusBarPadding;
    }

    /**
     * 获得状态栏高度
     *
     * @return int 返回状态栏的高度
     */
    public static int getsStatusBarPadding() {
        return sStatusBarPadding;
    }

    /**
     * 设置状态栏黑色字体图标，
     * 适配4.4以上版本MIUIV、Flyme和6.0以上版本其他Android
     *
     * @param activity
     * @return 1:MIUI 2:Flyme 3:android6.0 4.其他
     */
    public static int statusBarLightMode(Activity activity) {
        int result = 0;
        IgnoreStatusBar ignoreStatusBar = checkAnnotation(activity.getClass());
        if (ignoreStatusBar != null && ignoreStatusBar.isIgnoreStatusBarView()) {
            return result;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (miuiSetStatusBarLightMode(activity, true)) {
                result = 2;
            } else if (flymeSetStatusBarLightMode(activity.getWindow(), true)) {
                result = 3;
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                result = 1;
            }
        }
        return result;
    }

    /**
     * 状态栏透明
     *
     * @param activity
     * @return
     */
    public static int statusBarTrans(Activity activity) {
        int result = 0;
        IgnoreStatusBar ignoreStatusBar = checkAnnotation(activity.getClass());
        if (ignoreStatusBar != null && ignoreStatusBar.isIgnoreStatusBarView()) {
            return result;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (miuiSetStatusBarLightMode(activity, true)) {
                result = 2;
            } else if (flymeSetStatusBarLightMode(activity.getWindow(), true) && flymeSetImmersedWindow(activity.getWindow(), true)) {
                result = 3;
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                result = 1;
            } else {
                setupStatusBarView(activity, Color.GRAY);
                result = 4;
            }
        }
        return result;
    }

    /**
     * 字体图案设置为黑色
     * <p>
     * 状态栏字体图案默认是白色
     * 6.0以上版本选择状态栏黑色字体
     *
     * @param activity
     * @return
     */
    public static int statusBarDarkMode(Activity activity) {
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //Activity全屏显示，但是状态栏不会被覆盖掉，而是正常显示，只是Activity顶端布局会被覆盖住
                //选择黑色字体图案
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                result = 1;
            } else if (miuiSetStatusBarLightMode(activity, false)) {
                result = 2;
            } else if (flymeSetStatusBarLightMode(activity.getWindow(), false) && flymeSetImmersedWindow(activity.getWindow(), true)) {
                result = 3;
            } else {
                removeStatusBarView(activity);
                result = 4;
            }
        }
        return result;
    }

    private static IgnoreStatusBar checkAnnotation(Class<? extends Activity> activityClass) {
        Class mc = activityClass;
        IgnoreStatusBar ignoreStatusBar;
        while (Activity.class.isAssignableFrom(mc)) {
            ignoreStatusBar = (IgnoreStatusBar) mc.getAnnotation(IgnoreStatusBar.class);
            if (ignoreStatusBar != null)
                return ignoreStatusBar;
            mc = mc.getSuperclass();
        }
        return null;
    }

    /**
     * 是否支持沉浸式布局
     *
     * @param activity
     * @return true 支持沉浸式布局
     */
    public static boolean isSupportImmersiveBar(Activity activity) {
        boolean isSupport = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                isSupport = true;
            } else if (miuiSetStatusBarLightMode(activity, true)) {
                isSupport = true;
            } else if (flymeSetStatusBarLightMode(activity.getWindow(), true)) {
                isSupport = true;
            }
        }
        return isSupport;
    }

    /**
     * 魅族系统手机
     *
     * @return boolean 是魅族手机返回true
     */
    private static boolean isMeizuFlymeOS() {
        try {
            Class<?> clz = Class.forName("android.os.SystemProperties");
            Method get = clz.getMethod("get", String.class, String.class);
            String meizuFlymeOSFlag = (String) get.invoke(clz, "ro.build.display.id", "");
            if (!TextUtils.isEmpty(meizuFlymeOSFlag) && meizuFlymeOSFlag.toLowerCase().contains("flyme")) {
                return true;
            }
        } catch (Exception ignored) {

        }
        return false;
    }

    /**
     * 设置状态栏图标为深色和魅族特定的文字风格
     * 可以用来判断是否为Flyme用户
     *
     * @param window 需要设置的窗口
     * @param dark   是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    public static boolean flymeSetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }

    /**
     * 设置沉浸式窗口，设置成功后，状态栏则透明显示
     *
     * @param window    需要设置的窗口
     * @param immersive 是否把窗口设置为沉浸
     * @return boolean 成功执行返回true
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean flymeSetImmersedWindow(Window window, boolean immersive) {
        boolean result = false;
        if (!isMeizuFlymeOS()) {
            return result;
        }
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            int trans_status = 0;
            Field flags;
            if (Build.VERSION.SDK_INT < 19) {
                try {
                    trans_status = 1 << 6;
                    flags = lp.getClass().getDeclaredField("meizuFlags");
                    flags.setAccessible(true);
                    int value = flags.getInt(lp);
                    if (immersive) {
                        value = value | trans_status;
                    } else {
                        value = value & ~trans_status;
                    }
                    flags.setInt(lp, value);
                    result = true;
                } catch (Exception e) {

                }
            } else {
                lp.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                window.setAttributes(lp);
                result = true;
            }
        }
        return result;
    }

    /**
     * 设置状态栏字体图标为深色，需要MIUIV6以上
     *
     * @param activity 需要设置的窗口
     * @param dark     是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    public static boolean miuiSetStatusBarLightMode(Activity activity, boolean dark) {
        boolean result = false;
        if (activity == null) return result;
        Window window = activity.getWindow();
        if (window == null) return result;
        return miuiSetStatusBarLightMode(window, dark);
    }

    /**
     * 设置状态栏字体图标为深色，需要MIUIV6以上
     *
     * @param window 需要设置的窗口
     * @param dark   是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    public static boolean miuiSetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (dark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                }
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }

    /**
     * 正常显示Activity和状态栏
     *
     * @param activity
     */
    public static void showStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //状态栏和Activity共存，Activity不全屏显示。也就是应用平常的显示画面
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
        }
    }

    /**
     * @param activity
     * @param bDark
     */
    public static void setDarkStatusIcon(Activity activity, boolean bDark) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                View decorView = activity.getWindow().getDecorView();
                if (decorView != null) {
                    int vis = decorView.getSystemUiVisibility();
                    if (bDark) {
                        vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                    } else {
                        vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                    }
                    decorView.setSystemUiVisibility(vis);
                }
            }
        } catch (Exception e) {
            //为啥不用TLog打印出来，因为在此时Tlog还没有被初始化
            e.printStackTrace();
        }
    }

    /**
     * 修改状态栏为全透明
     *
     * @param activity 需要设置的activity
     */
    @TargetApi(19)
    public static void transparencyBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //Activity全屏显示，但是状态栏不会被覆盖掉，而是正常显示，只是Activity顶端布局会被覆盖住
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 修改状态栏颜色，支持4.4以上版本
     * <p>
     * 5.0（API 21） 以上API提供单独设置状态栏颜色
     * 4.4-5.0（API 19 - API 21） 状态栏透明 + View（颜色）
     * 4.4（API 19） 以前状态栏永远黑底白字，不能改变
     *
     * @param activity 需要设置的activity
     * @param colorId  状态栏的颜色
     */
    public static void setStatusBarColor(Activity activity, int colorId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.setStatusBarColor(colorId);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            transparencyBar(activity);
            setupStatusBarView(activity, colorId);
        }
    }

    /**
     * 使状态栏半透明
     * <p>
     * 适用于图片作为背景的界面,此时需要图片填充到状态栏
     *
     * @param activity 需要设置的activity
     */
    public static void setTranslucent(Activity activity) {
        setTranslucent(activity, DEFAULT_STATUS_BAR_ALPHA);
    }

    /**
     * 使状态栏半透明
     * <p>
     * 适用于图片作为背景的界面,此时需要图片填充到状态栏
     *
     * @param activity       需要设置的activity
     * @param statusBarAlpha 状态栏透明度
     */
    public static void setTranslucent(Activity activity, @IntRange(from = 0, to = 255) int statusBarAlpha) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        transparencyBar(activity);
        addTranslucentView(activity, statusBarAlpha);
    }

    /**
     * 添加半透明矩形条
     *
     * @param activity       需要设置的 activity
     * @param statusBarAlpha 透明值
     */
    private static void addTranslucentView(Activity activity, @IntRange(from = 0, to = 255) int statusBarAlpha) {
        ViewGroup contentView = (ViewGroup) activity.findViewById(android.R.id.content);
        View fakeTranslucentView = contentView.findViewById(TRANSLUCENT_VIEW_ID);
        if (fakeTranslucentView != null) {
            if (fakeTranslucentView.getVisibility() == View.GONE) {
                fakeTranslucentView.setVisibility(View.VISIBLE);
            }
            fakeTranslucentView.setBackgroundColor(Color.argb(statusBarAlpha, 0, 0, 0));
        } else {
            contentView.addView(createTranslucentStatusBarView(activity, statusBarAlpha));
        }
    }

    /**
     * 创建半透明矩形 View
     *
     * @param alpha 透明值
     * @return 半透明 View
     */
    private static View createTranslucentStatusBarView(Activity activity, int alpha) {
        // 绘制一个和状态栏一样高的矩形
        View statusBarView = new View(activity);
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity));
        statusBarView.setLayoutParams(params);
        statusBarView.setBackgroundColor(Color.argb(alpha, 0, 0, 0));
        statusBarView.setId(TRANSLUCENT_VIEW_ID);
        return statusBarView;
    }

    /**
     * 需要隐藏的Activity状态栏
     *
     * @param hashMapKey HashMap的key值,自己定义。
     * @param activity   需要设置的activity
     * @return true 需要将状态栏改成透明
     */
    private static boolean needStatusBarColorTran(String hashMapKey, Activity activity) {
        Set<String> activitySet = SHOW_STATUS_BAR_ACTIVITY.get(hashMapKey);
        if (activity != null && activitySet != null) {
            if (activitySet.contains(activity.getClass().getName())) return true;
        }
        return false;
    }

    /**
     * 注册需要隐藏的Activity状态栏
     *
     * @param hashMapKey    HashMap的key值,自己定义。
     * @param activityNameS ActivityName数组
     * @return void
     */
    public static void registerActivity(String hashMapKey, String[] activityNameS) {
        Set<String> stringSet = new HashSet<>();
        Collections.addAll(stringSet, activityNameS);
        SHOW_STATUS_BAR_ACTIVITY.put(hashMapKey, stringSet);
    }

    /**
     * 处理Activity状态栏
     *
     * @param hashMapKey HashMap的key值,自己定义。
     * @param activity   要处理的Activity
     * @return void
     */
    public static void toggleStatusBar(String hashMapKey, Activity activity) {
        if (needStatusBarColorTran(hashMapKey, activity)) {
            transparencyBar(activity); // Activity全屏显示 状态栏为透明
            statusBarDarkMode(activity);  //状态栏字体为黑色
            setStatusBarColor(activity, Color.TRANSPARENT);
        } else {
            showStatusBar(activity);
        }
    }

}
