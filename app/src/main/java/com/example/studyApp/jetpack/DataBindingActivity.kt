package com.example.studyApp.jetpack

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.studyApp.R
import com.example.studyApp.databinding.ActivityDataBindingBindingImpl

class DataBindingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityDataBindingBindingImpl>(this,R.layout.activity_data_binding)
    }
}