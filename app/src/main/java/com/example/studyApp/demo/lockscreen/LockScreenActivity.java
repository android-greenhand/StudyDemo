package com.example.studyApp.demo.lockscreen;

import android.app.KeyguardManager;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studyApp.R;
import com.jaeger.library.StatusBarUtil;

public class LockScreenActivity extends AppCompatActivity {

    private static final String TAG = "LockScreenActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        StatusBarUtil.setTransparent(this);
        Log.d(TAG, "onCreate");
        //四个标志位顾名思义，分别是锁屏状态下显示，解锁，保持屏幕长亮，打开屏幕。这样当Activity启动的时候，它会解锁并亮屏显示。
        Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED //锁屏状态下显示
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD //解锁
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON //保持屏幕长亮
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON); //打开屏幕
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);

        Drawable wallPaper = wallpaperManager.getDrawable();
        win.setBackgroundDrawable(wallPaper);


//        Drawable wallPaper = wallpaperManager.getBuiltInDrawable(WallpaperManager.FLAG_LOCK);
//        win.setBackgroundDrawable(wallPaper);
//        win.setBackgroundDrawable(null);

        setContentView(R.layout.activity_lock_screen);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "锁屏通知被点击");
                KeyguardManager keyguardManager = (KeyguardManager) v.getContext().getSystemService(Context.KEYGUARD_SERVICE);
                keyguardManager.newKeyguardLock("").disableKeyguard(); //解锁
//点击进入消息对应的页面
                //   v.getContext().startActivity(new Intent(v.getContext(), DetailsActivity.class));
                //先解锁系统自带锁屏服务，放在锁屏界面里面
                finish();
            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
}



