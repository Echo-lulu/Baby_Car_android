package com.example.smartbabycarriage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;


public class GPS extends AppCompatActivity implements Screensaver.OnTimeOutListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);
        ImageView back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });

        MainActivity.mScreensaver.resetTime();





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
