package com.example.smartbabycarriage;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.IOException;

import static java.lang.Thread.*;


public  class Fold extends AppCompatActivity  implements Screensaver.OnTimeOutListener {
    


    private TextView state1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fold);

        MainActivity.mScreensaver.resetTime();

        final ImageView foldbtn = findViewById(R.id.fold);
        foldbtn.setOnClickListener(new zhedie());

        final ImageView unfoldbtn = findViewById(R.id.unfold);
        ImageView lockbtn = findViewById(R.id.lock);
        unfoldbtn.setOnClickListener(new click2());

        ImageView back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
                MainActivity.mScreensaver.resetTime(); //重置时间
            }
        });

//        final TextView state1 = findViewById(R.id.state1);
//        state1.setText(MainActivity.true_menu);



        final TextView speed_=findViewById(R.id.speed6);

        final Handler handler= new Handler();

        final Runnable runnable = new Runnable() {
            public void run() {
                if (MainActivity.fold_state==0){
                    foldbtn.setImageDrawable(getResources().getDrawable(R.drawable.zhedie));
                }
                if (MainActivity.fold_state==1){
                    foldbtn.setImageDrawable(getResources().getDrawable(R.drawable.zhedie));
                }
                if(MainActivity.fold_state==2|MainActivity.fold_state==3){
                    warning();
                }

                if (MainActivity.unfold_state==0){
                    unfoldbtn.setImageDrawable(getResources().getDrawable(R.drawable.zhankai));
                }
                if (MainActivity.unfold_state==1){
                    unfoldbtn.setImageDrawable(getResources().getDrawable(R.drawable.zhankai));
                }

                if(MainActivity.unfold_state==2|MainActivity.unfold_state==3){
                    warning();
                }
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
                        sleep(200); //直接调用
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }
        };
        t.start();
    }
    class zhedie implements View.OnClickListener{
        @Override
        public void onClick(View v){
                byte[] sendbytes;
                sendbytes =Hex.strToHexBytes("aa0103020200");
                try {
                    MainActivity.outputStream_constant.write(sendbytes);
                    MainActivity.outputStream_constant.flush();
                    //ReceiveThread rt1=new ReceiveThread();
                    //rt1.start();
                }catch (IOException e){
                    e.printStackTrace();
                }

        }
    }

    class click2 implements View.OnClickListener{
        @Override
        public void onClick(View v){
            byte[] sendbytes;
            sendbytes =Hex.strToHexBytes("aa0103020201");
            try {
                MainActivity.outputStream_constant.write(sendbytes);
                MainActivity.outputStream_constant.flush();
                //ReceiveThread rt2=new ReceiveThread();
                //rt2.start();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public void warning(){
        final ImageView warn=findViewById(R.id.warning);
        warn.setVisibility(View.VISIBLE);
        //加一个扬声器报警报警
        warn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                warn.setVisibility(View.INVISIBLE);
                return false;
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

