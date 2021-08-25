package com.example.plugin;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class PluginAppActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.activity_plugin,null);
        setContentView(view);
    }


    @Override
    public Context getBaseContext() {
        Context context = PluginLoadUtil.getContext(getApplicationContext());
        return context == null ? super.getBaseContext() : context;
    }
}