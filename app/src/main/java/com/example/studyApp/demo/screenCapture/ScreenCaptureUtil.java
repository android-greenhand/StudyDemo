package com.example.studyApp.demo.screenCapture;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;

import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;


import java.nio.ByteBuffer;


/**
 * author: gezongpan
 * data: 2021/3/8
 * desc:
 */
public class ScreenCaptureUtil {

    //状态码
    private static final int FAILED_STATE = 0;
    private static final int SUCCESS_STATE = 1;       //成功状态
    private static final int SCROLL_STATE = 2;        //滚动状态
    private static final int REQUEST_MEDIA_PROJECTION = 1;
    private static final int LONG_SCREEN_CAPTURE_MAX_COUNT = 5;
    public static final String TAG = ScreenCaptureUtil.class.getCanonicalName();
    private static final Object LOCK = new Object();

    private int mResultCode;
    private Intent mResultData;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private MediaProjectionManager mMediaProjectionManager;
    private ImageReader mImageReader;
    private int mScreenWidth;
    private int mScreenHeight;
    private int mScreenDensity;
    private ImageReader.OnImageAvailableListener mImageAvailableListener;
    private ScreenCaptureResultListener mScreenCaptureResultListener;
    private Activity mActivity;
    private int mNavigationHeight;
    private int mStatusBarHeight;
    private View mCanScrollView;
    int mBitmapActualHeight = 0;
    //截图的方式(true=长截图 false=截图) 默认为false
    private boolean mIsLongScreenshot = false;
    /**
     * 在长截图之前的准备工作
     */

    private FrameLayout mSupernatantView;
    private TextView mSupernatantTextView;

    volatile private boolean mClickScreenShotEnd = false;
    private int mScrollHeight;
    //临时的存放图片变量
    private Bitmap mTempBitmap;
    //截图handler
    private ScreenCaptureHandler mScreenCaptureHandler;
    //长截图任务
    private LongScreenshotRunnable mLongScreenshotRunnable;
    //截图事件监听
    private ScreenshotListener mScreenCaptureListener = new ScreenshotListener() {
        @Override
        public void onSuccess(Bitmap bitmap, boolean isLongScreenshot) {
            removeSupernatantViewGroup();
            Toast.makeText(mActivity, "截屏成功", Toast.LENGTH_SHORT).show();
            Bitmap newBitmap = ScreenShotUtilHelper.Companion.compressBitmap(bitmap);
            mScreenCaptureResultListener.onScreenCaptureSuccess(newBitmap);
            ScreenShotUtilHelper.Companion.saveFile(newBitmap, "11.png");
            Log.d(TAG, "Bitmap大小:" + newBitmap.getByteCount() / 1024);
            onComplete();
        }

        @Override
        public void onFail() {
            removeSupernatantViewGroup();
            Toast.makeText(mActivity, "截屏失败", Toast.LENGTH_SHORT).show();
            mScreenCaptureResultListener.onFail();
            onComplete();
        }
    };


    public ScreenCaptureUtil(Activity activity, ScreenCaptureResultListener screenCaptureResultListener) {
        mActivity = activity;
        WindowManager windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getRealMetrics(metrics);
        mScreenDensity = metrics.densityDpi;
        mScreenWidth = metrics.widthPixels;
        mScreenHeight = metrics.heightPixels;
        mMediaProjectionManager = (MediaProjectionManager) activity.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        mScreenCaptureResultListener = screenCaptureResultListener;
        mScreenCaptureHandler = new ScreenCaptureHandler(activity.getMainLooper());
        mDecorView = (ViewGroup) mActivity.getWindow().getDecorView();
        mContentView = mDecorView.findViewById(android.R.id.content);
    }


