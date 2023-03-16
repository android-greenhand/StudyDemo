package com.example.studyApp.demo.lockscreen;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class LockScreenMsgReceiver extends BroadcastReceiver {
    private static final String TAG = "LockScreenMsgReceiver";
    public static final String ACTION = "com.example.studyApp.demo.lockscreen.LockScreenMsgReceiver";


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive:收到了锁屏消息 ");
        String action = intent.getAction();
        if (action.equals(ACTION)) {
            //管理锁屏的一个服务
            KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            String text = km.inKeyguardRestrictedInputMode() ? "锁屏了" : "屏幕亮着的";
            Log.i(TAG, "text: " + text);
            if (km.inKeyguardRestrictedInputMode()) {
                Log.i(TAG, "onReceive:锁屏了 ");
                //判断是否锁屏
                Intent alarmIntent = new Intent(context, LockScreenActivity.class);
                //在广播中启动Activity的context可能不是Activity对象，所以需要添加NEW_TASK的标志，否则启动时可能会报错。
                alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(alarmIntent); //启动显示锁屏消息的activity
            }

        }
    }
}
