package com.example.studyApp.demo.plugin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.studyApp.R;

public class PluginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plugin);
        TextView textView = findViewById(R.id.plugin_text);
        textView.setText(PluginTool.getStringFromPlugin(this));

        Log.e("gzp",PluginProxyActivity.class.getName());

        findViewById(R.id.open_plugin_activity).setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra(PluginTool.HOOK_FLAG,"PluginAppActivity");
            intent.setComponent(new ComponentName("com.example.plugin", "com.example.plugin.PluginAppActivity"));
            startActivity(intent);
        });

    }


}