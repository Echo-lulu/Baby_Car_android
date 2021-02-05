package com.example.smartbabycarriage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

import android_serialport_api.SerialPort;

public  class MainActivity extends AppCompatActivity implements Screensaver.OnTimeOutListener {
    public static String state="默认";
    public static boolean isStart=false;
    public static boolean isStart_constant=true;

    public static int unfold_state=0;
    public static int fold_state=0;
    public static double speed=0.0;
    public static double f_speed=0.0;
    public static double last_speed=0.0;
    public static String str_speed="";
    public static int angle=0;
    public static int journey=0;
    public static int car_battery=100;
    public static int pad_battery=100;
    public static boolean accelerate_flag;
    public static boolean decelerate_flag;
    public static boolean weight=false;

    public static byte[] heart=Hex.strToHexBytes("00000000");
    public static int count=0;

    public static int gear;//动力级别

//    public static SerialPort mSerialPort;
//    public static InputStream inputStream;
//    public static OutputStream outputStream ;

    public static SerialPort mSerialPort_constant;
    public static InputStream inputStream_constant;
    public static OutputStream outputStream_constant ;

    public static boolean logo_leg_light_state=false ;
    public static boolean night_light_state=false ;

    public static Screensaver mScreensaver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化
        ini();
        mScreensaver = new Screensaver(6000); //定时5秒
        mScreensaver.setOnTimeOutListener(this); //监听
        mScreensaver.start(); //开始计时


        try {
//            mSerialPort = new SerialPort(new File("/dev/ttymxc1"), 9600, 0);
//            inputStream = mSerialPort.getInputStream();
//            outputStream = mSerialPort.getOutputStream();
//            isStart = true;
            mSerialPort_constant = new SerialPort(new File("/dev/ttymxc2"), 9600, 0);
            inputStream_constant = mSerialPort_constant.getInputStream();
            outputStream_constant = mSerialPort_constant.getOutputStream();
            isStart_constant=true;
//            ReceiveThread rt=new ReceiveThread();
//            rt.start();
            constant_ReceiveThread con_re=new constant_ReceiveThread();
            con_re.start();

        }catch (IOException e){
            e.printStackTrace();
        }



        //实时状态显示
         //final TextView speed_=findViewById(R.id.speed6);

