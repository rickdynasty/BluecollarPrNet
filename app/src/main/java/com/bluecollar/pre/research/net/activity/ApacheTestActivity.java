package com.bluecollar.pre.research.net.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bluecollar.pre.research.net.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.InputStream;

public class ApacheTestActivity extends Activity implements View.OnClickListener {
    private EditText urlText, imageUrlText;
    private TextView resutlView;
    private ImageView imageView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test_apache);

        urlText = (EditText) findViewById(R.id.urlText);
        imageUrlText = (EditText) findViewById(R.id.imageurlText);
        resutlView = (TextView) findViewById(R.id.resultView);
        imageView = (ImageView) findViewById(R.id.imgeView01);

        findViewById(R.id.getBtn).setOnClickListener(this);
        findViewById(R.id.postBtn).setOnClickListener(this);
        findViewById(R.id.imgBtn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.getBtn:
                System.out.println(urlText.getText().toString());
                resutlView.setText(get(urlText.getText().toString()));
                break;
            case R.id.postBtn:
                System.out.println(urlText.getText().toString());
                resutlView.setText(post(urlText.getText().toString()));
                break;
            case R.id.imgBtn:
                getImage(imageUrlText.getText().toString());
                break;
        }
    }

    private String get(String url) {
//        HttpParams httpParams = new BasicHttpParams();
//        HttpConnectionParams.setConnectionTimeout(httpParams, KeySource);
        // 1.创建HttpClient对象，通过DefaultClient的excute方法执行返回一个HttpResponse对象
        HttpClient httpClient = new DefaultHttpClient();

        // 2.获取请求
        HttpGet httpGet = new HttpGet(url);
        StringBuffer result = new StringBuffer();
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            // 3.取得HttpEntiy
            HttpEntity httpEntity = httpResponse.getEntity();
            // 通过EntityUtils并指定编码方式取到返回的数据
            result.append(EntityUtils.toString(httpEntity, "utf-8"));
            //得到StatusLine接口对象
            StatusLine statusLine = httpResponse.getStatusLine();
            //得到协议
            result.append("协议:" + statusLine.getProtocolVersion() + "\r\n");
            int statusCode = statusLine.getStatusCode();
            result.append("状态码:" + statusCode + "\r\n");
        } catch (Exception e) {
            Toast.makeText(ApacheTestActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
        }
        return result.toString();
    }

    private String post(String url) {
        HttpResponse httpResponse = null;
        StringBuffer result = new StringBuffer();
        try {
            // 1.通过url创建HttpGet对象
            HttpPost httpPost = new HttpPost(url);
            // 2.通过DefaultClient的excute方法执行返回一个HttpResponse对象
            httpResponse = new DefaultHttpClient().execute(httpPost);
            // 3.取得HttpEntiy
            HttpEntity httpEntity = httpResponse.getEntity();
            // 通过EntityUtils并指定编码方式取到返回的数据
            result.append(EntityUtils.toString(httpEntity, "utf-8"));
            StatusLine statusLine = httpResponse.getStatusLine();
            statusLine.getProtocolVersion();
            int statusCode = statusLine.getStatusCode();
            result.append("状态码:" + statusCode + "\r\n");
        } catch (Exception e) {
            Toast.makeText(ApacheTestActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
        }
        return result.toString();
    }

    public void getImage(String url) {
        try {
            // 1.通过url创建HttpGet对象
            HttpGet httpGet = new HttpGet(url);
            // 2.通过DefaultClient的excute方法执行返回一个HttpResponse对象
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResponse = httpClient.execute(httpGet);
            // 3.取得相关信息
            // 取得HttpEntiy
            HttpEntity httpEntity = httpResponse.getEntity();
            // 4.通过HttpEntiy.getContent得到一个输入流
            InputStream inputStream = httpEntity.getContent();
            System.out.println(inputStream.available());
            //通过传入的流再通过Bitmap工厂创建一个Bitmap
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            //设置imageView
            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            Toast.makeText(ApacheTestActivity.this, "网络连接异常", Toast.LENGTH_LONG).show();
        }
    }
}