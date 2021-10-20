package com.example.studyApp.jetpack

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.studyApp.R
import com.example.studyApp.behavior.ItemDataBean
import com.example.studyApp.databinding.ActivityDataBindingBindingImpl

class DataBindingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewDataBinding = DataBindingUtil.setContentView<ActivityDataBindingBindingImpl>(this, R.layout.activity_data_binding)
        viewDataBinding.dataBindingBean = DataBindingBean("this is DataBinding Demo")
    }
}