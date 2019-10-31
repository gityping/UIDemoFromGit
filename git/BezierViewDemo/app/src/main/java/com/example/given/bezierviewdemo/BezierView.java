package com.example.given.bezierviewdemo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by Given on 2019/1/12 0012.
 * 带贝塞尔曲线效果的view
 * 参考博客：https://www.cnblogs.com/wjtaigwh/p/6750184.html，https://blog.csdn.net/u013087553/article/details/68490170
 */

public class BezierView extends View {

    private int duration1;//水位上升动画总时间
    private int duration2;//水波动画周期时长
    private int waveWidth;//波纹宽度(长度)
    private int waveHeight;//波纹高度
    private int originY;//初始点Y坐标
    private int width;//控件宽度
    private int height;//控件高度
    private int dex;//水位水平的属性动画，向右移动的值
    private int dey;//水位上升值
    private int cirColor;//背景圆的颜色
    private int cirSideColor;//背景圆的边的颜色
    private Paint paint;//贝塞尔画笔
    private Path path;//贝塞尔路径
    private int radius;//圆的半径
    private Paint cirPaint;//画圆的画笔
    private Paint bgCirPaint;//话画背景圆画笔
    private ValueAnimator animator1;//水波动画
    private ValueAnimator animator2;//水位上升动画
    private Bitmap mbitmap;//用于创建一张空白图片
    private Canvas mCanvas;//用于画圆的画布

    public BezierView(Context context) {
        this(context, null);
    }

    public BezierView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BezierView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //初始化
        init(context, attrs);
    }

    /*初始化*/
    private void init(Context context, AttributeSet attrs) {
        //获取TypedArray,取出自定义属性
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.BezierView);
        //水位上升动画时间，默认8秒
        duration1 = array.getInteger(R.styleable.BezierView_duration1, 80000);
        //水波周期性移动动画时间,默认1200毫秒
        duration2 = array.getInteger(R.styleable.BezierView_duration2,1200);
        //水波长度,默认500
        waveWidth = array.getInteger(R.styleable.BezierView_waveWidth, 500);
        //水波高度，默认100
        waveHeight = array.getInteger(R.styleable.BezierView_waveHeight, 100);
        //水位初始点，Y坐标,如果没有设置，则默认是从控件的最低部开始，这里的9527作为一个标志
        originY = array.getInteger(R.styleable.BezierView_originY, 9527);
        //背景圆颜色，默认为白色
        cirColor = array.getColor(R.styleable.BezierView_cirColor,Color.WHITE);
        //背景圆的轮廓的颜色，默认为黑色
        cirSideColor = array.getColor(R.styleable.BezierView_cirSideColor,Color.BLACK);
        //释放资源
        array.recycle();

        //初始化贝塞尔画笔（波浪）
        paint = new Paint();
        paint.setColor(Color.BLUE);//画笔颜色
        paint.setAntiAlias(true);//抗锯齿
        paint.setDither(true);//防抖动
        paint.setStyle(Paint.Style.FILL_AND_STROKE);//FILL_AND_STROKE即绘制内容，也绘制轮廓
        //初始化贝塞尔路径
        path = new Path();
        //获取重叠部分
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        //初始化画背景圆画笔
        bgCirPaint = new Paint();
        bgCirPaint.setColor(cirColor);//白色背景
        bgCirPaint.setStyle(Paint.Style.FILL);//充满

        //初始化画圆的轮廓画笔
        cirPaint = new Paint();
        cirPaint.setAntiAlias(true);//抗锯齿
        cirPaint.setDither(true);//防抖动
        cirPaint.setColor(cirSideColor);//黄色轮廓
        cirPaint.setStyle(Paint.Style.STROKE);//画边
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);//获取宽的模式，是wrap_content还是macth_parent
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);//获取高的模式
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);//获取宽的尺寸
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);//获取高的尺寸

        if (widthMode == MeasureSpec.EXACTLY) {//如果指定了控件大小
            width = widthSize;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        }

        //水位起始点Y坐标
        if(originY==9527){//没有设置，这默认为控件底部,为了一开始用户开不见，增加水波高度进去
            originY = height+waveHeight;
        }

        //圆的半径
        radius = Math.min(width,height)/2;

        //创建一张空白图片
        mbitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mbitmap);

        //保存测量结果
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画背景圆
        mCanvas.drawCircle(radius,radius,radius,bgCirPaint);
        //不断的计算波浪的路径
        calculatePath();
        //在背景圆上绘制水波
        mCanvas.drawPath(path,paint);
        //将画好的圆绘制到画布上
        canvas.drawBitmap(mbitmap,0,0,null);
        //绘制圆的轮廓（边）
        canvas.drawCircle(radius,radius,radius,cirPaint);
    }

    /*计算波浪的路径*/
    private void calculatePath() {
        //初始点
        path.moveTo(-waveWidth + dex, originY - dey);
        for (int i = -waveWidth; i < width + waveWidth; i += waveWidth) {
            //三阶贝塞尔曲线
//            path.rCubicTo(waveHeight/4,-waveHeight,waveWidth/4*3,waveHeight,waveWidth,0);

            //二阶贝塞尔曲线绘制
            path.rQuadTo(waveWidth / 4, -waveHeight, waveWidth / 2, 0);
            path.rQuadTo(waveWidth / 4, waveHeight, waveWidth / 2, 0);
        }

        //绘制连线
        path.lineTo(width, height);
        path.lineTo(0, height);
        path.close();//关闭当前轮廓。如果当前点不等于轮廓的第一个点，则自动添加线段,使其形成闭区间。
    }

    /*设置水波画笔颜色*/
    public void setPaintColor(int color){
        paint.setColor(color);
    }

    /*动画效果*/
    public void startAnimation() {
        //初始化水波效果动画
        animator1 = ValueAnimator.ofFloat(0, 1);//定义一个值从0到1的控制值
        animator1.setDuration(duration2);//水波移动周期时长
        animator1.setRepeatCount(ValueAnimator.INFINITE);//设置循环次数，0为1次，无穷尽的一直执行：ValueAnimator.INFINITE
        animator1.setInterpolator(new LinearInterpolator());//插值器，线性匀速播放动画，不设置其实也是默认匀速
        //监听
        animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //获取属性动画当前值（0-1）
                float fraction = (float) animator1.getAnimatedValue();
                dex = (int) (waveWidth * fraction);//根据动画进行进度，动态修改整条波浪线的长度
                //清空画布
                if (path != null)
                    path.reset();//TODO 作者原文少了这一步
                //刷新
                postInvalidate();
            }
        });
        //启动动画
        animator1.start();

        //初始化水位上升动画
        animator2 = ValueAnimator.ofFloat(0, 1);//定义一个值从0到1的控制值
        animator2.setDuration(duration1);//水波上升周期时长
        animator2.setRepeatCount(0);//设置循环次数，0为1次，无穷尽的一直执行：ValueAnimator.INFINITE
        animator2.setInterpolator(new LinearInterpolator());//插值器，线性匀速播放动画，不设置其实也是默认匀速
        //监听
        animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //获取属性动画当前值（0-1）
                float fraction = (float) animator2.getAnimatedValue();
                dey = (int) ((height+waveHeight*2) * fraction);//根据动画进度，dey的值会从0慢慢接近于heitht+waveHeight*2的高度
                //清空画布
                if (path != null)
                    path.reset();//TODO 作者原文少了这一步
                //刷新
                postInvalidate();
            }
        });
        //启动动画
        animator2.start();
    }

}
