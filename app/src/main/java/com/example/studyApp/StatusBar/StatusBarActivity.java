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
//        StatusBarUtils.statusBarLightModeWithFullScreen(this,false);
//      //  StatusBarUtils.statusBarLightMode(this,true);
//
//      //  StatusBarUtils.setStatusBarColor(this,R.color.colorAccent);
//
//      // StatusBarUtils.setTranslucent(this);
//        StatusBarUtils.transparencyBar(this);

    }
}