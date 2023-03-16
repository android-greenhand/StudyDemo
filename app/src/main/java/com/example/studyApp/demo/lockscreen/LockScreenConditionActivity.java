package com.example.studyApp.demo.lockscreen;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studyApp.R;
import com.example.studyApp.main.MainActivity;

public class LockScreenConditionActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen_condition);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LockScreenMsgReceiver receiver = new LockScreenMsgReceiver();
                IntentFilter filter = new IntentFilter();
                filter.addAction(LockScreenMsgReceiver.ACTION);
                registerReceiver(receiver, filter);
                startService(new Intent(LockScreenConditionActivity.this, LockScreenService.class));
                Toast.makeText(LockScreenConditionActivity.this, "启动服务，注册监听", Toast.LENGTH_SHORT).show();
            }
        });
          openLockScreenActivity();


//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                openNotification();
//            }
//        }, 5000);
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                openNotification2();
//            }
//        }, 10000);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private void openLockScreenActivity() {
        LockScreenMsgReceiver receiver = new LockScreenMsgReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(LockScreenMsgReceiver.ACTION);
        registerReceiver(receiver, filter);
        startService(new Intent(this, LockScreenService.class));
        Toast.makeText(this, "启动服务，注册监听", Toast.LENGTH_SHORT).show();
    }


    private void openNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String channelId = "imservice";
            String channelName = "前台通知渠道测试";
            String description = "测试内容";
            int importance = NotificationManager.IMPORTANCE_HIGH;// 这个重要等级设置低的话，需要手动去设置页面打开通知渠道
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setSound((Uri) null, (AudioAttributes) null); //设置声音
            channel.setDescription(description);
            channel.enableVibration(true);//设置振动
            notificationManager.createNotificationChannel(channel);
            Intent intent = new Intent(this, MainActivity.class);
            PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
            int notifyID = 1;
            Notification notification = new Notification.Builder(this, channelId)
                    .setContentTitle("通知标题")
                    .setContentText("通知内容")
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pi)
                    .setStyle(new Notification.MediaStyle())
                    .setAutoCancel(true)
                    .setCustomContentView(createRemoteViews(false))
                    .setVisibility(Notification.VISIBILITY_PUBLIC)
                    .setFullScreenIntent(pi, true)
                    .build();
            notificationManager.notify(notifyID, notification);
        }
    }

    private void openNotification2() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String channelId = "imservice";
            String channelName = "前台通知渠道测试";
            String description = "测试内容";
            int importance = NotificationManager.IMPORTANCE_HIGH;// 这个重要等级设置低的话，需要手动去设置页面打开通知渠道
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setSound((Uri) null, (AudioAttributes) null); //设置声音
            channel.setDescription(description);
            channel.enableVibration(true);//设置振动
            notificationManager.createNotificationChannel(channel);
            Intent intent = new Intent(this, MainActivity.class);
            PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
            int notifyID = 1;
            Notification notification = new Notification.Builder(this, channelId)
                    .setContentTitle("通知标题")
                    .setContentText("通知内容")
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pi)
                    .setStyle(new Notification.MediaStyle())
                    .setAutoCancel(true)
                    .setCustomContentView(createRemoteViews(true))
                    .setVisibility(Notification.VISIBILITY_PUBLIC)
                    .setFullScreenIntent(pi, true)
                    .build();
            notificationManager.notify(notifyID, notification);

            notificationManager.getNotificationChannel("1");

        }
    }

    private RemoteViews createRemoteViews(boolean update) {
        RemoteViews remoteViews = new RemoteViews(this.getPackageName(),
                R.layout.activity_lock_screen_remote_view);

        if (update) {
            remoteViews.setTextViewText(R.id.btn, "已更新");
        }

        return remoteViews;
    }


}



