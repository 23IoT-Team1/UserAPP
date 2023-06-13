package com.gachon.userapp;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class NavigationActivity extends AppCompatActivity implements SensorEventListener {

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
        pointerContainer =findViewById(R.id.pointerContainer);
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
        layout_LeftToDestination = findViewById(R.id.layout_LeftToDestination);
        button_BackToMain = findViewById(R.id.button_BackToMain);



        //Sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        magnetormeter = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelermeter = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


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
                ArrayList<Point> pathPoint = rp.getPathPoint();   // path의 좌표 arraylist
                int startFloor = rp.getStartFloor();    // path가 시작하는 층
                ArrayList<Integer> pointIndexOf4 = rp.getPointIndexOfFloor4();  // 4층 path의 꼭짓점들
                ArrayList<Integer> pointIndexOf5 = rp.getPointIndexOfFloor5();  // 5층 path의 꼭짓점들
                canvasView = new CanvasView(getApplicationContext(), view_scale, pathPoint, startFloor,
                        pointIndexOf4, pointIndexOf5);
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
                int x_c = RP.getRpList().get(rp.rpToIndex(currentRP)).getX();
                int y_c = RP.getRpList().get(rp.rpToIndex(currentRP)).getY();
                int x_d = RP.getRpList().get(rp.rpToIndex(destinationRP)).getX();
                int y_d = RP.getRpList().get(rp.rpToIndex(destinationRP)).getY();

                // 맵핀 위치 초기 세팅 (5초마다 업데이트할 때도 여기 코드 가져다 쓰기)
                currentLocationPin.setVisibility(View.VISIBLE);
                currentLocationPin.setX((x_c / view_scale - CURRENT_PIN_SIZE_HALF) * density);
                currentLocationPin.setY((y_c / view_scale - CURRENT_PIN_SIZE_HALF) * density);
                destinationPin.setVisibility(View.VISIBLE);
                destinationPin.setX((x_d / view_scale - DESTINATION_PIN_SIZE_HALF) * density);
                destinationPin.setY((y_d / view_scale - DESTINATION_PIN_SIZE_HALF*2) * density);
                //pointer animation
                pivotX = (x_c / view_scale - CURRENT_PIN_SIZE_HALF) * density ;
                pivotY = (y_c / view_scale - CURRENT_PIN_SIZE_HALF) * density ;

                rotateAnimationHelper = new RotateAnimationHelper(currentLocationPin, imageView);
                rotateAnimationHelper.initialize(pivotX,pivotY);
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
            
            // 남은 거리 textView(layout)를 안보이게 하고, BackToMain 버튼을 보이게 하기
            layout_LeftToDestination.setVisibility(View.GONE);
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
            String changePointPlace = RP.getRpList().get(pathIndex.get(nowDirectionIndex)).getPlace();

            // changePoint 까지의 거리를 dijkstra로 받고 보정값 곱하기 (소수점은 올림)
            int weightToChangePoint = (int) (Math.ceil(rp.dijkstra(rp.rpToIndex(currentRP), pathIndex.get(nowDirectionIndex))) * CALIBRATION);
            // 목적지까지의 거리를 dijkstra로 받고 보정값 곱하기
            int weightToDestination = (int) (Math.ceil(rp.dijkstra(rp.rpToIndex(currentRP), rp.rpToIndex(destinationRP))) * CALIBRATION);
            textView_LeftToDestination.setText(weightToDestination + "m");  // 목적지까지 거리 먼저 세팅

            // 혹시 모를 visability 세팅
            // 남은 거리 textView(layout)를 보이게 하고, BackToMain 버튼을 안보이게 하기
            layout_LeftToDestination.setVisibility(View.VISIBLE);
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
                    }
                    break;
            }
        }

        button_BackToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // rp에 있는 arraylist들 초기화
                rp.clearArrayLists();

                // main 화면으로 이동
                Intent intent = new Intent(NavigationActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Initialize the WifiScanner instance
        wifiScanner = new WifiScanner(this, (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE), new ArrayList<>());
        // Create a handler to schedule the Wi-Fi scan periodically
        handler = new Handler();

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //stepCounter
        /*if (event.sensor == stepSensor) {
                stepCounter++;
                txtSteps.setText(String.valueOf(stepCounter) + "\n" + String.valueOf((float) (stepCounter * 0.6)) + " m");
        }
        else */

        if (event.sensor == magnetormeter) {

            //System.arraycopy(event.values, 0, mLastMagnetormeter, 0, 2);
            mLastMagnetormeter[0] = event.values[0];
            mLastMagnetormeter[1] = event.values[1];
            mLastMagnetormeter[2] = event.values[2];
            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetormeter);

            mCurrentDegree = (int) (Math.toDegrees(SensorManager.getOrientation(mR, mOrientation)[0])+85) % 360;
            azimuthunDegress = (int) (Math.toDegrees(SensorManager.getOrientation(mR, mOrientation)[0]) + 360) % 360;

            //rotate
            rotateAnimationHelper.rotate(mCurrentDegree, -azimuthunDegress, 1000);
            mCurrentDegree = -azimuthunDegress;
        } else if (event.sensor == accelermeter) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    @Override
    protected void onStop() {
        super.onStop();

        // Stop the timer when the activity is stopped
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
    public void set_rpValue(String rpValue) {
        this.rpValue = rpValue;
        Log.d("제발제발제발제발이건 네비게이션네비게이션", this.rpValue);
    }
    // declaration
    private void startWifiScan() {
        // Schedule the Wi-Fi scan periodically
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start the Wi-Fi scan
                wifiScanner.scanWifi();

                // Schedule the next Wi-Fi scan after the specified interval
                handler.postDelayed(this, 5000);
            }
        }, 5000);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        // Start the periodic Wi-Fi scanning
        startWifiScan();
        //Start Sensors
        sensorManager.registerListener(this, accelermeter, sensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, magnetormeter, sensorManager.SENSOR_DELAY_FASTEST);

    }

    @Override
    protected void onPause()
    {
        super.onPause();
        sensorManager.unregisterListener(this, accelermeter);
        sensorManager.unregisterListener(this, magnetormeter);

        wifiScanner.stopWifiScan();
    }
    private  FrameLayout pointerContainer;
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
    private LinearLayout layout_LeftToDestination;
    private Button button_BackToMain;
    private Timer timer;
    public static String rpValue;
    private Handler handler;
    private WifiScanner wifiScanner;
    //Sensor 관련 선언들
    private SensorManager sensorManager;
    private Sensor magnetormeter,accelermeter;
    private float[] mR = new float[9];
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetormeter = new float[3];
    private float[] mOrientation = new float[3];
    private static float mCurrentDegree = 0f;
    private static float azimuthunDegress= 0f;
    private RotateAnimationHelper rotateAnimationHelper;
    private float pivotX,pivotY;
}