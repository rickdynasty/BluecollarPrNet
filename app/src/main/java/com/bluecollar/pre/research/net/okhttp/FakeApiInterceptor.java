package com.bluecollar.pre.research.net.okhttp;

import com.squareup.okhttp3.BuildConfig;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;

//使用拦截器模拟数据
public class FakeApiInterceptor implements Interceptor {
    private String API_URL = "";

    public void setApiULR(String url) {
        API_URL = url;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response;
        if (BuildConfig.DEBUG && chain.request().url().toString().equals(API_URL)) {
            String json = "{\"code\": 200, \"message\": \"success\"}";
            response = new Response.Builder()
                    .code(200)
                    .addHeader("Content-Type", "application/json")
                    .body(ResponseBody.create(MediaType.parse("application/json"), json))
                    .message(json)
                    .request(chain.request())
                    .protocol(Protocol.HTTP_2)
                    .build();
        } else {
            response = chain.proceed(chain.request());
        }
        return response;
    }
}