package com.example.studyApp.JNI;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studyApp.R;

public class JNIActivity extends AppCompatActivity {


   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.starred_discount);

        TextView tv = findViewById(R.id.main_activity_recycle_view_item_name);
        tv.setText(new JNITools().StringFromJNI());
    }




}
