package com.example.studyApp.customView.flowLayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.example.studyApp.R;
import java.util.ArrayList;
import java.util.List;

public class FlowLayout extends ViewGroup {

    public static final String TAG = "FlowLayout";
    private int mHorizontalSpace;
    private int mVerticalSpace;
    private List<List<View>> mAllView = new ArrayList<>();
    private List<Integer> mLineHeight = new ArrayList<>();

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout);
        mVerticalSpace = typedArray.getInt(R.styleable.FlowLayout_verticalSpace, 0);
        mHorizontalSpace = typedArray.getInt(R.styleable.FlowLayout_horizontalSpace, 0);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mAllView.clear();
        mLineHeight.clear();
        int selfWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        int selfHeightMode = MeasureSpec.getMode(heightMeasureSpec);
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        Log.d(TAG,selfWidthMode==MeasureSpec.EXACTLY? "exactly":"at_most");
        Log.d(TAG,parentWidth+"");

        int currHeight = 0;
        int lineMaxHeight = 0;
        int lineMaxWidth = 0;
        // 已经使用的宽度
        int lineWidthUsed = 0;
        List<View> lineView = new ArrayList<>();

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            LayoutParams layoutParams = childView.getLayoutParams();
            int childWidthMSpec = getChildMeasureSpec(widthMeasureSpec, getPaddingLeft() + getPaddingRight(), layoutParams.width);
            int childHeightMSpec = getChildMeasureSpec(heightMeasureSpec, getPaddingTop() + getPaddingBottom(), layoutParams.height);
            childView.measure(childWidthMSpec, childHeightMSpec);

            int childWidth = childView.getMeasuredWidth();
            int childHeight = childView.getMeasuredHeight();

            int widthTemp = lineWidthUsed + mHorizontalSpace + childWidth;
            if (widthTemp > parentWidth) {
                mAllView.add(lineView);
                currHeight += (lineMaxHeight + mVerticalSpace);
                mLineHeight.add(currHeight);
                lineMaxWidth = Math.max(lineMaxWidth, lineWidthUsed);
                widthTemp = childWidth;
                lineView = new ArrayList<>();
                lineMaxHeight = 0;
            }
            lineView.add(childView);
            lineMaxHeight = Math.max(lineMaxHeight, childHeight);
            lineWidthUsed = widthTemp;
        }

        lineMaxWidth = Math.max(lineMaxWidth, lineWidthUsed);

        mAllView.add(lineView);
        currHeight += lineMaxHeight;
        mLineHeight.add(currHeight);
        Log.d(TAG + "mAllView", mAllView.size() + "");

        Log.d(TAG + "currHeight", currHeight + "");
        Log.d(TAG + "lineMaxWidth", lineMaxWidth + "");

        setMeasuredDimension(selfWidthMode == MeasureSpec.EXACTLY ? parentWidth : lineMaxWidth,
                selfHeightMode == MeasureSpec.EXACTLY ? parentHeight : currHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int paddingLeft = getPaddingLeft();
        int curLeft = paddingLeft;
        int paddingTop = getPaddingTop();
        int curTop = getPaddingTop();
        for (int i = 0; i < mAllView.size(); i++) {
            List<View> lineView = mAllView.get(i);
            for (int j = 0; j < lineView.size(); j++) {
                int top = curTop;
                int left = curLeft;
                int right = left + lineView.get(j).getMeasuredWidth();
                int bottom = top + lineView.get(j).getMeasuredHeight();
                lineView.get(j).layout(left, top, right, bottom);
                curLeft = right + mHorizontalSpace;
            }
            curTop = paddingTop + mLineHeight.get(i);
            curLeft = paddingLeft;
        }

    }
}
