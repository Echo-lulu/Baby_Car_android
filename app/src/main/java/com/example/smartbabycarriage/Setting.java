package com.example.smartbabycarriage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class Setting extends AppCompatActivity implements Screensaver.OnTimeOutListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        MainActivity.mScreensaver.resetTime();
        ImageView back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });

        ImageView Wifi=findViewById(R.id.wifi);
        Wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivity(intent);
            }
        });

        ImageView bluetooth=findViewById(R.id.bluetooth);
        bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 =  new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivity(intent1);
            }
        });

        ImageView fly=findViewById(R.id.fly);
        fly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 =  new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent2);
            }
        });
    }

    /**
     * 当触摸就会执行此方法
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        MainActivity.mScreensaver.resetTime(); //重置时间
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 当使用键盘就会执行此方法
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        MainActivity.mScreensaver.resetTime(); //重置时间
        return super.dispatchKeyEvent(event);
    }

    /**
     * 时间到就会执行此方法
     */
    @Override
    public void onTimeOut(Screensaver screensaver) {
        Intent i = new Intent();
        i.setClass(this, Sleep.class);
        startActivity(i);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainActivity.mScreensaver.stop(); //停止计时
    }
}
