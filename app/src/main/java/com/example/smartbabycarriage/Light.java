package com.example.smartbabycarriage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;


public class Light extends AppCompatActivity implements Screensaver.OnTimeOutListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);

        MainActivity.mScreensaver.resetTime();
        final ImageView logo_leg_light=findViewById(R.id.logo_leg_light);
        logo_leg_light.setOnClickListener(new logo_leg_light_click());

        ImageView back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });


        final ImageView night_light=findViewById(R.id.night_light);
        night_light.setOnClickListener(new night_light_click());


        final Handler handler= new Handler();

        final Runnable runnable = new Runnable() {
            public void run() {
                if(!MainActivity.logo_leg_light_state){
                    logo_leg_light.setImageDrawable(getResources().getDrawable(R.drawable.light_off));
                }
                else {
                    logo_leg_light.setImageDrawable(getResources().getDrawable(R.drawable.light_on));
                }
                if(!MainActivity.night_light_state){
                    night_light.setImageDrawable(getResources().getDrawable(R.drawable.light_off));
                }
                else {
                    night_light.setImageDrawable(getResources().getDrawable(R.drawable.light_on));
                }
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

    class logo_leg_light_click implements View.OnClickListener{
        @Override
        public void onClick(View v){
            byte[] sendbytes1;
            byte[] sendbytes2;
            sendbytes1 =Hex.strToHexBytes("aa0103030111");
            sendbytes2 =Hex.strToHexBytes("aa0103030211");
            try {
                MainActivity.outputStream_constant.write(sendbytes1);

                MainActivity.outputStream_constant.write(sendbytes2);

                //ReceiveThread rt1=new ReceiveThread();
                //rt1.start();
            }catch (IOException e){
                e.printStackTrace();
            }


        }
    }

    class night_light_click implements View.OnClickListener{
        @Override
        public void onClick(View v){
            byte[] sendbytes;
            sendbytes =Hex.strToHexBytes("aa0103030311");
            try {
                MainActivity.outputStream_constant.write(sendbytes);

                //ReceiveThread rt1=new ReceiveThread();
                //rt1.start();
            }catch (IOException e){
                e.printStackTrace();
            }


        }
    }


    public void refresh(){
        TextView ad=findViewById(R.id.speed6);
        ad.setText(MainActivity.speed+"km/h");
        MainActivity.last_speed=MainActivity.speed;
        ImageView acc_state= findViewById(R.id.jiasu_icon);
        ImageView dec_state=findViewById(R.id.jiansu_icon);
        TextView slope=findViewById(R.id.slope);
        slope.setText("坡度:"+MainActivity.angle+"°");
        if(MainActivity.accelerate_flag) {
            acc_state.setImageDrawable(getResources().getDrawable(R.drawable.bk_grey));
        }
        else {
            acc_state.setImageDrawable(getResources().getDrawable(R.drawable.bk_normal));
        }
        if(MainActivity.decelerate_flag) {
            dec_state.setImageDrawable(getResources().getDrawable(R.drawable.bk_grey));
        }
        else {
            dec_state.setImageDrawable(getResources().getDrawable(R.drawable.bk_normal));
        }
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
