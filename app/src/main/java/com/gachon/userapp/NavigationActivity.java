package com.gachon.userapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;

public class NavigationActivity extends AppCompatActivity {

    final int CURRENT_PIN_SIZE_HALF = 6;
    RP rp = new RP();
    ArrayList<Integer> pathIndex;
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
                pathIndex = rp.getPath();   // path arraylist 받아오기
                canvasView = new CanvasView(getApplicationContext(), pathIndex, view_scale);
                canvasViewFrame.addView(canvasView);

                // add map pins
                int x_c = rp.getRpList().get(rp.rpToIndex(currentRP)).getX();
                int y_c = rp.getRpList().get(rp.rpToIndex(currentRP)).getY();
                int x_d = rp.getRpList().get(rp.rpToIndex(destinationRP)).getX();
                int y_d = rp.getRpList().get(rp.rpToIndex(destinationRP)).getY();

                // 맵핀 위치 바꾸기
                currentLocationPin.setVisibility(View.VISIBLE);
                currentLocationPin.setX((x_c / view_scale - CURRENT_PIN_SIZE_HALF) * density);
                currentLocationPin.setY((y_c / view_scale - CURRENT_PIN_SIZE_HALF) * density);
                destinationPin.setVisibility(View.VISIBLE);
                destinationPin.setX((x_d / view_scale - CURRENT_PIN_SIZE_HALF) * density);
                destinationPin.setY((y_d / view_scale - 11) * density);
            }
        }, 200);// 0.2초 딜레이를 준 후 시작




    }


    // declaration

    private ImageView imageView;
    private FrameLayout canvasViewFrame;
    private CanvasView canvasView;
    private ImageView currentLocationPin;
    private ImageView destinationPin;
}