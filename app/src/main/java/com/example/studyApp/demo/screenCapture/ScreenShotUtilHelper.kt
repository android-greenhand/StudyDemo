package com.example.studyApp.demo.screenCapture

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Base64
import android.util.Log
import java.io.*

/**
 *
 * author: a58
 * data: 2021/3/26
 * desc:
 *
 */
class ScreenShotUtilHelper {
    companion object {
        /**
         * 压缩图片
         *
         * @param bitmap 被压缩的图片
         * @return 压缩后的图片
         */
        fun compressBitmap(bitmap: Bitmap?): Bitmap? {
            if (bitmap != null) {
                Log.d(ScreenShotUtil.TAG, "压缩前的大小：" + bitmap.byteCount / 1024)
                val options = BitmapFactory.Options()
                options.inSampleSize = 1
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.WEBP, 100, byteArrayOutputStream)
                val newBitmap = BitmapFactory.decodeStream(ByteArrayInputStream(byteArrayOutputStream.toByteArray()), null, options)
                bitmap.recycle()
                if (newBitmap != null) {
                    Log.d(ScreenShotUtil.TAG, "压缩后的大小：" + newBitmap.byteCount / 1024)
                    return newBitmap
                }
            }
            return null
        }

        /**
         * 保存图片
         *
         * @param bitmap    图片
         * @param localPath 本地路径
         */
        private val ALBUM_PATH = Environment.getExternalStorageDirectory().toString() + "/download_test/"
        fun saveFile(bm: Bitmap, fileName: String) {
            var bos: BufferedOutputStream? = null
            try {
                val dirFile = File(ALBUM_PATH)
                if (!dirFile.exists()) {
                    dirFile.mkdir()
                }
                val myCaptureFile = File(ALBUM_PATH + fileName)
                bos = BufferedOutputStream(FileOutputStream(myCaptureFile))
                bm.compress(Bitmap.CompressFormat.WEBP, 100, bos)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (bos != null) {
                    try {
                        bos.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        /**
         * 图片转成string
         *
         * @param bitmap
         * @return
         */
        fun convertIconToString(bitmap: Bitmap): String? {
            val baos = ByteArrayOutputStream() // outputstream
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val appicon = baos.toByteArray() // 转为byte数组
            return Base64.encodeToString(appicon, Base64.DEFAULT)
        }

        /**
         * string转成bitmap
         *
         * @param st
         */
        fun convertStringToIcon(st: String?): Bitmap? {
            // OutputStream out;
            var bitmap: Bitmap? = null
            return try {
                // out = new FileOutputStream("/sdcard/aa.jpg");
                val bitmapArray: ByteArray
                bitmapArray = Base64.decode(st, Base64.DEFAULT)
                bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
                        bitmapArray.size)
                // bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                bitmap
            } catch (e: java.lang.Exception) {
                null
            }
        }

        fun getStatusBarHeight(context: Context): Int {
            val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
            return context.resources.getDimensionPixelSize(resourceId)
        }

        fun getNavigationHeight(context: Context): Int {
            var resourceId = 0
            val rid = context.resources.getIdentifier("config_showNavigationBar", "bool", "android")
            return if (rid != 0) {
                resourceId = context.resources.getIdentifier("navigation_bar_height", "dimen", "android")
                context.resources.getDimensionPixelSize(resourceId)
            } else {
                0
            }
        }
    }
}