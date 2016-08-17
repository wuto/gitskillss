package com.example.jd;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Scroller;

/**
 * Created by fantasee on 2016/8/16.
 */
public class ScaleMoney extends View{


    private Paint mLinePaint;
    private Paint mValuePaint;
    private Paint mVerticalPaint;
    private Paint textPaint;

    private int defaultValue;

    private int mStartX = 0, mStartY, mStopX, mFirstX = 0;
    private int mWidth;

    private VelocityTracker velocityTracker;
//    private boolean isCanMove = true;
    private int lastX;
    private int lastCurrentMoveX;
    private boolean isLeft = false;

    private Scroller scroller;



    public ScaleMoney(Context context) {
        this(context,null);
    }

    public ScaleMoney(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ScaleMoney(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        scroller = new Scroller(context);
        init();
    }

    private void init() {

        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(2);
        mLinePaint.setColor(0xFF999999);


        mVerticalPaint = new Paint();
        mVerticalPaint.setAntiAlias(true);
        mVerticalPaint.setStyle(Paint.Style.STROKE);
        mVerticalPaint.setStrokeWidth(2);
        mVerticalPaint.setColor(0xFF999999);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setStrokeWidth(2);
        textPaint.setTextSize(30);
        textPaint.setColor(0xFF999999);
        textPaint.setTextAlign(Paint.Align.CENTER);

        mValuePaint = new Paint();
        mValuePaint.setAntiAlias(true);
        mValuePaint.setStyle(Paint.Style.STROKE);
        mValuePaint.setStrokeWidth(3);
        mValuePaint.setColor(0xFFFE7F1A);

    }




    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
        initSize();
    }

    /**
     * 测量高度
     *
     * @param heightMeasureSpec
     * @return
     */
    private int measureHeight(int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        //默认宽高;
        defaultValue = 150;
        switch (mode) {
            case MeasureSpec.AT_MOST:
                //最大值模式
                int value = Math.min(size, defaultValue);
                return value;
            case MeasureSpec.EXACTLY:
                return size;
        }
        return size;
    }
    /**
     * 测量宽度
     *
     * @param widthMeasureSpec
     * @return
     */
    private int measureWidth(int widthMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        //默认宽高;
        defaultValue = Integer.MAX_VALUE;
        switch (mode) {
            case MeasureSpec.AT_MOST:
                //最大值模式
                int value = Math.min(size, defaultValue);
                return value;

            case MeasureSpec.EXACTLY:
                return size;

        }
        return size;

    }

    private int moveX, lastMoveX = 0;
    private boolean isDrawText;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(0, mStartY, getWidth(), mStartY, mLinePaint);
        canvas.drawLine(mWidth / 2, mStartY, mWidth / 2,0, mValuePaint);
        if (moveScaleInterface != null) {
            moveScaleInterface.getValue((-lastMoveX + mWidth / 2 + 7) / 13);
        }
        for (int start = 0; start < mWidth; start++) {
            int top = mStartY - 10;
            //lastMoveX变成负的是因为向左画为负 实际意义向左画为增大
            //lastMoveX+start是因为从屏幕左侧开始为0加lastMoveX为实际数值
            if ((-lastMoveX + start) % (13 * 10) == 0) {
                top = top - 20;
                isDrawText = true;
            } else {
                isDrawText = false;
            }
            if ((-lastMoveX + start) % 13 == 0) {
                if ((-lastMoveX + start) >= 0 && (-lastMoveX + start) <= 20000 * 13) {
                    canvas.drawLine(start, mStartY, start, top, mVerticalPaint);
                }

            }

            if (isDrawText) {
                if ((-lastMoveX + start) >= 0 && (-lastMoveX + start) <= 20000 * 13)
                    canvas.drawText((-lastMoveX + start) / 13 + "", start, top - 8, textPaint);
            }
        }



    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = (int) event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                moveX = (int) (event.getX() - lastX);
//                LogUtil.e("移动的moveX" + moveX);
                if (lastCurrentMoveX == moveX) {
//                    LogUtil.e("相等");
                    return true;
                }
                lastMoveX = lastMoveX + moveX;
                if (moveX < 0) {
                    //向左滑动
                    isLeft = true;
                    if (-lastMoveX + mWidth / 2 > 20000 * 13) {
                        lastMoveX = lastMoveX - moveX;
                        return true;
                    }
                } else {
                    //向右滑动
                    isLeft = false;
//                    LogUtil.e("向右滑动lastMoveX" + lastMoveX);
                    if (lastMoveX > mWidth / 2) {
                        lastMoveX = lastMoveX - moveX;
                        return true;
                    }
                }
                lastCurrentMoveX = moveX;
                lastX = (int) event.getX();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                velocityTracker.computeCurrentVelocity(1000);
                float xVelocity = velocityTracker.getXVelocity();
                float yVelocity = velocityTracker.getYVelocity();
                if (Math.abs(xVelocity) < 800) {
                    return true;
                }
                scroller.fling(130, mStartY, (int) (-Math.abs(xVelocity) + 0.5), (int) (Math.abs(yVelocity) + 0.5), 000, 10080, 0, 1920);
                velocityTracker.recycle();
                velocityTracker = null;
                break;
        }
        return true;
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            int currX = scroller.getCurrX();
//            LogUtil.e("currX" + currX);
            if (isLeft) {
                lastMoveX = lastMoveX - currX;
            } else {
                lastMoveX = lastMoveX + currX;
            }
            //向右滑动
            if (lastMoveX > mWidth / 2) {
                lastMoveX = lastMoveX - currX;
                return;
            }
            //向左
            if (-lastMoveX + mWidth / 2 > 20000 * 13) {
                lastMoveX = lastMoveX - moveX;
                return ;
            }
            invalidate();

        }
    }

    private void initSize() {
//        LogUtil.e("getHeight" + getHeight());
        mStartY = getHeight() / 2;
        mWidth = getWidth();
    }

    private MoveScaleInterface moveScaleInterface;

    public void setMoveScaleInterface(MoveScaleInterface moveScaleInterface) {
        this.moveScaleInterface = moveScaleInterface;
    }

    public interface MoveScaleInterface {
        public void getValue(int value);
    }



/**
 * startY 滚动起始点Y坐标

 　　velocityX   当滑动屏幕时X方向初速度，以每秒像素数计算

 　　velocityY   当滑动屏幕时Y方向初速度，以每秒像素数计算

 　　minX    X方向的最小值，scroller不会滚过此点。

 　　maxX    X方向的最大值，scroller不会滚过此点。

 　　minY    Y方向的最小值，scroller不会滚过此点。

 　　maxY    Y方向的最大值，scroller不会滚过此点。
 */
//    scroller.fling(int startX, int startY, int velocityX, int velocityY,
//
//    int minX, int maxX, int minY, int maxY)

/**
 * 计算那些已经发生触摸事件点的当前速率。这个函数只有在你需要得到速率消息的情况下才调用，因为使用它需要消耗很大的性能。
 * units: 你使用的速率单位.1的意思是，以一毫秒运动了多少个像素的速率， 1000表示 一秒时间内运动了多少个像素。
 *  velocityTracker.computeCurrentVelocity(1000);
 */


}
