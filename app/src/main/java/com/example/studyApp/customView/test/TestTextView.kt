package com.example.studyApp.customView.test

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity

/**
 *
 * author: gezongpan
 * data: 2021/4/28
 * desc:
 *
 */

var TAG = TestTextView::class.java.name

class TestTextView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : androidx.appcompat.widget.AppCompatTextView(context, attrs, defStyleAttr) {

    var mTextString: String = "hello world"

    var mBelowTextColor: Int = Color.BLACK
    var mAboveTextColor: Int = Color.RED

    var mPercent: Float = 0f
        set(value) {
            field = value
            invalidate()
        }

    var mTextWith: Float = 0f

    var mDrawTextOriginX:Float = 0f


    var baseLine: Float = 0f

    var mClipRectLeft = 0

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {
        Log.d(TAG, "次构造器初始化")
        if (!text.isNullOrBlank()) {
            mTextString = text.toString()
        }
        mTextWith = paint.measureText(mTextString)



    }

    constructor(context: Context) : this(context, null)

    init {
        Log.d(TAG, mTextString)
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.apply {
            drawTextBelowColorText(this)
            drawTextAboveColorText(this)
        }
    }


    private fun drawTextBelowColorText(canvas: Canvas) {

        paint.fontMetrics.let {
            baseLine = height / 2 + (it.descent - it.ascent) / 2 - it.descent
        }

        when (gravity) {
            Gravity.RIGHT ->{
                mDrawTextOriginX = wii
            }

            Gravity.CENTER -> {
                mDrawTextOriginX = wi/2

            }
            else -> {

                mDrawTextOriginX = 0f
            }
        }

        val dy = mPercent * mTextWith

        canvas.apply {
            paint.color = mBelowTextColor
            drawText(mTextString, mDrawTextOriginX, baseLine, paint)
            clipRect(mClipRectLeft, 0, (mClipRectLeft + dy).toInt(), height)
        }
    }


    private fun drawTextAboveColorText(canvas: Canvas) {
        canvas.apply {
            save()
            paint.color = mAboveTextColor
            drawText(mTextString, mDrawTextOriginX, baseLine, paint)
            restore()
        }
    }

}