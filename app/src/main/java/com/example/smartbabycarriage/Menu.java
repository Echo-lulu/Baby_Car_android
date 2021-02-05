package com.example.smartbabycarriage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class Menu extends AppCompatActivity implements Screensaver.OnTimeOutListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        menu_ini();
        MainActivity.mScreensaver.resetTime();

    }


    public  void menu_ini(){

        TextView text_shouqi=findViewById(R.id.text_shouqi);
        text_shouqi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                    i.setClass(Menu.this, MainActivity.class);
                startActivity(i);
            }
        });

        ImageView image_shouqi=findViewById(R.id.shouqi);
        image_shouqi.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setClass(Menu.this, MainActivity.class);
                startActivity(i);
            }
        });
        ImageView Fold=findViewById(R.id.main_fold);
        Fold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent i = new Intent();
                i.setClass(Menu.this, Fold.class);
                startActivity(i);
            }
        });


        ImageView Early_edu=findViewById(R.id.main_early_edu);
        Early_edu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent i = new Intent();
                i.setClass(Menu.this, Early_edu.class);
                startActivity(i);
            }
        });


        ImageView Air_pure=findViewById(R.id.main_air_pure);
        Air_pure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent i = new Intent();
                i.setClass(Menu.this, Air_pure.class);
                startActivity(i);
            }
        });


        ImageView Light=findViewById(R.id.main_light);
        Light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent i = new Intent();
                i.setClass(Menu.this, Light.class);
                startActivity(i);
            }
        });


        ImageView Battery=findViewById(R.id.main_battery);
        Battery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent i = new Intent();
                i.setClass(Menu.this, Battery.class);
                startActivity(i);
            }
        });

        ImageView GPS=findViewById(R.id.main_gps);
        GPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent i = new Intent();
                i.setClass(Menu.this, GPS.class);
                startActivity(i);
            }
        });

        ImageView Car_state=findViewById(R.id.main_car_state);
        Car_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent i = new Intent();
                i.setClass(Menu.this, Car_state.class);
                startActivity(i);
            }
        });


        ImageView Camera=findViewById(R.id.main_camera);
        Camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent i = new Intent();
                i.setClass(Menu.this, Camera.class);
                startActivity(i);
            }
        });


//        ImageView Help=findViewById(R.id.);
//        Help.setOnClickListener(new Help_click());

        ImageView Settings=findViewById(R.id.main_settings);
        Settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent i = new Intent();
                i.setClass(Menu.this, Setting.class);
                startActivity(i);
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
