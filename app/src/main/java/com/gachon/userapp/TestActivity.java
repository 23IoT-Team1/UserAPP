package com.gachon.userapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        textView_RP = findViewById(R.id.textView_RP);
        button_Test = findViewById(R.id.button_Test);
        button_BackToMain = findViewById(R.id.button_BackToMain);

        button_Test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // wifi scan 후 현위치 결과 뱉기

                // 이후 RP textView 바꾸기
                String RP = "~~";    // ex
                textView_RP.setText(RP);
                // 나중에 가능하면 imageView도
            }
        });

        button_BackToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // main activity로 돌아가기
                Intent intent = new Intent(TestActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();   // To prevent confusing
            }
        });
    }

    // declaration
    private TextView textView_RP;
    private Button button_Test;
    private Button button_BackToMain;

}