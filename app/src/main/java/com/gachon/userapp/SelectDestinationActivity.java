package com.gachon.userapp;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

public class SelectDestinationActivity extends AppCompatActivity {

    final int CURRENT_PIN_SIZE_HALF = 6;
    RP rp = new RP();
    float view_scale;
    String currentPlace;
    String currentRP;
    String destinationPlace;
    String destinationRP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_destination);

        spinner_Floor = findViewById(R.id.spinner_Floor);
        spinner_RP = findViewById(R.id.spinner_RP);
        radioButton_4F = findViewById(R.id.radioButton_4F);
        radioButton_5F = findViewById(R.id.radioButton_5F);
        radioGroup = findViewById(R.id.radioGroup);
        imageView = findViewById(R.id.imageView);
        currentLocationPin = findViewById(R.id.currentLocationPin);
        destinationPin = findViewById(R.id.destinationPin);
        textView_Current = findViewById(R.id.textView_Current);
        textView_Destination = findViewById(R.id.textView_Destination);
        button_Navigate = findViewById(R.id.button_Navigate);
        button_nearby_man = findViewById(R.id.button_nearby_man);
        button_nearby_woman = findViewById(R.id.button_nearby_woman);
        button_nearby_stair = findViewById(R.id.button_nearby_stair);
        button_nearby_elev = findViewById(R.id.button_nearby_elev);
        imageView_NearbyIcon = findViewById(R.id.imageView_NearbyIcon);
        float density = getResources().getDisplayMetrics().density;

        // Get Intent (rp of current location)
        Intent intent = getIntent();
        currentPlace = intent.getStringExtra("current_place");
        currentRP = intent.getStringExtra("current_rp");
        view_scale = intent.getFloatExtra("view_scale", 0.0f);