        //线程用来实时发送协议来获取状态
         final Handler handler_constant_send= new Handler();
         final Runnable runnable1 = new Runnable() {
            public void run() {

                byte[] sendbytes1;
                byte[] sendbytes2;
                byte[] sendbytes3;
                byte[] sendbytes4;
                byte[] sendbytes5;
                byte[]  heart1;

                if(count==65535){
                    count=1;
                }
                String s=String.format("%08X",count);
                heart1= Hex.strToHexBytes("aa02040601"+s);
                count++;
                sendbytes1 =Hex.strToHexBytes("aa010405040000");//获取速度
                sendbytes2 =Hex.strToHexBytes("aa010305020000");//获取负重
                sendbytes3 =Hex.strToHexBytes("aa010405010000");//获取电量
                sendbytes4 =Hex.strToHexBytes("aa010405060000");//获取倾角

                try {
                    if(heart1!=null){
                        outputStream_constant.write(heart1);
                    }
                    outputStream_constant.write(sendbytes1);
                    outputStream_constant.write(sendbytes2);
                    outputStream_constant.write(sendbytes3);
                    outputStream_constant.write(sendbytes4);
                }catch (IOException e){
                    e.printStackTrace();
                    System.out.println(heart1);
                }
            }
        };
         Thread t1 = new Thread(){
            //public boolean isrun=true;
            @Override
            public void run() {
                while(true)
                {
                    handler_constant_send.post(runnable1); //加入到消息队列 　这样没有启动新的线程，虽然没有报异常。但仍然阻塞ProgressDialog的显示
                    try {
                        sleep(1000); //直接调用
                    } catch (InterruptedException e) {
                        return;
                    }

                }
            }
        };
        t1.start();
    }


    //监听线程
    public Handler handler_constant_listen = new Handler();
    public  Handler test=new Handler();
    public class constant_ReceiveThread extends Thread {
        @Override
        public void run() {
            while (isStart_constant)
            {
                byte[] readData2 = new byte[256];
                try {
//                    if (inputStream_constant == null) {
//                        continue;
//                    }
                    int size =inputStream_constant.read(readData2);

                    if (size > 0) {
                        String readString = null;
                        try {
                            readString = Hex.getHexString(readData2);
                            int unfold_state_loc=readString.indexOf("aa02030201");
                            if (unfold_state_loc>=0){
                                String unfold_state_tmp=readString.substring(unfold_state_loc+10,unfold_state_loc+12);
                                if (unfold_state_tmp.equals("00")){
                                    unfold_state=1;//小车展开成功
                                    fold_state=0;
                                }
                                if (unfold_state_tmp.equals("01")){
                                    unfold_state=2;//小车展开卡死
                                    fold_state=1;
                                }
                                if (unfold_state_tmp.equals("02")){
                                    unfold_state=3;//小车展开超时
                                    fold_state=1;
                                }

                            }
                            int fold_state_loc=readString.indexOf("aa02030201");
                            if (fold_state_loc>=0){
                                String fold_state_tmp=readString.substring(unfold_state_loc+10,unfold_state_loc+12);
                                if (fold_state_tmp.equals("00")){
                                    unfold_state=0;
                                    fold_state=1;//小车折叠成功
                                }
                                if (fold_state_tmp.equals("01")){
                                    unfold_state=1;
                                    fold_state=2;//小车折叠卡死
                                }
                                if (fold_state_tmp.equals("02")){
                                    unfold_state=1;
                                    fold_state=3;//小车折叠超时
                                }
                            }

                            int speed_loc=readString.indexOf("aa02040504");
                            if(speed_loc>=0){
                                String speed_temp=readString.substring(speed_loc+10,speed_loc+18);
                                speed=Integer.parseInt(speed_temp,16)/100;

                                f_speed=speed*3.6;
                                java.text.DecimalFormat myformat=new java.text.DecimalFormat("0.0");
                                str_speed= myformat.format(f_speed);

                            }

                            int journey_loc=readString.indexOf("aa02040505");
                            if(journey_loc>=0){
                                String journey_temp=readString.substring(journey_loc+10,journey_loc+18);
                                journey=Integer.parseInt(journey_temp,16)/100;
                            }

                            int weight_loc=readString.indexOf("aa02030502");
                            if (weight_loc>=0){
                                String weight_temp=readString.substring(weight_loc+10,weight_loc+12);
                                if(weight_temp.equals("00")){
                                    weight=false;
                                }
                                if(weight_temp.equals("01")){
                                    weight=true;
                                }
                            }

                            int battery_loc=readString.indexOf("aa02040501");
                            if(battery_loc>=0){
                                String battery_temp=readString.substring(battery_loc+10,battery_loc+18);
                                car_battery=Integer.parseInt(battery_temp,16)/100;
                            }


                            int angle_loc=readString.indexOf("aa02040501");
                            if (angle_loc>=0){
                                String angel_temp=readString.substring(angle_loc+10,angle_loc+18);
                                angle=Integer.parseInt(angel_temp,16)/100;
                            }

                            if(speed>last_speed){
                                accelerate_flag=true;
                                decelerate_flag=false;
                            }
                            if (speed<last_speed){
                                decelerate_flag=true;
                                accelerate_flag=false;
                            }
                            if (speed==last_speed){
                                accelerate_flag=false;
                                decelerate_flag=false;
                            }
                            last_speed=speed;
                            handler_constant_listen.post(new Runnable() {
                                @Override
                                public void run() {
                                    //刷新菜单栏
                                    refresh();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }




    public void refresh(){

        //菜单栏婴儿图标
        ImageView kid=findViewById(R.id.baobao);
        if (weight) {
            kid.setImageDrawable(getResources().getDrawable(R.drawable.baobao));
        }else {
            kid.setImageDrawable(getResources().getDrawable(R.drawable.wubaobao));
        }
        //菜单栏速度图标
        TextView ad=findViewById(R.id.speed6);
        ad.setText(str_speed);

        ImageView acc_state= findViewById(R.id.jiasu_icon);
        ImageView dec_state=findViewById(R.id.jiansu_icon);

        if(accelerate_flag) {
            acc_state.setImageDrawable(getResources().getDrawable(R.drawable.jiasu2));
            TextView text_acc=findViewById(R.id.accelerate);
            text_acc.setTextColor(Color.parseColor("#00FEEF"));
        }
        else {
            acc_state.setImageDrawable(getResources().getDrawable(R.drawable.jiasu1));
            TextView text_acc=findViewById(R.id.accelerate);
            text_acc.setTextColor(Color.parseColor("#FFFFFF"));
        }
        if(decelerate_flag) {
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
        slope.setText(angle+"°");

    }

    public void  menu_btn_onClick(View v){
        Intent i = new Intent();
        i.setClass(MainActivity.this, Menu.class);
        startActivity(i);
    }
    public  void ini(){

        ImageView Fold=findViewById(R.id.main_fold);
        Fold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

                Intent i = new Intent();
                i.setClass(MainActivity.this, Fold.class);
                startActivity(i);
            }
        });


        ImageView Early_edu=findViewById(R.id.main_early_edu);
        Early_edu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

                Intent i = new Intent();
                i.setClass(MainActivity.this, Early_edu.class);
                startActivity(i);
            }
        });


        ImageView Air_pure=findViewById(R.id.main_air_pure);
        Air_pure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent i = new Intent();
                i.setClass(MainActivity.this, Air_pure.class);
                startActivity(i);
            }
        });


        ImageView Light=findViewById(R.id.main_light);
        Light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent i = new Intent();
                i.setClass(MainActivity.this, Light.class);
                startActivity(i);
            }
        });


        ImageView Battery=findViewById(R.id.main_battery);
        Battery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent i = new Intent();
                i.setClass(MainActivity.this, Battery.class);
                startActivity(i);
            }
        });

        ImageView GPS_btn=findViewById(R.id.main_gps);
        GPS_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick( View v){
                ComponentName componentName = new ComponentName("cn.gogocity.suibian", "cn.gogocity.suibian.activities.ARMainActivity");//这里是 包名  以及 页面类的全称
                Intent intent = new Intent();
                intent.setComponent(componentName);
                intent.putExtra("type", "110");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        ImageView Car_state=findViewById(R.id.main_car_state);
        Car_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent i = new Intent();
                i.setClass(MainActivity.this, Car_state.class);
                startActivity(i);
            }
        });


        ImageView Camera=findViewById(R.id.main_camera);
        Camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent2 =  new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent2);
            }
        });


