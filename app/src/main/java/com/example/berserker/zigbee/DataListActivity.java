package com.example.berserker.zigbee;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DataListActivity extends AppCompatActivity {
    private TextView tvContent;
    Vibrator vibrator;
    private TextView tvErrorContent;

    private String mBaseUrl = "http://10.0.116.74:8080/sensor/json";

    private String mErrorDataUrl = "http://10.0.116.74:8080/error/json";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datalist);

        tvContent = findViewById(R.id.tv_content);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    initData();
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        tvErrorContent = findViewById(R.id.tv_error_content);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    initErrorData();
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);

    }

    public void initData(){
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder builder = new Request.Builder();
        Request request = builder
                .get()
                .url(mBaseUrl)
                .build();
        Call call = okHttpClient.newCall(request);
        //Response response = call.execute();
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                L.e("onFailure"+e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                L.e("onResponse");
                final String res = response.body().string();
                L.e(res);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //mTvResult.setText(res);
                        try {
                            //清空标签内容
                            tvContent.setText("");
                            //基于content字符串创建Json数组
                            JSONArray jsonArray = new JSONArray(res);
                            double t1 = Double.parseDouble(jsonArray.getJSONObject(0).getString("temp"));
                            double l1 = Double.parseDouble(jsonArray.getJSONObject(0).getString("light"));

                            if(t1 > 580| l1 < 200){
                                System.out.println(t1+"  数据超标啦");
                                showNormalDialog();
                                vibrator.vibrate(2000);
                            }
                            //遍历Json数组
                            for (int i=0;i<jsonArray.length();i++){
                                //通过下标获取json数组元素-json对象
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                //对Json对象按键取值
                                String date = jsonObject.getString("date");
                                String huml = jsonObject.getString("humi");
                                String temp = jsonObject.getString("temp");
                                String light = jsonObject.getString("light");

                                //拼接
                                String data = date+"   "+huml+"       "+temp+"         "+light+"\n";
                                //数据追加到标签中
                                tvContent.append(data);
                            }
                            tvContent.setTextColor(Color.BLACK);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public void initErrorData(){
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder builder = new Request.Builder();
        Request request = builder
                .get()
                .url(mErrorDataUrl)
                .build();
        Call call = okHttpClient.newCall(request);
        //Response response = call.execute();
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                L.e("onFailure"+e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                L.e("onResponse");
                final String res = response.body().string();
                L.e(res);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //mTvResult.setText(res);
                        try {
                            //清空标签内容
                            tvErrorContent.setText("");
                            //基于content字符串创建Json数组
                            JSONArray jsonArray = new JSONArray(res);
                            //遍历Json数组

                            for (int i=0;i<jsonArray.length();i++){
                                //通过下标获取json数组元素-json对象
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                //对Json对象按键取值
                                String errorDate =jsonObject.getString("errorDate");
                                String errorTemp = jsonObject.getString("errorTemp");
                                String errorLight = jsonObject.getString("errorLight");
                                String errorInfo = jsonObject.getString("errorInfo");

                                //拼接
                                String data = errorDate+"   "+errorTemp+"       "+errorLight+"         "+errorInfo+"\n";
                                //数据追加到标签中

                            tvErrorContent.append(data);
                        }

                        tvErrorContent.setTextColor(Color.RED);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void showNormalDialog() {
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(DataListActivity.this);
        //normalDialog.setIcon(R.drawable.icon_dialog);
        normalDialog.setTitle("警告");
        normalDialog.setMessage("温度数据超标啦");
        normalDialog.setNegativeButton("我知道了",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return ;
                    }
                });
        // 显示
        normalDialog.show();
    }
}

