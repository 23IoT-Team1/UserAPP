package com.gachon.userapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class CanvasView extends View {

    Paint paint = new Paint();
    float density = getResources().getDisplayMetrics().density;
//    public boolean showPath;   // drawPath 보여줄지말지

    public ArrayList<Integer> pathIndex;
    float view_scale;
    public RP rp;


    public CanvasView(Context context, ArrayList<Integer> pathIndex, float view_scale) {
        super(context);
        this.pathIndex = pathIndex;
        this.view_scale = view_scale;

        for (int i = 0; i < pathIndex.size(); i++) {
            System.out.println(pathIndex.get(i));
        }

        // paint 기본 설정
        paint.setStrokeWidth(9f);
        paint.setStyle(Paint.Style.STROKE);


    }

    @Override
    protected void onDraw(Canvas canvas) {

//        if (showPath == true) { // 4층/5층 보여주는거랑 연결해서 4층일 때 4층 길, 5층일 때 5층 길 보여주기

            Path path = new Path();
            paint.setColor(Color.rgb(255, 114, 86));  // 코랄
            
            // 출발지 좌표는 moveTo로 따로 넣어줘야함
            path.moveTo(rp.getRpList().get(pathIndex.get(0)).getX() /view_scale *density,rp.getRpList().get(pathIndex.get(0)).getY() /view_scale *density); // 출발지 좌표

            // path 저장
            for (int i = 1; i < pathIndex.size(); i++) {    // 0은 위에서 설정했으니까 1부터
                path.lineTo(rp.getRpList().get(pathIndex.get(i)).getX() /view_scale *density, rp.getRpList().get(pathIndex.get(i)).getY() /view_scale *density);
            }
            canvas.drawPath(path, paint);   // draw
//        }
        for (int i = 0; i < pathIndex.size(); i++) {
            System.out.println(i + " - " + pathIndex.get(i));
        }
    }

    // set direction list to guide
    public void setDirectionList() {

    }

    // set path on each floor to draw
    public void setPathOnEachFloor() {

    }


}
