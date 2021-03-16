package com.example.studyApp.demo.ScreenCapture;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
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
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


/**
 * author: gezongpan
 * data: 2021/3/8
 * desc:
 */
public class ScreenCaptureUtil {

    //状态码
    private static final int SUCCESS_STATE = 1;       //成功状态
    private static final int SCROLL_STATE = 2;        //滚动状态
    private static final int REQUEST_MEDIA_PROJECTION = 1;
    private static final int LONG_SCREEN_CAPTURE_MAX_COUNT = 10;
    private static final String TAG = ScreenCaptureUtil.class.getCanonicalName();
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
    //截图的方式(true=长截图 false=截图) 默认为false
    private boolean mIsLongScreenshot = false;
    private int mScrollHeight;
    private int mContentHeight;
    private int mTotalScrollCount;
    private int mRemainScrollHeight;
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
            Toast.makeText(mActivity, "截屏成功", Toast.LENGTH_SHORT).show();
            Bitmap newBitmap = compressBitmap(bitmap);
            mScreenCaptureResultListener.onScreenCaptureSuccess(newBitmap);
            saveFile(newBitmap, "11.png");
            onComplete();
            Log.d(TAG, "Bitmap大小:" + newBitmap.getByteCount() / 1024);
        }

        @Override
        public void onFail() {
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
    }


    /**
     * 设置要滚动的View和其内容的高度
     *
     * @param view
     * @param contentHeight
     */
    public void setScrollViewAndContentHeight(View view, int contentHeight) {
        this.mCanScrollView = view;
        this.mContentHeight = contentHeight;
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

    @SuppressLint("WrongConstant")
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


    /**
     * 在长截图之前的准备工作
     */
    private void preLongScreenshot() {
        mNavigationHeight = getNavigationHeight(mActivity);
        mStatusBarHeight = getStatusBarHeight(mActivity);

        if (mContentHeight <= 0) {
            //1:发起测量
            mCanScrollView.measure(0, 0);
            //2:获取测量后高度 == Webview内容的高度
            mContentHeight = mCanScrollView.getMeasuredHeight();
            //3:获取Webview控件的高度
            mScrollHeight = mCanScrollView.getHeight() / 2;
        }


        int temp = mContentHeight - (mScreenHeight - mNavigationHeight - mStatusBarHeight);
        //4:计算滚动次数
        mTotalScrollCount = temp / mScrollHeight;
        //5:有余数(剩余高度)的情况下
        mRemainScrollHeight = temp - (mTotalScrollCount * mScrollHeight);
        //如果有剩余高度,+1次滚动截屏
        if (mRemainScrollHeight > 0) {
            mTotalScrollCount++;
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
    private void startScrollAnimation(final int scrollHeight) {
        final int lastScrollHeight = mCanScrollView.getScrollY();
        //scrollHeight = 0,证明是第一次截图,那就无需滚动控件
        if (scrollHeight <= 0) {
            startScreenCapture();
            return;
        }
        ValueAnimator scrollAnimator = ValueAnimator.ofInt(0, scrollHeight);
        scrollAnimator.setInterpolator(new LinearInterpolator());
        scrollAnimator.setDuration(1000);
        scrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                mCanScrollView.scrollTo(0, value + lastScrollHeight);
            }
        });
        scrollAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                startScreenCapture();
            }

            @Override
            public void onAnimationStart(Animator animation) {
            }
        });
        scrollAnimator.start();
    }


    private Bitmap mergeBitmap(@NonNull List<Bitmap> bitmapList) {
        if (bitmapList.isEmpty()) {
            return null;
        }

        if (bitmapList.size() == 1) {
            return bitmapList.get(0);
        }

        //图纸宽度(因为是截图,图片宽度大小都是一样的)
        int bitmapWidth = bitmapList.get(0).getWidth();

        int countNum = bitmapList.size() - 1;
        if (mRemainScrollHeight > 0) {
            countNum--;
        }
        //图纸高度
        int bitmapHeight = mScreenHeight + countNum * mScrollHeight + mRemainScrollHeight;
        //1:创建图纸
        Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.RGB_565);
        //2:创建画布,并绑定图纸
        Canvas canvas = new Canvas(bitmap);
        //3:创建画笔
        Paint paint = new Paint();
        float top = 0;
        float left = 0;
        for (int count = bitmapList.size(), i = 0; i < count; i++) {
            Bitmap data = bitmapList.get(i);
            Rect src = null;
            RectF des = null;
            /**
             * Rect src = new Rect(); 代表图片矩形范围
             * RectF des = new RectF(); 代表Canvas的矩形范围(显示位置)
             */
            if (i == count - 1 && mRemainScrollHeight > 0) {
                src = new Rect(0, data.getHeight() - mNavigationHeight - mRemainScrollHeight, data.getWidth(), data.getHeight());
                des = new RectF(left, top, data.getWidth(), top + mRemainScrollHeight + mNavigationHeight);
                top += mRemainScrollHeight + mNavigationHeight;
            } else if (i == count - 1 && mRemainScrollHeight == 0) {
                src = new Rect(0, data.getHeight() - mNavigationHeight - mScrollHeight, data.getWidth(), data.getHeight());
                des = new RectF(left, top, data.getWidth(), top + mScrollHeight + mNavigationHeight);
                top = top + mScrollHeight + mNavigationHeight;
            } else if (i == 0) {
                int bottom = data.getHeight() - mNavigationHeight;
                src = new Rect(0, 0, data.getWidth(), bottom);
                des = new RectF(left, top, data.getWidth(), top + bottom);
                top = top + bottom;
            } else {
                src = new Rect(0, data.getHeight() - mNavigationHeight - mScrollHeight, data.getWidth(), data.getHeight() - mNavigationHeight);
                des = new RectF(left, top, data.getWidth(), top + mScrollHeight);
                top = top + mScrollHeight;
            }
            canvas.drawBitmap(data, src, des, paint);
        }

        for (Bitmap bitmap1 : bitmapList) {
            bitmap1.recycle();
        }

        return bitmap;
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
        mContentHeight = 0;
        mRemainScrollHeight = 0;
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

    /**
     * 图片转成string
     *
     * @param bitmap
     * @return
     */
    public static String convertIconToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();// outputstream
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] appicon = baos.toByteArray();// 转为byte数组
        return Base64.encodeToString(appicon, Base64.DEFAULT);

    }

    /**
     * string转成bitmap
     *
     * @param st
     */
    public static Bitmap convertStringToIcon(String st) {
        // OutputStream out;
        Bitmap bitmap = null;
        try {
            // out = new FileOutputStream("/sdcard/aa.jpg");
            byte[] bitmapArray;
            bitmapArray = Base64.decode(st, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
                    bitmapArray.length);
            // bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            return bitmap;
        } catch (Exception e) {
            return null;
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
                //保存图片的集合
                List<Bitmap> cacheBitmaps = new ArrayList<>();
                int count = mTotalScrollCount;
                //需要截屏的次数 = 滚动的次数+刚开始的截屏
                for (int i = 0; i < count + 1; i++) {
                    if (isDestroy) {
                        break;
                    }
                    if (i >= LONG_SCREEN_CAPTURE_MAX_COUNT) {
                        mRemainScrollHeight = 0;
                        break;
                    }
                    if (i == 0) {
                        mScreenCaptureHandler.sendScrollMessage(0);
                    } else if (i == count && mRemainScrollHeight > 0) {
                        mScreenCaptureHandler.sendScrollMessage(mRemainScrollHeight);
                    } else {
                        mScreenCaptureHandler.sendScrollMessage(mScrollHeight);
                    }
                    try {
                        Log.d(TAG, "当前线程阻塞,等待主(UI)线程滚动截图");
                        LOCK.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    cacheBitmaps.add(mTempBitmap);
                }
                if (!isDestroy) {
                    //合并图片
                    Bitmap bitmap = mergeBitmap(cacheBitmaps);
                    Log.d(TAG, "合并图片成功");
                    //回调成功
                    mScreenCaptureHandler.sendSuccessMessage(bitmap);
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


    public static int getStatusBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    public static int getNavigationHeight(Context context) {
        int resourceId = 0;
        int rid = context.getResources().getIdentifier("config_showNavigationBar", "bool", "android");
        if (rid != 0) {
            resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            return context.getResources().getDimensionPixelSize(resourceId);
        } else {
            return 0;
        }
    }


    /**
     * 压缩图片
     *
     * @param bitmap 被压缩的图片
     * @return 压缩后的图片
     */
    private Bitmap compressBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            Log.d(TAG, "压缩前的大小：" + bitmap.getByteCount() / 1024);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            Bitmap newBitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()), null, options);
            bitmap.recycle();
            if (newBitmap != null) {
                Log.d(TAG, "压缩后的大小：" + newBitmap.getByteCount() / 1024);
                return newBitmap;
            }

        }
        return null;

    }


    /**
     * 保存图片
     *
     * @param bitmap    图片
     * @param localPath 本地路径
     */

    private final static String ALBUM_PATH
            = Environment.getExternalStorageDirectory() + "/download_test/";

    public void saveFile(Bitmap bm, String fileName) {
        BufferedOutputStream bos = null;
        try {
            File dirFile = new File(ALBUM_PATH);
            if (!dirFile.exists()) {
                dirFile.mkdir();
            }
            File myCaptureFile = new File(ALBUM_PATH + fileName);
            bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
            bm.compress(Bitmap.CompressFormat.WEBP, 100, bos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


}
