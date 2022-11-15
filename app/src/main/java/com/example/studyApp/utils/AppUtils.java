package com.example.studyApp.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;


import java.util.List;

public class AppUtils {
    public static boolean isInstalledApk(String packageName, Context context) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (Throwable e) {
            Log.e("AppUtils", e.toString());
        }
        return false;
    }


    public static boolean isIntentAvailable(String scheme, Context context) {
        try {
            final PackageManager packageManager = context.getPackageManager();
            final Intent intent = new Intent();
            intent.setData(Uri.parse(scheme));
            List<ResolveInfo> resolveInfo =
                    packageManager.queryIntentActivities(intent,
                            PackageManager.MATCH_DEFAULT_ONLY);
            if (resolveInfo.size() > 0) {
                return true;
            }
        } catch (Throwable e) {
            Log.e("AppUtils", e.toString());
        }
        return false;
    }

    public static void openAppByPackageName(String packageName, Context context) {
        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            if (intent != null) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setAction(Intent.ACTION_VIEW);
                context.startActivity(intent);
            }
        } catch (Throwable e) {
            Log.e("AppUtils", e.toString());
        }
    }

    public static void openAppBySchema(String scheme, Context context) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(scheme));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Throwable e) {
            Log.e("AppUtils", e.toString());
        }
    }

    public static void openAppBySchemaThrows(String scheme, Context context) throws Throwable {
        try {
            //唯品会拉起有问题，添加Intent.ACTION_VIEW
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(scheme));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Throwable e) {
            Log.e("AppUtils", e.toString());
            throw e;
        }
    }


}
