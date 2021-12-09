package com.example.studyApp.demo

import android.content.Context
import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView

import com.example.studyApp.R

const val TAG = "SurfaceViewActivity_TAG"

class SurfaceViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_surface_view)
    }
}


class CustomSurfaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defaultStyle: Int = 0
) : SurfaceView(context, attrs, defaultStyle), SurfaceHolder.Callback {
    init {
        Log.d(TAG, "init");
        holder.addCallback(this)
      //  setZOrderOnTop(true)
    }

    private val mPaint by lazy {
        Paint().apply {
            this.color = Color.RED
        }
    }


    override fun surfaceCreated(holder: SurfaceHolder) {

        Log.d(TAG, "surfaceCreated");
//        val lockCanvas = holder.lockCanvas()
//        lockCanvas.drawColor(Color.WHITE);
//        holder.unlockCanvasAndPost(lockCanvas)

        Thread{
            drawText()
        }.start()
    }

    private fun drawText(){
        while (true){
            val canvas: Canvas = holder.lockCanvas(null)
            canvas.drawColor(Color.WHITE)
            canvas.drawRect(0F, 0F, 400F, 400F, mPaint)
            holder.unlockCanvasAndPost(canvas)

            try {
                Thread.sleep(50)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }

//        val lockCanvas = holder.lockCanvas()
//        lockCanvas.drawText("this is surfaceView",100.0f,100.0f,mPaint)
//        holder.unlockCanvasAndPost(lockCanvas)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        Log.d(TAG, "surfaceChanged");

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        Log.d(TAG, "surfaceDestroyed");
    }

}
