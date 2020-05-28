package com.bluecollar.pre.research.net.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bluecollar.pre.research.net.R;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkhttpTestActivity extends Activity implements View.OnClickListener {
    public static final String TAG = "MainActivity";

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_okhttp);

        initButton();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        // if(requestQueue!=null){
        // requestQueue.cancelAll();
        // }
    }

    private void initButton() {
        findViewById(R.id.btn_async_get_request).setOnClickListener(this);
        findViewById(R.id.btn_sync_get_request).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String url;
        switch (v.getId()) {
            case R.id.btn_async_get_request:
                asyncGet("http://wwww.baidu.com");
                break;
            case R.id.btn_sync_get_request:
                syncGet("http://wwww.baidu.com");
                break;

        }
    }

    private void syncGet(String url) {
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .build();
        final Call call = okHttpClient.newCall(request);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = call.execute();
                    Log.d(TAG, "run: " + response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void asyncGet(String url) {
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .get()//默认就是GET请求，可以不写
                .build();
        okhttp3.Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "onResponse: " + response.body().string());
            }
        });
    }
}
