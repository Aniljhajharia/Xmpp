package com.example.user.xmppchat;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.nio.file.Path;

public class Splash extends AppCompatActivity {
    int splash_timeout=3000;
    TextView tv1,tv2,tv3,tv4;
    Path path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        tv1= (TextView) findViewById(R.id.text1);
        final float startSize = 5; // Size in pixels
        final float endSize = 42;
        long animationDuration = 3000; // Animation duration in ms
        ValueAnimator animator = ValueAnimator.ofFloat(startSize,endSize);
        animator.setDuration(animationDuration);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
           android.graphics.Path path = new android.graphics.Path();
            path.arcTo(0f, 0f, 1000f, 1500f, 180f, -180f, true);
            ObjectAnimator animator1 = ObjectAnimator.ofFloat(tv1, View.X, View.Y, path);
            animator1.setDuration(2000);
            animator1.start();
        } else {
            // Create animator without using curved path
        }
//        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                float animatedValue = (float) valueAnimator.getAnimatedValue();
//                tv1.setTextSize(animatedValue);
//
//            }
//        });
//
//        animator.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(Splash.this, Log_in.class);
                startActivity(i);
                finish();

            }
        }, splash_timeout);
    }
}