    public void findCanScrollVerticalView(View rootView){
        if(rootView.canScrollVertically(1)){
            Log.d(TAG+"findCanScrollVerticalView",rootView.getClass().toString());
            Rect rect = new Rect();
            rootView.getGlobalVisibleRect(rect);
            int height =  rect.bottom - rect.top;
            if(height>=mScreenHeight/3){
                setScrollViewAndContentHeight(rootView);
                Log.d(TAG+"setScrollListener",rootView.getClass().toString());
            }
        }
        if (rootView instanceof ViewGroup){
            ViewGroup viewGroup = (ViewGroup) rootView;
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = viewGroup.getChildAt(i);
                setScrollListener(child);
                findCanScrollVerticalView(child);
            }
        }

    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    void setScrollListener(final View view){
        if(view instanceof RecyclerView){
            ((RecyclerView)view).addOnScrollListener(new RecyclerView.OnScrollListener(){
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    mEndValue += dy;
                }
            });

            return;
        }

        if(view instanceof AppBarLayout){
            ( (AppBarLayout)view).addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    Log.d(TAG+"AppBarLayout",verticalOffset+"");
                    mEndValue1 = -verticalOffset;
                }
            });
            return;
        }

        view.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                mEndValue3 = scrollY;
                Log.d(TAG+"other View",view.getClass().toString());
            }
        });

    }

    private ViewGroup mDecorView;
    /**
     * 设置要滚动的View和其内容的高度
     *
     * @param view
     */

    private void setScrollViewAndContentHeight(View view) {
        this.mCanScrollView = view;
    }

    public void startScreenCapture() {
        if (mMediaProjection != null) {
            setUpVirtualDisplay();
        } else if (mResultCode != 0 && mResultData != null) {
            setUpMediaProjection();
            setUpVirtualDisplay();
        } else {
            mActivity.startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
        }
    }

    public void startLongScreenCapture() {
        findCanScrollVerticalView(mDecorView);
        if (mCanScrollView == null) {
            Toast.makeText(mActivity, "请先设置长截图要滚动的View", Toast.LENGTH_SHORT).show();
            mScreenCaptureListener.onFail();
            return;
        }
        mIsLongScreenshot = true;
        preLongScreenshot();
    }

    private void setUpMediaProjection() {
        mMediaProjection = mMediaProjectionManager.getMediaProjection(mResultCode, mResultData);
        mImageAvailableListener = new ImageAvailableListener();
    }

    private void setUpVirtualDisplay() {
        mImageReader = ImageReader.newInstance(mScreenWidth, mScreenHeight, PixelFormat.RGBA_8888, 2);
        mImageReader.setOnImageAvailableListener(mImageAvailableListener, null);
        mScreenCaptureHandler.postDelayed(mCreateVirtualDisplay, 30);
    }

    private Runnable mCreateVirtualDisplay = new Runnable() {
        @Override
        public void run() {
            mVirtualDisplay = mMediaProjection.createVirtualDisplay("ScreenCapture",
                    mScreenWidth, mScreenHeight, mScreenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                    mImageReader.getSurface(), null, null);
        }
    };


    private Runnable mImageTransformBitmap = new Runnable() {
        @Override
        public void run() {
            try (Image image = mImageReader.acquireLatestImage()) {
                if (image != null) {
                    Bitmap bitmap = imageToBitmap(image);
                    if (bitmap != null) {
                        if (mIsLongScreenshot) {
                            synchronized (LOCK) {
                                mTempBitmap = bitmap;
                                LOCK.notify();
                            }
                        } else {
                            mScreenCaptureListener.onSuccess(bitmap, false);
                        }

                    } else {
                        mScreenCaptureListener.onFail();
                    }
                } else {
                    mScreenCaptureListener.onFail();
                }
            } catch (Exception e) {
                Log.d(TAG, e.toString());
                mScreenCaptureListener.onFail();
            } finally {
                if (mVirtualDisplay != null) {
                    mVirtualDisplay.release();
                    mVirtualDisplay = null;
                }
                if (mImageReader != null) {
                    mImageReader.setOnImageAvailableListener(null, null);
                    mImageReader.close();
                    mImageReader = null;
                }
            }
        }
    };

    /**
     * 将Image转化成Bitmap的固定用法
     *
     * @param image
     * @return bitmap
     */
    private Bitmap imageToBitmap(Image image) {
        if (image == null) {
            return null;
        }
        int width = image.getWidth();
        int height = image.getHeight();
        Image.Plane[] planes = image.getPlanes();
        ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;
        Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        return bitmap;
    }


    private void createSupernatantViewGroup() {
        mSupernatantView = new FrameLayout(mActivity);
        mSupernatantTextView = new TextView(mActivity);
        mSupernatantTextView.setText("点击屏幕，停止截屏");
        mSupernatantTextView.setGravity(Gravity.BOTTOM | Gravity.LEFT);
        mSupernatantTextView.setTextSize(20);
        mSupernatantTextView.setBackgroundColor(Color.argb(50, 0, 0, 0));
        mSupernatantView.addView(mSupernatantTextView, FrameLayout.LayoutParams.MATCH_PARENT, 300);

        mSupernatantView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getSource() == InputDevice.SOURCE_ANY){
                    return false;
                }else{
                    mClickScreenShotEnd = true;
                    if (mSupernatantTextView != null) {
                        mSupernatantTextView.setText("正在合并截图");
                    }
                }
                return true;
            }
        });


        ViewGroup viewGroup = (ViewGroup) mActivity.getWindow().getDecorView();
        viewGroup.addView(mSupernatantView, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
    }

    private void removeSupernatantViewGroup() {
        if (mSupernatantView != null) {
            mSupernatantView.setVisibility(View.GONE);
            ViewGroup viewGroup = (ViewGroup) mActivity.getWindow().getDecorView();
            viewGroup.removeView(mSupernatantView);
        }

    }

    private void preLongScreenshot() {

        mNavigationHeight = ScreenShotUtilHelper.Companion.getNavigationHeight(mActivity);
        mStatusBarHeight = ScreenShotUtilHelper.Companion.getStatusBarHeight(mActivity);
        mScrollHeight = mCanScrollView.getHeight() / 2;
        if (mScrollHeight <= 0) {
            mScrollHeight = 20;
        }
        longScreenshot();
    }

    /**
     * 长截图
     */
    private void longScreenshot() {
        mLongScreenshotRunnable = new LongScreenshotRunnable();
        Thread thread = new Thread(mLongScreenshotRunnable);
        thread.start();
    }

    /**
     * 执行滚动的高度动画
     *
     * @param scrollHeight
     */

    volatile boolean isScrollBottom = false;
    volatile int mScrollActualHeight = 0;


    int mEndValue = 0;
    int mEndValue1 = 0;
    int mEndValue2 = 0;
    int mEndValue3 = 0;
    int startY = mEndValue;


     ViewGroup mContentView ;
    
    private void startScrollAnimation(final int scrollHeight) {

        //scrollHeight = 0,证明是第一次截图,那就无需滚动控件
        if (scrollHeight <= 0) {
            startScreenCapture();

            return;
        }
        if (mSupernatantView == null) {
            createSupernatantViewGroup();
        }

        final int x = mScreenWidth /2;
        final int y = mScreenHeight - mNavigationHeight - mStatusBarHeight ;

        final MotionEvent event =
                MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, x, y, 0);
        event.setSource(InputDevice.SOURCE_ANY);

        mActivity.dispatchTouchEvent(event);

        ValueAnimator scrollAnimator = ValueAnimator.ofInt(y,y/2);
        scrollAnimator.setInterpolator(new LinearInterpolator());
        scrollAnimator.setDuration(1000);
        scrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                if (!mClickScreenShotEnd && mCanScrollView.canScrollVertically(1)) {
                    event.setLocation(x,value);
                    event.setAction(MotionEvent.ACTION_MOVE);
                    mActivity.dispatchTouchEvent(event);
                } else {
                    isScrollBottom = true;
                }
            }
        });
        
        scrollAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Log.d(TAG+"onAnimationEnd:\t   event.getY:",String.valueOf(event.getY()));
                Log.d(TAG+"onAnimationEnd:\t  event.getRawY:",String.valueOf(event.getRawY()));
                Log.d(TAG+"onAnimationEnd:\t  motionEventDiff:",String.valueOf(y-event.getY()));
                event.setAction(MotionEvent.ACTION_UP);
                mActivity.dispatchTouchEvent(event);
                event.recycle();
                mScrollActualHeight =  mEndValue+ mEndValue1+ mEndValue2+ mEndValue3 - startY;
                Log.d(TAG+"onAnimationEnd:\t  mScrollActualHeight:",String.valueOf(mScrollActualHeight));
                startScreenCapture();
            }

            @Override
            public void onAnimationStart(Animator animation) {
              startY = mEndValue+ mEndValue1+ mEndValue2+ mEndValue3;
                Log.d(TAG+"onAnimationStart:\t event.getY:",String.valueOf(event.getY()));
                Log.d(TAG+"onAnimationStart:\t event.getRawY:",String.valueOf(event.getRawY()));
                Log.d(TAG+"onAnimationStart:\t startY:",String.valueOf(startY));
            }
        });
        scrollAnimator.start();
    }


    /**
     * @param
     * @return
     */

    private void mergeBitmap(Bitmap resultBitmap, Bitmap scrollBitmap, Canvas canvas, Paint paint, int srcTop, int srcBottom) {
        if (resultBitmap == null) {
            return;
        }
        int height = srcBottom - srcTop;
        Rect src = new Rect(0, srcTop, scrollBitmap.getWidth(), srcBottom);
        RectF des = new RectF(0, mBitmapActualHeight, scrollBitmap.getWidth(), mBitmapActualHeight + height);
        canvas.drawBitmap(scrollBitmap, src, des, paint);
        mBitmapActualHeight += height;
    }

    private class ScreenCaptureHandler extends Handler {
        public ScreenCaptureHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUCCESS_STATE:
                    Bitmap bitmap = (Bitmap) msg.obj;
                    if (mScreenCaptureListener != null) {
                        mScreenCaptureListener.onSuccess(bitmap, mIsLongScreenshot);
                    }

                    Log.d(TAG, "------------ finish screenshot ------------");
                    break;
                case FAILED_STATE:
                    if (mScreenCaptureListener != null) {
                        mScreenCaptureListener.onFail();
                    }
                    Log.d(TAG, "------------ failed  screenshot ------------");
                    break;
                case SCROLL_STATE:
                    int scrollHeight = msg.arg1;
                    startScrollAnimation(scrollHeight);
                    break;
            }
        }

        /**
         * 发送滚动消息
         *
         * @param scrollHeight 滚动的高度
         */
        public void sendScrollMessage(int scrollHeight) {
            Message msg = this.obtainMessage(SCROLL_STATE);
            msg.arg1 = scrollHeight;
            this.sendMessage(msg);
        }

        /**
         * 发送成功消息
         *
         * @param bitmap 图片
         */
        public void sendSuccessMessage(Bitmap bitmap) {
            Message msg = this.obtainMessage(SUCCESS_STATE);
            msg.obj = bitmap;
            this.sendMessage(msg);
        }


        /**
         * 发送失败消息
         */
        public void sendFailedMessage() {
            Message msg = this.obtainMessage(FAILED_STATE);
            this.sendMessage(msg);
        }


    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_MEDIA_PROJECTION && resultCode != 0) {
            mResultCode = resultCode;
            mResultData = data;
            setUpMediaProjection();
            setUpVirtualDisplay();
        } else {
            mScreenCaptureResultListener.onFail();
        }
    }

    private void onComplete() {
        if (mTempBitmap != null) {
            mTempBitmap.recycle();
            mTempBitmap = null;
        }

        if (mImageReader != null) {
            mImageReader.setOnImageAvailableListener(null, null);
            mImageReader.close();
            mImageReader = null;
        }

        mScrollHeight = 0;
        mIsLongScreenshot = false;
    }

    /**
     *
     */
    public void onDestroy() {
        if (mTempBitmap != null) {
            mTempBitmap.recycle();
            mTempBitmap = null;
        }

        if (mLongScreenshotRunnable != null) {
            //更改标志位,让线程安全退出
            mLongScreenshotRunnable.isDestroy = true;
        }
        //销毁,防止内存泄漏
        if (mScreenCaptureHandler != null) {
            mScreenCaptureHandler.removeCallbacksAndMessages(null);
        }

        if (mVirtualDisplay != null) {
            mVirtualDisplay.release();
            mVirtualDisplay = null;

        }
        if (mImageReader != null) {
            mImageReader.setOnImageAvailableListener(null, null);
            mImageReader.close();
            mImageReader.close();
        }
        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
    }

    private class ImageAvailableListener implements ImageReader.OnImageAvailableListener {
        @Override
        public void onImageAvailable(final ImageReader reader) {
            if (reader == null || reader != mImageReader) {
                mScreenCaptureListener.onFail();
                return;
            }
            mScreenCaptureHandler.postDelayed(mImageTransformBitmap, 1000);
        }
    }


    //子线程进行的任务
    private class LongScreenshotRunnable implements Runnable {
        //控制线程退出的标志位.
        private boolean isDestroy = false;

        @Override
        public void run() {
            synchronized (LOCK) {
                //1:创建图纸
                Bitmap resultBitmap = Bitmap.createBitmap(mScreenWidth, LONG_SCREEN_CAPTURE_MAX_COUNT * mScreenHeight, Bitmap.Config.RGB_565);
                //2:创建画布,并绑定图纸
                Canvas canvas = new Canvas(resultBitmap);
                //3:创建画笔
                Paint paint = new Paint();

                for (int i = 0; i < LONG_SCREEN_CAPTURE_MAX_COUNT; i++) {
                    // 如果销毁 点击停止 滚动到底部 则结束
                    if (isDestroy || mClickScreenShotEnd || isScrollBottom) {
                        Log.d(TAG,"截屏任务异常退出：isDestroy:"+isDestroy+"\t mClickScreenShotEnd:"+mClickScreenShotEnd
                        +"\t isScrollBottom:"+isScrollBottom);
                        break;
                    }
                    if (i == 0) {
                        mScreenCaptureHandler.sendScrollMessage(0);
                    } else {
                        mScreenCaptureHandler.sendScrollMessage(mScrollHeight);
                    }
                    try {
                        Log.d(TAG, "当前线程阻塞,等待主(UI)线程滚动截图");
                        LOCK.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (i == 0) {
                        mergeBitmap(resultBitmap, mTempBitmap, canvas, paint, 0, mTempBitmap.getHeight() - mNavigationHeight);
                    } else {
                        mergeBitmap(resultBitmap, mTempBitmap, canvas, paint, mTempBitmap.getHeight() - mNavigationHeight - mScrollActualHeight, mTempBitmap.getHeight() - mNavigationHeight);
                    }
                }

                if (mSupernatantTextView != null) {
                    mSupernatantTextView.setText("正在合并截图");
                }

                if (mTempBitmap == null) {
                    mScreenCaptureHandler.sendFailedMessage();
                    return;
                } else {
                    mergeBitmap(resultBitmap, mTempBitmap, canvas, paint, mTempBitmap.getHeight() - mNavigationHeight, mTempBitmap.getHeight());
                }

                if (!isDestroy) {
                    //合并图片
                    Log.d(TAG, "合并图片成功");
                    //回调成功
                    resultBitmap.setHeight(mBitmapActualHeight);
                    mScreenCaptureHandler.sendSuccessMessage(resultBitmap);
                }
            }
        }
    }


    /**
     * 提供给外部的截图回调
     */
    public interface ScreenCaptureResultListener {
        /**
         * 截图成功的回调
         *
         * @param bitmap 图片
         */
        void onScreenCaptureSuccess(Bitmap bitmap);

        /**
         * 截图失败的回掉
         */
        void onFail();
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
        void onSuccess(Bitmap bitmap, boolean isLongScreenshot);

        /**
         * 截图失败
         */
        void onFail();
    }
}
