//나침반 코드 참고
//출처 : https://copycoding.tistory.com/tag/TYPE_ACCELEROMETER
package com.gachon.userapp;

import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;


public class RotateAnimationHelper {
    private ImageView pointer;
    private ImageView imageView;
    private float containerWidth, containerHeight;
    private static float pivotX, pivotY;
    public RotateAnimationHelper(ImageView pointer, ImageView imageView) {
        this.pointer = pointer;
        this.imageView = imageView;

    }

    public void initialize(float x, float y) {

        imageView.post(new Runnable() {
            @Override
            public void run() {

                //pointer가 LinearLayout 내부의 RelativeLayout에 있어
                //pointer의 상대적 위치를 측정할 때 RelativeLayout을 이용함

                containerWidth = imageView.getWidth();
                containerHeight = imageView.getHeight();
                pivotX = x+((float)pointer.getWidth()/2);
                pivotY = y+((float)pointer.getHeight()/2);


            }
        });
    }

    public void rotate(float mCurrentDegree, float azimuthunDegress, int duration) {


        RotateAnimation ra = new RotateAnimation(
                mCurrentDegree,
                -azimuthunDegress,
                Animation.ABSOLUTE, pivotX,
                Animation.ABSOLUTE, pivotY
        );

        ra.setDuration(duration);
        ra.setFillBefore(true);
        pointer.startAnimation(ra);
    }
}