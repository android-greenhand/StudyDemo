package com.example.studyApp.verify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.studyApp.R;

public class VerifyActivity extends AppCompatActivity {

    Integer[] mInteger;

    static final String TAG = "VerifyActivityTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);


        findViewById(R.id.launch_many_times_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MemoryDetectionCallback2.getInstance().addActivity(VerifyActivity.this);
                verifyActivityRecycle();
            }
        });

        Thread.setDefaultUncaughtExceptionHandler(new CustomUncaughtExceptionHandler());

        Thread.currentThread().setUncaughtExceptionHandler(new CustomUncaughtExceptionHandler());
        findViewById(R.id.uncaughtExceptionHandler).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = 1 / 0;
            }
        });


    }

    /**
     * 打开多个Activity
     * 验证Activity 在内存不足时，是否被回收
     */
    private void verifyActivityRecycle() {
        findViewById(R.id.launch_many_times_activity).setOnClickListener(v -> {
            mInteger = new Integer[1024 * 1024 * 10];
            startActivity(new Intent(VerifyActivity.this, VerifyActivity.class));
        });
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d("VerifyActivity", "onLowMemory");

    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.d("VerifyActivity", "onTrimMemory");

    }

    @Override
    protected void onStop() {
        super.onStop();
        //  mInteger = null;
        Log.d("VerifyActivity", "VerifyActivity 页面停止");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("VerifyActivity", "VerifyActivity 页面被回收");
    }


    static class CustomUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

        @Override
        public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
            Log.d(TAG, "当前线程：\t" + t.getName() + "异常：\t" + e.getMessage());
        }
    }
}