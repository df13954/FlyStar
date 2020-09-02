package com.demo.flystar.flyanim;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.Nullable;


import java.util.LinkedList;


public class Comet extends View {

    private static final String TAG = Comet.class.getSimpleName();
    private final int DEFAULT_DUST_SIZE = 20;
    private final int DEFAULT_INNER_MARGIN = 10;
    private float mRadius;
    private float mCx;
    private float mCY;
    private int mStrokeWidth = 4;
    private float mAnimatedValue = 0;
    private LinkedList<Dust> mDusts = new LinkedList<>();
    private ValueAnimator mValueAnimator;
    private boolean mIsRunning = false;
    private int mInnerMargin;
    private Paint mPaint;

    public Comet(Context context) {
        super(context);
        init();
    }

    public Comet(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

        mInnerMargin = DEFAULT_INNER_MARGIN;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mCx = getWidth() / 2;
                mCY = getHeight() / 2;
                float t = mCx > mCY ? mCx - mStrokeWidth : mCY - mStrokeWidth;
                t -= dp2px(getContext(), mInnerMargin);
                // 对 radius 进行校正，但通常在配置文件中就应该配置好了。
                if (mRadius == 0 || mRadius > t) {
                    mRadius = t;
                }

                getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        Log.d(TAG, "animatedValue " + mAnimatedValue);
        for (Dust mDust : mDusts) {
            mDust.draw(canvas);
        }
        if (mIsRunning && mDusts.size() < DEFAULT_DUST_SIZE) {
            addDust();
        }


        if (!mIsRunning && mDusts.size() > 0) {
            invalidate();
        }

    }

    public void start(float angle) {
        mIsRunning = true;
        mAnimatedValue = angle;
        invalidate();
    }

    public void end() {
        mIsRunning = false;
    }


    public float getRadius(float mRadius) {
        return this.mRadius;
    }



    public void addDust() {

        final Dust dust = new Dust(mAnimatedValue, mCx, mCY);
        dust.addAnimatorListener(new Dust.AnimatorEndListener() {
                                     @Override
                                     public void onAnimatorEnd() {
                                         mDusts.remove(dust);
                                     }
                                 }
        );
        mDusts.add(dust);
        dust.fire();
    }

    public int getInnerMargin() {
        return mInnerMargin;
    }

    public  int dp2px(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }
}
