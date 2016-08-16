package com.zq.aiwuziqi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fantasee on 2016/8/15.
 */
public class WuziqiPanel extends View {

    private int mPanelWidth;
    private float mLineHeight;
    private int MAX_LINE = 10;
    private Paint mPaint;
    private Bitmap mWhitePiece;
    private Bitmap mBlackPiece;
    private float ratioPieceOfLineHeight = 3 * 1.0f / 4;

    private boolean mIsWhite = true;//白棋先手，当前轮到白棋
    private ArrayList<Point> mWhiteArray = new ArrayList<>();
    private ArrayList<Point> mBlackArray = new ArrayList<>();

    private boolean mIsGameOver;//游戏是否结束
    private boolean mIsWhiteWinner;//白棋是否是赢家


    private int MAX_COUNT_IN_LIN = 5;


    public WuziqiPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
//        setBackgroundColor(0x44ff0000);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(0x88000000);
        mPaint.setAntiAlias(true);//抗锯齿
        mPaint.setDither(true);//防抖动
        mPaint.setStyle(Paint.Style.STROKE);

        mWhitePiece = BitmapFactory.decodeResource(getResources(), R.drawable.stone_w2);
        mBlackPiece = BitmapFactory.decodeResource(getResources(), R.drawable.stone_b1);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);


        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);


        int width = Math.min(widthSize, heightSize);

        if (widthMode == MeasureSpec.UNSPECIFIED) {
            width = heightSize;
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            width = widthSize;
        }

        setMeasuredDimension(width, width);
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Aipaint();
                    invalidate();
                    mIsWhite = !mIsWhite;
                    break;
            }
        }
    };


    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        drawBoard(canvas);
        drawPieces(canvas);

        checkGameOver();

        if (!mIsWhite && !mIsGameOver) {
            mHandler.sendEmptyMessageDelayed(1, 500);
        }

    }

    private void checkGameOver() {
        boolean whiteWin = checkFiveInLine(mWhiteArray);
        boolean blackWin = checkFiveInLine(mBlackArray);
        int max = MAX_LINE * MAX_LINE;
        if (whiteWin || blackWin) {
            mIsGameOver = true;
            mIsWhiteWinner = whiteWin;
            String text = mIsWhiteWinner ? "白棋胜利" : "黑棋胜利";
            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
        } else if (mWhiteArray.size() + mBlackArray.size() == max) {
            mIsGameOver = true;
            Toast.makeText(getContext(), "和棋", Toast.LENGTH_SHORT).show();
        }


    }

    private boolean checkFiveInLine(List<Point> points) {
        for (Point p : points) {
            int x = p.x;
            int y = p.y;
            boolean win = checkHorizontal(x, y, points);
            if (win) return true;
            win = checkVertical(x, y, points);
            if (win) return true;
            win = checkLeftDiagonal(x, y, points);
            if (win) return true;
            win = checkRightDiagonal(x, y, points);
            if (win) return true;
        }
        return false;
    }

    /**
     * 判断棋子是否横向相邻五个一致
     *
     * @param x
     * @param y
     * @param points
     * @return
     */
    private boolean checkHorizontal(int x, int y, List<Point> points) {
        int count = 1;
        //左
        for (int i = 1; i < MAX_COUNT_IN_LIN; i++) {
            if (points.contains(new Point(x - i, y))) {
                count++;
            } else {
                break;
            }
        }
        //右
        for (int i = 1; i < MAX_COUNT_IN_LIN; i++) {
            if (points.contains(new Point(x + i, y))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LIN) return true;
        return false;
    }


    /**
     * 判断棋子是否竖向相邻五个一致
     *
     * @param x
     * @param y
     * @param points
     * @return
     */
    private boolean checkVertical(int x, int y, List<Point> points) {
        int count = 1;
        //上
        for (int i = 1; i < MAX_COUNT_IN_LIN; i++) {
            if (points.contains(new Point(x, y - i))) {
                count++;
            } else {
                break;
            }
        }
        //下
        for (int i = 1; i < MAX_COUNT_IN_LIN; i++) {
            if (points.contains(new Point(x, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LIN) return true;
        return false;
    }

    /**
     * 判断棋子是否左斜相邻五个一致
     *
     * @param x
     * @param y
     * @param points
     * @return
     */
    private boolean checkLeftDiagonal(int x, int y, List<Point> points) {
        int count = 1;
        //左下
        for (int i = 1; i < MAX_COUNT_IN_LIN; i++) {
            if (points.contains(new Point(x - i, y + i))) {
                count++;
            } else {
                break;
            }
        }
        //右上
        for (int i = 1; i < MAX_COUNT_IN_LIN; i++) {
            if (points.contains(new Point(x + i, y - i))) {
                count++;
            } else {
                break;
            }
        }

        if (count == MAX_COUNT_IN_LIN) return true;
        return false;
    }

    /**
     * 判断棋子是否右斜相邻五个一致
     *
     * @param x
     * @param y
     * @param points
     * @return
     */
    private boolean checkRightDiagonal(int x, int y, List<Point> points) {
        int count = 1;
        //右下
        for (int i = 1; i < MAX_COUNT_IN_LIN; i++) {
            if (points.contains(new Point(x + i, y + i))) {
                count++;
            } else {
                break;
            }
        }
        //左上
        for (int i = 1; i < MAX_COUNT_IN_LIN; i++) {
            if (points.contains(new Point(x - i, y - i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LIN) return true;
        return false;
    }

    private void drawPieces(Canvas canvas) {
        for (int i = 0; i < mWhiteArray.size(); i++) {
            Point whitePoint = mWhiteArray.get(i);
            canvas.drawBitmap(mWhitePiece, (whitePoint.x + (1 - ratioPieceOfLineHeight) / 2) * mLineHeight, (whitePoint.y + (1 - ratioPieceOfLineHeight) / 2) * mLineHeight, null);
        }
        for (int i = 0; i < mBlackArray.size(); i++) {
            Point blackPoint = mBlackArray.get(i);
            canvas.drawBitmap(mBlackPiece, (blackPoint.x + (1 - ratioPieceOfLineHeight) / 2) * mLineHeight, (blackPoint.y + (1 - ratioPieceOfLineHeight) / 2) * mLineHeight, null);
        }

    }

    /**
     * 绘制棋盘
     *
     * @param canvas
     */
    private void drawBoard(Canvas canvas) {
        int w = mPanelWidth;
        float lineHeight = mLineHeight;

        for (int i = 0; i < MAX_LINE; i++) {
            int startX = (int) (lineHeight / 2);
            int endX = (int) (w - lineHeight / 2);

            int y = (int) ((0.5 + i) * lineHeight);
            canvas.drawLine(startX, y, endX, y, mPaint);//横线

            canvas.drawLine(y, startX, y, endX, mPaint);//竖线


        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mPanelWidth = w;
        mLineHeight = mPanelWidth * 1.0f / MAX_LINE;

        int pieceWhite = (int) (mLineHeight * ratioPieceOfLineHeight);

        mWhitePiece = Bitmap.createScaledBitmap(mWhitePiece, pieceWhite, pieceWhite, false);
        mBlackPiece = Bitmap.createScaledBitmap(mBlackPiece, pieceWhite, pieceWhite, false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mIsGameOver) {
            return false;
        }

        if (!mIsWhite) {//如果为黑子执棋，即电脑，则屏蔽落子操作
            return false;
        }

        int action = event.getAction();

        if (action == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            Point point = getValidPoint(x, y);

            if (mWhiteArray.contains(point) || mBlackArray.contains(point)) {
                return false;
            }
            if (mIsWhite) {
                mWhiteArray.add(point);
            } else {
                mBlackArray.add(point);
            }
            invalidate();
            mIsWhite = !mIsWhite;
        }


        return true;
    }


    private Point getValidPoint(int x, int y) {
        return new Point((int) (x / mLineHeight), (int) (y / mLineHeight));
    }


    /**
     * 重来一局
     */
    public void start() {
        mWhiteArray.clear();
        mBlackArray.clear();
        mIsGameOver = false;
        mIsWhiteWinner = false;
        invalidate();
    }

    /**
     * 悔棋
     */
    public void regret() {
        if (mIsWhite && mBlackArray.size() >= 1) {//当前轮到白棋,黑棋悔棋

            mBlackArray.remove(mBlackArray.size() - 1);
        } else if (!mIsWhite && mWhiteArray.size() >= 1) {//当前轮到黑棋，白棋悔棋
            mWhiteArray.remove(mWhiteArray.size() - 1);
        } else {
            Toast.makeText(getContext(), "当前不可悔棋", Toast.LENGTH_SHORT).show();
            return;
        }
        mIsGameOver = false;//设置游戏未结束
        mIsWhite = !mIsWhite;//改变当前的下棋者
        invalidate();

    }

    private static final String INSTANCE = "instance";
    private static final String INSTANCE_GAME_OVER = "instance_game_over";
    private static final String INSTANCE_WHITE_ARRAY = "instance_white_array";
    private static final String INSTANCE_BLACK_ARRAY = "instance_black_array";

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE, super.onSaveInstanceState());
        bundle.putBoolean(INSTANCE_GAME_OVER, mIsGameOver);
        bundle.putParcelableArrayList(INSTANCE_WHITE_ARRAY, mWhiteArray);
        bundle.putParcelableArrayList(INSTANCE_BLACK_ARRAY, mBlackArray);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mIsGameOver = bundle.getBoolean(INSTANCE_GAME_OVER);
            mWhiteArray = bundle.getParcelableArrayList(INSTANCE_WHITE_ARRAY);
            mBlackArray = bundle.getParcelableArrayList(INSTANCE_BLACK_ARRAY);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));
            return;
        }
        super.onRestoreInstanceState(state);
    }


    /**
     * 电脑下棋 黑棋
     */
    private void Aipaint() {
        if (putOneBlack()) return;

        //如果黑子两两相连
        if (checktwoBlackLeft()) return;
        //如果白子，黑子两两不相连
        if (checkOneBlackLeft()) return;
        ///黑子有四个一行的直接完成五子
        boolean isFourInLine = checkBlackFourInLine(mBlackArray);
        if (isFourInLine) return;


        //判断有没有四个连续的，如果有就组成五个
        checkBlackTwoTwo(mBlackArray);
        //判断对手（白棋有没有四个连续的，如果有，阻止）
        checkWhiteTwoTwo(mWhiteArray);
        //判断有没有三个连续的，如果有就组成四个
        //判断对手（白棋有没有三个连续的，如果有，阻止）
        //判断有没有两个连续的，如果有就组成三个
        //判断对手（白棋有没有两个连续的，如果有，阻止）


    }

    private boolean checktwoBlackLeft() {
        if (!checktwo(mWhiteArray) && !checktwo(mBlackArray)) {//如果都没有两两相连的


        }


        return false;
    }

    /**
     * 判断棋子四周有无空白
     *
     * @return
     */
    private boolean checkPiece(Point point) {
        int x = point.x;
        int y = point.y;
        if (x - 1 >= 0) {
            if (!mWhiteArray.contains(new Point(x - 1, y)) && !mBlackArray.contains(new Point(x - 1, y))) {
                return true;
            }
        }
        if (y - 1 >= 0) {//上
            if (!mWhiteArray.contains(new Point(x, y - 1)) && !mBlackArray.contains(new Point(x, y - 1))) {
                return true;
            }
        }
        if (x + 1 <= MAX_LINE - 1) {
            if (!mWhiteArray.contains(new Point(x + 1, y)) && !mBlackArray.contains(new Point(x + 1, y))) {
                return true;
            }
        }
        if (y + 1 <= MAX_LINE - 1) {
            if (!mWhiteArray.contains(new Point(x, y + 1)) && !mBlackArray.contains(new Point(x, y + 1))) {
                return true;
            }
        }
        return false;
    }


    /**
     * 遍历了所有棋盘，都没子可落。相当于黑子被白子给包围了。那么，你在遍历下棋盘，哪里空着就放哪
     *
     * @return
     */
    private boolean NoPieceConnect() {
        for (int x = 0; x < MAX_LINE; x++) {
            for (int y = 0; y < MAX_LINE; y++) {
                if (!mWhiteArray.contains(new Point(x, y)) && !mBlackArray.contains(new Point(x, y))) {
                    mBlackArray.add(new Point(x, y));
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 随机选择一个周围有空白的黑子
     */
    private Point getPoint() {
        ArrayList<Point> points = checkAlls();
        if (points.size() > 0) {
            return points.get((int) Math.random() * points.size());
        }
        return null;
    }

    /**
     * 判断是否全部黑子四周都没空白
     */
    private boolean checkAll() {
        for (Point point : mBlackArray) {
            if (checkPiece(point)) return false;//有空白
        }
        return true;//无空白
    }

    /**
     * 判断是否全部黑子四周都没空白
     */
    private ArrayList<Point> checkAlls() {
        ArrayList<Point> blackPiece = new ArrayList<>();
        for (Point point : mBlackArray) {
            if (checkPiece(point)) blackPiece.add(point);//有空白
        }
        return blackPiece;//无空白
    }


    /**
     * 如果白子两两不相连，黑子也两两不相连，白子落完，黑子怎么走？
     * 我这边是遍历整个棋盘，优先落在黑子左边，然后是上、右、下,
     * 这边好像还要判断下x-1不小于0.我给忘了- - 至于上，右，下，改坐标就可以了。
     *
     * @param
     * @return
     */
    private boolean checkOneBlackLeft() {
        if (!checktwo(mWhiteArray) && !checktwo(mBlackArray)) {//如果都没有两两相连的

            Point p = getPoint();
            int x = p.x;
            int y = p.y;

            if (x - 1 >= 0) {//左
                if (!mWhiteArray.contains(new Point(x - 1, y)) && !mBlackArray.contains(new Point(x - 1, y))) {
                    mBlackArray.add(new Point(x - 1, y));
                    return true;
                }
            }
            if (y - 1 >= 0) {//上
                if (!mWhiteArray.contains(new Point(x, y - 1)) && !mBlackArray.contains(new Point(x, y - 1))) {
                    mBlackArray.add(new Point(x, y - 1));
                    return true;
                }
            }
            if (x + 1 <= MAX_LINE - 1) {
                if (!mWhiteArray.contains(new Point(x + 1, y)) && !mBlackArray.contains(new Point(x + 1, y))) {
                    mBlackArray.add(new Point(x + 1, y));
                    return true;
                }
            }
            if (y + 1 <= MAX_LINE - 1) {
                if (!mWhiteArray.contains(new Point(x, y + 1)) && !mBlackArray.contains(new Point(x, y + 1))) {
                    mBlackArray.add(new Point(x, y + 1));
                    return true;
                }
            }

        }
        return false;
//        return checkOneBlackLeft();
    }

    /**
     * 判断是否有两两相连
     *
     * @param points
     * @return
     */
    private boolean checktwo(ArrayList<Point> points) {
        for (Point p : points) {
            int x = p.x;
            int y = p.y;
            if (x - 1 >= 0) {
                if (points.contains(new Point(x - 1, y))) {
                    return true;//有两两相连的
                }
            }
            if (y - 1 >= 0) {
                if (points.contains(new Point(x, y - 1))) {
                    return true;//有两两相连的
                }
            }
            if (x + 1 <= MAX_LINE - 1) {
                if (points.contains(new Point(x + 1, y))) {
                    return true;//有两两相连的
                }
            }
            if (y + 1 <= MAX_LINE - 1) {
                if (points.contains(new Point(x, y + 1))) {
                    return true;//有两两相连的
                }
            }
        }
        return false;
    }


    /**
     * 出现电脑22022的时候，他肯定下中间直接成5点，而不会优先赌你的子。
     *
     * @param mWhiteArray
     */
    private void checkWhiteTwoTwo(ArrayList<Point> mWhiteArray) {
    }

    /**
     * 白子快成5的时候（11011），电脑下在中间。
     *
     * @param mBlackArray
     */
    private void checkBlackTwoTwo(ArrayList<Point> mBlackArray) {
    }


    /**
     * 电脑冲5的时候需要判断左边，右边，左上，右下，是否属于在屏幕内。这边我把代码贴出来。后面所有的判断是差不多的
     *
     * @param point
     * @return
     */
    private boolean checkBlackFourInLine(ArrayList<Point> point) {
        for (Point p : point) {
            int x = p.x;
            int y = p.y;
            boolean checkHorizontal = checkBlackFourHorizontalInLine(x, y, point);
            if (checkHorizontal) {
                return true;
            }
            boolean checkVertical = checkBlackFourVerticalInLine(x, y, point);
            if (checkVertical) {
                return true;
            }
            boolean checkLeftDiagonal = checkBlackFourLeftInLine(x, y, point);
            if (checkLeftDiagonal) {
                return true;
            }
            boolean checkRightDiagonal = checkBlackFourRightInLine(x, y, point);
            if (checkRightDiagonal) {
                return true;
            }
        }
        return false;
    }

    /**
     * 横向上黑子是否有四个连续的
     *
     * @param x
     * @param y
     * @param point
     * @return
     */
    private boolean checkBlackFourHorizontalInLine(int x, int y, ArrayList<Point> point) {
        int count = 1;
        for (int i = 1; i < 4; i++) {
            if (point.contains(new Point(x - i, y))) {
                count++;
            } else {
                break;
            }
        }
        if (count == 4) {//如果一个黑子左边三个都是黑子
            if (x - 4 < 0) {//左边没有地方下棋（3-4=-1<0,坐标是从0开始的）
                if (!mBlackArray.contains(new Point(x + 1, y)) && !mWhiteArray.contains(new Point(x + 1, y))) {//如果右边没有白棋子也没有黑棋子，黑棋子下在这里
                    mBlackArray.add(new Point(x + 1, y));
                    return true;
                }
            } else if (x + 1 > MAX_LINE - 1) {//右边没有地方下棋（9+1>10-1,坐标是从0开始的,到MAX_LINE-1结束）
                if (!mBlackArray.contains(new Point(x - 4, y)) && !mWhiteArray.contains(new Point(x - 4, y))) {
                    mBlackArray.add(new Point(x - 4, y));
                    return true;
                }
            } else if (!mBlackArray.contains(new Point(x - 4, y)) && !mBlackArray.contains(new Point(x + 1, y))) {//如果左边和右边都没有黑子
                if (!mWhiteArray.contains(new Point(x - 4, y)) && !mWhiteArray.contains(new Point(x + 1, y))) {//如果左边和右边都没有白子
                    mBlackArray.add(Math.random() > 0.5 ? new Point(x - 4, y) : new Point(x + 1, y));//随机放在左边或右边
                    return true;
                } else if (mWhiteArray.contains(new Point(x - 4, y)) && !mWhiteArray.contains(new Point(x + 1, y))) {//如果左边有白子右边没白子
                    mBlackArray.add(new Point(x + 1, y));
                    return true;
                } else if (mWhiteArray.contains(new Point(x + 1, y)) && !mWhiteArray.contains(new Point(x - 4, y))) {//如果右边有白子左边没白子
                    mBlackArray.add(new Point(x - 4, y));
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 竖向上黑子是否有四个连续的
     *
     * @param x
     * @param y
     * @param point
     * @return
     */
    private boolean checkBlackFourVerticalInLine(int x, int y, ArrayList<Point> point) {
        int count = 1;
        for (int i = 1; i < 4; i++) {
            if (point.contains(new Point(x, y - i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == 4) {//如果一个黑子左边三个都是黑子
            if (y - 4 < 0) {//左边没有地方下棋（3-4=-1<0,坐标是从0开始的）
                if (!mBlackArray.contains(new Point(x, y + 1)) && !mWhiteArray.contains(new Point(x, y + 1))) {//如果右边没有白棋子也没有黑棋子，黑棋子下在这里
                    mBlackArray.add(new Point(x, y + 1));
                    return true;
                }
            } else if (y + 1 > MAX_LINE - 1) {//右边没有地方下棋（9+1>10-1,坐标是从0开始的,到MAX_LINE-1结束）
                if (!mBlackArray.contains(new Point(x, y - 4)) && !mWhiteArray.contains(new Point(x, y - 4))) {
                    mBlackArray.add(new Point(x, y - 4));
                    return true;
                }
            } else if (!mBlackArray.contains(new Point(x, y - 4)) && !mBlackArray.contains(new Point(x, y + 1))) {//如果左边和右边都没有黑子
                if (!mWhiteArray.contains(new Point(x, y - 4)) && !mWhiteArray.contains(new Point(x, y + 1))) {//如果左边和右边都没有白子
                    mBlackArray.add(Math.random() > 0.5 ? new Point(x, y - 4) : new Point(x, y + 1));//随机放在左边或右边
                    return true;
                } else if (mWhiteArray.contains(new Point(x, y - 4)) && !mWhiteArray.contains(new Point(x, y + 1))) {//如果左边有白子右边没白子
                    mBlackArray.add(new Point(x, y + 1));
                    return true;
                } else if (mWhiteArray.contains(new Point(x, y + 1)) && !mWhiteArray.contains(new Point(x, y - 4))) {//如果右边有白子左边没白子
                    mBlackArray.add(new Point(x, y - 4));
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 左斜方向上黑子是否有四个连续的
     *
     * @param x
     * @param y
     * @param point
     * @return
     */
    private boolean checkBlackFourLeftInLine(int x, int y, ArrayList<Point> point) {
        int count = 1;
        for (int i = 1; i < 4; i++) {
            if (point.contains(new Point(x - 1, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == 4) {//如果一个黑子左边三个都是黑子
            if (x - 4 < 0 || y + 4 > MAX_LINE - 1) {//左边没有地方下棋（3-4=-1<0,坐标是从0开始的）
                if (x + 1 <= MAX_LINE - 1 && y - 1 >= 0) {
                    if (!mBlackArray.contains(new Point(x + 1, y - 1)) && !mWhiteArray.contains(new Point(x + 1, y - 1))) {//如果右边没有白棋子也没有黑棋子，黑棋子下在这里
                        mBlackArray.add(new Point(x + 1, y - 1));
                        return true;
                    }
                }
            } else if (x + 1 > MAX_LINE - 1 || y - 1 < 0) {//右边没有地方下棋（9+1>10-1,坐标是从0开始的,到MAX_LINE-1结束）
                if (x - 4 >= 0 && y + 4 <= MAX_LINE - 1) {
                    if (!mBlackArray.contains(new Point(x - 4, y + 4)) && !mWhiteArray.contains(new Point(x - 4, y + 4))) {
                        mBlackArray.add(new Point(x - 4, y + 4));
                        return true;
                    }
                }

            } else if (!mBlackArray.contains(new Point(x - 4, y + 4)) && !mBlackArray.contains(new Point(x + 1, y - 1))) {//如果左边和右边都没有黑子
                if (!mWhiteArray.contains(new Point(x - 4, y + 4)) && !mWhiteArray.contains(new Point(x + 1, y - 1))) {//如果左边和右边都没有白子
                    mBlackArray.add(Math.random() > 0.5 ? new Point(x - 4, y + 4) : new Point(x + 1, y - 1));//随机放在左边或右边
                    return true;
                } else if (mWhiteArray.contains(new Point(x - 4, y + 4)) && !mWhiteArray.contains(new Point(x + 1, y - 1))) {//如果左边有白子右边没白子
                    mBlackArray.add(new Point(x + 1, y - 1));
                    return true;
                } else if (mWhiteArray.contains(new Point(x + 1, y - 1)) && !mWhiteArray.contains(new Point(x - 4, y + 4))) {//如果右边有白子左边没白子
                    mBlackArray.add(new Point(x - 4, y + 4));
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 右斜方向上黑子是否有四个连续的
     *
     * @param x
     * @param y
     * @param point
     * @return
     */
    private boolean checkBlackFourRightInLine(int x, int y, ArrayList<Point> point) {
        int count = 1;
        for (int i = 1; i < 4; i++) {
            if (point.contains(new Point(x + 1, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == 4) {//如果一个黑子左边三个都是黑子
            if (x - 1 < 0 || y - 1 < 0) {//左边没有地方下棋（3-4=-1<0,坐标是从0开始的）
                if (x + 4 <= MAX_LINE - 1 && y + 4 <= MAX_LINE - 1) {
                    if (!mBlackArray.contains(new Point(x + 4, y + 4)) && !mWhiteArray.contains(new Point(x + 4, y + 4))) {//如果右边没有白棋子也没有黑棋子，黑棋子下在这里
                        mBlackArray.add(new Point(x + 4, y + 4));
                        return true;
                    }
                }
            } else if (x + 4 > MAX_LINE - 1 || y + 4 > MAX_LINE - 1) {//右边没有地方下棋（9+1>10-1,坐标是从0开始的,到MAX_LINE-1结束）
                if (x - 1 >= 0 && y - 1 >= 0) {
                    if (!mBlackArray.contains(new Point(x - 1, y - 1)) && !mWhiteArray.contains(new Point(x - 1, y - 1))) {
                        mBlackArray.add(new Point(x - 1, y - 1));
                        return true;
                    }
                }

            } else if (!mBlackArray.contains(new Point(x - 1, y - 1)) && !mBlackArray.contains(new Point(x + 4, y + 4))) {//如果左边和右边都没有黑子
                if (!mWhiteArray.contains(new Point(x - 1, y - 1)) && !mWhiteArray.contains(new Point(x + 4, y + 4))) {//如果左边和右边都没有白子
                    mBlackArray.add(Math.random() > 0.5 ? new Point(x - 1, y - 1) : new Point(x + 4, y + 4));//随机放在左边或右边
                    return true;
                } else if (mWhiteArray.contains(new Point(x - 1, y - 1)) && !mWhiteArray.contains(new Point(x + 4, y + 4))) {//如果左边有白子右边没白子
                    mBlackArray.add(new Point(x + 4, y + 4));
                    return true;
                } else if (mWhiteArray.contains(new Point(x + 4, y + 4)) && !mWhiteArray.contains(new Point(x - 1, y - 1))) {//如果右边有白子左边没白子
                    mBlackArray.add(new Point(x - 1, y - 1));
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 放置第一个黑子
     */
    private boolean putOneBlack() {

        if (mWhiteArray.size() == 1) {
            for (Point p : mWhiteArray) {
                int x = p.x;
                int y = p.y;
                if (x == 0 || y == 0 || x == MAX_LINE - 1 || y == MAX_LINE - 1) {
                    mBlackArray.add(new Point(MAX_LINE / 2, MAX_LINE / 2));
                    return true;
                } else if (x == MAX_LINE - 1 && y == 0) {
                    mBlackArray.add(Math.random() > 0.5 ? new Point(x - 1, y) : new Point(x, y + 1));
                    return true;
                } else if (x == 0 && y == MAX_LINE - 1) {
                    mBlackArray.add(Math.random() > 0.5 ? new Point(0, y - 1) : new Point(x + 1, y));
                    return true;
                } else if (Math.random() > 0.5) {
                    mBlackArray.add(Math.random() > 0.5 ? new Point(x + 1, y) : new Point(x - 1, y));
                    return true;
                } else {
                    mBlackArray.add(Math.random() > 0.5 ? new Point(x, y + 1) : new Point(x, y - 1));
                    return true;
                }
            }

        }

        return false;
    }
}