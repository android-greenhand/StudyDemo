package com.example.studyApp.demo.lockscreen;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class LockScreenService extends Service {
    private static final String TAG = "LockScreenService";

    MyBinder myBinder = new MyBinder();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //放回的Ibinder是一个接口，放回Binder对象（继承了IBinder）也可以
        //返回的Binder会传入到ServiceConnection的重写方法onServiceConnected中
        Log.d(TAG, "onBind: ");
        return myBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d(TAG, "onRebind: ");
        //当onUnbind的返回值为true时，与该服务绑定的活动离开视线后再重新回到视线后，重新bindService时会调用
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind: ");
        return true;

    }


    @Override
    public void onCreate() {
        //在onBind或onStartCommand前调用
        super.onCreate();
        Log.d(TAG, "onCreate: ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        sendMessage();
        return super.onStartCommand(intent, flags, startId);
        //startService时调用

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        //stopService时调用
    }

    /**
     * 模仿推送，发消息
     */
    private void sendMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                while (true){
//
//                }

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent();
                intent.setAction(LockScreenMsgReceiver.ACTION);
                sendBroadcast(intent); //发送广播

            }
        }).start();
    }

    public static class MyBinder extends Binder {

    }
}


