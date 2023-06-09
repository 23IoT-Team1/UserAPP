package com.gachon.userapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class SelectLocationActivity extends AppCompatActivity {
    final int CURRENT_PIN_SIZE_HALF = 6;
    RP rp = new RP();
    float view_scale;
    String currentRP;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);

        spinner_Floor = findViewById(R.id.spinner_Floor);
        spinner_RP = findViewById(R.id.spinner_RP);
        imageView = findViewById(R.id.imageView);
        textView_RP = findViewById(R.id.textView_RP);
        button_Next = findViewById(R.id.button_Next);
        currentLocationPin = findViewById(R.id.currentLocationPin);

        float density = getResources().getDisplayMetrics().density;
        // 조정된 scale 값 받아 오기 (좌표 맞추기 위해)
        imageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                imageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                view_scale = 1200 / (imageView.getWidth() / density);    // 1200은 이미지 원본 width
            }
        });

        // floor 선택 spinner 세팅 (default: 4F)
        floorAdapter = ArrayAdapter.createFromResource(this, R.array.floors, android.R.layout.simple_spinner_item);
        floorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_Floor.setAdapter(floorAdapter);

        // RP 선택 spinner 세팅 (default: 4F RP array)
        rp4Adapter = ArrayAdapter.createFromResource(this, R.array.user_4F, android.R.layout.simple_spinner_item);
        rp4Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rp5Adapter = ArrayAdapter.createFromResource(this, R.array.user_5F, android.R.layout.simple_spinner_item);
        rp5Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_RP.setAdapter(rp4Adapter);

        // floor를 선택하면, 옆의 spinner에 그 층의 RP 리스트를 띄움
        spinner_Floor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) {
                    spinner_RP.setAdapter(rp4Adapter);
                    imageView.setImageResource(R.drawable.floor4);
                }
                else if(position == 1) {
                    spinner_RP.setAdapter(rp5Adapter);
                    imageView.setImageResource(R.drawable.floor5);
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
                String curPlace = spinner_RP.getSelectedItem().toString();
                textView_RP.setText(curPlace);  // 텍스트뷰에 세팅

                String tempRP = "";
                String tempFloor = spinner_Floor.getSelectedItem().toString();
                // 받아 온 place 값을 rp랑 매칭
                switch (curPlace) {
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
                        for (int i = 0; i < rp.getRpList().size(); i++) {
                            if (rp.getRpList().get(i).getPlace().equals(curPlace)) {
                                tempRP = rp.getRpList().get(i).getRp();
                            }
                        }
                        break;
                }
                currentRP = tempRP; // 구한 RP를 저장


                int x = 0;
                int y = 0;
                // currentLocation맵핀 표시를 위한 좌표 받아오기
                for (int i = 0; i < rp.getRpList().size(); i++) {
                    if (rp.getRpList().get(i).getRp().equals(tempRP)) {
                        x = rp.getRpList().get(i).getX();
                        y = rp.getRpList().get(i).getY();
                    }
                }

                // 맵핀 위치 바꾸기
                currentLocationPin.setVisibility(View.VISIBLE);
                currentLocationPin.setX((x / view_scale - CURRENT_PIN_SIZE_HALF) * density);
                currentLocationPin.setY((y / view_scale - CURRENT_PIN_SIZE_HALF) * density);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                textView_RP.setText("");
            }
        });

        // next 버튼
        button_Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                // place, rp, view_scale 넘겨주면서, 목적지 입력 받는 activity로 이동
                Intent intent = new Intent(SelectLocationActivity.this, SelectDestinationActivity.class);
                intent.putExtra("current_place", textView_RP.getText());
                intent.putExtra("current_rp", currentRP);
                intent.putExtra("view_scale", view_scale);
                startActivity(intent);
            }
        });

    }

    // declaration
    private Spinner spinner_Floor;
    private Spinner spinner_RP;
    private ImageView imageView;
    private ImageView currentLocationPin;
    private TextView textView_RP;
    private Button button_Next;

    private ArrayAdapter floorAdapter;
    private ArrayAdapter rp4Adapter;
    private ArrayAdapter rp5Adapter;
}