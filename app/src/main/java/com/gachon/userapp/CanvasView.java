package com.gachon.userapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Shader;
import android.view.View;

import java.util.ArrayList;

    public class CanvasView extends View {
    Paint paint = new Paint();
    float density = getResources().getDisplayMetrics().density;
    public boolean isFloor4;   // 층에따라 다른 경로 보여주기
    float view_scale;
    private ArrayList<Point> pathPoint;
    private int startFloor;
    private ArrayList<Integer> pointIndexOf4;
    private ArrayList<Integer> pointIndexOf5;
    private int firstColorOf4;
    private int firstColorOf5;
    private int lastColorOf4;
    private int lastColorOf5;

    // 빨주노초파남보핑
    int[] colors = {Color.rgb(235,107,107), Color.rgb(231,186,61), Color.rgb(102,200,118),
            Color.rgb(71,199,227), Color.rgb(49,103,208), Color.rgb(126,99,202),
            Color.rgb(209,106,225)};


    public CanvasView(Context context, float view_scale, ArrayList<Point> pathPoint, int startFloor,
                      ArrayList<Integer> pointIndexOf4, ArrayList<Integer> pointIndexOf5) {
        super(context);
        this.view_scale = view_scale;
        this.pathPoint = pathPoint;
        this.startFloor = startFloor;
        this.pointIndexOf4 = pointIndexOf4;
        this.pointIndexOf5 = pointIndexOf5;
        
        // color를 구분할 cursor 설정
        if (startFloor == 4) {  // 4층부터 시작
            firstColorOf4 = 0;
            lastColorOf4 = pointIndexOf4.size() - 1;
            firstColorOf5 = pointIndexOf4.size() - 1;
            lastColorOf5 = pointIndexOf4.size() + pointIndexOf5.size() - 2;
        }
        else {  // 5층부터 시작
            firstColorOf5 = 0;
            lastColorOf5 = pointIndexOf5.size() - 1;
            firstColorOf4 = pointIndexOf5.size() - 1;
            lastColorOf4 = pointIndexOf5.size() + pointIndexOf4.size() - 2;
        }

//        System.out.println("first4 " + firstColorOf4);
//        System.out.println("last4 " + lastColorOf4);
//        System.out.println("first5 " + firstColorOf5);
//        System.out.println("last5 " + lastColorOf5);

        // paint 기본 설정
        paint.setStrokeWidth(9f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (isFloor4 == true) { // 4층 보여줄 때

            if (pointIndexOf4.size() == 1) {
                paint.setColor(colors[firstColorOf4]);
                canvas.drawCircle(pathPoint.get(pointIndexOf4.get(0)).x / view_scale * density, pathPoint.get(pointIndexOf4.get(0)).y / view_scale * density, 3f, paint);
            }
            else if (pointIndexOf4.size() > 1) {

                int index = 0;
                for (int i = firstColorOf4; i < lastColorOf4; i++) {

                    float x1 = pathPoint.get(pointIndexOf4.get(index)).x / view_scale * density;
                    float y1 = pathPoint.get(pointIndexOf4.get(index)).y / view_scale * density;
                    float x2 = pathPoint.get(pointIndexOf4.get(index+1)).x / view_scale * density;
                    float y2 = pathPoint.get(pointIndexOf4.get(index+1)).y / view_scale * density;

                    if (i != 0) {
                        // i 동그라미
                        paint.setColor(colors[i]);
                        canvas.drawCircle(x1, y1, 1f, paint);
                    }

                    // draw gradient line (drawPath 대신)
                    paint.setShader(new LinearGradient(x1,y1,x2,y2, colors[i], colors[i+1], Shader.TileMode.CLAMP));
                    canvas.drawLine(x1,y1,x2,y2, paint);

                    index++;
                }
                // 4층의 마지막 동그라미
                float x = pathPoint.get(pointIndexOf4.get(index)).x / view_scale * density;
                float y = pathPoint.get(pointIndexOf4.get(index)).y / view_scale * density;
                paint.setColor(colors[lastColorOf4]);
                canvas.drawCircle(x, y, 3f, paint);
            }
        }
        else {  // 5층 보여줄 때

            if (pointIndexOf5.size() == 1) {
                paint.setColor(colors[firstColorOf5]);
                canvas.drawCircle(pathPoint.get(pointIndexOf5.get(0)).x / view_scale * density, pathPoint.get(pointIndexOf5.get(0)).y / view_scale * density, 2f, paint);
            }
            else if (pointIndexOf5.size() > 1) {

                int index = 0;
                for (int i = firstColorOf5; i < lastColorOf5; i++) {

                    float x1 = pathPoint.get(pointIndexOf5.get(index)).x / view_scale * density;
                    float y1 = pathPoint.get(pointIndexOf5.get(index)).y / view_scale * density;
                    float x2 = pathPoint.get(pointIndexOf5.get(index+1)).x / view_scale * density;
                    float y2 = pathPoint.get(pointIndexOf5.get(index+1)).y / view_scale * density;

                    if (i != 0) {
                        // i 동그라미
                        paint.setColor(colors[i]);
                        canvas.drawCircle(x1, y1, 1f, paint);
                    }

                    // draw gradient line (drawPath 대신)
                    paint.setShader(new LinearGradient(x1,y1,x2,y2, colors[i], colors[i+1], Shader.TileMode.CLAMP));
                    canvas.drawLine(x1,y1,x2,y2, paint);

                    index++;
                }
                // 5층의 마지막 동그라미
                float x = pathPoint.get(pointIndexOf5.get(index)).x / view_scale * density;
                float y = pathPoint.get(pointIndexOf5.get(index)).y / view_scale * density;
                paint.setColor(colors[lastColorOf5]);
                canvas.drawCircle(x, y, 3f, paint);

            }
        }
    }

}
