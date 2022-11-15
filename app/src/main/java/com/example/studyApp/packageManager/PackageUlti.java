package com.example.studyApp.packageManager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PackageUlti {
    /**
     * 获取手机已安装应用列表
     *
     * @param ctx
     * @param isFilterSystem 是否过滤系统应用
     * @return
     */
    public static ArrayList<AppInfo> getAllAppInfo(Context ctx, boolean isFilterSystem) {
        ArrayList<AppInfo> appBeanList = new ArrayList<>();
        AppInfo bean = null;

        PackageManager packageManager = ctx.getPackageManager();
        List<PackageInfo> list = packageManager.getInstalledPackages(0);
        for (PackageInfo p : list) {
            bean = new AppInfo();
            bean.setIcon(p.applicationInfo.loadIcon(packageManager));
            bean.setLabel(packageManager.getApplicationLabel(p.applicationInfo).toString());
            bean.setPackage_name(p.applicationInfo.packageName);
            int flags = p.applicationInfo.flags;
            // 判断是否是属于系统的apk
            if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0 && isFilterSystem) {
//                bean.setSystem(true);
            } else {
                appBeanList.add(bean);
            }
        }
        return appBeanList;
    }


    public static List<PackageInfo> getDeviceApp(Context context) {
        return context.getPackageManager().getInstalledPackages(PackageManager.GET_ACTIVITIES);
    }

    public static boolean isInstalled(Context context, String pkg) {
        //默认不存在
        boolean exit = false;
        try {
            //不为空则存在
            exit = context.getPackageManager().getPackageInfo(pkg, PackageManager.GET_ACTIVITIES) != null;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return exit;
    }

    public void getAllApp(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> appsInfo = packageManager.queryIntentActivities(intent, 0);
        Collections.sort(appsInfo, new ResolveInfo.DisplayNameComparator(packageManager));
        for (ResolveInfo info : appsInfo) {
            String pkg = info.activityInfo.packageName;
            String cls = info.activityInfo.name;
            Log.e("app_info", "pkg:" + pkg + " —— cls:" + cls);
        }
    }

    static public class AppInfo {
        public int uid;
        public String label;//应用名称
        public String package_name;//应用包名
        public Drawable icon;//应用icon

        public AppInfo() {
            uid = 0;
            label = "";
            package_name = "";
            icon = null;
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getPackage_name() {
            return package_name;
        }

        public void setPackage_name(String package_name) {
            this.package_name = package_name;
        }

        public Drawable getIcon() {
            return icon;
        }

        public void setIcon(Drawable icon) {
            this.icon = icon;
        }
    }
}
