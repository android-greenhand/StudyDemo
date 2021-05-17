package com.example.studyApp.demo.screenCapture

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.ImageReader.OnImageAvailableListener
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.*
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.View.OnTouchListener
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.studyApp.demo.screenCapture.ScreenShotUtilHelper.Companion.compressBitmap
import com.example.studyApp.demo.screenCapture.ScreenShotUtilHelper.Companion.getNavigationHeight
import com.example.studyApp.demo.screenCapture.ScreenShotUtilHelper.Companion.getStatusBarHeight
import com.example.studyApp.demo.screenCapture.ScreenShotUtilHelper.Companion.saveFile
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener

/**
 *
 * author: a58
 * data: 2021/3/24
 * desc:
 *
 */
class ScreenShotUtil(val mActivity: Activity, val mScreenCaptureResultListener: ScreenShotUtil.ScreenCaptureResultListener) {

    companion object {
        //状态码
        private const val FAILED_STATE = 0
        private const val SUCCESS_STATE = 1 //成功状态
        private const val SCROLL_STATE = 2 //滚动状态

        private const val REQUEST_MEDIA_PROJECTION = 1
        private const val LONG_SCREEN_CAPTURE_MAX_COUNT = 5
        public val TAG = ScreenShotUtil::class.java.canonicalName
        private val LOCK = Object()
    }

    @Volatile
    var isScrollBottom = false

    @Volatile
    var mScrollActualHeight = 0


    val mEndValueArray = mutableListOf<Int>()
    var startY = 0
    private var mResultCode = 0
    private var mResultData: Intent? = null
    private var mMediaProjection: MediaProjection? = null
    private var mVirtualDisplay: VirtualDisplay? = null
    private var mMediaProjectionManager: MediaProjectionManager
    private var mImageReader: ImageReader? = null
    private var mScreenWidth = 0
    private var mScreenHeight = 0
    private var mScreenDensity = 0
    private var mImageAvailableListener: OnImageAvailableListener? = null
    private var mNavigationHeight = 0
    private var mStatusBarHeight = 0
    private var mCanScrollView: View? = null
    private var mBitmapActualHeight = 0

    //截图的方式(true=长截图 false=截图) 默认为false
    private var mIsLongScreenshot = false

    private var mDecorView: ViewGroup

    /**
     * 在长截图之前的准备工作
     */
    private var mSupernatantView: FrameLayout? = null
    private lateinit var mSupernatantTextView: TextView

    @Volatile
    private var mClickScreenShotEnd = false
    private var mScrollHeight = 0

    //临时的存放图片变量
    private var mTempBitmap: Bitmap? = null

    //截图handler
    private val mScreenCaptureHandler: ScreenCaptureHandler

    //长截图任务
    private var mLongScreenshotRunnable: LongScreenshotRunnable? = null


    init {
        val windowManager = mActivity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(metrics)
        mScreenDensity = metrics.densityDpi
        mScreenWidth = metrics.widthPixels
        mScreenHeight = metrics.heightPixels
        mMediaProjectionManager = mActivity.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        mDecorView = mActivity.window.decorView as ViewGroup
        mScreenCaptureHandler = ScreenCaptureHandler(mActivity.mainLooper)
    }

    //截图事件监听
    private val mScreenCaptureListener: ScreenshotListener = object : ScreenshotListener {
        override fun onSuccess(bitmap: Bitmap, isLongScreenshot: Boolean) {
            removeSupernatantViewGroup()
            removeAllScrollListener()
            Toast.makeText(mActivity, "截屏成功", Toast.LENGTH_SHORT).show()
            val newBitmap = compressBitmap(bitmap)
            newBitmap?.let {
                mScreenCaptureResultListener.onScreenCaptureSuccess(newBitmap)
                saveFile(newBitmap, "11.png")
                Log.d(TAG, "Bitmap大小:" + newBitmap.byteCount / 1024)
                onComplete()
                return
            }
            Toast.makeText(mActivity, "截屏失败", Toast.LENGTH_SHORT).show()
            onComplete()

        }

        override fun onFail() {
            removeSupernatantViewGroup()
            Toast.makeText(mActivity, "截屏失败", Toast.LENGTH_SHORT).show()
            mScreenCaptureResultListener.onFail()
            onComplete()
        }
    }

