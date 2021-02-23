package com.example.studyApp.customView;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studyApp.R;

public class ToastActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toast);

//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//              TextView textView=  new TextView(ToastActivity.this);
//                textView.setText("dbkgfbhkf");
//            }
//        }).start();

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        Toast.makeText(getApplication(),"您好 旧时光",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }).start();

            }
        });
    }
}