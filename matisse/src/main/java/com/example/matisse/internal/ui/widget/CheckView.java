package com.example.matisse.internal.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.print.PrinterId;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.example.matisse.R;

import androidx.core.content.res.ResourcesCompat;

public class CheckView extends View {
    private static final String TAG = "CheckView";

    /**
     * 多选状态下，没有被选中时的checkNum.
     */
    public static final int UNCHECKED = Integer.MIN_VALUE;

    private static final float STROKE_WIDTH = 3.0f;
    private static final float SHADOW_WIDTH = 6.0f;
    private static final int SIZE = 48;
    private static final float STROKE_RADIUS = 11.5f;
    private static final float BG_RADIUS = 11.0f;
    private static final int CONTENT_SIZE = 16;

    /**
     * 是否多选.
     */
    private boolean mCountable;

    /**
     * 单选情况下，是否被选中
     */
    private boolean mChecked;

    /**
     * 多选情况下，被选中时的标号.
     */
    private int mCheckedNum;

    /**
     * 白色圆环的画笔.
     */
    private Paint mStrokePaint;

    /**
     * 阴影的画笔.
     */
    private Paint mShadowPaint;

    private Paint mBackgroundPaint;

    private TextPaint mTextPaint;

    /**
     * 加载 勾勾 的Drawable.
     */
    private Drawable mCheckDrawable;

    /**
     * 像素单位.
     */
    private float mDensity;

    /**
     * 放 勾勾 的矩形.
     */
    private Rect mCheckRect;

    /**
     * 是否还能被点击.
     */
    private boolean mEnabled = true;

    public CheckView(Context context) {
        super(context);
        init(context);
    }

