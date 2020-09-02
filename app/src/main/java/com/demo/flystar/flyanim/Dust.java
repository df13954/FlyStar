package com.demo.flystar.flyanim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.animation.AccelerateInterpolator;


import com.demo.flystar.BaseApplication;
import com.demo.flystar.R;

import java.util.ArrayList;
import java.util.Random;


public class Dust {
    private static final String TAG = Dust.class.getSimpleName();

    private static final int DEFAULT_ELEMENT_COUNT = 2;
    private static final float DEFAULT_ELEMENT_SIZE = 8;
    private static final int DEFAULT_DURATION = 3000;
    private static final int DEFAULT_LAUNCH_SPEED = 20;

    private Paint mPaint;
    private int mCount;
    private int mDuration;
    private float mLaunchSpeed;
    private float mLocationX;
    private float mLocationY;
    private float mElementSize;
    private float mAngle;
    private ValueAnimator mAnimator;
    private float mAnimatedValue;
    private AnimatorEndListener mListener;

    private ArrayList<Element> elements = new ArrayList<>();

    public Dust(float angle, float cx, float cy) {

        this.mAngle = angle;
        mCount = DEFAULT_ELEMENT_COUNT;
        mElementSize = DEFAULT_ELEMENT_SIZE;
        mLaunchSpeed = DEFAULT_LAUNCH_SPEED;
        mLocationX = cx;
        mLocationY = cy;
        init();
    }

    private void init() {
        // 0~360
        float direction;
        if (mAngle >= 0 && mAngle < 90) {
            // 270~360
            direction = 270 + mAngle;
        } else if (mAngle >= 90 && mAngle < 180) {
            // 0~90
            direction = mAngle - 90;
        } else if (mAngle >= 180 && mAngle < 270) {
            // 90~180
            direction = mAngle - 90;
        } else {
            // 180~270
            direction = mAngle - 90;
        }

        Random random = new Random();
        for (int i = 0; i < mCount; i++) {
            // 偏移角度在 -15~15
            float t = random.nextInt(30) - 15;
            t = direction + t;
            if (t > 360) {
                t = t - 360;
            }
            if (t < 0) {
                t = t + 360;
            }
            t = (float) Math.toRadians(t);
            elements.add(new Element(t, mLaunchSpeed * random.nextFloat()));
        }

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);

        mAnimator = ValueAnimator.ofFloat(1, 0);
        mAnimator.setDuration(DEFAULT_DURATION);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                mAnimatedValue = (float) animator.getAnimatedValue();
                // 计算位置
                for (Element element : elements) {
                    element.x += (float) (Math.cos(element.direction) * element.speed) * mAnimatedValue;
                    element.y += (float) (Math.sin(element.direction) * element.speed) * mAnimatedValue;
                }
            }
        });
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mListener != null)
                    mListener.onAnimatorEnd();
            }
        });
    }

    public void fire() {
        mAnimator.start();
    }

    public void draw(Canvas canvas) {
        mPaint.setAlpha((int) (255 * mAnimatedValue));
        Bitmap bitmap = BitmapFactory.decodeResource(BaseApplication.getContext().getResources(), R.mipmap.icon_fly_star);
        Random random = new Random();
        int radomSize = bitmap.getWidth() / 10 + random.nextInt(bitmap.getWidth() / 2);
        bitmap = imageScale(bitmap, radomSize, radomSize);
        for (Element element : elements) {
            //画圆点性能更高
//            canvas.drawCircle(mLocationX + element.x, mLocationY + element.y, mElementSize, mPaint);
            canvas.drawBitmap(bitmap, mLocationX + element.x, mLocationY + element.y, mPaint);
        }
    }

    /*
    设置bitmap尺寸
     */
    public Bitmap imageScale(Bitmap bitmap, int dst_w, int dst_h) {
        int src_w = bitmap.getWidth();
        int src_h = bitmap.getHeight();
        float scale_w = ((float) dst_w) / src_w;
        float scale_h = ((float) dst_h) / src_h;
        Matrix matrix = new Matrix();
        matrix.postScale(scale_w, scale_h);
        Bitmap dstbmp = Bitmap.createBitmap(bitmap, 0, 0, src_w, src_h, matrix,
                true);
//        Random random = new Random();
//        return getAlplaBitmap(dstbmp, 20+random.nextInt(80));

        return dstbmp;
    }

    /*
    设置bitmap颜色
     */
    public Bitmap tintBitmap(Bitmap inBitmap, int tintColor) {
        if (inBitmap == null) {
            return null;
        }
        Bitmap outBitmap = Bitmap.createBitmap(inBitmap.getWidth(), inBitmap.getHeight(), inBitmap.getConfig());
        Canvas canvas = new Canvas(outBitmap);
        Paint paint = new Paint();
        paint.setColorFilter(new PorterDuffColorFilter(tintColor, PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(inBitmap, 0, 0, paint);
        return outBitmap;
    }

    public void addAnimatorListener(AnimatorEndListener listener) {
        this.mListener = listener;
    }

    interface AnimatorEndListener {
        void onAnimatorEnd();
    }
}
