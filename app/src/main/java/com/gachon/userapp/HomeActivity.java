package com.gachon.userapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        button_Next = findViewById(R.id.button_Next);
        wifiScanner = new WifiScanner(this, (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE), new ArrayList<>());
        wifiScanner.requestPermissions();

        button_Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, GetLocationActivity.class);
                startActivity(intent);
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        this.registerReceiver(wifiScanner.getWifiReceiver(), new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }
    @Override
    protected void onStop() {
        super.onStop();
        // Set activity status
        ActivityStatusChecker.setActivityStatus(false);
//        Toast.makeText(this, "onPause 호출됨", Toast.LENGTH_SHORT).show();
        this.unregisterReceiver(wifiScanner.getWifiReceiver());
    }
    // declaration
    private Button button_Next;
    private WifiScanner wifiScanner;

}