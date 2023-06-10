package com.gachon.userapp;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class NavigationActivity extends AppCompatActivity {

    final int CURRENT_PIN_SIZE_HALF = 6;
    RP rp = new RP();
    float view_scale;
    float density;
    String currentPlace;
    String currentRP;
    String destinationPlace;
    String destinationRP;

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

        // Get Intent (rp of current location)
        Intent intent = getIntent();
        currentPlace = intent.getStringExtra("current_place");
        currentRP = intent.getStringExtra("current_rp");
        destinationPlace = intent.getStringExtra("destination_place");
        destinationRP = intent.getStringExtra("destination_rp");

        // index로 바꿔서 dikstra 넘기기
//        if () {}  // destinationPlace가 nearby 어쩌구면 할 처리를 앞에 두기
        rp.dijkstra(rp.rpToIndex(currentRP), rp.rpToIndex(destinationRP));

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
                destinationPin.setX((x_d / view_scale - CURRENT_PIN_SIZE_HALF) * density);
                destinationPin.setY((y_d / view_scale - 13) * density);

                // current location이 있는 층으로 spinner랑 radioButton 초기화
                if (currentRP.startsWith("4")) {
                    radioButton_4F.setChecked(true);
                }
                else {
                    radioButton_5F.setChecked(true);
                }
            }
        }, 200);// 0.2초 딜레이를 준 후 시작




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

}