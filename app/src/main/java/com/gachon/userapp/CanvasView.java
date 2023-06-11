package com.gachon.userapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class CanvasView extends View {

    Paint paint = new Paint();
    float density = getResources().getDisplayMetrics().density;
    public boolean isFloor4;   // 층에따라 다른 경로 보여주기
    float view_scale;
    private ArrayList<Point> pathOfFloor4;
    private ArrayList<Point> pathOfFloor5;




    public CanvasView(Context context, float view_scale, ArrayList<Point> pathOfFloor4, ArrayList<Point> pathOfFloor5) {
        super(context);
        this.view_scale = view_scale;
        this.pathOfFloor4 = pathOfFloor4;
        this.pathOfFloor5 = pathOfFloor5;

        System.out.println("floor4-----------");
        for (int i = 0; i < pathOfFloor4.size(); i++) {
            System.out.println(pathOfFloor4.get(i).x + ", " + pathOfFloor4.get(i).y);
        }
        System.out.println("floor5-----------");
        for (int i = 0; i < pathOfFloor5.size(); i++) {
            System.out.println(pathOfFloor5.get(i).x + ", " + pathOfFloor5.get(i).y);
        }

        // paint 기본 설정
        paint.setStrokeWidth(9f);
        paint.setStyle(Paint.Style.STROKE);
        // 무지개색 넣을 거면 pathOfFloor4와 pathOfFloor5의 size를 비율로 비교해서 넣어주기

    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (isFloor4 == true) { // 4층 보여줄 때

            Path path4 = new Path();
            paint.setColor(Color.rgb(255, 114, 86));  // 코랄

            if (pathOfFloor4.size() == 1) {
                canvas.drawCircle(pathOfFloor4.get(0).x / view_scale * density, pathOfFloor4.get(0).y / view_scale * density, 3f, paint);
            }

            if (pathOfFloor4.size() > 1) {

                // 처음과 끝 동그라미
                canvas.drawCircle(pathOfFloor4.get(0).x / view_scale * density, pathOfFloor4.get(0).y / view_scale * density, 3f, paint);
                canvas.drawCircle(pathOfFloor4.get(pathOfFloor4.size()-1).x / view_scale * density, pathOfFloor4.get(pathOfFloor4.size()-1).y / view_scale * density, 3f, paint);

                // 출발지 좌표는 moveTo로 따로 넣어줘야함
                path4.moveTo(pathOfFloor4.get(0).x / view_scale * density, pathOfFloor4.get(0).y / view_scale * density); // 출발지 좌표
                // path 저장
                for (int i = 1; i < pathOfFloor4.size(); i++) {    // 0은 위에서 설정했으니까 1부터
                    path4.lineTo(pathOfFloor4.get(i).x / view_scale * density, pathOfFloor4.get(i).y / view_scale * density);
                }
                canvas.drawPath(path4, paint);   // draw
            }
        }
        else {  // 5층 보여줄 때
            Path path5 = new Path();
            paint.setColor(Color.rgb(114, 225, 86));  // 다른 색 테스트

            if (pathOfFloor5.size() == 1) {
                canvas.drawCircle(pathOfFloor5.get(0).x / view_scale * density, pathOfFloor5.get(0).y / view_scale * density, 3f, paint);
            }

            if (pathOfFloor5.size() > 1) {

                // 처음과 끝 동그라미
                canvas.drawCircle(pathOfFloor5.get(0).x / view_scale * density, pathOfFloor5.get(0).y / view_scale * density, 3f, paint);
                canvas.drawCircle(pathOfFloor5.get(pathOfFloor5.size()-1).x / view_scale * density, pathOfFloor5.get(pathOfFloor5.size()-1).y / view_scale * density, 3f, paint);

                // 출발지 좌표는 moveTo로 따로 넣어줘야함
                path5.moveTo(pathOfFloor5.get(0).x / view_scale * density, pathOfFloor5.get(0).y / view_scale * density); // 출발지 좌표
                // path 저장
                for (int i = 1; i < pathOfFloor5.size(); i++) {    // 0은 위에서 설정했으니까 1부터
                    path5.lineTo(pathOfFloor5.get(i).x / view_scale * density, pathOfFloor5.get(i).y / view_scale * density);
                }
                canvas.drawPath(path5, paint);   // draw
            }
        }
    }






}
