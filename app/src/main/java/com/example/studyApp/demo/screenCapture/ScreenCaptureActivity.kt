package com.example.studyApp.demo.screenCapture

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.*
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.studyApp.R
import kotlinx.android.synthetic.main.activity_screen_capture.*

class ScreenCaptureActivity : AppCompatActivity() {
    lateinit var mScreenShotUtil: ScreenShotUtil
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
        val url = "http://www.baidu.com"
        web_view.loadUrl(url)
        mScreenShotUtil = ScreenShotUtil(this,
                object : ScreenShotUtil.ScreenCaptureResultListener {
                    override fun onFail() {
                    }

                    override fun onScreenCaptureSuccess(bitmap: Bitmap?) {
                        screen_capture_result.visibility = View.VISIBLE
                        screen_capture_result.setImageBitmap(bitmap)

                    }
                })




    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mScreenShotUtil.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onStop() {
        super.onStop()
        mScreenShotUtil.onDestroy();
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_screen_capture_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.screen_capture -> mScreenShotUtil.startScreenCapture()
            R.id.long_screen_capture -> {
                screen_capture_result.visibility = View.GONE
                mScreenShotUtil.startLongScreenCapture()
            }
        }
        return true
    }

}