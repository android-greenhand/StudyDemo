package com.example.studyApp.customView.comment

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.webkit.WebView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView


class CustomWebView @JvmOverloads constructor(context: Context, attrs: AttributeSet) : WebView(context, attrs) {

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return super.dispatchTouchEvent(ev)
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(canScrollVertically(-1)) //负值向下滚动
        parent.requestDisallowInterceptTouchEvent(true)
        return super.onTouchEvent(event)
    }


    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return super.onInterceptTouchEvent(ev)
    }
}