//        ImageView Help=findViewById(R.id.);
//        Help.setOnClickListener(new Help_click());

        ImageView Settings1=findViewById(R.id.main_settings);
        Settings1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivity(intent2);
            }
        });



    }

    protected void connectServerWithTCPSocket() {

        Socket socket;
        try {// 创建一个Socket对象，并指定服务端的IP及端口号
            socket = new Socket("192.168.1.32", 1989);
            // 创建一个InputStream用户读取要发送的文件。
            InputStream inputStream = new FileInputStream("e://a.txt");
            // 获取Socket的OutputStream对象用于发送数据。
            OutputStream outputStream = socket.getOutputStream();
            // 创建一个byte类型的buffer字节数组，用于存放读取的本地文件
            byte buffer[] = new byte[4 * 1024];
            int temp = 0;
            // 循环读取文件
            while ((temp = inputStream.read(buffer)) != -1) {
                // 把数据写入到OuputStream对象中
                outputStream.write(buffer, 0, temp);
            }
            // 发送读取的数据到服务端
            outputStream.flush();

            /** 或创建一个报文，使用BufferedWriter写入,看你的需求 **/
//          String socketData = "[2143213;21343fjks;213]";
//          BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
//                  socket.getOutputStream()));
//          writer.write(socketData.replace("\n", " ") + "\n");
//          writer.flush();
            /************************************************/
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 当触摸就会执行此方法
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mScreensaver.resetTime(); //重置时间
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 当使用键盘就会执行此方法
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        mScreensaver.resetTime(); //重置时间
        return super.dispatchKeyEvent(event);
    }

    /**
     * 时间到就会执行此方法
     */
    @Override
    public void onTimeOut(Screensaver screensaver) {
        Intent i = new Intent();
        i.setClass(MainActivity.this, Sleep.class);
        startActivity(i);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mScreensaver.stop(); //停止计时
    }


}












