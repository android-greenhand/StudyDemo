package com.example.studyApp.demo.ScreenCapture

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.toBitmap
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.util.function.Function

/**
 *
 * author: a58
 * data: 2021/3/24
 * desc:
 *
 */
class ScreenShotUtil(val mActivity: Activity) {
//    var mDecorView: View = mActivity.window.decorView
//    public fun startScreenShot(): Bitmap {
//
//        val bitmap = Bitmap.createBitmap(mDecorView.width, mDecorView.height, Bitmap.Config.ARGB_8888)
//        val canvas = Canvas(bitmap)
//        val drawableBg: Drawable? = mDecorView.background
//
//        if (drawableBg != null) {
//            drawableBg.draw(canvas);
//        } else {
//            canvas.drawColor(Color.WHITE);
//        }
//        mDecorView.draw(canvas);
//
//
//    }
}