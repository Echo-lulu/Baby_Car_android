package com.example.smartbabycarriage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class Battery extends AppCompatActivity implements Screensaver.OnTimeOutListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery);
        MainActivity.mScreensaver.resetTime();

        ImageView back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });
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
                        sleep(500); //直接调用
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }
        };
        t.start();


    }


    public void refresh() {
        //car pad 电量刷新
        BatteryManager batteryManager = (BatteryManager)getSystemService(BATTERY_SERVICE);
        MainActivity.pad_battery = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatusIntent = registerReceiver(null, ifilter);
        //如果设备正在充电，可以提取当前的充电状态和充电方式（无论是通过 USB 还是交流充电器），如下所示：

        // Are we charging / charged?
        int status = batteryStatusIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;
        if (isCharging){
            ImageView pad_batt=findViewById(R.id.pad_battery_kuang);
            pad_batt.setImageDrawable(getResources().getDrawable(R.drawable.charging));
        }
        else {
            ImageView pad_batt=findViewById(R.id.pad_battery_kuang);
            pad_batt.setImageDrawable(getResources().getDrawable(R.drawable.batttery70));
        }


        TextView pad_battery_show=findViewById(R.id.pad_battery_show);
        pad_battery_show.setText(MainActivity.pad_battery+"%");

        TextView car_battery_show=findViewById(R.id.car_battery_show);
        car_battery_show.setText(MainActivity.car_battery+"%");

    };

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
