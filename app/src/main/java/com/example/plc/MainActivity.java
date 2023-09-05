package com.example.plc;

import android.os.Bundle;
import android.service.controls.Control;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.plc.pojo.MultipleResource;
import com.example.plc.pojo.JsonString;

import org.jetbrains.annotations.NotNull;

import org.json.JSONObject;
import org.json.JSONException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "PLC:";
    private String message = "--";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.refreshLayout);
        Button button_off = findViewById(R.id.button_off);
        Button button_on = findViewById(R.id.button_on);
        Button button_reset = findViewById(R.id.button_reset);
        LinearLayoutCompat bottomLayout = findViewById(R.id.linearLayoutBottom);
        TextView lights = findViewById(R.id.varLights);

        MultipleResource mResource = new MultipleResource();
        OkHttpClient okHttpClient = new OkHttpClient();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            Request request = new Request.Builder()
                    .url(Constant.apiURL)
                    .get()
                    .build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "server down", Toast.LENGTH_SHORT).show();
                            lights.setText("--");
                        }
                    });
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    try {
                        String body = response.body().string();

                        JSONObject obj = new JSONObject(body);
                        mResource.lightStatus = obj.optString("lightState");
                        mResource.overrideStatus = obj.optString("overrideState");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mResource.overrideStatus.equals("true")) {
                                    message = "Lights are " + mResource.lightStatus + "\nSchedule paused";
                                    lights.setText(message);
                                }
                                else if (mResource.overrideStatus.equals("false")) {
                                    message = "Lights are " + mResource.lightStatus;
                                    lights.setText(message);
                                }
                                Log.d(TAG, mResource.lightStatus);

                                if (mResource.lightStatus.equals("on")) {
                                    button_off.setVisibility(View.VISIBLE);
                                    button_on.setVisibility(View.GONE);
                                }
                                else {
                                    button_off.setVisibility(View.GONE);
                                    button_on.setVisibility(View.VISIBLE);
                                }
                                if (mResource.overrideStatus.equals("false")) {
                                    bottomLayout.setVisibility(View.GONE);
                                }
                                else {
                                    bottomLayout.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    }
                    catch(Throwable t) {
                        t.printStackTrace();
                    }
                }
            });

            // This line is important as it explicitly
            // refreshes only once
            // If "true" it implicitly refreshes forever
            swipeRefreshLayout.setRefreshing(false);
        });

        button_off.setOnClickListener(v -> {

            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("lightState", "off");
                jsonBody.put("overrideState", "true");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody requestBody = RequestBody.create(jsonBody.toString(), Constant.JSON);

            Request request = new Request.Builder()
                    .url(Constant.apiURL)
                    .patch(requestBody)
                    .build();

            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "server down", Toast.LENGTH_SHORT).show();
                            lights.setText("--");
                        }
                    });
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful()){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "data received", Toast.LENGTH_SHORT).show();
                                message = "Lights are off\nSchedule paused";
                                lights.setText(message);
                                button_off.setVisibility(View.GONE);
                                button_on.setVisibility(View.VISIBLE);
                                bottomLayout.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }
            });
        });

        button_on.setOnClickListener(v -> {

            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("lightState", "on");
                jsonBody.put("overrideState", "true");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody requestBody = RequestBody.create(jsonBody.toString(), Constant.JSON);

            Request request = new Request.Builder()
                    .url(Constant.apiURL)
                    .patch(requestBody)
                    .build();

            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "server down", Toast.LENGTH_SHORT).show();
                            lights.setText("--");
                        }
                    });
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful()){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "data received", Toast.LENGTH_SHORT).show();
                                message = "Lights are on\nSchedule paused";
                                lights.setText(message);
                                button_off.setVisibility(View.VISIBLE);
                                button_on.setVisibility(View.GONE);
                                bottomLayout.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }
            });
        });

        button_reset.setOnClickListener(v -> {

            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("overrideState", "false");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody requestBody = RequestBody.create(jsonBody.toString(), Constant.JSON);

            Request request = new Request.Builder()
                    .url(Constant.apiURL)
                    .patch(requestBody)
                    .build();

            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "server down", Toast.LENGTH_SHORT).show();
                            lights.setText("--");
                        }
                    });
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful()){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "data received", Toast.LENGTH_SHORT).show();
                                message = "Schedule resumed";
                                lights.setText(message);
                                bottomLayout.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            });
        });
    }
}
