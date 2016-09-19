package com.yushilei.myshop;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.graphics.Point;
import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity implements ItemAdapter.AddClickListener, Animator.AnimatorListener {

    private ImageView shopImg;//购物车 IMG
    private RelativeLayout container;//ListView 购物车View的父布局
    private ListView itemLv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        initViews();
    }

    private void initViews() {
        ItemAdapter adapter = new ItemAdapter(this);
        //当前Activity实现 adapter内部 点击的回调
        adapter.setListener(this);
        itemLv.setAdapter(adapter);
    }

    /**
     * ListView + 点击回调方法
     */
    @Override
    public void add(View addV) {
        int[] childCoordinate = new int[2];
        int[] parentCoordinate = new int[2];
        int[] shopCoordinate = new int[2];
        //1.分别获取被点击View、父布局、购物车在屏幕上的坐标xy。
        addV.getLocationInWindow(childCoordinate);
        container.getLocationInWindow(parentCoordinate);
        shopImg.getLocationInWindow(shopCoordinate);

        //2.自定义ImageView 继承ImageView
        MoveImageView img = new MoveImageView(this);
        img.setImageResource(R.mipmap.heart1);
        //3.设置img在父布局中的坐标位置
        img.setX(childCoordinate[0] - parentCoordinate[0]);
        img.setY(childCoordinate[1] - parentCoordinate[1]);
        //4.父布局添加该Img
        container.addView(img);

        //5.利用 二次贝塞尔曲线 需首先计算出 MoveImageView的2个数据点和一个控制点
        PointF startP = new PointF();
        PointF endP = new PointF();
        PointF controlP = new PointF();
        //开始的数据点坐标就是 addV的坐标
        startP.x = childCoordinate[0] - parentCoordinate[0];
        startP.y = childCoordinate[1] - parentCoordinate[1];
        //结束的数据点坐标就是 shopImg的坐标
        endP.x = shopCoordinate[0] - parentCoordinate[0];
        endP.y = shopCoordinate[1] - parentCoordinate[1];
        //控制点坐标 x等于 购物车x；y等于 addV的y
        controlP.x = endP.x;
        controlP.y = startP.y;

        //启动属性动画
        ObjectAnimator animator = ObjectAnimator.ofObject(img, "mPointF",
                new PointFTypeEvaluator(controlP), startP, endP);
        animator.setDuration(1000);
        animator.addListener(this);
        animator.start();
    }

    @Override
    public void onAnimationStart(Animator animation) {
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        //动画结束后 父布局移除 img
        Object target = ((ObjectAnimator) animation).getTarget();
        container.removeView((View) target);
        //shopImg 开始一个放大动画
        Animation scaleAnim = AnimationUtils.loadAnimation(this, R.anim.shop_scale);
        shopImg.startAnimation(scaleAnim);
    }

    @Override
    public void onAnimationCancel(Animator animation) {
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
    }

    /**
     * 自定义估值器
     */
    public class PointFTypeEvaluator implements TypeEvaluator<PointF> {
        /**
         * 每个估值器对应一个属性动画，每个属性动画仅对应唯一一个控制点
         */
        PointF control;
        /**
         * 估值器返回值
         */
        PointF mPointF = new PointF();

        public PointFTypeEvaluator(PointF control) {
            this.control = control;
        }

        @Override
        public PointF evaluate(float fraction, PointF startValue, PointF endValue) {
            return getBezierPoint(startValue, endValue, control, fraction);
        }

        /**
         * 二次贝塞尔曲线公式
         *
         * @param start   开始的数据点
         * @param end     结束的数据点
         * @param control 控制点
         * @param t       float 0-1
         * @return 不同t对应的PointF
         */
        private PointF getBezierPoint(PointF start, PointF end, PointF control, float t) {
            mPointF.x = (1 - t) * (1 - t) * start.x + 2 * t * (1 - t) * control.x + t * t * end.x;
            mPointF.y = (1 - t) * (1 - t) * start.y + 2 * t * (1 - t) * control.y + t * t * end.y;
            return mPointF;
        }
    }

    private void findViews() {
        shopImg = (ImageView) findViewById(R.id.main_img);
        container = (RelativeLayout) findViewById(R.id.main_container);
        itemLv = (ListView) findViewById(R.id.main_lv);
    }
}
