package com.demo.flystar;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.demo.flystar.flyanim.Comet;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private TextView tv_target;
    private TextView tv_start;
    private Comet view_anim;
    private TextView tv_play;


    private void initView() {
        tv_target = findViewById(R.id.tv_target);
        tv_start = findViewById(R.id.tv_start);
        view_anim = findViewById(R.id.view_anim);
        tv_play = findViewById(R.id.tv_play);
        tv_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveStartLocation();

            }
        });
    }


    public int dp2px(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }

    private int rotationBetweenLines = 10;

    private void moveStartLocation() {
        view_anim.setVisibility(View.INVISIBLE);
        int[] startlocation = new int[2];
        tv_start.getLocationOnScreen(startlocation);
        final int startx = startlocation[0];
        final int starty = startlocation[1];
        int[] targetlocation = new int[2];
        tv_target.getLocationOnScreen(targetlocation);
        final int targetx = targetlocation[0];
        final int targety = targetlocation[1];
        rotationBetweenLines = getRotationBetweenLines(targetx, targety, startx, starty);

        final int viewSize = dp2px(this, 150);

        ObjectAnimator translationX = new ObjectAnimator().ofFloat(view_anim, "translationX", 0, startx + viewSize);
        ObjectAnimator translationY = new ObjectAnimator().ofFloat(view_anim, "translationY", 0, starty + viewSize);
        AnimatorSet animatorSet = new AnimatorSet();  //组合动画
        animatorSet.playTogether(translationX, translationY); //设置动画
        animatorSet.setDuration(20);  //设置动画时间
        animatorSet.start(); //启动
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view_anim.setVisibility(View.VISIBLE);
                showFlyAnim(startx - viewSize, starty - viewSize, targetx - viewSize, targety - viewSize);

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }


    private void showFlyAnim(int startx, int starty, int targetx, int targety) {

        ObjectAnimator translationX = new ObjectAnimator().ofFloat(view_anim, "translationX", startx, targetx);
        ObjectAnimator translationY = new ObjectAnimator().ofFloat(view_anim, "translationY", starty, targety);
        AnimatorSet animatorSet = new AnimatorSet();  //组合动画
        animatorSet.playTogether(translationX, translationY); //设置动画
        animatorSet.setDuration(3000);  //设置动画时间
        animatorSet.start(); //启动
        Message handlemsg = new Message();
        handlemsg.what = 1;
        mHandler.sendMessage(handlemsg);
        translationY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Log.e("slkdfhjksjss", "========");

            }
        });
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view_anim.setVisibility(View.GONE);

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }

    private int handlerSize = 0;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (handlerSize < 1000) {
                        handlerSize += 1;
                        Message handlemsg = new Message();
                        handlemsg.what = 1;
                        mHandler.sendMessageDelayed(handlemsg, 1);
                        view_anim.start(rotationBetweenLines);
                    } else {
                        handlerSize = 0;
                        view_anim.end();
                    }

                    break;
            }
        }
    };

    //获取角度
    public int getRotationBetweenLines(float centerX, float centerY, float xInView, float yInView) {
        double rotation = 0;

        double k1 = (double) (centerY - centerY) / (centerX * 2 - centerX);
        double k2 = (double) (yInView - centerY) / (xInView - centerX);
        double tmpDegree = Math.atan((Math.abs(k1 - k2)) / (1 + k1 * k2)) / Math.PI * 180;

        if (xInView > centerX && yInView < centerY) {  //第一象限
            rotation = 90 - tmpDegree;
        } else if (xInView > centerX && yInView > centerY) //第二象限
        {
            rotation = 90 + tmpDegree;
        } else if (xInView < centerX && yInView > centerY) { //第三象限
            rotation = 270 - tmpDegree;
        } else if (xInView < centerX && yInView < centerY) { //第四象限
            rotation = 270 + tmpDegree;
        } else if (xInView == centerX && yInView < centerY) {
            rotation = 0;
        } else if (xInView == centerX && yInView > centerY) {
            rotation = 180;
        }

        return (int) rotation;
    }
}
