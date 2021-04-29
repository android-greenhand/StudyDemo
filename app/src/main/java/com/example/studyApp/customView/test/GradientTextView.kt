package com.example.studyApp.customView.test

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
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

var TAG = GradientTextView::class.java.name

class GradientTextView : androidx.appcompat.widget.AppCompatTextView{

    /**
     *
     * 底色颜色
     * 默认颜色为黑色
     */
    var mBelowTextColor: Int = Color.BLACK

    /**
     *
     * 渐变的颜色
     * 默认为红色
     *
     */
    var mAboveTextColor: Int = Color.RED

    /**
     *
     * 渐变的百分比
     * 0-100%
     */
    private var mPercent: Float = 0f
        set(value) {
            field = value
            mWithDy = mPercent * mTextWith
            invalidate()
        }

    /**
     *
     * 渐变的宽度
     */
    private var mWithDy = 0f

    /**
     *
     * 文本内容的宽度
     *
     */
    private var mTextWith: Float = 0f

    /**
     * 绘制文本X坐标轴的起点
     * 根据gravity不同
     * 默认左对齐
     */
    private var mDrawTextOriginX: Float = 0f

    /**
     *
     * 绘制文本的baseLine
     * 默认文本为垂直居中
     */
    private var mBaseLine: Float = 0f

    /**
     * 裁剪起始距离
     */
    private var mClipRectOriginLeft = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int):super(context, attrs, defStyleAttr){
        Log.d(TAG, "次构造器初始化")
    }

    /**
     *
     * 在布局结束后 初始化文本参数
     *
     */
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        mTextWith = paint.measureText(text.toString())
        paint.fontMetrics.let {
            mBaseLine = height / 2 + (it.descent - it.ascent) / 2 - it.descent
        }
        mDrawTextOriginX = when (gravity) {
            Gravity.RIGHT -> width - mTextWith
            Gravity.CENTER -> (width / 2).toFloat() - mTextWith / 2
            else -> 0f
        }
        mClipRectOriginLeft = mDrawTextOriginX.toInt()
    }


    override fun onDraw(canvas: Canvas?) {
        canvas?.apply {
            drawTextBelowColorText(this)
            drawTextAboveColorText(this)
        }
    }

    /**
     *
     * 绘制底色文本
     */
    private fun drawTextBelowColorText(canvas: Canvas) {
        val clipRectLeft = (mClipRectOriginLeft + mWithDy).toInt()
        val clipRectRight = (mClipRectOriginLeft + mTextWith).toInt()
        canvas.apply {
            save()
            paint.color = mBelowTextColor
            clipRect(clipRectLeft, 0, clipRectRight, height)
            drawText(text.toString(), mDrawTextOriginX, mBaseLine, paint)
            restore()
        }
    }

    /**
     *
     * 绘制渐变文本
     */
    private fun drawTextAboveColorText(canvas: Canvas) {
        val clipRectLeft = mClipRectOriginLeft
        val clipRectRight = (mClipRectOriginLeft + mWithDy).toInt()
        canvas.apply {
            save()
            paint.color = mAboveTextColor
            clipRect(clipRectLeft, 0, clipRectRight, height)
            drawText(text.toString(), mDrawTextOriginX, mBaseLine, paint)
            restore()
        }
    }

}