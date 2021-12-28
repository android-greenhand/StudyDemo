package com.example.studyApp.network;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;


public class NetWorkHook {


    private static final int CUSTOM_MAX_ENTRIES = 32;
    private static final long CUSTOM_TTL_NANOS = 100 * 1000000000L;


    /**
     * android 9.0以下
     *
     * 注意⚠️：这个方法不可以，cache取值的时候，都是重新构建新的对象，从而拿到的value为null
     */
    public static void hookDnsCacheSizeAndTime() {
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

    public static Map<String, InetAddress[]> sDNSMap = new HashMap();
    public static void hookDnsCacheSizeAndTime(Context context) {
        try {

            Class inetAddressClass = Class.forName("java.net.InetAddress");
            Field implClassDeclaredField = inetAddressClass.getDeclaredField("impl");
            implClassDeclaredField.setAccessible(true);
            Object impl = implClassDeclaredField.get(null);

            Class Inet6AddressImplClass = Class.forName("java.net.Inet6AddressImpl");
            Object newProxyInstance = Proxy.newProxyInstance(context.getClassLoader(), Inet6AddressImplClass.getInterfaces(), new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                    Log.d("gzp_invoke_Inet6AddressImplClass", method.getName());
                    if ("lookupAllHostAddr".equals(method.getName()) && args.length > 0) {
                        InetAddress[] addresses = sDNSMap.get(args[0]);
                        if (addresses != null) {
                            for (InetAddress address : addresses) {
                                if (address != null) {
                                    Log.d("gzp_invoke_cache", address.toString());
                                }
                            }
                            return addresses;
                        }

                        addresses = (InetAddress[]) method.invoke(impl, args);
                        for (InetAddress address : addresses) {
                            if (address != null) {
                                Log.d("gzp_invoke_return", address.toString());
                            }
                        }
                        sDNSMap.put((String) args[0], addresses);
                        return addresses;
                    }
                    return method.invoke(impl, args);
                }
            });
            implClassDeclaredField.set(null, newProxyInstance);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }


}
