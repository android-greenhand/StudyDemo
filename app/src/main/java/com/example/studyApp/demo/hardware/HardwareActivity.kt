package com.example.studyApp.demo.hardware

import android.content.Context
import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log

import com.example.studyApp.R
import kotlinx.android.synthetic.main.activity_hardware.*

const val TAG = "HardwareActivity_TAG"

class HardwareActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hardware)

        btn.setOnClickListener {
            text.animate()
                .translationX(90F)
                .setDuration(5000)
//                .withLayer()
        }
    }
}


class CustomHardwareView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defaultStyle: Int = 0
) : androidx.appcompat.widget.AppCompatTextView(context, attrs, defaultStyle) {

    init {
        Log.d(TAG, "init");
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.d(TAG, "onMeasure")
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        Log.d(TAG, "onLayout")
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        Log.d(TAG, "onDraw")
    }


}