//        System.out.println("viewScale " + view_scale);
        // current location 관련 정보 초기화
        textView_Current.setText(currentPlace);
        for (int i = 0; i < RP.getRpList().size(); i++) {
            if (RP.getRpList().get(i).getRp().equals(currentRP)) {
                int x = RP.getRpList().get(i).getX();
                int y = RP.getRpList().get(i).getY();
                // 맵핀 위치 바꾸기
                currentLocationPin.setVisibility(View.VISIBLE);
                currentLocationPin.setX((x / view_scale - CURRENT_PIN_SIZE_HALF) * density);
                currentLocationPin.setY((y / view_scale - CURRENT_PIN_SIZE_HALF) * density);
            }
        }

        // floor 선택 spinner 세팅
        floorAdapter = ArrayAdapter.createFromResource(this, R.array.floors, android.R.layout.simple_spinner_item);
        floorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_Floor.setAdapter(floorAdapter);

        // RP 선택 spinner 세팅
        rp4Adapter = ArrayAdapter.createFromResource(this, R.array.user_4F, android.R.layout.simple_spinner_item);
        rp4Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rp5Adapter = ArrayAdapter.createFromResource(this, R.array.user_5F, android.R.layout.simple_spinner_item);
        rp5Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // current location이 있는 층으로 spinner랑 radioButton 초기화
        if (currentRP.startsWith("4")) {
            radioButton_4F.setChecked(true);
            spinner_Floor.setSelection(0);
        }
        else {
            radioButton_5F.setChecked(true);
            spinner_Floor.setSelection(1);
        }

        // floor를 선택하면, 옆의 spinner에 그 층의 RP 리스트를 띄움
        spinner_Floor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) {
                    spinner_RP.setAdapter(rp4Adapter);
                    imageView.setImageResource(R.drawable.floor4);
                    radioButton_4F.setChecked(true);
                }
                else if(position == 1) {
                    spinner_RP.setAdapter(rp5Adapter);
                    imageView.setImageResource(R.drawable.floor5);
                    radioButton_5F.setChecked(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        // RP 선택 spinner
        spinner_RP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 선택한 string 값 받아오기
                destinationPlace = spinner_RP.getSelectedItem().toString();
                textView_Destination.setText(destinationPlace);  // 텍스트뷰에 세팅

                // nearby 버튼이 선택되어 있었다면 pressed UI를 원래대로 돌리기
                button_nearby_man.setBackground(getDrawable(R.drawable.small_button_background));
                button_nearby_woman.setBackground(getDrawable(R.drawable.small_button_background));
                button_nearby_stair.setBackground(getDrawable(R.drawable.small_button_background));
                button_nearby_elev.setBackground(getDrawable(R.drawable.small_button_background));
                imageView_NearbyIcon.setVisibility(View.INVISIBLE); // nearby icon 숨기기

                String tempRP = "";
                String tempFloor = spinner_Floor.getSelectedItem().toString();
                // 받아 온 place 값을 rp랑 매칭
                switch (destinationPlace) {
                    case "405호 앞":
                        tempRP = "4_27";
                        break;
                    case "505호 앞":
                        tempRP = "5_27";
                        break;
                    case "512호 앞":
                        tempRP = "5_16";
                        break;
                    case "복정 방향 엘리베이터 앞":
                        if (tempFloor.equals("4F")) { tempRP = "4_43"; }
                        else if (tempFloor.equals("5F")) { tempRP = "5_40"; }
                        break;
                    case "중간 엘리베이터 앞":
                        if (tempFloor.equals("4F")) { tempRP = "4_44"; }
                        else if (tempFloor.equals("5F")) { tempRP = "5_41"; }
                        break;
                    case "운동장 방향 엘리베이터 옆 복도":
                        if (tempFloor.equals("4F")) { tempRP = "4_48"; }
                        else if (tempFloor.equals("5F")) { tempRP = "5_45"; }
                        break;
                    case "운동장 방향 엘리베이터 앞":
                        if (tempFloor.equals("4F")) { tempRP = "4_49"; }
                        else if (tempFloor.equals("5F")) { tempRP = "5_46"; }
                        break;
                    default:
                        for (int i = 0; i < RP.getRpList().size(); i++) {
                            if (RP.getRpList().get(i).getPlace().equals(destinationPlace)) {
                                tempRP = RP.getRpList().get(i).getRp();
                            }
                        }
                        break;
                }
                destinationRP = tempRP; // 구한 RP를 저장

                int x = 0;
                int y = 0;
                // destination맵핀 표시를 위한 좌표 받아오기
                for (int i = 0; i < RP.getRpList().size(); i++) {
                    if (RP.getRpList().get(i).getRp().equals(tempRP)) {
                        x = RP.getRpList().get(i).getX();
                        y = RP.getRpList().get(i).getY();
                    }
                }

                // 맵핀 위치 바꾸기
                destinationPin.setVisibility(View.VISIBLE);
                destinationPin.setX((x / view_scale - CURRENT_PIN_SIZE_HALF) * density);
                destinationPin.setY((y / view_scale - 15) * density);
            }



            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                textView_Destination.setText("");
            }
        });

        //라디오 그룹 클릭 리스너 (각 층에 있는 핀만 보이게)
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if(i == R.id.radioButton_4F){
                    imageView.setImageResource(R.drawable.floor4);
                    if (currentRP.startsWith("4")) { currentLocationPin.setVisibility(View.VISIBLE); }
                    else { currentLocationPin.setVisibility(View.INVISIBLE); }
                    if (destinationRP.startsWith("4")) { destinationPin.setVisibility(View.VISIBLE); }
                    else { destinationPin.setVisibility(View.INVISIBLE); }
                }
                else if(i == R.id.radioButton_5F){
                    imageView.setImageResource(R.drawable.floor5);
                    if (currentRP.startsWith("5")) { currentLocationPin.setVisibility(View.VISIBLE); }
                    else { currentLocationPin.setVisibility(View.INVISIBLE); }
                    if (destinationRP.startsWith("5")) { destinationPin.setVisibility(View.VISIBLE); }
                    else { destinationPin.setVisibility(View.INVISIBLE); }
                }
            }
        });

        // navigate 시작 버튼
        button_Navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 출발지&목적지의 place, rp 넘겨주면서, navigation activity로 이동
                Intent intent = new Intent(SelectDestinationActivity.this, NavigationActivity.class);
                intent.putExtra("current_place", currentPlace);
                intent.putExtra("current_rp", currentRP);
                intent.putExtra("destination_place", destinationPlace);
                intent.putExtra("destination_rp", destinationRP);
                startActivity(intent);
                finish();
            }
        });

        // 가장 가까운 남자화장실로 목적지를 설정하는 버튼
        button_nearby_man.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                textView_Destination.setText("nearby");  // 텍스트뷰에 세팅
                imageView_NearbyIcon.setImageResource(R.drawable.nearby_man);
                imageView_NearbyIcon.setVisibility(View.VISIBLE); // nearby icon 보이게 하기

                // 가장 가까운 목적지 찾아서 destination rp, place에 저장
                int[] man = {3, 19, 51, 68};
                int minIndex = 0;
                double min = Double.POSITIVE_INFINITY;
                for (int i : man) {
                    double distance = rp.dijkstra(rp.rpToIndex(currentRP), i);
                    if (distance < min) {
                        min = distance;
                        minIndex = i;
                    }
                }
                destinationPlace = RP.getRpList().get(minIndex).getPlace();
                destinationRP = RP.getRpList().get(minIndex).getRp();

                // 목적지 맵핀 위치 변경
                int x = RP.getRpList().get(minIndex).getX();
                int y = RP.getRpList().get(minIndex).getY();
                destinationPin.setX((x / view_scale - CURRENT_PIN_SIZE_HALF) * density);
                destinationPin.setY((y / view_scale - 15) * density);

                // 보이는 층 바뀌게
                if ((spinner_Floor.getSelectedItem().equals("4F") || radioButton_4F.isChecked()) &&
                        ((minIndex >= 49 && minIndex < 95) || minIndex == 98 || minIndex == 99 )) {
                    radioButton_5F.setChecked(true);
                }
                else if ((spinner_Floor.getSelectedItem().equals("5F") || radioButton_5F.isChecked()) &&
                        (minIndex < 49) || minIndex == 95 || minIndex == 96 || minIndex == 97) {
                    radioButton_4F.setChecked(true);
                }

                // 버튼 선택된 게 티나도록 UI 설정
                button_nearby_man.setBackground(getDrawable(R.drawable.small_button_pressed));
                button_nearby_woman.setBackground(getDrawable(R.drawable.small_button_background));
                button_nearby_stair.setBackground(getDrawable(R.drawable.small_button_background));
                button_nearby_elev.setBackground(getDrawable(R.drawable.small_button_background));
            }
        });

        // 가장 가까운 여자화장실로 목적지를 설정하는 버튼
        button_nearby_woman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                textView_Destination.setText("nearby");  // 텍스트뷰에 세팅
                imageView_NearbyIcon.setImageResource(R.drawable.nearby_woman);
                imageView_NearbyIcon.setVisibility(View.VISIBLE); // nearby icon 보이게 하기

                // 가장 가까운 목적지 찾아서 destination rp, place에 저장
                int[] woman = {37, 20, 84, 69};
                int minIndex = 0;
                double min = Double.POSITIVE_INFINITY;
                for (int i : woman) {
                    double distance = rp.dijkstra(rp.rpToIndex(currentRP), i);
                    if (distance < min) {
                        min = distance;
                        minIndex = i;
                    }
                }
                destinationPlace = RP.getRpList().get(minIndex).getPlace();
                destinationRP = RP.getRpList().get(minIndex).getRp();

                // 목적지 맵핀 위치 변경
                int x = RP.getRpList().get(minIndex).getX();
                int y = RP.getRpList().get(minIndex).getY();
                destinationPin.setX((x / view_scale - CURRENT_PIN_SIZE_HALF) * density);
                destinationPin.setY((y / view_scale - 15) * density);

                // 보이는 층 바뀌게
                if ((spinner_Floor.getSelectedItem().equals("4F") || radioButton_4F.isChecked()) &&
                        ((minIndex >= 49 && minIndex < 95) || minIndex == 98 || minIndex == 99 )) {
                    radioButton_5F.setChecked(true);
                }
                else if ((spinner_Floor.getSelectedItem().equals("5F") || radioButton_5F.isChecked()) &&
                        (minIndex < 49) || minIndex == 95 || minIndex == 96 || minIndex == 97) {
                    radioButton_4F.setChecked(true);
                }

                // 버튼 선택된 게 티나도록 UI 설정
                button_nearby_woman.setBackground(getDrawable(R.drawable.small_button_pressed));
                button_nearby_man.setBackground(getDrawable(R.drawable.small_button_background));
                button_nearby_stair.setBackground(getDrawable(R.drawable.small_button_background));
                button_nearby_elev.setBackground(getDrawable(R.drawable.small_button_background));
            }
        });

        button_nearby_stair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                textView_Destination.setText("nearby ");  // 텍스트뷰에 세팅
                imageView_NearbyIcon.setImageResource(R.drawable.nearby_stair);
                imageView_NearbyIcon.setVisibility(View.VISIBLE); // nearby icon 보이게 하기

                // 가장 가까운 목적지 찾아서 destination rp, place에 저장
                int[] stair = {11, 17, 20, 31, 42, 57, 66, 69, 80, 88};
                int minIndex = 0;
                double min = Double.POSITIVE_INFINITY;
                for (int i : stair) {
                    double distance = rp.dijkstra(rp.rpToIndex(currentRP), i);
                    if (distance < min) {
                        min = distance;
                        minIndex = i;
                    }
                }
                destinationPlace = RP.getRpList().get(minIndex).getPlace();
                destinationRP = RP.getRpList().get(minIndex).getRp();

                // 목적지 맵핀 위치 변경
                int x = RP.getRpList().get(minIndex).getX();
                int y = RP.getRpList().get(minIndex).getY();
                destinationPin.setX((x / view_scale - CURRENT_PIN_SIZE_HALF) * density);
                destinationPin.setY((y / view_scale - 15) * density);

                // 보이는 층 바뀌게
                if ((spinner_Floor.getSelectedItem().equals("4F") || radioButton_4F.isChecked()) &&
                        ((minIndex >= 49 && minIndex < 95) || minIndex == 98 || minIndex == 99 )) {
                    radioButton_5F.setChecked(true);
                }
                else if ((spinner_Floor.getSelectedItem().equals("5F") || radioButton_5F.isChecked()) &&
                        (minIndex < 49) || minIndex == 95 || minIndex == 96 || minIndex == 97) {
                    radioButton_4F.setChecked(true);
                }

                // 버튼 선택된 게 티나도록 UI 설정
                button_nearby_stair.setBackground(getDrawable(R.drawable.small_button_pressed));
                button_nearby_man.setBackground(getDrawable(R.drawable.small_button_background));
                button_nearby_woman.setBackground(getDrawable(R.drawable.small_button_background));
                button_nearby_elev.setBackground(getDrawable(R.drawable.small_button_background));
            }
        });

        button_nearby_elev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                textView_Destination.setText("nearby ");  // 텍스트뷰에 세팅
                imageView_NearbyIcon.setImageResource(R.drawable.nearby_elev);
                imageView_NearbyIcon.setVisibility(View.VISIBLE); // nearby icon 보이게 하기

                // 가장 가까운 목적지 찾아서 destination rp, place에 저장
                int[] elev = {42, 43, 48, 88, 89, 94};
                int minIndex = 0;
                double min = Double.POSITIVE_INFINITY;
                for (int i : elev) {
                    double distance = rp.dijkstra(rp.rpToIndex(currentRP), i);
                    if (distance < min) {
                        min = distance;
                        minIndex = i;
                    }
                }
                destinationPlace = RP.getRpList().get(minIndex).getPlace();
                destinationRP = RP.getRpList().get(minIndex).getRp();

                // 목적지 맵핀 위치 변경
                int x = RP.getRpList().get(minIndex).getX();
                int y = RP.getRpList().get(minIndex).getY();
                destinationPin.setX((x / view_scale - CURRENT_PIN_SIZE_HALF) * density);
                destinationPin.setY((y / view_scale - 15) * density);

                // 보이는 층 바뀌게
                if ((spinner_Floor.getSelectedItem().equals("4F") || radioButton_4F.isChecked()) &&
                        ((minIndex >= 49 && minIndex < 95) || minIndex == 98 || minIndex == 99 )) {
                    radioButton_5F.setChecked(true);
                }
                else if ((spinner_Floor.getSelectedItem().equals("5F") || radioButton_5F.isChecked()) &&
                        (minIndex < 49) || minIndex == 95 || minIndex == 96 || minIndex == 97) {
                    radioButton_4F.setChecked(true);
                }

                // 버튼 선택된 게 티나도록 UI 설정
                button_nearby_elev.setBackground(getDrawable(R.drawable.small_button_pressed));
                button_nearby_man.setBackground(getDrawable(R.drawable.small_button_background));
                button_nearby_woman.setBackground(getDrawable(R.drawable.small_button_background));
                button_nearby_stair.setBackground(getDrawable(R.drawable.small_button_background));
            }
        });

    }

    // declaration
    private Spinner spinner_Floor;
    private Spinner spinner_RP;
    private RadioGroup radioGroup;
    private RadioButton radioButton_4F;
    private RadioButton radioButton_5F;
    private ImageView imageView;
    private ImageView currentLocationPin;
    private ImageView destinationPin;
    private TextView textView_Current;
    private TextView textView_Destination;
    private Button button_Navigate;

    private ArrayAdapter floorAdapter;
    private ArrayAdapter rp4Adapter;
    private ArrayAdapter rp5Adapter;

    private Button button_nearby_man;
    private Button button_nearby_woman;
    private Button button_nearby_stair;
    private Button button_nearby_elev;
    private ImageView imageView_NearbyIcon;
}