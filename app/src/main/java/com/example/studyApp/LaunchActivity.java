package com.example.studyApp;

import android.content.Intent;
import android.os.Bundle;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studyApp.demo.plugin.PluginTool;
import com.example.studyApp.main.MainActivity;
import com.hd.splashscreen.text.SimpleConfig;
import com.hd.splashscreen.text.SimpleSplashFinishCallback;
import com.hd.splashscreen.text.SimpleSplashScreen;


public class LaunchActivity extends AppCompatActivity implements SimpleSplashFinishCallback{

    Class secondActivity =  MainActivity.class; // MainActivity.class
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        SimpleSplashScreen screen = findViewById(R.id.screen);
        SimpleConfig simpleConfig=new SimpleConfig(this);
        simpleConfig.setCallback(this);
        simpleConfig.setIconSize(2f);
        screen.addConfig(simpleConfig);

        screen.start();


        PluginTool.hooKStartActivity(this);
        PluginTool.hookInstrumentation();
    }

    @Override
    public void loadFinish() {
        startActivity(new Intent(this,secondActivity));
        finish();
    }
}