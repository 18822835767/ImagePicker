package com.example.matisse.internal.ui.widget;

import android.content.Context;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

/**
 * 一个正方形的FrameLayout.
 */
public class SquareFrameLayout extends FrameLayout {

    public SquareFrameLayout(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
