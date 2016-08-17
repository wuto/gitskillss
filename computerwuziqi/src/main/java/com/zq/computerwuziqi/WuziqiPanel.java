package com.zq.computerwuziqi;

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
import android.util.Log;
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
    private int MAX_LINE = 15;
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


    // 赢法数组
    int[][][] wins = new int[MAX_LINE][MAX_LINE][572];


    // 赢法统计数组
    int[] myWin;
    int[] computerWin;

    // 胜利方式
    private int count;

    // 棋盘上两方棋子的标志 0 无子 ； 1 我方 ； 2 电脑
    private int[][] chessBoard = new int[MAX_LINE][MAX_LINE];


    //位置重要性价值表,此表从中间向外,越往外价值越低
    private int PosValue[][]=new int[][]{
        {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
        {0,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
        {0,1,2,2,2,2,2,2,2,2,2,2,2,1,0},
        {0,1,2,3,3,3,3,3,3,3,3,3,2,1,0},
        {0,1,2,3,4,4,4,4,4,4,4,3,2,1,0},
        {0,1,2,3,4,5,5,5,5,5,4,3,2,1,0},
        {0,1,2,3,4,5,6,6,6,5,4,3,2,1,0},
        {0,1,2,3,4,5,6,7,6,5,4,3,2,1,0},
        {0,1,2,3,4,5,6,6,6,5,4,3,2,1,0},
        {0,1,2,3,4,5,5,5,5,5,4,3,2,1,0},
        {0,1,2,3,4,4,4,4,4,4,4,3,2,1,0},
        {0,1,2,3,3,3,3,3,3,3,3,3,2,1,0},
        {0,1,2,2,2,2,2,2,2,2,2,2,2,1,0},
        {0,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
        {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
    };


    public WuziqiPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
//        setBackgroundColor(0x44ff0000);
        init();
        Wininit();
    }

    public void clear() {

        for (int i = 0; i < MAX_LINE; i++) {
            for (int j = 0; j < MAX_LINE; j++) {
                chessBoard[i][j] = 0;
            }
        }

        for (int i = 0; i < count; i++) {
            myWin[i] = 0;
            computerWin[i] = 0;
        }

    }

    public void Wininit() {


        count = 0;
        // 横向赢法统计
        for (int i = 0; i < MAX_LINE; i++) {
            for (int j = 0; j < MAX_LINE - 4; j++) {
                for (int k = 0; k < MAX_COUNT_IN_LIN; k++) {
                    wins[i][j + k][count] = 1;
                }
                count++;
            }
        }
        // 纵向赢法统计
        for (int i = 0; i < MAX_LINE; i++) {
            for (int j = 0; j < MAX_LINE - 4; j++) {
                for (int k = 0; k < MAX_COUNT_IN_LIN; k++) {
                    wins[j + k][i][count] = 1;
                }
                count++;
            }
        }

        // 左上到右下斜线赢法统计
        for (int i = 0; i < MAX_LINE - 4; i++) {
            for (int j = 0; j < MAX_LINE - 4; j++) {
                for (int k = 0; k < MAX_COUNT_IN_LIN; k++) {
                    wins[i + k][j + k][count] = 1;
                }
                count++;
            }
        }

        // 右上到左下斜线赢法统计
        for (int i = 0; i < MAX_LINE - (MAX_COUNT_IN_LIN - 1); i++) {
            for (int j = MAX_LINE - 1; j > MAX_COUNT_IN_LIN - 2; j--) {
                for (int k = 0; k < MAX_COUNT_IN_LIN; k++) {
                    wins[i + k][j - k][count] = 1;
                }
                count++;
            }
        }
        myWin = new int[count];
        computerWin = new int[count];
        clear();
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
                    mIsWhite = !mIsWhite;
                    invalidate();
                    break;
            }
        }
    };

    private void Aipaint() {

        int u = 0, v = 0;

        // 保存最高得分
        int max = 0;

        int[][] myScore = new int[MAX_LINE][MAX_LINE];
        int[][] computerScore = new int[MAX_LINE][MAX_LINE];
        // 初始化分数值
        for (int i = 0; i < MAX_LINE; i++) {
            for (int j = 0; j < MAX_LINE; j++) {
                myScore[i][j] = 0;
                computerScore[i][j] = 0;
            }
        }

        for (int i = 0; i < MAX_LINE; i++) {
            for (int j = 0; j < MAX_LINE; j++) {
                if (chessBoard[i][j] == 0) {//这个点没有棋子
                    for (int k = 0; k < count; k++) {
                        if (wins[i][j][k] == 1) {//赢法数组中包含这个点的
                            // 我方得分，计算机拦截

                            if (myWin[k]==-1){
                                myScore[i][j]+= -9;
                            }
                            else if (myWin[k] == 1) {
                                myScore[i][j] += 90;
                            } else if (myWin[k] == 2) {
                                myScore[i][j] += 990;
                            } else if (myWin[k] == 3) {
                                myScore[i][j] += 9990;
                            } else if (myWin[k] == 4) {
                                myScore[i][j] += 99900;
                            }
                            // 计算机走法 得分
                            if (computerWin[k]==-1){
                                computerScore[i][j]+=-9;
                            }else if (computerWin[k] == 1) {
                                computerScore[i][j] += 99;
                            } else if (computerWin[k] == 2) {
                                computerScore[i][j] += 999;
                            } else if (computerWin[k] == 3) {
                                computerScore[i][j] += 9999;
                            } else if (computerWin[k] == 4) {
                                computerScore[i][j] += 99999;
                            }
                        }
                    }

                    computerScore[i][j]+=PosValue[i][j];
                    myScore[i][j]+=PosValue[i][j];

                    Log.i("TAG","x-y-max---"+i+"-"+j+"-"+max);

                    // 判断我方最高得分，将最高分数的点获取出来, u，v为计算机要落下的子的坐标
                    if (myScore[i][j] > max) {
                        max = myScore[i][j];
                        u = i;
                        v = j;
                    } else if (myScore[i][j] == max) {
                        if (computerScore[i][j] > computerScore[u][v]) {
                            // 认为i，j点比u，v点好
                            u = i;
                            v = j;
                        }
                    }

                    // 判断电脑方最高得分，将最高分数的点获取出来
                    if (computerScore[i][j] > max) {
                        max = computerScore[i][j];
                        u = i;
                        v = j;
                    } else if (computerScore[i][j] == max) {
                        if (myScore[i][j] > myScore[u][v]) {
                            // 认为i，j点比u，v点好
                            u = i;
                            v = j;
                        }
                    }
//                    Log.i("TAG","u-v-max---"+u+"-"+v+"-"+max);

                }
            }
        }

        Log.i("TAG","max--------"+max);
        Log.i("TAG","u-v--------"+u+"--"+v);
        chessBoard[u][v] = 2;

        Point point = new Point(u, v);
        mBlackArray.add(point);


        for (int k = 0; k < count; k++) {
            if (wins[point.x][point.y][k] == 1) {
                computerWin[k]++;
                myWin[k]=-1;
                if (computerWin[k] == MAX_COUNT_IN_LIN) {
                    mIsGameOver = true;
                    mIsWhiteWinner = false;
                }
            }
        }


        invalidate();


    }


    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        drawBoard(canvas);
        drawPieces(canvas);

        if (mWhiteArray.size() > 0) {
            if (checkGameOver()) return;
        }
        if (!mIsWhite && !mIsGameOver) {
            mHandler.sendEmptyMessageDelayed(1, 500);
        }


    }


    private boolean checkGameOver() {
        int max = MAX_LINE * MAX_LINE;
        if (mIsGameOver) {
            String text = mIsWhiteWinner ? "白棋胜利" : "黑棋胜利";
            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
        } else if (mWhiteArray.size() + mBlackArray.size() == max) {
            mIsGameOver = true;
            Toast.makeText(getContext(), "和棋", Toast.LENGTH_SHORT).show();
        }


        return mIsGameOver;

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

        if (!mIsWhite) {
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
                chessBoard[point.x][point.y] = 1;


                for (int k = 0; k < count; k++) {
                    if (wins[point.x][point.y][k] == 1) {
                        myWin[k]++;
                        computerWin[k]=-1;

                        if (myWin[k] == MAX_COUNT_IN_LIN) {
                            mIsGameOver = true;
                            mIsWhiteWinner = true;
                        }
                    }
                }

            } else {
                mBlackArray.add(point);
            }
            mIsWhite = !mIsWhite;
            invalidate();

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
        clear();
        mWhiteArray.clear();
        mBlackArray.clear();

        mIsGameOver = false;
        mIsWhiteWinner = false;
        invalidate();
    }


    /**
     * 清除赢法数组中包含这个点的数量
     */
    private void clearWinArr(Point point, boolean isWhiteArr) {
        int x = point.x;
        int y = point.y;
        for (int k = 0; k < count; k++) {
            if (wins[x][y][k] == 1) {//赢法数组中包含这个点的
                if (isWhiteArr) {
                    myWin[k]--;
                } else {
                    computerWin[k]--;
                }
            }
        }
    }

    /**
     * 悔棋
     */
    public void regret() {

        if (mIsWhite && mBlackArray.size() >= 1) {//当前轮到白棋


            int blackLast = mBlackArray.size() - 1;
            int whiteLast = mWhiteArray.size() - 1;
            Point blackPoint = mBlackArray.get(blackLast);
            Point whitePoint = mWhiteArray.get(whiteLast);
            chessBoard[blackPoint.x][blackPoint.y] = 0;
            chessBoard[whitePoint.x][whitePoint.y] = 0;

            clearWinArr(whitePoint, true);
            clearWinArr(blackPoint, false);

            mBlackArray.remove(blackLast);
            mWhiteArray.remove(whiteLast);
        } else if (!mIsWhite && mWhiteArray.size() >= 1) {//当前轮到黑棋
            mHandler.removeMessages(1);

            int whiteLast = mWhiteArray.size() - 1;
            Point whitePoint = mWhiteArray.get(whiteLast);
            chessBoard[whitePoint.x][whitePoint.y] = 0;

            clearWinArr(whitePoint, true);

            mWhiteArray.remove(whiteLast);

            mIsWhite = !mIsWhite;
        } else {
            Toast.makeText(getContext(), "当前不可悔棋", Toast.LENGTH_SHORT).show();
            return;
        }
        mIsGameOver = false;//设置游戏未结束
//        mIsWhite = mIsWhite;//不改变当前的下棋者
        invalidate();


//        if (mIsWhite && mBlackArray.size() >= 1) {//当前轮到白棋,黑棋悔棋
//
//            mBlackArray.remove(mBlackArray.size() - 1);
//        } else if (!mIsWhite && mWhiteArray.size() >= 1) {//当前轮到黑棋，白棋悔棋
//            mWhiteArray.remove(mWhiteArray.size() - 1);
//        } else {
//            Toast.makeText(getContext(), "当前不可悔棋", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        mIsGameOver = false;//设置游戏未结束
//        mIsWhite = !mIsWhite;//改变当前的下棋者
//        invalidate();

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
}
