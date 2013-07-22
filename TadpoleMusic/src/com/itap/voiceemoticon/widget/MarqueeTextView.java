package com.itap.voiceemoticon.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.Scroller;
import android.widget.TextView;

/**
 * 跑马灯效果
 * <br>==========================
 * <br> author：Zenip
 * <br> email：lxyczh@gmail.com
 * <br> create：2013-1-26下午12:02:43
 * <br>==========================
 */

public class MarqueeTextView extends TextView implements Runnable {
    private int currentScrollX;// 当前滚动的位置 
    private boolean isStop = false;
    private int textWidth;
    private String mLastTextValue = null;
    private Scroller mScroller;
    

    public MarqueeTextView(Context context) {
        super(context);
        init();
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void init() {
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        getTextWidth();
    }

    /**
     * 获取文字宽度
     */
    private void getTextWidth() {
        Paint paint = this.getPaint();
        String str = this.getText().toString();
        if (!str.equals(mLastTextValue)) {
            textWidth = (int) paint.measureText(str);
        }
    }

    @Override
    public void run() {
        currentScrollX -= 2;// 滚动速度 
        scrollTo(currentScrollX, 0);
        if (isStop) {
            return;
        }
        if (getScrollX() <= -(this.getWidth())) {
            scrollTo(textWidth, 0);
            currentScrollX = textWidth;
        }
        postDelayed(this, 30);
    }

    // 开始滚动 
    public void startScroll() {
        isStop = false;
        this.removeCallbacks(this);
        post(this);
    }

    // 停止滚动 
    public void stopScroll() {
        isStop = true;
        currentScrollX = 0;
        post(this);
    }

    // 从头开始滚动 
    public void startFor0() {
        currentScrollX = 0;
        startScroll();
    }
}