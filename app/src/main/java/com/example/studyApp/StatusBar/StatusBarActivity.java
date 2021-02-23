package com.example.studyApp.StatusBar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import com.example.studyApp.R;

public class StatusBarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_bar);
        getSupportActionBar().hide();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//
//            View decorView = getWindow().getDecorView();
//            //
//            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
//            decorView.setSystemUiVisibility(option);
//            getSupportActionBar().hide();
//            // 状态栏透明
//            getWindow().setStatusBarColor(Color.TRANSPARENT);
//        }

    }
}