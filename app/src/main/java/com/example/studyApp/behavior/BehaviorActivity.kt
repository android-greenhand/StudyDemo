package com.example.studyApp.behavior

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.example.studyApp.R
import com.example.studyApp.demo.screenCapture.ScreenCaptureUtil
import com.example.studyApp.demo.screenCapture.ScreenShotUtil
import com.squareup.cycler.Recycler
import com.squareup.cycler.toDataSource
import kotlinx.android.synthetic.main.activity_behavior.*

@Route(path = "/test/BehaviorActivity")
class BehaviorActivity() : AppCompatActivity() {

    lateinit var mScreenShotUtil: ScreenShotUtil
    private val data: List<ItemDataBean> = initData()
    private val recyclerView: RecyclerView by lazy {
        findViewById<RecyclerView>(R.id.recyclerview)
    }
    lateinit var recycler: Recycler<ItemDataBean>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_behavior)

        recyclerView.apply {
            this.layoutManager = LinearLayoutManager(context)
            addItemDecoration(this)
        }

        recycler = Recycler.adopt(recyclerView) {
            row<ItemDataBean, TextView> {
                create { context: Context ->
                    view = TextView(context).apply {
                        val lp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200);
                        lp.bottomMargin = 10;
                        this.layoutParams = lp
                        gravity = Gravity.CENTER
                        textSize = 40f
                        val alpha = (0..255).random()
                        val red = (0..255).random()
                        val green = (0..255).random()
                        val blue = (0..255).random()
                        setBackgroundColor(Color.argb(alpha, red, green, blue))

                    }

                    bind { itemDataBean -> view.text = itemDataBean.string }
                }
            }

        }

        recycler.update { data = this@BehaviorActivity.data.toDataSource() }

        mScreenShotUtil = ScreenShotUtil(this,
                object : ScreenShotUtil.ScreenCaptureResultListener {
                    override fun onFail() {
                    }

                    override fun onScreenCaptureSuccess(bitmap: Bitmap?) {
                        screen_capture_result_behavior_.visibility = View.VISIBLE
                        screen_capture_result_behavior_.setImageBitmap(bitmap)
                    }
                })


        auto_scroll_btn.setOnClickListener {
            // animation()
            mScreenShotUtil.startLongScreenCapture()
        }

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }

    private fun initData(): List<ItemDataBean> {
        val list = ArrayList<ItemDataBean>();

        for (i in 1..100) {
            list.add(ItemDataBean(i.toString()))
        }
        return list
    }

    fun animation() {
        val viewHeight = 2000
        val anim = ValueAnimator.ofInt(viewHeight, viewHeight / 2)
        Log.d("gzp", viewHeight.toString())
        anim.duration = 1000;
        val event =
                MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 500f, viewHeight.toFloat(), 0);
        this.dispatchTouchEvent(event);
        anim.addUpdateListener { animation ->
            animation?.apply {
                val y = this.animatedValue as Int
                event.setLocation(500f, y.toFloat());
                event.action = MotionEvent.ACTION_MOVE;
                this@BehaviorActivity.dispatchTouchEvent(event);
                Log.d("gzp", recyclerView.canScrollVertically(1).toString())
            }
        };

        anim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                event.action = MotionEvent.ACTION_UP;
                this@BehaviorActivity.dispatchTouchEvent(event);
                event.recycle()
            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationStart(animation: Animator?) {

            }

        })
        anim.start();

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


    /**
     *
     *添加ItemDecoration
     *
     */

    private fun addItemDecoration(view:RecyclerView){
        view.addItemDecoration(CustomItemDecoration())
    }
}