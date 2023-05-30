package com.gachon.userapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class SelectLocationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);

        spinner_Floor = findViewById(R.id.spinner_Floor);
        spinner_RP = findViewById(R.id.spinner_RP);
        imageView = findViewById(R.id.imageView);
        textView_RP = findViewById(R.id.textView_RP);
        button_ScanWifi = findViewById(R.id.button_ScanWifi);

        // floor 선택 spinner 세팅 (default: 4F)
        floorAdapter = ArrayAdapter.createFromResource(this, R.array.floors, android.R.layout.simple_spinner_item);
        floorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_Floor.setAdapter(floorAdapter);

        // RP 선택 spinner 세팅 (default: 4F RP array)
        rp4Adapter = ArrayAdapter.createFromResource(this, R.array.reference_points_4F, android.R.layout.simple_spinner_item);
        rp4Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rp5Adapter = ArrayAdapter.createFromResource(this, R.array.reference_points_5F, android.R.layout.simple_spinner_item);
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
                textView_RP.setText(spinner_RP.getSelectedItem().toString());
                // 나중엔 imageView도 변하게 하기
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                textView_RP.setText("");
            }
        });

        // Scan WiFi 버튼
//        button_ScanWifi.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(SelectLocationActivity.this, ScanWifiActivity.class);
//
//                intent.putExtra("reference_point", textView_RP.getText().toString());
//                startActivity(intent);
//            }
//        });

    }

    // declaration
    private Spinner spinner_Floor;
    private Spinner spinner_RP;
    private ImageView imageView;
    private TextView textView_RP;
    private Button button_ScanWifi;

    private ArrayAdapter floorAdapter;
    private ArrayAdapter rp4Adapter;
    private ArrayAdapter rp5Adapter;
}