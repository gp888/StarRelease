package com.xingfabu.direct.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by guoping on 16/9/28.
 */
public class CustomViewPager extends ViewPager{

    private boolean isPagingEnabled = true;//是否禁止滑动

    public CustomViewPager(Context context) {

        super(context);

    }

    public CustomViewPager(Context context, AttributeSet attrs) {

        super(context, attrs);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return this.isPagingEnabled && super.onTouchEvent(event);

    }

    @Override

    public boolean onInterceptTouchEvent(MotionEvent event) {

        return this.isPagingEnabled && super.onInterceptTouchEvent(event);

    }

    public void setPagingEnabled(boolean b) {

        this.isPagingEnabled = b;

    }
}
