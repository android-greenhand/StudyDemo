package com.example.studyApp.xiangxue.prxoy;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studyApp.R;

import java.lang.annotation.Annotation;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

/**
 * author: a58
 * data: 2021/2/26
 * desc:
 */
public class ProxyActivity extends AppCompatActivity implements View.OnClickListener {

    int[ ] mViewIds;
     @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proxy);
         sss(this);
    }


    void sss(Activity activity){
        Class activityClass = activity.getClass();
        try {
           Method onClickMethod =  activityClass.getDeclaredMethod("onClick", View.class);
            if(onClickMethod.isAnnotationPresent(onClick.class)){
               Annotation[] annotations =  onClickMethod.getDeclaredAnnotations();
                for (Annotation annotation : annotations) {
                    if(annotation instanceof  onClick){
                        mViewIds  = ((onClick) annotation).value();
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        if(mViewIds != null && mViewIds.length>0){
            for (int viewId : mViewIds) {
                    findViewById(viewId).setOnClickListener(this);
            }
        }


    }

    @Override
    @onClick({R.id.proxy_button1,R.id.proxy_button2})
    public void onClick(View v) {
        Log.d("gzp",String.valueOf(v.getId()));
    }
}
