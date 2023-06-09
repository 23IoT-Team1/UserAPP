package com.gachon.userapp;
import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WifiScanner {
    public String rpValue;
    public static final int REQUEST_PERMISSION_CODE = 123;
    public final String[] permissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE
    };

    public final Context context;
    public final  WifiManager wifiManager;
    public final List<ScanResult> scanResults;
    public final ArrayList<WifiDTO> arrayList;
    public static boolean start;

    public WifiScanner(Context context, WifiManager wifiManager, ArrayList<WifiDTO> arrayList) {
        this.context = context;
        this.wifiManager = wifiManager;
        this.arrayList = arrayList;
        this.scanResults = new ArrayList<>();
    }



    public void scanWifi() {
        if (start) {
            if (!wifiManager.isWifiEnabled()) {
                Toast.makeText(context, "WIFI DISABLED", Toast.LENGTH_LONG).show();
                wifiManager.setWifiEnabled(true);
            }
            wifiManager.startScan();
            //Toast.makeText(context, "SCANNING", Toast.LENGTH_LONG).show();
        } else {
            requestPermissions();
        }
    }

    public void requestPermissions() {
        if (ContextCompat.checkSelfPermission(context, permissions[0]) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, permissions[1]) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, permissions[2]) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, permissions[3]) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions((HomeActivity) context, permissions, REQUEST_PERMISSION_CODE);

        } else {
            Toast.makeText(context, "Permissions already granted", Toast.LENGTH_SHORT).show();
            start = true;
        }
    }

    public BroadcastReceiver getWifiReceiver() {

        return wifiReceiver;
    }

        public final BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            scanResults.clear();
            scanResults.addAll(wifiManager.getScanResults());

            Collections.sort(scanResults, new Comparator<ScanResult>() {
                @Override
                public int compare(ScanResult result1, ScanResult result2) {
                    return Integer.compare(result2.level, result1.level);
                }
            });

            arrayList.clear();

            int count = 0; // Variable to keep track of the number of items added
            for (ScanResult res : scanResults) {

                // 아래 if문으로 전체 wifi와 GC_free_WiFi 구분 가능
                //-----------------------------
                if (!res.SSID.equals("GC_free_WiFi"))
                    continue;
                //------------------------------
                if (count < 5) { // Add only the top 5 values
                    WifiDTO temp = new WifiDTO(res.SSID, res.BSSID, String.valueOf(res.level));
                    arrayList.add(temp);
                    count++;
                } else {
                    break; // Break the loop once 5 values have been added
                }
            }
            SenderToServer sender = new SenderToServer(arrayList);

            if (context.getClass() == GetLocationActivity.class) {
                GetLocationActivity GLA = (GetLocationActivity) context;
                GLA.btnclick = true;
                rpValue = String.valueOf(sender.send());
                GLA.set_rpValue(rpValue);

            }
            else if (context.getClass() == NavigationActivity.class) {
                NavigationActivity NA = (NavigationActivity) context;
                rpValue = String.valueOf(sender.send());
                NA.set_rpValue(rpValue);
            }
            else {
                Toast.makeText(context, "Scanning Problem ", Toast.LENGTH_SHORT).show();
            }

        }
    };
    public void stopWifiScan() {
        if (wifiReceiver != null) {
            context.unregisterReceiver(wifiReceiver);
        }
    }
}
