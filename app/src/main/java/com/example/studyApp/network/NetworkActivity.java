package com.example.studyApp.network;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studyApp.CustomApplication;
import com.example.studyApp.R;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);

        findViewById(R.id.post_net_work_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                InetAddress[] addresses = new InetAddress[0];
//                try {
//                   addresses = InetAddress.getAllByName("app.58.com");
//                } catch (UnknownHostException e) {
//                    e.printStackTrace();
//                }
//                for (InetAddress ad : addresses) {
//                    Log.d("gxd", ad.getHostName() + "," + ad.getHostAddress());
//                }
//
//                OkHttpClient ok = new OkHttpClient();
//                try {
//                    List<InetAddress> addresses1  =ok.dns().lookup("58.com");
//                    for (InetAddress ad : addresses1) {
//                        Log.d("gxd", "ok:"+ad.getHostName() + "," + ad.getHostAddress());
//                    }
//                } catch (UnknownHostException e) {
//                    e.printStackTrace();
//                }


                Observable
                        .create(new ObservableOnSubscribe<Object>() {
                            @Override
                            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Exception {
                                //  requestByGet();

                                requestByGetWithOkhttp();
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(o -> {

                        });

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        requestByGet();
                    }
                }).start();

            }
        });
    }


    private void requestByGetWithOkhttp() {
        final Request request = new Request.Builder().url("https://www.baidu.com/").build();
        OkHttpClient client = new OkHttpClient();
        try {
            Response response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void requestByGet() {
        try {
            URL url = new URL("https://www.baidu.com/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(30 * 1000);//设置超时时长，单位ms
            connection.setRequestMethod("GET");//设置请求格式
            connection.setRequestProperty("Content-Type", "Application/json");//期望返回的数据格式
            connection.setRequestProperty("CharSet", "UTF-8");//设置字符集
            connection.setRequestProperty("Accept-CharSet", "UTF-8");//请求的字符集
            connection.connect();//发送请求

            int responseCode = connection.getResponseCode();//获取返回码
            String responseMessage = connection.getResponseMessage();//获取返回信息
            if (responseCode == HttpURLConnection.HTTP_OK)//请求成功操作
            {
                //TODO
                Log.d("gzpNetworkActivity", "请求成功操作");
            }

            runOnUiThread(new Runnable() //TODO 执行更新UI操作
            {
                @Override
                public void run() {
                    Toast.makeText(NetworkActivity.this, "请求成功", Toast.LENGTH_SHORT);
                }
            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void httpsRequest() {

    }
}