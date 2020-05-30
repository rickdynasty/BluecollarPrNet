package com.bluecollar.pre.research.net.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bluecollar.pre.research.net.R;
import com.bluecollar.pre.research.net.okhttp.FakeApiInterceptor;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
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
    private final int MSG_TEST_INTERCEPTOR = 5;

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
                case MSG_TEST_INTERCEPTOR:
                    Toast.makeText(OkhttpTestActivity.this, "测试拦截器！ 内容：" + msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
            }
            //处理消息
            return false;
        }
    });

    private void sureOkHttpClient() {
        if (null == okHttpClient) {
//            okHttpClient = new OkHttpClient();

            Cache cache = new Cache(new File(Environment.getDataDirectory(), "cache"), 10 * 1024 * 1024);
            okHttpClient = new OkHttpClient.Builder()
//                    .addInterceptor(loggingInterceptor)     //
                    .cache(cache)                           //添加缓存处理
                    .build();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_okhttp);

        findViewById(R.id.btn_async_get_request).setOnClickListener(this);
        findViewById(R.id.btn_sync_get_request).setOnClickListener(this);
        findViewById(R.id.btn_post_request).setOnClickListener(this);
        findViewById(R.id.btn_test_interceptor).setOnClickListener(this);
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
            case R.id.btn_test_interceptor:
                testInterceptor();
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

    private void testInterceptor() {
        //Application Interceptor能拦截所有类型的请求，包括缓存命中的请求；而Network Interceptors仅拦截非WebSocket的情况下产生真正网络访问的请求。
        // 因此在Network Interceptors上做网络上传和下载进度的监听器是比较合适的。
        Interceptor loggingInterceptor = new Interceptor() {
            @SuppressLint("DefaultLocale")
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Logger logger = Logger.getGlobal();
                //我这里参考的是官网的，你也可以定义里自己的打印方式
                long t1 = System.nanoTime();
                logger.info(String.format("Sending request %s on %s%n%s", request.url(), chain.connection(), request.headers()));
                Response response = chain.proceed(request);
                long t2 = System.nanoTime();
                logger.info(String.format("Received response for %s in %.1fms%n%s", response.request().url(), (t2 - t1) / 1e6d, response.headers()));
                return response;
            }
        };

        final String url = "https://www.httpbin.org/get?id=111";
        FakeApiInterceptor fakeApiInterceptor = new FakeApiInterceptor();
        fakeApiInterceptor.setApiULR(url);

        //1、创建OkHttpClient实例对象
        okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(loggingInterceptor)
                .addInterceptor(fakeApiInterceptor)
                //.addNetworkInterceptor() // 添加网络拦截器
                .build();

        //2、创建Request实例对象
        final Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        //3、使用client执行request请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = okHttpClient.newCall(request).execute();
                    Message msg = Message.obtain();
                    String body = response.body().string();
                    msg.obj = body;
                    msg.what = MSG_TEST_INTERCEPTOR;
                    mHandler.sendMessage(msg);
                    System.out.println(body);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