    private fun removeAllScrollListener() {

    }


    var mChildCount = 0
    private fun findCanScrollVerticalView(rootView: View) {
        if (rootView.canScrollVertically(1)) {
            Log.d(TAG + "CanScrollVerticalView", rootView.javaClass.toString())
            val rect = Rect()
            rootView.getGlobalVisibleRect(rect)
            val height = rect.bottom - rect.top
            if (height >= mScreenHeight / 3) {
                setScrollViewAndContentHeight(rootView)
                Log.d(TAG + "setScrollListener", rootView.javaClass.toString())
            }
        }
        if (rootView is ViewGroup) {
            val childCount = rootView.childCount
            for (i in 0 until childCount) {
                val child = rootView.getChildAt(i)
                mEndValueArray.add(0)
                setScrollListener(child,mChildCount)
                mChildCount++
                findCanScrollVerticalView(child)
            }
        }
    }


    private fun setScrollListener(view: View,childCount :Int) {
        if (view is RecyclerView) {
            view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    mEndValueArray[childCount] += dy
                }
            })
            return
        }
        if (view is AppBarLayout) {
            view.addOnOffsetChangedListener(OnOffsetChangedListener { _, verticalOffset ->
                Log.d(TAG + "AppBarLayout", verticalOffset.toString() + "")
                mEndValueArray[childCount] = -verticalOffset
            })
            return
        }
        view.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            mEndValueArray[childCount] = scrollY
            Log.d(TAG + "other View", view.javaClass.toString())
        }
    }


    /**
     * 设置要滚动的View和其内容的高度
     *
     * @param view
     */
    private fun setScrollViewAndContentHeight(view: View?) {
        mCanScrollView = view
    }

    fun startScreenCapture() {
        if (mMediaProjection != null) {
            setUpVirtualDisplay()
        } else if (mResultCode != 0 && mResultData != null) {
            setUpMediaProjection()
            setUpVirtualDisplay()
        } else {
            mActivity.startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION)
        }
    }

    fun startLongScreenCapture() {
        findCanScrollVerticalView(mDecorView)
        if (mCanScrollView == null) {
            Toast.makeText(mActivity, "请先设置长截图要滚动的View", Toast.LENGTH_SHORT).show()
            mScreenCaptureListener.onFail()
            return
        }
        mIsLongScreenshot = true
        preLongScreenshot()
    }

    private fun setUpMediaProjection() {
        mMediaProjection = mMediaProjectionManager.getMediaProjection(mResultCode, mResultData!!)
        mImageAvailableListener = ImageAvailableListener()
    }

    private fun setUpVirtualDisplay() {
        mImageReader = ImageReader.newInstance(mScreenWidth, mScreenHeight, PixelFormat.RGBA_8888, 2)
        mImageReader?.setOnImageAvailableListener(mImageAvailableListener, null)
        mScreenCaptureHandler.postDelayed(mCreateVirtualDisplay, 30)
    }

    private val mCreateVirtualDisplay = Runnable {
        mVirtualDisplay = mMediaProjection?.createVirtualDisplay("ScreenCapture",
                mScreenWidth, mScreenHeight, mScreenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mImageReader?.surface, null, null)
    }
    
    private val mImageTransformBitmap = Runnable {
        try {
            mImageReader?.acquireLatestImage().apply {
                val bitmap = imageToBitmap(this)
                if (bitmap != null) {
                    if (mIsLongScreenshot) {
                        synchronized(LOCK) {
                            mTempBitmap = bitmap
                            LOCK.notify()
                        }
                    } else {
                        mScreenCaptureListener.onSuccess(bitmap, false)
                    }
                } else {
                    mScreenCaptureListener.onFail()
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, e.toString())
            mScreenCaptureListener.onFail()
        } finally {
            mVirtualDisplay?.let {
                it.release()
                mVirtualDisplay = null
            }

            mImageReader?.let {
                it.setOnImageAvailableListener(null, null)
                it.close()
                mImageReader = null
            }
        }
    }

    /**
     * 将Image转化成Bitmap的固定用法
     *
     * @param image
     * @return bitmap
     */
    private fun imageToBitmap(image: Image?): Bitmap? {
        if (image == null) {
            return null
        }
        val width = image.width
        val height = image.height
        val planes = image.planes
        val buffer = planes[0].buffer
        val pixelStride = planes[0].pixelStride
        val rowStride = planes[0].rowStride
        val rowPadding = rowStride - pixelStride * width
        val bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888)
        bitmap.copyPixelsFromBuffer(buffer)
        return bitmap
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun createSupernatantViewGroup() {
        mSupernatantView = FrameLayout(mActivity)
        mSupernatantTextView = TextView(mActivity)
        mSupernatantTextView.apply {
            this.text = "点击屏幕，停止截屏"
            this.gravity = Gravity.BOTTOM or Gravity.LEFT
            this.textSize = 20f
            this.setBackgroundColor(Color.argb(50, 0, 0, 0))
        }

        mSupernatantView?.apply {
            this.addView(mSupernatantTextView, FrameLayout.LayoutParams.MATCH_PARENT, 300)
            this.setOnTouchListener(OnTouchListener { _, event ->
                if (event.source == InputDevice.SOURCE_ANY) {
                    return@OnTouchListener false
                } else {
                    mClickScreenShotEnd = true
                    mSupernatantTextView.text = "正在合并截图"
                }
                true
            })
        }
        mDecorView.addView(mSupernatantView, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
    }

    private fun removeSupernatantViewGroup() {
        mSupernatantView?.let {
            it.visibility = View.GONE
            mDecorView.removeView(it)
        }
    }

    private fun preLongScreenshot() {
        mNavigationHeight = getNavigationHeight(mActivity)
        mStatusBarHeight = getStatusBarHeight(mActivity)
        mScrollHeight = mCanScrollView!!.height / 2
        if (mScrollHeight <= 0) {
            mScrollHeight = 20
        }
        longScreenshot()
    }

    /**
     * 长截图
     */
    private fun longScreenshot() {
        mLongScreenshotRunnable = LongScreenshotRunnable()
        val thread = Thread(mLongScreenshotRunnable)
        thread.start()
    }

    /**
     * 执行滚动的高度动画
     *
     * @param scrollHeight
     */


    private fun startScrollAnimation(scrollHeight: Int) {

        //scrollHeight = 0,证明是第一次截图,那就无需滚动控件
        if (scrollHeight <= 0) {
            startScreenCapture()
            return
        }
        if (mSupernatantView == null) {
            createSupernatantViewGroup()
        }
        val x = mScreenWidth / 2
        val y = mScreenHeight - mNavigationHeight - mStatusBarHeight
        val event = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, x.toFloat(), y.toFloat(), 0)
        event.source = InputDevice.SOURCE_ANY
        mActivity.dispatchTouchEvent(event)
        val scrollAnimator = ValueAnimator.ofInt(y, y / 2)
        scrollAnimator.interpolator = LinearInterpolator()
        scrollAnimator.duration = 1000
        scrollAnimator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            if (!mClickScreenShotEnd && mCanScrollView!!.canScrollVertically(1)) {
                event.setLocation(x.toFloat(), value.toFloat())
                event.action = MotionEvent.ACTION_MOVE
                mActivity.dispatchTouchEvent(event)
            } else {
                isScrollBottom = true
            }
        }
        scrollAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                Log.d(TAG + "onAnimationEnd:getY:", event.y.toString())
                Log.d(TAG + "onAnimationEnd:getRawY:", event.rawY.toString())
                Log.d(TAG + "onAnimationEnd:" + "Diff:", (y - event.y).toString())
                event.action = MotionEvent.ACTION_UP
                mActivity.dispatchTouchEvent(event)
                event.recycle()
                mScrollActualHeight = mEndValueArray.sum() - startY
                Log.d(TAG + "onAnEnd:ActualHeight:", mScrollActualHeight.toString())
                startScreenCapture()
            }

            override fun onAnimationStart(animation: Animator) {
                startY = mEndValueArray.sum()
                Log.d(TAG + "onStart:getY:", event.y.toString())
                Log.d(TAG + "onStart:getRawY:", event.rawY.toString())
                Log.d(TAG + "onStart:startY:", startY.toString())
            }
        })
        scrollAnimator.start()
    }


    /**
     * @param
     * @return
     */
    private fun mergeBitmap(resultBitmap: Bitmap?, scrollBitmap: Bitmap, canvas: Canvas, paint: Paint, srcTop: Int, srcBottom: Int) {
        if (resultBitmap == null) {
            return
        }
        val height = srcBottom - srcTop
        val src = Rect(0, srcTop, scrollBitmap.width, srcBottom)
        val des = RectF(0F, mBitmapActualHeight.toFloat(), scrollBitmap.width.toFloat(), (mBitmapActualHeight + height).toFloat())
        canvas.drawBitmap(scrollBitmap, src, des, paint)
        mBitmapActualHeight += height
    }

    private inner class ScreenCaptureHandler(looper: Looper?) : Handler(looper!!) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                SUCCESS_STATE -> {
                    val bitmap = msg.obj as Bitmap
                    mScreenCaptureListener.onSuccess(bitmap, mIsLongScreenshot)
                    Log.d(TAG, "------------ finish screenshot ------------")
                }
                FAILED_STATE -> {
                    mScreenCaptureListener.onFail()
                    Log.d(TAG, "------------ failed  screenshot ------------")
                }
                SCROLL_STATE -> {
                    val scrollHeight = msg.arg1
                    startScrollAnimation(scrollHeight)
                }
            }
        }

        /**
         * 发送滚动消息
         *
         * @param scrollHeight 滚动的高度
         */
        fun sendScrollMessage(scrollHeight: Int) {
            val msg = this.obtainMessage(SCROLL_STATE)
            msg.arg1 = scrollHeight
            sendMessage(msg)
        }

        /**
         * 发送成功消息
         *
         * @param bitmap 图片
         */
        fun sendSuccessMessage(bitmap: Bitmap?) {
            val msg = this.obtainMessage(SUCCESS_STATE)
            msg.obj = bitmap
            sendMessage(msg)
        }

        /**
         * 发送失败消息
         */
        fun sendFailedMessage() {
            val msg = this.obtainMessage(FAILED_STATE)
            sendMessage(msg)
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_MEDIA_PROJECTION && resultCode != 0) {
            mResultCode = resultCode
            mResultData = data
            setUpMediaProjection()
            setUpVirtualDisplay()
        } else {
            mScreenCaptureResultListener.onFail()
        }
    }

    private fun onComplete() {
        mTempBitmap?.let {
            it.recycle()
            mTempBitmap = null
        }
        mImageReader?.let {
            it.setOnImageAvailableListener(null, null)
            it.close()
            mImageReader = null
        }
        mScrollHeight = 0
        mIsLongScreenshot = false
    }

    /**
     *
     */
    fun onDestroy() {
        mTempBitmap?.let {
            it.recycle()
            mTempBitmap = null
        }
        //更改标志位,让线程安全退出
        mLongScreenshotRunnable?.isDestroy = true

        //销毁,防止内存泄漏
        mScreenCaptureHandler.removeCallbacksAndMessages(null)

        mVirtualDisplay?.let {
            it.release()
            mVirtualDisplay = null
        }

        mImageReader?.let {
            it.setOnImageAvailableListener(null, null)
            it.close()
            mImageReader = null
        }

        mMediaProjection?.let {
            it.stop()
            mMediaProjection = null
        }
    }

    private inner class ImageAvailableListener : OnImageAvailableListener {
        override fun onImageAvailable(reader: ImageReader) {
            if (reader !== mImageReader) {
                mScreenCaptureListener.onFail()
                return
            }
            mScreenCaptureHandler.postDelayed(mImageTransformBitmap, 1000)
        }
    }


    //子线程进行的任务
    private inner class LongScreenshotRunnable : Runnable {
        //控制线程退出的标志位.
        var isDestroy = false
        override fun run() {
            synchronized(LOCK) {

                //1:创建图纸
                val resultBitmap = Bitmap.createBitmap(mScreenWidth, LONG_SCREEN_CAPTURE_MAX_COUNT * mScreenHeight, Bitmap.Config.RGB_565)
                //2:创建画布,并绑定图纸
                val canvas = Canvas(resultBitmap)
                //3:创建画笔
                val paint = Paint()
                for (i in 0 until LONG_SCREEN_CAPTURE_MAX_COUNT) {
                    // 如果销毁 点击停止 滚动到底部 则结束
                    if (isDestroy || mClickScreenShotEnd || isScrollBottom) {
                        Log.d(TAG, "截屏任务异常退出：isDestroy:" + isDestroy + "\t mClickScreenShotEnd:" + mClickScreenShotEnd
                                + "\t isScrollBottom:" + isScrollBottom)
                        break
                    }
                    if (i == 0) {
                        mScreenCaptureHandler.sendScrollMessage(0)
                    } else {
                        mScreenCaptureHandler.sendScrollMessage(mScrollHeight)
                    }
                    try {
                        Log.d(TAG, "当前线程阻塞,等待主(UI)线程滚动截图")
                        LOCK.wait()
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                    if (i == 0) {
                        mTempBitmap?.let {
                            mergeBitmap(resultBitmap, it, canvas, paint, 0, it.height - mNavigationHeight)
                        }
                    } else {
                        mTempBitmap?.let { mergeBitmap(resultBitmap, it, canvas, paint, it.height - mNavigationHeight - mScrollActualHeight, it.height - mNavigationHeight) }
                    }
                }
                mSupernatantTextView.text = "正在合并截图"
                if (mTempBitmap == null) {
                    mScreenCaptureHandler.sendFailedMessage()
                    return
                } else {
                    mTempBitmap?.let {
                        mergeBitmap(resultBitmap, it, canvas, paint, it.height - mNavigationHeight, it.height)
                    }
                }
                if (!isDestroy) {
                    //合并图片
                    Log.d(TAG, "合并图片成功")
                    //回调成功
                    resultBitmap.height = mBitmapActualHeight
                    mScreenCaptureHandler.sendSuccessMessage(resultBitmap)
                }
            }
        }
    }


    /**
     * 提供给外部的截图回调
     */
    interface ScreenCaptureResultListener {
        /**
         * 截图成功的回调
         *
         * @param bitmap 图片
         */
        fun onScreenCaptureSuccess(bitmap: Bitmap?)

        /**
         * 截图失败的回掉
         */
        fun onFail()
    }

    /**
     * 截图监听器
     */
    private interface ScreenshotListener {
        /**
         * 截图成功
         *
         * @param bitmap           图片
         * @param isLongScreenshot 是否是长截图
         */
        fun onSuccess(bitmap: Bitmap, isLongScreenshot: Boolean)

        /**
         * 截图失败
         */
        fun onFail()
    }
}