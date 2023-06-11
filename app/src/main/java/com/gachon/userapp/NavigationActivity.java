package com.gachon.userapp;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class NavigationActivity extends AppCompatActivity {

    final int CURRENT_PIN_SIZE_HALF = 7;
    final int DESTINATION_PIN_SIZE_HALF = 8;
    final double CALIBRATION = 0.049;
    RP rp = new RP();
    float view_scale;
    float density;
    String currentPlace;
    String currentRP;
    String destinationPlace;
    String destinationRP;
    ArrayList<Integer> pathIndex = new ArrayList<>();
    ArrayList<String> pathDirection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        imageView = findViewById(R.id.imageView);
        canvasViewFrame = findViewById(R.id.canvasViewFrame);
        currentLocationPin = findViewById(R.id.currentLocationPin);
        destinationPin = findViewById(R.id.destinationPin);
        radioButton_4F = findViewById(R.id.radioButton_4F);
        radioButton_5F = findViewById(R.id.radioButton_5F);
        radioGroup = findViewById(R.id.radioGroup);
        textView_Current = findViewById(R.id.textView_Current);
        textView_Destination = findViewById(R.id.textView_Destination);
        imageView_direction = findViewById(R.id.imageView_direction);
        textView_LeftToChangePoint = findViewById(R.id.textView_LeftToChangePoint);
        textView_Direction = findViewById(R.id.textView_Direction);
        textView_ChangePointPlace = findViewById(R.id.textView_ChangePointPlace);
        textView_LeftToDestination = findViewById(R.id.textView_LeftToDestination);
        button_BackToMain = findViewById(R.id.button_BackToMain);

        // Get Intent (rp of current location)
        Intent intent = getIntent();
        currentPlace = intent.getStringExtra("current_place");
        currentRP = intent.getStringExtra("current_rp");
        destinationPlace = intent.getStringExtra("destination_place");
        destinationRP = intent.getStringExtra("destination_rp");

        // index로 바꿔서 dikstra 넘기기
//        if () {}  // destinationPlace가 nearby 어쩌구면 할 처리를 앞에 두기
        rp.dijkstra(rp.rpToIndex(currentRP), rp.rpToIndex(destinationRP));
        rp.setDirectionList();  // 방향 arrayList 생성
        rp.setPathOnEachFloor();    // 층별 path의 좌표 arrayList 생성
        for (int i = 0; i < rp.getPathIndex().size(); i++) {
            pathIndex.add(rp.getPathIndex().get(i));    // pathIndex 받아두기 (앞으로 변경 x)
        }

        // density와 view_scale 세팅
        density = getResources().getDisplayMetrics().density;
        imageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                imageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                view_scale = 1200 / (imageView.getWidth() / density);    // 1200은 이미지 원본 width
            }
        });

        new Handler().postDelayed(new Runnable()    // view_scale을 받아온 후에 그 뒤 실행
        {
            @Override
            public void run()
            {
                // add canvas view
                ArrayList<Point> pathOfFloor4 = rp.getPathOfFloor4();   // 4층 path arraylist 받아오기
                ArrayList<Point> pathOfFloor5 = rp.getPathOfFloor5();   // 5층 path arraylist 받아오기
                canvasView = new CanvasView(getApplicationContext(), view_scale, pathOfFloor4, pathOfFloor5);
                canvasViewFrame.addView(canvasView);

                //라디오 그룹 클릭 리스너 (각 층에 있는 맵핀과 path만 보이게)
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                        if(i == R.id.radioButton_4F){
                            imageView.setImageResource(R.drawable.floor4);
                            if (currentRP.startsWith("4")) { currentLocationPin.setVisibility(View.VISIBLE); }
                            else { currentLocationPin.setVisibility(View.INVISIBLE); }
                            if (destinationRP.startsWith("4")) { destinationPin.setVisibility(View.VISIBLE); }
                            else { destinationPin.setVisibility(View.INVISIBLE); }
                            canvasView.isFloor4 = true;
                            canvasView.invalidate();
                        }
                        else if(i == R.id.radioButton_5F){
                            imageView.setImageResource(R.drawable.floor5);
                            if (currentRP.startsWith("5")) { currentLocationPin.setVisibility(View.VISIBLE); }
                            else { currentLocationPin.setVisibility(View.INVISIBLE); }
                            if (destinationRP.startsWith("5")) { destinationPin.setVisibility(View.VISIBLE); }
                            else { destinationPin.setVisibility(View.INVISIBLE); }
                            canvasView.isFloor4 = false;
                            canvasView.invalidate();
                        }
                    }
                });

                // set textView
                textView_Current.setText(currentPlace);
                textView_Destination.setText(destinationPlace);

                // add map pins
                int x_c = rp.getRpList().get(rp.rpToIndex(currentRP)).getX();
                int y_c = rp.getRpList().get(rp.rpToIndex(currentRP)).getY();
                int x_d = rp.getRpList().get(rp.rpToIndex(destinationRP)).getX();
                int y_d = rp.getRpList().get(rp.rpToIndex(destinationRP)).getY();

                // 맵핀 위치 초기 세팅 (5초마다 업데이트할 때도 여기 코드 가져다 쓰기)
                currentLocationPin.setVisibility(View.VISIBLE);
                currentLocationPin.setX((x_c / view_scale - CURRENT_PIN_SIZE_HALF) * density);
                currentLocationPin.setY((y_c / view_scale - CURRENT_PIN_SIZE_HALF) * density);
                destinationPin.setVisibility(View.VISIBLE);
                destinationPin.setX((x_d / view_scale - DESTINATION_PIN_SIZE_HALF) * density);
                destinationPin.setY((y_d / view_scale - DESTINATION_PIN_SIZE_HALF*2) * density);

                // current location이 있는 층으로 spinner랑 radioButton 초기화
                if (currentRP.startsWith("4")) {
                    radioButton_4F.setChecked(true);
                }
                else {
                    radioButton_5F.setChecked(true);
                }
            }
        }, 200);// 0.2초 딜레이를 준 후 시작 (imageView의 width를 먼저 받아오기 위해)

        

        // 방향 안내 코드 (얘도 현위치 받아올 때마다 체크할 것에 포함됨)
        
        pathDirection = rp.getPathDirection();  // 여기서 한번만 받아 오기
        // 현위치의 path에서의 순서(index)를 알아내기
        int nowIndex = -1;   // 처음에는 당연히 0이지만 매번 받아올 때를 기준으로 코드 짬
        for (int i = 0; i < pathIndex.size(); i++) {    // 이건 nowIndex가 0일땐 안써도 되는데 5초마다 받아올땐 써야함
            if (pathIndex.get(i) == rp.rpToIndex(currentRP)) { nowIndex = i; }
        }

        if (nowIndex == -1) {}  // 아예 path에 없는 rp가 현위치로 잡히면, 현위치마커(핀)만 바꾸고 방향 안내는 바꾸지 말기
        else if (nowIndex == pathIndex.size() - 1) { // 목적지에 도착했다면
            imageView_direction.setImageResource(R.drawable.d_destination);
            textView_LeftToChangePoint.setText("Arrived");
            textView_Direction.setText("at " + destinationPlace);
            textView_ChangePointPlace.setText("");
            
            // 남은 거리 textView를 안보이게 하고, BackToMain 버튼을 보이게 하기
            textView_LeftToDestination.setVisibility(View.GONE);
            button_BackToMain.setVisibility(View.VISIBLE);
        }
        else {
            // 현위치 바로 다음에 있는 left/right/endOfFloor/elevator/destination 값에 따라 UI 변경
            String nowDirection = "";   // 받아올 string
            int nowDirectionIndex = -1;
            for (int i = nowIndex + 1; i < pathDirection.size(); i++) {
                if (!pathDirection.get(i).equals("way")) {  // way가 아니라 특정값이라면
                    nowDirection = pathDirection.get(i);
                    nowDirectionIndex = i;
                    break;
                }
            }
            // nowDirectionIndex로 거기 place 받아오기
            String changePointPlace = rp.getRpList().get(pathIndex.get(nowDirectionIndex)).getPlace();

            // changePoint 까지의 거리를 dijkstra로 받고 보정값 곱하기 (소수점은 올림)
            int weightToChangePoint = (int) (Math.ceil(rp.dijkstra(rp.rpToIndex(currentRP), pathIndex.get(nowDirectionIndex))) * CALIBRATION);
            // 목적지까지의 거리를 dijkstra로 받고 보정값 곱하기
            int weightToDestination = (int) (Math.ceil(rp.dijkstra(rp.rpToIndex(currentRP), rp.rpToIndex(destinationRP))) * CALIBRATION);
            textView_LeftToDestination.setText(weightToDestination + "m");  // 목적지까지 거리 먼저 세팅

            // 혹시 모를 visability 세팅
            // 남은 거리 textView를 보이게 하고, BackToMain 버튼을 안보이게 하기
            textView_LeftToDestination.setVisibility(View.VISIBLE);
            button_BackToMain.setVisibility(View.GONE);
            
            // case에 따라 처리
            switch (nowDirection) {
                case "left":    // 좌회전
                    imageView_direction.setImageResource(R.drawable.d_left);
                    textView_LeftToChangePoint.setText(weightToChangePoint + "m");
                    textView_Direction.setText("Turn left at ");
                    textView_ChangePointPlace.setText(changePointPlace);    // ~에서 좌회전. 이런 느낌
                    break;

                case "right":   // 우회전
                    imageView_direction.setImageResource(R.drawable.d_right);
                    textView_LeftToChangePoint.setText(weightToChangePoint + "m");
                    textView_Direction.setText("Turn right at ");
                    textView_ChangePointPlace.setText(changePointPlace);
                    break;

                case "endOfFloor" : // 직진
                case "destination": // 직진
                    imageView_direction.setImageResource(R.drawable.d_straight);
                    textView_LeftToChangePoint.setText(weightToChangePoint + "m");
                    textView_Direction.setText("Go straight to ");
                    textView_ChangePointPlace.setText(changePointPlace);
                    break;

                case "elevator":    // 층이 바뀜
                    imageView_direction.setImageResource(R.drawable.d_stair);
                    // 층을 올라가는지 내려가는지 판별
                    if (pathIndex.get(nowDirectionIndex) < 49) {    // 4층으로 갈 때
                        textView_LeftToChangePoint.setText("Go down");
                        textView_Direction.setText("to the 4th floor");
                        textView_ChangePointPlace.setText("");
                    }
                    else {  // 5층으로 갈 떄
                        textView_LeftToChangePoint.setText("Go up");
                        textView_Direction.setText("to the 5th floor");
                        textView_ChangePointPlace.setText("");
                        break;
                    }
            }
        }

        button_BackToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // main 화면으로 이동
                Intent intent = new Intent(NavigationActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }


    // declaration

    private ImageView imageView;
    private FrameLayout canvasViewFrame;
    private CanvasView canvasView;
    private ImageView currentLocationPin;
    private ImageView destinationPin;
    private RadioGroup radioGroup;
    private RadioButton radioButton_4F;
    private RadioButton radioButton_5F;
    private TextView textView_Current;
    private TextView textView_Destination;
    private ImageView imageView_direction;
    private TextView textView_LeftToChangePoint;
    private TextView textView_Direction;
    private TextView textView_ChangePointPlace;
    private TextView textView_LeftToDestination;
    private Button button_BackToMain;

}