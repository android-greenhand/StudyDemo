package com.example.studyApp.demo.screenCapture;

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.studyApp.R
import kotlinx.android.synthetic.main.activity_screen_capture.*

class ScreenCaptureActivity : AppCompatActivity() {
    lateinit var mScreenCaptureUtil:ScreenCaptureUtil
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_capture)

        web_view.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }
        }
        web_view.settings.javaScriptEnabled = true
        val url = "https://www.baidu.com"
        web_view.loadUrl(url)
        mScreenCaptureUtil = ScreenCaptureUtil(this,
                object : ScreenCaptureUtil.ScreenCaptureResultListener {
                    override fun onFail() {
                    }

                    override fun onScreenCaptureSuccess(bitmap: Bitmap?) {
                        screen_capture_result.visibility = View.VISIBLE
                        screen_capture_result.setImageBitmap(bitmap)

                    }
                })

        mScreenCaptureUtil.setScrollViewAndContentHeight(web_view)
        screen_capture.setOnClickListener {
            mScreenCaptureUtil.startScreenCapture()
        }

        long_screen_capture.setOnClickListener {
            screen_capture_result.visibility = View.GONE
            mScreenCaptureUtil.startLongScreenCapture()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mScreenCaptureUtil.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onStop() {
        super.onStop()
        mScreenCaptureUtil.onDestroy();
    }



}