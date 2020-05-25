package com.bluecollar.pre.research.net;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bluecollar.pre.research.net.bean.Weather;
import com.bluecollar.pre.research.net.bean.WeatherInfo;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    public static final String LOG_TAG = "MainActivity";

    private Button btnString, btnJson, btnImage, btnMyXml, btnMyJson, btnImagerLoader, btnNewWorkImageView;
    private ImageView imageView;
    private NetworkImageView networkImageView;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initButton();

        requestQueue = Volley.newRequestQueue(this);

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
        btnString = (Button) this.findViewById(R.id.btn_test_string);
        btnJson = (Button) this.findViewById(R.id.btn_test_json);
        btnImage = (Button) this.findViewById(R.id.btn_test_image);
        btnMyXml = (Button) this.findViewById(R.id.btn_test_myxml);
        btnMyJson = (Button) this.findViewById(R.id.btn_test_myjson);
        btnImagerLoader = (Button) this.findViewById(R.id.btn_test_imageloader);
        btnNewWorkImageView = (Button) this.findViewById(R.id.btn_test_networkimageview);

        imageView = (ImageView) this.findViewById(R.id.iv_test_image);
        networkImageView = (NetworkImageView) this.findViewById(R.id.network_image_view);

        ButtonOnClickLisener btnListener = new ButtonOnClickLisener();
        btnString.setOnClickListener(btnListener);
        btnJson.setOnClickListener(btnListener);
        btnImage.setOnClickListener(btnListener);
        btnMyXml.setOnClickListener(btnListener);
        btnMyJson.setOnClickListener(btnListener);
        btnImagerLoader.setOnClickListener(btnListener);
        btnNewWorkImageView.setOnClickListener(btnListener);
    }

    private class ButtonOnClickLisener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            String url;
            switch (v.getId()) {
                case R.id.btn_test_string:

                    // 创建请求
                    url = "http://www.baidu.com";
                    StringRequest strReq = new StringRequest(url, new Response.Listener<String>() {

                        @Override
                        public void onResponse(String arg0) {
                            Log.v(LOG_TAG, "StringRequest onResponse result->" + arg0);
                            Toast.makeText(MainActivity.this, "onResponse返回的长度：" + arg0.length(), Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError arg0) {
                            Log.v(LOG_TAG, "onErrorResponse message->" + arg0.getMessage());
                        }
                    });

                    // 添加到请求队列中
                    requestQueue.add(strReq);

                    break;

                case R.id.btn_test_json:
                    //
                    url = "http://app.bang365.cn/home/api/";
                    JsonObjectRequest joReq = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject arg0) {
                            Log.d("TAG", arg0.toString());
                            Toast.makeText(MainActivity.this, "json：" + arg0.toString(), Toast.LENGTH_SHORT).show();
                        }

                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError arg0) {
                            Log.v(LOG_TAG, "onErrorResponse message->" + arg0.getMessage());

                        }
                    });
                    // 添加到请求队列中
                    requestQueue.add(joReq);

                    break;

                case R.id.btn_test_image:
                    url = "https://www.baidu.com/img/bdlogo.png";

                    /**
                     * 第三第四个参数分别用于指定允许图片最大的宽度和高度，如果指定的网络图片的宽度或高度大于这里的最大值，则会对图片进行压缩，
                     * 指定成0的话就表示不管图片有多大，都不会进行压缩。第五个参数用于指定图片的颜色属性，Bitmap.
                     * Config下的几个常量都可以在这里使用，其中ARGB_8888可以展示最好的颜色属性，每个图片像素占据4个字节的大小，
                     * 而RGB_565则表示每个图片像素占据2个字节大小。
                     */

                    ImageRequest imReq = new ImageRequest(url, new Response.Listener<Bitmap>() {

                        @Override
                        public void onResponse(Bitmap arg0) {
                            imageView.setImageBitmap(arg0);

                        }
                    }, 0, 0, Config.RGB_565, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError arg0) {
                            Log.v(LOG_TAG, "onErrorResponse message->" + arg0.getMessage());
                            imageView.setImageResource(R.mipmap.ic_launcher);
                        }

                    });
                    requestQueue.add(imReq);
                    break;

                case R.id.btn_test_imageloader:
                    url = "https://www.baidu.com/img/bdlogo.png";

                    ImageLoader imageLoader = new ImageLoader(requestQueue, new MyImageCache());

                    ImageListener imageListener = imageLoader.getImageListener(imageView, R.mipmap.default_icon,
                            R.mipmap.error_icon);

                    // imageLoader.get(url, imageListener);
                    imageLoader.get(url, imageListener, 200, 200);
                    break;

                case R.id.btn_test_networkimageview:
                    url = "http://bluecollarhub.cn/static/img/avatar.bffd256.jpg";

                    ImageLoader networkimageLoader = new ImageLoader(requestQueue, new MyImageCache());
                    networkImageView.setDefaultImageResId(R.mipmap.default_icon);
                    networkImageView.setErrorImageResId(R.mipmap.error_icon);
                    networkImageView.setImageUrl(url, networkimageLoader);

                    break;

                case R.id.btn_test_myxml:
                    // 返回天气预报
                    url = "http://flash.weather.com.cn/wmaps/xml/china.xml";

                    MyXMLRequest xmlRequest = new MyXMLRequest(url, new Response.Listener<XmlPullParser>() {
                        @Override
                        public void onResponse(XmlPullParser response) {
                            try {
                                int eventType = response.getEventType();
                                while (eventType != XmlPullParser.END_DOCUMENT) {
                                    switch (eventType) {
                                        case XmlPullParser.START_TAG:
                                            String nodeName = response.getName();
                                            if ("city".equals(nodeName)) {
                                                String pName = response.getAttributeValue(0);
                                                Log.d("TAG", "pName is " + pName);
                                            }
                                            break;
                                    }
                                    eventType = response.next();
                                }
                            } catch (XmlPullParserException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(LOG_TAG, "onErrorResponse message:" + error.getMessage(), error);
                        }
                    });

                    requestQueue.add(xmlRequest);
                    break;
                case R.id.btn_test_myjson:
                    url = "http://www.weather.com.cn/data/sk/101010100.html";

                    MyGsonRequest<Weather> mjReq = new MyGsonRequest<Weather>(url, Weather.class,
                            new Response.Listener<Weather>() {

                                @Override
                                public void onResponse(Weather weather) {
                                    WeatherInfo weatherInfo = weather.getWeatherinfo();
                                    Log.v("TAG", "city is " + weatherInfo.getCity());

                                }
                            }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(LOG_TAG, "onErrorResponse message:" + error.getMessage(), error);

                        }
                    });

                    requestQueue.add(mjReq);

                    break;

            }
        }
    }

    private class MyImageCache implements ImageLoader.ImageCache {

        private LruCache<String, Bitmap> mCache;

        public MyImageCache() {
            // 缓存图片的大小设置为10M
            int maxSize = 10 * 1024 * 1024;
            mCache = new LruCache<String, Bitmap>(maxSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getRowBytes() * bitmap.getHeight();
                }
            };
        }

        @Override
        public Bitmap getBitmap(String url) {
            return mCache.get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            mCache.put(url, bitmap);
        }

    }
}
