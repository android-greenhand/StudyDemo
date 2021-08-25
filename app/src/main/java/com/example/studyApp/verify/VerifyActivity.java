package com.example.studyApp.verify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.studyApp.R;

public class VerifyActivity extends AppCompatActivity {

    Integer [] mInteger;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        MemoryDetectionCallback2.getInstance().addActivity(this);
        verifyActivityRecycle();
    }

    /**
     *
     * 打开多个Activity
     * 验证Activity 在内存不足时，是否被回收
     */
    private void verifyActivityRecycle() {
        findViewById(R.id.launch_many_times_activity).setOnClickListener(v -> {
            mInteger = new Integer[1024* 1024*10];
            startActivity(new Intent(VerifyActivity.this,VerifyActivity.class));
        });
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d("VerifyActivity","onLowMemory");

    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.d("VerifyActivity","onTrimMemory");

    }

    @Override
    protected void onStop() {
        super.onStop();
      //  mInteger = null;
        Log.d("VerifyActivity","VerifyActivity 页面停止");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("VerifyActivity","VerifyActivity 页面被回收");
    }
}