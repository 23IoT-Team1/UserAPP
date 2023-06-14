package com.gachon.userapp;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class GetLocationActivity extends AppCompatActivity {

    final int CURRENT_PIN_SIZE_HALF = 6;
    float density;
    float view_scale;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_location);
        ActivityStatusChecker.setActivityStatus(true);
        btnclick = false;
        imageView = findViewById(R.id.imageView);
        textView_RP = findViewById(R.id.textView_RP);
        button_Retry = findViewById(R.id.button_Retry);
        button_Right = findViewById(R.id.button_Right);
        button_Wrong = findViewById(R.id.button_Wrong);
        currentLocationPin = findViewById(R.id.currentLocationPin);

        density = getResources().getDisplayMetrics().density;
        // 조정된 scale 값 받아 오기 (좌표 맞추기 위해)
        imageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                imageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                view_scale = 1200 / (imageView.getWidth() / density);    // 1200은 이미지 원본 width
            }
        });

        // activity 불러 오자 마자 바로 wifi scan 후 현위치 결과 받아 오기
        wifiScanner = new WifiScanner(this, (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE), new ArrayList<>());
        wifiScanner.requestPermissions();

        // 현 위치 다시 인식하는 버튼
        button_Retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 위에서 처음 불러올 때 한 거 똑같이 하기
                btnclick = false;
                wifiScanner.scanWifi();
            }
        });

        // That's right 버튼 -> 목적지 입력 화면으로

        button_Right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnclick) {
                    wifiScanner.stopWifiScan();
                    // place, rp, view_scale 넘겨주면서, 목적지 입력 받는 activity로 이동
                    Intent intent = new Intent(GetLocationActivity.this, SelectDestinationActivity.class);
                    intent.putExtra("current_place", placeValue);
                    intent.putExtra("current_rp", rpValue);
                    intent.putExtra("view_scale", view_scale);
                    startActivity(intent);
                    finish();
                }
            }
        });

        // That's wrong 버튼 -> 현 위치 선택 화면으로
        button_Wrong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnclick){
                    wifiScanner.stopWifiScan();
                    // 현 위치를 유저에게 직접 입력 받는 화면으로 넘어가기
                    Intent intent = new Intent(GetLocationActivity.this, SelectLocationActivity.class);
                    startActivity(intent);
                    finish();   // To prevent confusing
                }

            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        this.registerReceiver(wifiScanner.getWifiReceiver(), new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiScanner.scanWifi();
    }
    @Override
    protected void onPause() {
        super.onPause();
        // Set activity status
        ActivityStatusChecker.setActivityStatus(false);

    }

    public void set_rpValue(String rpValue) {
        this.rpValue = rpValue;
        Log.d("GLA에 rpValue 세팅", this.rpValue);
        placeValue = rp.rpToPlace(rpValue);
        textView_RP.setText(placeValue);

        // 받아와서 imageView도 그 층에 맞는 걸로 바꿔주기
        if(rpValue.startsWith("4")) {
            imageView.setImageResource(R.drawable.floor4);
        }
        else if(rpValue.startsWith("5")) {
            imageView.setImageResource(R.drawable.floor5);
        }
        // 그 rp의 좌표에 맵핀을 표시
        int x = rp.getRpList().get(rp.rpToIndex(rpValue)).getX();
        int y = rp.getRpList().get(rp.rpToIndex(rpValue)).getY();

        // 맵핀 위치 바꾸기
        currentLocationPin.setVisibility(View.VISIBLE);
        currentLocationPin.setX((x / view_scale - CURRENT_PIN_SIZE_HALF) * density);
        currentLocationPin.setY((y / view_scale - CURRENT_PIN_SIZE_HALF) * density);


    }


    // declaration
    private ImageView imageView;
    private ImageView currentLocationPin;
    private TextView textView_RP;
    private Button button_Retry;
    private Button button_Right;
    private Button button_Wrong;
    private WifiScanner wifiScanner;
    public String rpValue;
    public String placeValue;
    RP rp = new RP();
    public static boolean btnclick;
}