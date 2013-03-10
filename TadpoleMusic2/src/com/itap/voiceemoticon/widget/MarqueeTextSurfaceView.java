package com.itap.voiceemoticon.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.tadpolemusic.R;
import com.tadpolemusic.TMLog;

/**
 * 跑马灯效果
 * <br>==========================
 * <br> author：Zenip
 * <br> email：lxyczh@gmail.com
 * <br> create：2013-1-26下午12:02:43
 * <br>==========================
 */
public class MarqueeTextSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    static final String TAG = "MarqueeTextSurfaceView";

    private SurfaceHolder mHolder;
    private MyThread myThread;
    private String mText = "Zenip, MarqueeTextSurfaceView";
    private int xOffset = 0;
    private boolean isSurfaceValid = false;

    public MarqueeTextSurfaceView(Context context) {
        super(context);
        init();
    }

    public MarqueeTextSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MarqueeTextSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init() {
        setZOrderOnTop(true);
        mHolder = getHolder();
        mHolder.addCallback(this);
        myThread = new MyThread(mHolder);
        mHolder.setFormat(PixelFormat.TRANSPARENT);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        TMLog.step(TAG, "surfaceChanged");
        holder.setFixedSize(width, height);
    }

    public void setText(String str) {
        mText = str;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        TMLog.step(TAG, "surfaceCreated");
        isSurfaceValid = true;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        TMLog.step(TAG, "surfaceDestroyed");
        isSurfaceValid = false;
    }

    class MyThread extends Thread {
        private SurfaceHolder holder;
        public boolean canRun;

        public MyThread(SurfaceHolder holder) {
            this.canRun = true;
            this.holder = holder;
        }

        @Override
        public void run() {
            Canvas c = null;
            while (canRun) {
                try {
                    c = holder.lockCanvas();//锁定画布，一般在锁定后就可以通过其返回的画布对象Canvas，在其上面画图等操作了。
                    handlerDrawInternal(c);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (c != null) {
                        holder.unlockCanvasAndPost(c);//结束锁定画图，并提交改变。
                    }
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }//睡眠时间为1秒

            }



            // reset 
            try {
                xOffset = 0;
                c = holder.lockCanvas();
                handlerDrawInternal(c);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (c != null) {
                    holder.unlockCanvasAndPost(c);
                }
            }
        }
    }


    public void startScroll() {
        if (!isSurfaceValid) {
            return;
        }
        myThread.canRun = true;
        if (!myThread.isAlive()) {
            xOffset = 0;
            myThread = new MyThread(mHolder);
            myThread.start();
        }
    }

    public void stopScroll() {
        myThread.canRun = false;
        xOffset = 0;
    }



    private void handlerDrawInternal(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG); //创建画笔
        paint.setTextSize(spToPixel(getContext(), 12f));
        paint.setColor(Color.WHITE);

        // font height
        float fontHeight = getFontHeight(paint);

        int height = mHolder.getSurfaceFrame().height();
        int width = mHolder.getSurfaceFrame().width();

        Rect textBound = new Rect();
        paint.getTextBounds(mText.toCharArray(), 0, mText.length(), textBound);

        int textWidth = textBound.width();


        // base line
        float baseLineY = height / 2 + fontHeight / 2;
        canvas.drawText(mText, xOffset, baseLineY, paint);

        if (xOffset > (width + 10)) {
            xOffset = -textWidth - 10;
        } else {
            xOffset += 2;
        }

    }

    private static float getFontHeight(Paint paint) {
        Rect bounds = new Rect();
        paint.getTextBounds("这", 0, 1, bounds);
        return bounds.height();
    }

    private static float pixelsToSp(Context context, Float px) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return px / scaledDensity;
    }

    private static float spToPixel(Context context, Float sp) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return sp * scaledDensity;
    }

}