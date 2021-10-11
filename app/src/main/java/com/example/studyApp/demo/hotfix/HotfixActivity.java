package com.example.studyApp.demo.hotfix;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.studyApp.R;

public class HotfixActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotfix);
        TextView textView = findViewById(R.id.activity_hot_fix_text);
        textView.setText(HotfixException.getStringWithException());
    }
}