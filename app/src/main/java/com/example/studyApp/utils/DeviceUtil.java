package com.example.studyApp.utils;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class DeviceUtil {

    public static boolean isMeizuFlymeOS() {
        /* 获取魅族系统操作版本标识*/
        String meizuFlymeOSFlag = getSystemProperty("ro.build.display.id", "");
        if (!TextUtils.isEmpty(meizuFlymeOSFlag) && meizuFlymeOSFlag.toLowerCase().contains("flyme")) {
            return true;
        }
        return false;
    }

    /**
     * 获取系统属性
     * <h3>Version</h3> 1.0
     * <h3>CreateTime</h3> 2016/6/18,9:35
     * <h3>UpdateTime</h3> 2016/6/18,9:35
     * <h3>CreateAuthor</h3> vera
     * <h3>UpdateAuthor</h3>
     * <h3>UpdateInfo</h3> (此处输入修改内容,若无修改可不写.)
     *
     * @param key          ro.build.display.id
     * @param defaultValue 默认值
     * @return 系统操作版本标识
     */
    private static String getSystemProperty(String key, String defaultValue) {
        try {
            Class<?> clz = Class.forName("android.os.SystemProperties");
            Method get = clz.getMethod("get", String.class, String.class);
            return (String) get.invoke(clz, key, defaultValue);
        } catch (ClassNotFoundException e) {
            Log.e("getSystemProperty", "SystemUtil=================>" + e.getMessage());
            return null;
        } catch (NoSuchMethodException e) {
            Log.e("getSystemProperty", "SystemUtil=================>" + e.getMessage());
            return null;
        } catch (IllegalAccessException e) {
            Log.e("getSystemProperty", "SystemUtil=================>" + e.getMessage());
            return null;
        } catch (IllegalArgumentException e) {
            Log.e("getSystemProperty", "SystemUtil=================>" + e.getMessage());
            return null;
        } catch (InvocationTargetException e) {
            Log.e("getSystemProperty", "SystemUtil=================>" + e.getMessage());
            return null;
        }
    }

    public static void actionLogDeviceInfo() {
        HashMap<String, Object> cancelSelectMap = new HashMap<String, Object>();
        String cpuAbi = null;
        String cpuAbi2 = null;
        String abis = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                if (Build.SUPPORTED_ABIS != null) {
                    int length = Build.SUPPORTED_ABIS.length;
                    if (length > 0) {
                        cpuAbi = Build.SUPPORTED_ABIS[0];
                        cpuAbi2 = Build.SUPPORTED_ABIS[length - 1];
                        String abi;
                        for (int i = 0; i < length; i++) {
                            abi = Build.SUPPORTED_ABIS[i];
                            if (i == 0) {
                                abis = abi;
                            } else {
                                abis = abis + "|" + abi;
                            }
                        }

                    }
                }
            } catch (Throwable e) {
                Log.e("DeviceUtil", "actionLogDeviceInfo", e);
            }
        } else {
            cpuAbi = Build.CPU_ABI;
            cpuAbi2 = Build.CPU_ABI2;
        }

        if (cpuAbi == null) {
            cpuAbi = "default";
        } else {
            cpuAbi = cpuAbi.toLowerCase();
        }
        if (cpuAbi2 == null) {
            cpuAbi2 = "default";
        } else {
            cpuAbi2 = cpuAbi2.toLowerCase();
        }
        LinkedHashMap<String, String> cancelSelectJson = new LinkedHashMap<>();
        // 9.18.5 添加cpu架构类型, 如: x86/arm/mips;
        if (!TextUtils.isEmpty(cpuAbi)) {
            cancelSelectJson.put("abi", cpuAbi);
        }
        if (!TextUtils.isEmpty(cpuAbi2)) {
            cancelSelectJson.put("abi2", cpuAbi2);
        }
        if (!TextUtils.isEmpty(abis)) {
            cancelSelectJson.put("abis", abis);
        }
        cancelSelectMap.put("json", cancelSelectJson);
    }
}
