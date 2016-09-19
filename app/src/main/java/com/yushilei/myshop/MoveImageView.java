package com.yushilei.myshop;

import android.content.Context;
import android.graphics.PointF;
import android.widget.ImageView;

/**
 * @author by  yushilei.
 * @time 2016/9/19 -14:27.
 * @Desc
 */
public class MoveImageView extends ImageView {

    public MoveImageView(Context context) {
        super(context);
    }

    public void setMPointF(PointF pointF) {
        setX(pointF.x);
        setY(pointF.y);
    }
}
