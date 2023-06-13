//arrayList->gson->json 데이터 변환과 okhttp3를 이용한 데이터 전송
//출처 : https://snowdeer.github.io/android/2017/03/03/get-and-post-and-put-using-okhttp/
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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SenderToServer {

        // Convert the measured values to JSON

        private static node request_body;
        private String rpValue="4_1";

        public SenderToServer(ArrayList arrayList){

                // rp는 아래 함수에 매칭 후 넣기

                //빈 arraylist에 집어넣어줌
                //constructor에서 받아온 arraylist는
                // scanWifiActivity에서 저장한 5개의 AP 정보를 WifiDTO 형식으로 담은 Arraylist이다
                request_body = new node(arrayList);

        }
        public String send() {
                Gson gson = new Gson();
                String json = gson.toJson(request_body);
                Log.d(TAG, json);


//                OkHttpClient client = new OkHttpClient();

                Log.e("테스트","testsets");
/*
                Request request = new Request.Builder()
                        .url("http://172.16.63.238:8080/rp")
                        .post(RequestBody.create(MediaType.parse("application/json"), json))
                        .build();

                client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                if (response.isSuccessful()) {
                                        String responseBody = response.body().string();
                                        Log.d(TAG, "Server Response: " + responseBody);

                                        try {
                                                JSONObject jsonObject = new JSONObject(responseBody);
                                                rpValue = jsonObject.getString("rp");
                                                Log.d(TAG, "RP Value: " + rpValue);

                                        } catch (JSONException e) {
                                                e.printStackTrace();
                                        }
                                }
                        }

                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                e.printStackTrace();
                        }
                });*/

                return rpValue;
        }




}

