package com.example.given.bezierviewdemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static final String TAG = "Activity";

    private SeekBar sbRed;//红色进度条
    private TextView tvRed;//红色文字
    private SeekBar sbYellow;//黄色进度条
    private TextView tvYellow;//黄色文字
    private SeekBar sbBlue;//蓝色进度条
    private TextView tvBlue;//蓝色文字
    private BezierView bezierView;//贝塞尔控件
    private TextView tvButton;//界面上的“按钮”
    //SeekBar当前进度值
    private String redNumber;
    private String yellowNumber;

    private String blueNumber;
    private SharedPreferences sharedPreferences;//偏好设置

    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化偏好设置
        initSharedPreferences();
        //初始化控件
        assignViews();
        //设置监听
        sbRed.setOnSeekBarChangeListener(new MySeekBarListener());
        sbYellow.setOnSeekBarChangeListener(new MySeekBarListener());
        sbBlue.setOnSeekBarChangeListener(new MySeekBarListener());
        //按钮监听
        setButtonListener();
        Log.i(TAG, "onCreate()");
    }

    private void setButtonListener() {
        //确定按钮
        tvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //启动动画
                handler.sendEmptyMessage(1);
            }
        });
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //设置背景色
            setBezierViewColor();
            //启动动画
            bezierView.startAnimation();
        }
    };

    /*初始化话偏好设置*/
    private void initSharedPreferences() {
        sharedPreferences = getSharedPreferences("shared_data", MODE_PRIVATE);//文件名，存储方式
        editor = sharedPreferences.edit();//初始化编辑器
        //通过名称查找偏好设置中的值，空则返回默认值"ff"
        redNumber = sharedPreferences.getString("redNumber", "7F");
        yellowNumber = sharedPreferences.getString("yellowNumber", "7F");
        blueNumber = sharedPreferences.getString("blueNumber", "7F");
    }

    /*监听类*/
    class MySeekBarListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {//i 进度，b 是否来自用户
            Log.i(TAG, "拖动结束，onProgressChanged");
            //将int值转为16进制（不转也可以，看后面用什么方法设置颜色）
            String position;
            if (i < 10) position = "0" + i;
            else if (i < 16) position = "0" + Integer.toHexString(i);
            else position = Integer.toHexString(i);

            switch (seekBar.getId()) {
                case R.id.sb_red://红色
                    redNumber = position;//保存值
                    editor.putString("redNumber", redNumber);//保存到偏好设置
                    tvRed.setText(Integer.valueOf(redNumber, 16) + "");//设置文本
                    break;
                case R.id.sb_yellow://黄色
                    yellowNumber = position;//保存值
                    editor.putString("yellowNumber", yellowNumber);//保存到偏好设置
                    tvYellow.setText(Integer.valueOf(yellowNumber, 16) + "");//设置文本
                    break;
                case R.id.sb_blue://蓝色
                    blueNumber = position;//保存值
                    tvBlue.setText(Integer.valueOf(blueNumber, 16) + "");//设置文本
                    editor.putString("blueNumber", blueNumber);//保存到偏好设置
                    break;
            }
            //提交偏好设置事务
            editor.commit();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            Log.i(TAG, "开始点击：onStartTrackingTouch");
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            Log.i(TAG, "停止点击：onStopTrackingTouch");
        }
    }

    /**
     * 设置背景色方法
     * blog：https://www.cnblogs.com/whycxb/p/6851660.html
     */
    private void setBezierViewColor() {
        Log.i(TAG, "setImageViewSrc");
        String color = "#" + redNumber + yellowNumber + blueNumber;
        bezierView.setPaintColor(Color.parseColor(color));
    }


    /*初始化控件*/
    private void assignViews() {
        sbRed = findViewById(R.id.sb_red);
        tvRed = findViewById(R.id.tv_red);
        sbYellow = findViewById(R.id.sb_yellow);
        tvYellow = findViewById(R.id.tv_yellow);
        sbBlue = findViewById(R.id.sb_blue);
        tvBlue = findViewById(R.id.tv_blue);
        bezierView = findViewById(R.id.bzv_wave);
        tvButton = findViewById(R.id.tv_button);

        //初始化图片背景色
        handler.sendEmptyMessage(1);
        //文本
        tvRed.setText(Integer.valueOf(redNumber, 16) + "");
        tvYellow.setText(Integer.valueOf(yellowNumber, 16) + "");
        tvBlue.setText(Integer.valueOf(blueNumber, 16) + "");
        //进度条
        sbRed.setProgress(Integer.valueOf(redNumber, 16));
        sbYellow.setProgress(Integer.valueOf(yellowNumber, 16));
        sbBlue.setProgress(Integer.valueOf(blueNumber, 16));
    }
}