//public class ReceiveThread extends Thread {
//        @Override
//        public void run() {
//            while (isStart) {
////                if (inputStream == null) {
////                    return;
////                }
//                byte[] readData = new byte[12];
//                try {
//                    int size = inputStream.read(readData);
//
//                    if (size > 0) {
//                        String readString = null;
//                        try {
//                            readString = Hex.getHexString(readData);
//                            Message msg = new Message();
//                            msg.arg1= 0;
//                            Bundle bundle = new Bundle();
//                            bundle.putString("text1",readString);  //往Bundle中存放数据
//                            msg.setData(bundle);//mes利用Bundle传递数据
//                            //小车折叠展开状态返回
//                            if(readString.contains("aa0203020100")){//小车展开成功
//                                msg.arg1= 1;
//                            }
//                            if(readString.contains("aa0203020101")){//小车展开卡死
//                                msg.arg1= 2;
//                            }
//                            if(readString.contains("aa0203020102")){//小车展开超时
//                                msg.arg1= 3;
//                            }
//                            if(readString.contains("aa0203020200")){//小车折叠成功
//                                msg.arg1=4;
//                            }
//                            if(readString.contains("aa0203020201")){//小车卡死成功
//                                msg.arg1= 5;
//                            }
//                            if(readString.contains("aa0203020202")){//小车折叠超时
//                                msg.arg1= 6;
//                            }
//                            //灯光控制信息返回
//                            if(readString.contains("aa0203030100")){//小车logo_腿灯关闭
//                                msg.arg1= 7;
//                            }
//                            if(readString.contains("aa0203030111")){//小车logo_腿灯打开
//                                msg.arg1= 8;
//                            }
//                            if(readString.contains("aa0203030300")){//小车夜行灯关闭
//                                msg.arg1= 9;
//                            }
//                            if(readString.contains("aa0203030311")){//小车夜行灯打开
//                                msg.arg1= 10;
//                            }
//                            handler.sendMessage(msg);
//
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        }
//    }



//    public Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//
//            switch (msg.arg1){
//                default:
//                    String str1 = msg.getData().getString("text1");//
//                    true_menu=str1;
//                    break;
//                case 1:
//                    true_menu="小车展开成功";
//
//                    break;
//                case 2:
//                    true_menu="小车展开卡死";
//                    break;
//                case 3:
//                    true_menu="小车展开超时";
//                    break;
//                case 4:
//                    true_menu="小车折叠成功";
//                    break;
//                case 5:
//                    true_menu="小车折叠卡死";
//                    break;
//                case 6:
//                    true_menu="小车折叠超时";
//                    break;
//                case 7:
//                    logo_leg_light_state=false;
//                    break;
//                case 8:
//                    logo_leg_light_state=true;
//                    break;
//                case 9:
//                    night_light_state=false;
//                    break;
//                case 10:
//                    night_light_state=true;
//                    break;
//            }}
//
//    };