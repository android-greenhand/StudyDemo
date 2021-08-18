package com.example.studyApp.demo.plugin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.studyApp.R;
import com.example.studyApp.utils.PluginTool;

import org.w3c.dom.Text;

public class PluginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plugin);
        TextView textView =  findViewById(R.id.plugin_text);
        textView.setText(PluginTool.getStringFromPlugin(this));
    }


}