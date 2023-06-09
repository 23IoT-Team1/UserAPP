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

        int[] elevatorIndexList = {11, 17, 20, 25, 31, 42, 43, 48,
                57, 66, 69, 80, 88, 89, 94};    // 계단/엘베 파악용으로 쓸 index array

        // pathIndex로 좌표를 받아와서 새로운 arrayList를 만들고
        ArrayList<Point> pathPoint = new ArrayList<>();
        for (int i : pathIndex) {
            pathPoint.add(new Point(rp.getRpList().get(i).getX(), rp.getRpList().get(i).getY()));
        }

        // 그 arrayList를 기반으로 방향을 판단해서, 
        // "way" / "left" / "right"/ "elevator" / "destination" 가 들어간 리스트 만들기
        //// "way"는 가는 중간 과정
        //// "elevator"는 두개가 항상 쌍으로 다님. 첫번째 꺼 도달 전에는 직진 표시, 두번째 꺼 도달 전에는 계단엘베 표시
        ArrayList<String> pathDirection = new ArrayList<>();

        if (pathPoint.size() > 2) { // 일반적인 경우

            // 1. 출발지 노드
            boolean startIsElevator = false;
            for (int i : elevatorIndexList) {
                if (pathIndex.get(0) == i) {
                    startIsElevator = true;
                }
            }
            if (startIsElevator) { pathDirection.add("elevator"); } // 계단/엘베
            else { pathDirection.add("way"); }  // 일반적인 경우

            // 2. 중간 노드
            for (int i = 1; i < pathPoint.size() - 1; i++) {   
                Point previous = pathPoint.get(i - 1);
                Point now = pathPoint.get(i);
                Point next = pathPoint.get(i + 1);

                boolean IsElevator = false;
                for (int k : elevatorIndexList) {
                    if (pathIndex.get(k) == i) {
                        IsElevator = true;
                    }
                }
                if (IsElevator) { pathDirection.add("elevator"); }  // 계단/엘베 먼저 처리
                else {  // 나머지는 way 혹은 방향
                    // calculate ccw
                    int ccw = ((now.x - previous.x) * (next.y - previous.y)) - ((next.x - previous.x) * (now.y - now.y));

                    if (ccw > 0) { pathDirection.add("left"); }
                    else if (ccw < 0) { pathDirection.add("right"); }
                    else { pathDirection.add("way"); }  // ccw == 0
                }
            }

            // 3. 목적지 노드
            boolean lastIsElevator = false;
            for (int i : elevatorIndexList) {
                if (pathIndex.get(0) == i) {
                    lastIsElevator = true;
                }
            }
            if (lastIsElevator) { pathDirection.add("elevator"); } // 계단/엘베
            else { pathDirection.add("destination"); }  // 일반적인 경우

        }
        else {  // 엄청 가까운 거리여서 path가 2개 이하일 때 처리
            switch (pathPoint.size()) {
                case 0:
                    Log.d("path", "path not found");
                    break;
                case 1:
                    pathDirection.add("destination");
                    break;
                case 2:
                    pathDirection.add("way");
                    pathDirection.add("destination");
            }
        }
        
        // 나중에 거리 받아올 때는 현위치에서 다음 방향까지 dijkstra 리턴값에 거리 보정값을 곱하고 나서 띄우기.

        
        
        
    }

    // set path on each floor to draw
    public void setPathOnEachFloor() {

    }


}
