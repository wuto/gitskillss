package com.example.gallery_qq;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by CXX on 2016/8/3.
 */
public class CustomerGallery extends Gallery {
    public CustomerGallery(Context context) {
        super(context);
        // 启用getChildStaticTransformation被调用
        setStaticTransformationsEnabled(true);
    }

    public CustomerGallery(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 启用getChildStaticTransformation被调用
        setStaticTransformationsEnabled(true);
    }

    public CustomerGallery(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 启用getChildStaticTransformation被调用
        setStaticTransformationsEnabled(true);

    }
    //-----------------------------------------------------------------
    private int galleryCenter; //Gallery的中心点
    private int viewCenter;   //view的中心点
    private float MIN_SCALE = 0.65f;  //最小的缩放比例
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        galleryCenter = getWidth() / 2;
    }

    private TextView tvTitle, tvNumber;
    /**
     * 返回Galleryitem的子图形的变化效果
     * @param child
     * @param t 指定当前item的变化效果
     * @return 返回true代表使用当前效果
     */
        @Override
    protected boolean getChildStaticTransformation(View child, Transformation t) {
        LinearLayout item = (LinearLayout) child;
            tvTitle = (TextView) child.findViewById(R.id.tv_title);
            tvNumber = (TextView) child.findViewById(R.id.tv_number);
        if (android.os.Build.VERSION.SDK_INT >= 16) {
            item.invalidate();
        }

        final int childWidth = item.getWidth();
        final int childHeight = item.getHeight();
        t.clear();
        t.setTransformationType(Transformation.TYPE_BOTH);

        final Matrix matrix = t.getMatrix();
            float scale=1;
            viewCenter = child.getWidth() / 2 + child.getLeft();
            if (viewCenter < galleryCenter) {
                scale = (viewCenter) *1.0f/ galleryCenter;
            } else {
                scale = (galleryCenter * 2 - viewCenter )*1.0f/ galleryCenter;
            }
            if (scale < MIN_SCALE) {
                scale =MIN_SCALE;
            } else if (scale > 1) {
                scale=1.0f;
            }
            if (scale>=0.85&&scale <= 1) {
                tvTitle.setTextColor(Color.parseColor("#FFCE43"));
                tvNumber.setTextColor(Color.parseColor("#FFCE43"));
            } else {
                tvTitle.setTextColor(Color.parseColor("#333333"));
                tvNumber.setTextColor(Color.parseColor("#333333"));
            }
            t.setAlpha(scale);
            final float translateX = childWidth / 2.0f;
            final float translateY = childHeight / 2.0f;
            matrix.postScale(scale, scale);  //进行缩放
            matrix.preTranslate(-translateX, -translateY);  //矩阵前乘
            matrix.postTranslate(translateX, translateY);   //矩阵后乘

        return true;
    }


}
