package com.mosl.module.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class SquareLinearLayout extends LinearLayout {

    public SquareLinearLayout(Context context) {
        super(context);
    }

    public SquareLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
        int childWidthSize = getMeasuredWidth();

        widthMeasureSpec = MeasureSpec.makeMeasureSpec( childWidthSize, MeasureSpec.EXACTLY);
        // 高度和宽度一样
        heightMeasureSpec = widthMeasureSpec;
        //设定高是宽的比例
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
