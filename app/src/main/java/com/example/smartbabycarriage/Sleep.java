package com.example.smartbabycarriage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MotionEventCompat;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

import java.nio.charset.MalformedInputException;

public class Sleep extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);
        MainActivity.mScreensaver.stop();

        final Handler handler= new Handler();

        final Runnable runnable = new Runnable() {
            public void run() {
                    refresh();
            }
        };

        final Thread t = new Thread(){
            //public boolean isrun=true;
            @Override
            public void run() {
                while(true)
                {
                    handler.post(runnable); //加入到消息队列 　这样没有启动新的线程，虽然没有报异常。但仍然阻塞ProgressDialog的显示
                    try {
                        sleep(1000); //直接调用
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }
        };
        t.start();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        MainActivity.mScreensaver.resetTime();
        finish();
        return false;
    }

    public void refresh(){

        //菜单栏婴儿图标
        ImageView kid=findViewById(R.id.baobao);
        if (MainActivity.weight) {
            kid.setImageDrawable(getResources().getDrawable(R.drawable.baobao));
        }else {
            kid.setImageDrawable(getResources().getDrawable(R.drawable.wubaobao));
        }
        //菜单栏速度图标
        TextView ad=findViewById(R.id.speed6);
        ad.setText(MainActivity.str_speed);

        ImageView acc_state= findViewById(R.id.jiasu_icon);
        ImageView dec_state=findViewById(R.id.jiansu_icon);

        if(MainActivity.accelerate_flag) {
            acc_state.setImageDrawable(getResources().getDrawable(R.drawable.jiasu2));
            TextView text_acc=findViewById(R.id.accelerate);
            text_acc.setTextColor(Color.parseColor("#00FEEF"));
        }
        else {
            acc_state.setImageDrawable(getResources().getDrawable(R.drawable.jiasu1));
            TextView text_acc=findViewById(R.id.accelerate);
            text_acc.setTextColor(Color.parseColor("#FFFFFF"));
        }
        if(MainActivity.decelerate_flag) {
            dec_state.setImageDrawable(getResources().getDrawable(R.drawable.jiansu2));
            TextView text_dec=findViewById(R.id.decelerate);
            text_dec.setTextColor(Color.parseColor("#00FEEF"));
        }
        else {
            dec_state.setImageDrawable(getResources().getDrawable(R.drawable.jiansu1));
            TextView text_dec=findViewById(R.id.decelerate);
            text_dec.setTextColor(Color.parseColor("#FFFFFF"));
        }
        //菜单栏坡度图标
        TextView slope=findViewById(R.id.slope);
        slope.setText(MainActivity.angle+"°");

        //car pad 电量刷新
//        BatteryManager batteryManager = (BatteryManager)getSystemService(BATTERY_SERVICE);
//        pad_battery = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
//        TextView pad_battery_show=findViewById(R.id.pad_battery_show);
//        pad_battery_show.setText(pad_battery+"%");

//        TextView car_battery_show=findViewById(R.id.car_battery_show);
//        car_battery_show.setText(car_battery+"%");
    }
}
