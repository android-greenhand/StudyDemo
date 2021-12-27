package com.example.studyApp.network;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class NetWorkHook {


    private static final int CUSTOM_MAX_ENTRIES = 32;
    private static final long CUSTOM_TTL_NANOS = 200 * 1000000000L;


    /**
     * android 9.0以下
     */
    public static void hookDnsCacheSizeAndTime2() {
        try {

            Class addressCacheClass = Class.forName("java.net.AddressCache");
            if (addressCacheClass != null) {
                Field max_entries = addressCacheClass.getDeclaredField("MAX_ENTRIES");
                max_entries.setAccessible(true);
                max_entries.set(null, CUSTOM_MAX_ENTRIES);
                Field ttl_nanos = addressCacheClass.getDeclaredField("TTL_NANOS");
                ttl_nanos.setAccessible(true);
                ttl_nanos.set(null, CUSTOM_TTL_NANOS);
            }
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    public static void hookDnsCacheSizeAndTime3() {
        try {
            Class Inet6AddressImplClass = Class.forName("java.net.Inet6AddressImpl");
            Class aClass = Class.forName("java.net.AddressCache");
            if (aClass != null) {
                Field declaredField = Inet6AddressImplClass.getDeclaredField("addressCache");
                declaredField.setAccessible(true);
                Object addressCacheObject = declaredField.get(null);
                Field addressCaches = aClass.getDeclaredField("cache");
                addressCaches.setAccessible(true);
            }
        } catch (Exception e) { //
            e.printStackTrace();
        }


        try {
            Class AddressCacheEntryClass = Class.forName("java.net.AddressCache$AddressCacheEntry");
            if (AddressCacheEntryClass != null) {
                Field expiryNanos = AddressCacheEntryClass.getDeclaredField("expiryNanos");
                expiryNanos.setAccessible(true);
                // expiryNanos.set(value, Long.MAX_VALUE);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public void hookDnsCacheSizeAndTime(Context context) {
        try {
            Class Inet6AddressImplClass = Class.forName("java.net.Inet6AddressImpl");

            Method[] declaredMethods = Inet6AddressImplClass.getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                Log.d("gzp", declaredMethod.getName());
            }
            Class aClass = Class.forName("java.net.AddressCache");

            Log.d("gzp  getInterfaces", aClass.getInterfaces()[0].getSimpleName());
            Object newProxyInstance = Proxy.newProxyInstance(context.getClassLoader(), aClass.getInterfaces(), new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    Log.d("gzp_invoke", method.getName());
                    return method.invoke(null, args);
                }
            });
            Field declaredField = Inet6AddressImplClass.getDeclaredField("addressCache");
            Log.d("gzp", declaredField.getType().getName());

            declaredField.setAccessible(true);
            // declaredField.set(null,);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }


    }
}
