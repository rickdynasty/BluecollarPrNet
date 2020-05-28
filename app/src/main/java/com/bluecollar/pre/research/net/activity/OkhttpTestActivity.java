package com.bluecollar.pre.research.net.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bluecollar.pre.research.net.R;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkhttpTestActivity extends Activity implements View.OnClickListener {
    public static final String TAG = "MainActivity";

    private ImageView imageView;
    OkHttpClient okHttpClient;
    private final int MSG_SYNC_GET = 0;
    private final int MSG_ASYNC_GET_FAIL = 1;
    private final int MSG_ASYNC_GET_RES = 2;
    private final int MSG_POST_RES_FAIL = 3;
    private final int MSG_POST_RES = 4;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SYNC_GET:
                    Toast.makeText(OkhttpTestActivity.this, "同步Get请求Success！ 长度：" + msg.obj.toString().length(), Toast.LENGTH_SHORT).show();
                    break;
                case MSG_ASYNC_GET_FAIL:
                    Toast.makeText(OkhttpTestActivity.this, "异步Get请求失败~┭┮﹏┭┮", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_ASYNC_GET_RES:
                    Toast.makeText(OkhttpTestActivity.this, "异步Get请求Success！ 长度：" + msg.obj.toString().length(), Toast.LENGTH_SHORT).show();
                    break;
                case MSG_POST_RES_FAIL:
                    Toast.makeText(OkhttpTestActivity.this, "Post请求失败~┭┮﹏┭┮ ：" + msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case MSG_POST_RES:
                    Toast.makeText(OkhttpTestActivity.this, "Post请求Success！ 内容：" + msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
            }
            //处理消息
            return false;
        }
    });

    private void sureOkHttpClient() {
        if (null == okHttpClient) {
            okHttpClient = new OkHttpClient();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_okhttp);

        findViewById(R.id.btn_async_get_request).setOnClickListener(this);
        findViewById(R.id.btn_sync_get_request).setOnClickListener(this);
        findViewById(R.id.btn_post_request).setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        // if(requestQueue!=null){
        // requestQueue.cancelAll();
        // }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_async_get_request:
                asyncGet("http://wwww.baidu.com");
                break;
            case R.id.btn_sync_get_request:
                syncGet("http://wwww.baidu.com");
                break;
            case R.id.btn_post_request:
                postForLoginBaidu("https://www.wanandroid.com/user/login");
                break;
        }
    }

    //同步Get请求
    private void syncGet(String url) {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        sureOkHttpClient();
        final Call call = okHttpClient.newCall(request);

        //通过 call.excute() 方法来提交同步请求，这种方式会阻塞线程，而为了避免 ANR 异常，Android3.0 之后已经不允许在主线程中访问网络了
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = call.execute();
                    Message msg = Message.obtain();
                    msg.obj = response.body().string();
                    msg.what = MSG_SYNC_GET;   //标志消息的标志
                    mHandler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //异步Get请求
    private void asyncGet(String url) {
        sureOkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .get()//默认就是GET请求，可以不写
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg = Message.obtain(mHandler);
                msg.what = MSG_ASYNC_GET_FAIL;   //标志消息的标志
                msg.sendToTarget();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message message = mHandler.obtainMessage();
                message.obj = response.body().string();
                //这里这个 what 是Message对象携带的参数我主要用它来区分消息对象(Message)
                message.what = MSG_ASYNC_GET_RES;   //标志消息的标志
                //把消息发送给目标对象，目标对象就是 myHandler 就是关联我们得到的那个消息对象的Handler
                message.sendToTarget();
            }
        });
    }

    //post请求
    private void postForLoginBaidu(String url) {
        sureOkHttpClient();
        //创建表单请求参数【FormBody extends RequestBody】
        FormBody formBody = new FormBody.Builder()
                .add("username", "bluecollar")
                .add("password", "111111")
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg = Message.obtain(mHandler);
                msg.what = MSG_POST_RES_FAIL;   //标志消息的标志
                msg.obj = e.getMessage();
                msg.sendToTarget();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message msg = Message.obtain(mHandler);
                msg.what = MSG_POST_RES;   //标志消息的标志
                msg.obj = response.body().string();
                msg.sendToTarget();
            }
        });
    }
}
