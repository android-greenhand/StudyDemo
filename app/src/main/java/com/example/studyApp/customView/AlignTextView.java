package com.example.studyApp.customView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class AlignTextView extends View {
    private Paint mPaint;
    private String mText1;
    private String mText2;
    private Rect mText1Bound;
    private Rect mText2Bound;

    public AlignTextView(Context context) {
        super(context);
        init();
    }

    public AlignTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AlignTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(50);
        mPaint.setColor(Color.BLACK);
        mText1 = "One line text";
        mText2 = "Two line text";
        mText1Bound = new Rect();
        mText2Bound = new Rect();
        mPaint.getTextBounds(mText1, 0, mText1.length(), mText1Bound);
        mPaint.getTextBounds(mText2, 0, mText2.length(), mText2Bound);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        // 计算第一行文本的宽度
        float text1Width = mPaint.measureText(mText1);

        // 计算两端空格的宽度
        float spaceWidth = (width - text1Width) / (mText1.length() - 1);

        // 绘制第一行文本
        float x = 0;
        for (int i = 0; i < mText1.length(); i++) {
            String c = String.valueOf(mText1.charAt(i));
            canvas.drawText(c, x, mText1Bound.height(), mPaint);
            x += mPaint.measureText(c) + spaceWidth;
        }

        // 绘制第二行文本
        float text2Width = mPaint.measureText(mText2);
        float text2X = (width - text2Width) / 2;
        float text2Y = height - mText2Bound.height();
        canvas.drawText(mText2, text2X, text2Y, mPaint);
    }
}
