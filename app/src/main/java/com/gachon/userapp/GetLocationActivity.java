package com.gachon.userapp;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class GetLocationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_location);
        ActivityStatusChecker.setActivityStatus(true);

        imageView = findViewById(R.id.imageView);
        textView_RP = findViewById(R.id.textView_RP);
        button_Retry = findViewById(R.id.button_Retry);
        button_Right = findViewById(R.id.button_Right);
        button_Wrong = findViewById(R.id.button_Wrong);

        // TODO: activity 불러 오자 마자 바로 wifi scan 후 현위치 결과 뱉기

        // 이후 RP textView 바꾸기. rp를 받아오면 place로 바꿔서 알려주기
        String RP = "~~";    // ex
        textView_RP.setText(RP);
        // 받아와서 imageView도 그 층에 맞는 걸로 바꿔주기
        // 그 후 rp에 따른 좌표에 맵핀을 표시해주기
        // rp는 저장해서 intent로 넘겨줘야 함


        wifiScanner = new WifiScanner(this, (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE), new ArrayList<>());
        this.registerReceiver(wifiScanner.getWifiReceiver(), new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiScanner.requestPermissions();
        wifiScanner.scanWifi();


        // 현 위치 다시 인식하는 버튼
        button_Retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 위에서 처음 불러올 때 한 거 똑같이 하기
            }
        });

        // That's right 버튼 -> 목적지 입력 화면으로
        button_Right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 목적지 입력 받는 activity로 넘어가기
//                Intent intent = new Intent(GetLocationActivity.this, SelectDestinationActivity.class);
//                startActivity(intent);
//                finish();   // To prevent confusing
            }
        });

        // That's wrong 버튼 -> 현 위치 선택 화면으로
        button_Wrong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 현 위치를 유저에게 직접 입력 받는 화면으로 넘어가기
                Intent intent = new Intent(GetLocationActivity.this, SelectLocationActivity.class);
                startActivity(intent);
                finish();   // To prevent confusing
            }
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        // Set activity status
        ActivityStatusChecker.setActivityStatus(false);
        Toast.makeText(this, "onPause 호출됨", Toast.LENGTH_SHORT).show();
    }

    public void set_rpValue(String rpValue) {
        this.rpValue = rpValue;
        Log.d("제발제발제발제발", this.rpValue);
    }
    // declaration
    private ImageView imageView;
    private TextView textView_RP;
    private Button button_Retry;
    private Button button_Right;
    private Button button_Wrong;
    private WifiScanner wifiScanner;
    public static String rpValue;

}