package com.example.matisse.internal.ui.widget;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewTreeObserver;


public class ZoomImageView extends androidx.appcompat.widget.AppCompatImageView implements
        ScaleGestureDetector.OnScaleGestureListener, View.OnTouchListener,
        ViewTreeObserver.OnGlobalLayoutListener {

    private static final String TAG = ZoomImageView.class.getSimpleName();

    /**
     * 记录初始的图片的大小.
     */
    private float initScale = 1.0f;
    /**
     * 双击放大一次后的放大倍数.
     */
    private static final float SCALE_MID = 2.0f;
    /**
     * 第二次双击放大后的放大倍数.
     */
    private static final float SCALE_MAX = 4.0f;
    /**
     * 记录矩阵值的数组.
     */
    private float[] matrixValues = new float[9];
    private final Matrix mScaleMatrix = new Matrix();

    /**
     * 用于双击检测.
     */

    private GestureDetector mGestureDetector;

    /**
     * 用于检测缩放.
     */
    private ScaleGestureDetector mScaleGestureDetector;

    /**
     * 用于记录上次平移时的x,y坐标.
     */
    private float mLastX;
    private float mLastY;

    /**
     * 记录上次平移时的手指个数.
     */
    private int lastPointCount;

    /**
     * 双击放大缩小时，有个类似动画的操作，这里用来标记是否正处于该"动画"中.
     */
    private boolean isAutoScale;

    /**
     * 用于记录图片是否为第一次加载进入视图.
     */
    private boolean once = true;

    /**
     * 平移时，用于标记是否要检车竖直方向上的边界.
     */
    private boolean isCheckTopAndBottom = true;

    private boolean isCheckLeftAndRight = true;

    public ZoomImageView(Context context) {
        this(context, null);
    }

    public ZoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setScaleType(ScaleType.MATRIX);
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            /**
             * 监听双击事件.
             * */
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                //若当前正在执行缩放操作，直接返回true
                if (isAutoScale) {
                    return true;
                }

                float x = e.getX();
                float y = e.getY();

                //当前放大小于SCALE_MID倍
                if (getScale() < SCALE_MID) {
                    //放大至SCALE_MID倍
                    ZoomImageView.this.postDelayed(new AutoScaleRunnable(SCALE_MID, x, y), 16);
                    //当前放大倍数在 SCALE_MID-SCALE_MAX之间
                } else if (getScale() >= SCALE_MID && getScale() < SCALE_MAX) {
                    //放大至SCALE_MAX倍
                    ZoomImageView.this.postDelayed(new AutoScaleRunnable(SCALE_MAX, x, y), 16);
                    //当前放大倍数达到SCALE_MAX
                } else {
                    //缩小到初始大小
                    ZoomImageView.this.postDelayed(new AutoScaleRunnable(initScale, x, y), 16);
                }

                return true;
            }
        });
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
        this.setOnTouchListener(this);
    }

    /**
     * 缩放过程中.
     */
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scale = getScale();
        float scaleFactor = detector.getScaleFactor();//缩放因子，代表

        if (getDrawable() == null) {
            return true;
        }
        
          /*
          缩放的范围控制.
          两种情况下可以进行缩放：
          1.将要放大，且当前放大值为未超过SCALE_MAX倍
          2.将要缩小，且当前缩小值未小于initScale(初始值)
          */
        if ((scale < SCALE_MAX && scaleFactor > 1.0f) ||
                (scale > initScale && scaleFactor < 1.0f)) {

            //若缩小后，小于初始值，那么让缩放因子进行运算，使得缩小后到达初始值
            if (scaleFactor * scale < initScale) {
                scaleFactor = initScale / scale;
            }

            //若放大后，大于最大放大值，那么让缩放因子进行运算，使得放大后到达最大值.
            if (scaleFactor * scale > SCALE_MAX) {
                scaleFactor = SCALE_MAX / scale;
            }
            //设置缩放比例
            mScaleMatrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
            //检测若进行缩放后，会不会有白边出现，或者不居中，会的话，先进行相应的偏移
            checkBorderAndCenterWhenScale();
            //缩放
            setImageMatrix(mScaleMatrix);
        }
        return false;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
    }

    /**
     * 手指在手机屏幕上就会调用，不管手指在干嘛，不管几根手指.
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //先判断是不是双击事件，这里只有双击事件才会返回true(因为只重写了onDoubleTap方法)，GestureDetector消耗该事件.
        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }

        //----缩放操作
        mScaleGestureDetector.onTouchEvent(event);

        //----平移操作
        float x = 0, y = 0;
        //触摸点个数
        final int pointerCount = event.getPointerCount();
        //得到多个触摸点的x与y均值
        for (int i = 0; i < event.getPointerCount(); i++) {
            x += event.getX(i);
            y += event.getY(i);
        }
        x = x / pointerCount;
        y = y / pointerCount;

        //每当触摸点发生变化时，重置mLasX , mLastY，防止手指数量变化时，产生一段突然的移动
        if (pointerCount != lastPointCount) {
            mLastX = x;
            mLastY = y;
        }

        lastPointCount = pointerCount;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //偏移量
                float dx = x - mLastX;
                float dy = y - mLastY;

                RectF rectF = getMatrixRectF();
                if (getDrawable() != null) {
                    isCheckLeftAndRight = isCheckTopAndBottom = true;
                    // 如果宽度小于屏幕宽度，则禁止左右移动
                    if (rectF.width() < getWidth()) {
                        dx = 0;
                        //因为左右没有移动，所以无需检查边界.
                        isCheckLeftAndRight = false;
                    }
                    // 如果高度小于屏幕高度，则禁止上下移动
                    if (rectF.height() < getHeight()) {
                        dy = 0;
                        isCheckTopAndBottom = false;
                    }
                    //设置偏移量
                    mScaleMatrix.postTranslate(dx, dy);
                    //边界检查，看是否移动过后会出现白边，出现则调整.
                    checkMatrixBounds();
                    //移动
                    setImageMatrix(mScaleMatrix);
                }
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                lastPointCount = 0;
                break;
        }

        return true;
    }

    /**
     * 获得缩放的倍数.
     */
    public float getScale() {
        mScaleMatrix.getValues(matrixValues);
        return matrixValues[Matrix.MSCALE_X];
    }

    /**
     * 在缩放过程中，进行图片显示范围的控制.
     * 否则，有可能出现白边或者图片不居中的情况.
     */
    private void checkBorderAndCenterWhenScale() {
        RectF rect = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;

        int width = getWidth();
        int height = getHeight();

        // 如果宽大于屏幕，则控制器范围，主要是防止出现白边
        if (rect.width() >= width) {
            if (rect.left > 0) {
                deltaX = -rect.left;
            }
            if (rect.right < width) {
                deltaX = width - rect.right;
            }
        }

        //如果高大于屏幕，则控制其范围，主要是防止出现白边
        if (rect.height() >= height) {
            if (rect.top > 0) {
                deltaY = -rect.top;
            }
            if (rect.bottom < height) {
                deltaY = height - rect.bottom;
            }
        }

        //如果宽度小于屏幕宽度，那么通过水平方向上的偏移使图片有水平居中的效果
        if (rect.width() < width) {
            deltaX = width * 0.5f - rect.right + 0.5f * rect.width();
        }

        //如果高度小于屏幕高度，那么通过竖直方向上的偏移使图片有垂直居中的效果
        if (rect.height() < height) {
            deltaY = height * 0.5f - rect.bottom + 0.5f * rect.height();
        }

        mScaleMatrix.postTranslate(deltaX, deltaY);
    }

    /**
     * 移动时，进行边界判断，主要判断宽或高大于屏幕的.
     */
    private void checkMatrixBounds() {
        RectF rect = getMatrixRectF();

        float deltaX = 0, deltaY = 0;
        final float viewWidth = getWidth();
        final float viewHeight = getHeight();
        //判断移动后，图片显示是否超出屏幕边界.
        if (rect.top > 0 && isCheckTopAndBottom) {
            deltaY = -rect.top;
        }
        if (rect.bottom < viewHeight && isCheckTopAndBottom) {
            deltaY = viewHeight - rect.bottom;
        }
        if (rect.left > 0 && isCheckLeftAndRight) {
            deltaX = -rect.left;
        }
        if (rect.right < viewWidth && isCheckLeftAndRight) {
            deltaX = viewWidth - rect.right;
        }
        mScaleMatrix.postTranslate(deltaX, deltaY);
    }

    /**
     * 根据当前图片的Matrix获得图片的范围.
     *
     * @return 返回的矩形的四条边代表图片的范围.
     */
    private RectF getMatrixRectF() {
        RectF rect = new RectF();
        Drawable d = getDrawable();
        if (d != null) {
            //图片大小信息
            rect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            //将矩阵的信息映射到矩形上面
            mScaleMatrix.mapRect(rect);
        }
        return rect;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    /**
     * 主要布局视图变化且有图片时，将图片初始化到一个合适的大小，设置在ImageView中.
     */
    @Override
    public void onGlobalLayout() {
        if (once) {
            Drawable d = getDrawable();
            if (d == null) {
                return;
            }
            //获取ImageView宽高信息
            int width = getWidth();
            int height = getHeight();
            //获取图片的宽高信息
            int dw = d.getIntrinsicWidth();
            int dh = d.getIntrinsicHeight();
            float scale = 1.0f;
            //缩放到合适的大小;若图片宽高均小于ImageView大小，那么不进行缩放
            if (dw > width && dh <= height) {
                scale = width * 1.0f / dw;
            }
            if (dh > height && dw <= width) {
                scale = height * 1.0f / dh;
            }
            if (dw > width && dh > height) {
                scale = Math.min(width * 1.0f / dw, dh * 1.0f / height);
            }
            initScale = scale;
            //将图片移至中心
            mScaleMatrix.postTranslate((width - dw) * 1.0f / 2, (height - dh) * 1.0f / 2);
            //缩放
            mScaleMatrix.postScale(scale, scale, getWidth() * 1.0f / 2, getHeight() * 1.0f / 2);
            setImageMatrix(mScaleMatrix);
            once = false;
        }
    }

    /**
     * 自动缩放图片的任务，主要靠的是不断的发送延迟消息进行处理(有个动画的效果)，直到缩放到需要的大小.
     */
    private class AutoScaleRunnable implements Runnable {

        /**
         * 放大或缩小的因子.
         */
        static final float BIGGER = 1.07f;
        static final float SMALLER = 0.93f;

        /**
         * 目标缩放值.
         */
        private float mTargetScale;

        /**
         * 存放缩放因子.
         */
        private float tmpScale;

        /**
         * 缩放中心.
         */
        private float x;
        private float y;

        /**
         * 传入目标缩放值，根据目标值与当前值，判断应该放大还是缩小.
         */
        AutoScaleRunnable(float targetScale, float x, float y) {
            mTargetScale = targetScale;
            this.x = x;
            this.y = y;
            if (getScale() < mTargetScale) {
                tmpScale = BIGGER;
            } else {
                tmpScale = SMALLER;
            }
        }

        @Override
        public void run() {
            mScaleMatrix.postScale(tmpScale, tmpScale, x, y);//进行缩放
            checkBorderAndCenterWhenScale();//边界检测
            setImageMatrix(mScaleMatrix);

            final float currentScale = getScale();
            //如果值在合法范围内，继续缩放
            if (((tmpScale > 1f) && (currentScale < mTargetScale))
                    || ((tmpScale < 1f) && (mTargetScale < currentScale))) {
                ZoomImageView.this.postDelayed(this,16);
            //调整大小(因为比如放大后可能超过需要的大小一点点)，设置为目标缩放比例
            } else {
                final float deltaScale = mTargetScale / currentScale;
                mScaleMatrix.postScale(deltaScale,deltaScale,x,y);
                checkBorderAndCenterWhenScale();
                setImageMatrix(mScaleMatrix);
                isAutoScale = false;
            }
        }
    }
}
