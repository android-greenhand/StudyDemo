package com.example.studyApp.demo.ScreenCapture

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
    lateinit var mScreenCaptureUtil: ScreenCaptureUtil
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

//         val bitmap=   ScreenShotUtil(this@ScreenCaptureActivity).startScreenShot()
//            screen_capture_result.setImageBitmap(bitmap)
        }

        var y =1000;

        long_screen_capture.setOnClickListener {
            screen_capture_result.visibility = View.GONE
            mScreenCaptureUtil.startLongScreenCapture()
//            web_view.scrollTo(0,y)
//
//                    Log.d("gzpcanScrollVertically",web_view.canScrollVertically(-1).toString())
//                    Log.d("gzpcanScrollVertically",web_view.canScrollVertically(1).toString())
//                    Log.d("gzpcancontentHeight",web_view.contentHeight.toString())
//            Log.d("gzpcancontentHeight",web_view.scrollY.toString())
//
//            y+=100


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