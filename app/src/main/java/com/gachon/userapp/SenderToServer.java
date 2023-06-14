package com.gachon.userapp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SenderToServer {

        private static node request_body;
        private String rpValue="GOD BLESS YOU";
        private CountDownLatch latch;

        public SenderToServer(ArrayList arrayList) {
                request_body = new node(arrayList);
                latch = new CountDownLatch(1);
        }

        public String send() {
                Gson gson = new Gson();
                String json = gson.toJson(request_body);
                if (json != null) {
                        Log.d("SCAN 값~~~~~~~~~~~~~~", json);
                } else {
                        Log.d(TAG, "JSON is null");
                }
/*
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url("http://172.16.232.218:8080/rp/position")
                        .post(RequestBody.create(MediaType.parse("application/json"), json))
                        .build();

                client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                if (response.isSuccessful()) {
                                        String responseBody = response.body().string();
                                        Log.d("!!!!!!!!!!!!!!!!", "Server Response: " + responseBody);

                                        try {
                                                JSONObject jsonObject = new JSONObject(responseBody);
                                                rpValue = jsonObject.getString("rp");
                                                Log.d(TAG, "RP Value: " + rpValue);
                                        } catch (JSONException e) {
                                                e.printStackTrace();
                                        }
                                }
                                latch.countDown(); // Release the latch after receiving the response
                        }

                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                e.printStackTrace();
                                latch.countDown(); // Release the latch in case of failure
                        }
                });

                try {
                        latch.await(); // Wait until the latch is released
                } catch (InterruptedException e) {
                        e.printStackTrace();
                }*/

                return rpValue;
        }
}