    public CheckView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CheckView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //设置为固定的大小
        int sizeSpec = MeasureSpec.makeMeasureSpec((int) (SIZE * mDensity), MeasureSpec.EXACTLY);
        super.onMeasure(sizeSpec, sizeSpec);
    }

    private void init(Context context) {
        //像素单位
        mDensity = context.getResources().getDisplayMetrics().density;

        mStrokePaint = new Paint();//初始化画白色圆环的画笔
        mStrokePaint.setAntiAlias(true);//使用锯齿功能
        mStrokePaint.setStyle(Paint.Style.STROKE);//设置画笔样式为空心.
        mStrokePaint.setStrokeWidth(STROKE_WIDTH);//当画笔样式为STROKE或FILL_OR_STROKE时，设置笔刷的粗细度  
        int defaultColor = ResourcesCompat.getColor(
                getResources(), R.color.checkCircle_borderColor, getContext().getTheme());
        mStrokePaint.setColor(defaultColor);//设置画笔颜色

        //勾勾图片
        mCheckDrawable = ResourcesCompat.getDrawable(context.getResources(),
                R.drawable.ic_check_white_18dp, context.getTheme());
    }

    public void setChecked(boolean checked) {
        mChecked = checked;
        //重新绘制
        invalidate();
    }

    public void setCountable(boolean countable) {
        mCountable = countable;
    }

    public void setCheckedNum(int checkedNum) {
        mCheckedNum = checkedNum;
        //重新绘制
        invalidate();
    }

    public void setEnable(boolean enabled) {
        if (mEnabled != enabled) {
            mEnabled = enabled;
            //重新绘制
            invalidate();
        }
    }

    @Override
    public boolean isEnabled() {
        return mEnabled;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        initShadowPaint();//初始化阴影的画笔.

        //画阴影
        canvas.drawCircle(SIZE * mDensity / 2, SIZE * mDensity / 2,
                (STROKE_RADIUS + STROKE_WIDTH / 2 + SHADOW_WIDTH) * mDensity, mShadowPaint);
        //画白色圆环
        canvas.drawCircle(SIZE * mDensity / 2, SIZE * mDensity / 2,
                STROKE_RADIUS * mDensity, mStrokePaint);

        //多选
        if (mCountable) {
            //多选，并且被选中，那么绘制 背景+数字
            if (mCheckedNum != UNCHECKED) {
                initBackgroundPaint();//背景画笔
                //背景圈
                canvas.drawCircle(SIZE * mDensity / 2, SIZE * mDensity / 2,
                        BG_RADIUS * mDensity, mBackgroundPaint);
                initTextPaint();//写字的画笔
                String text = String.valueOf(mCheckedNum);
                //基线的xy坐标
                int baseX = (int) (getWidth() - mTextPaint.measureText(text)) / 2;
                int baseY = (int) (getHeight() - mTextPaint.descent() - mTextPaint.ascent()) / 2;
                canvas.drawText(text, baseX, baseY, mTextPaint);
            }
            //单选
        } else {
            //被选中
            if (mChecked) {
                initBackgroundPaint();//背景画笔
                //背景圈
                canvas.drawCircle(SIZE * mDensity / 2, SIZE * mDensity / 2,
                        BG_RADIUS * mDensity, mBackgroundPaint);

                //勾勾画在画布上
                mCheckDrawable.setBounds(getCheckRect());
                mCheckDrawable.draw(canvas);
            }
        }

        // 设置绘制图形的透明度，主要是为了区别 可选 和 不可选 下的圈圈颜色
        setAlpha(mEnabled ? 1.0f : 0.5f);
    }

    /**
     * 初始化，画阴影的画笔.
     */
    private void initShadowPaint() {
        if (mShadowPaint == null) {
            mShadowPaint = new Paint();
            mShadowPaint.setAntiAlias(true);

            float outerRadius = STROKE_RADIUS + STROKE_WIDTH / 2;
            float innerRadius = outerRadius - STROKE_WIDTH;
            float gradientRadius = outerRadius + SHADOW_WIDTH;
            float stop0 = (innerRadius - SHADOW_WIDTH) / gradientRadius;
            float stop1 = innerRadius / gradientRadius;
            float stop2 = outerRadius / gradientRadius;
            float stop3 = 1.0f;
            //设置渐变效果，画阴影
            mShadowPaint.setShader(
                    new RadialGradient((float) SIZE * mDensity / 2,
                            (float) SIZE * mDensity / 2,
                            gradientRadius * mDensity,
                            new int[]{Color.parseColor("#00000000"), Color.parseColor("#0D000000"),
                                    Color.parseColor("#0D000000"), Color.parseColor("#00000000")},
                            new float[]{stop0, stop1, stop2, stop3},
                            Shader.TileMode.CLAMP));
        }
    }

    /**
     * 初始化,画背景的画笔.
     */
    private void initBackgroundPaint() {
        if (mBackgroundPaint == null) {
            mBackgroundPaint = new Paint();
            mBackgroundPaint.setAntiAlias(true);
            mBackgroundPaint.setStyle(Paint.Style.FILL);//填充样式

            int defaultColor = ResourcesCompat.getColor(getResources(),
                    R.color.checkCircle_backgroundColor, getContext().getTheme());
            mBackgroundPaint.setColor(defaultColor);
        }
    }

    /**
     * 初始化，写字的画笔.
     */
    private void initTextPaint() {
        if (mTextPaint == null) {
            mTextPaint = new TextPaint();
            mTextPaint.setAntiAlias(true);
            mTextPaint.setColor(Color.WHITE);
            //设置字体样式，字体为默认字体，加粗
            mTextPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            mTextPaint.setTextSize(12.0f * mDensity);
        }
    }

    /**
     * 矩形，用于限制勾勾的边框.
     */
    private Rect getCheckRect() {
        if (mCheckRect == null) {
            int rectPadding = (int) (SIZE * mDensity / 2 - CONTENT_SIZE * mDensity / 2);
            mCheckRect = new Rect(rectPadding, rectPadding,
                    (int) (SIZE * mDensity - rectPadding), (int) (SIZE * mDensity - rectPadding));
        }
        return mCheckRect;
    }
}
