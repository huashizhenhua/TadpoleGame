package org.tadpole.widget;

import java.util.Arrays;
import java.util.HashMap;

import org.tadpole.adapter.IDragGridAdapter;
import org.tadpole.app.R;
import org.tadpole.common.TLog;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;

/**
 * 
 * a gridview that item can be drag for sorting
 * 
 * <br>==========================
 * <br> author：Zenip
 * <br> email：lxyczh@gmail.com
 * <br> create：2012-12-30下午8:55:52
 * <br>==========================
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DragGridView extends GridView {
    public static final int EVENT_START_DRAG = -1;
    public static final int EVENT_END_DRAG = -2;

    public static final int EVENT_SLIDING_PAGE = 0;
    public static final int EVENT_DEL_UP = 1;
    public static final int EVENT_DEL_COLOR_DEEP = 2;  // 删除按钮颜色变深
    public static final int EVENT_DEL_COLOR_LIGHT = 3;
    public static final int EVENT_DEL_CANCEL = 4;
    public static final int EVENT_DEL_DONE = 5;
    public static final int PAGE_UNKNOWN = 5;

    private static final String TAG = "DragGridView";

    private int dragPosition;
    private int dropPosition;

    ViewGroup fromView;
    Animation AtoB, BtoA, DelDone;
    int stopCount = 0;
    private G_PageListener pageListener;
    int movePageNum;
    private G_ItemChangeListener itemListener;

    private int mLastX, xtox;
    boolean isCountXY = false;
    private int mLastY, ytoy;

    private WindowManager windowManager;
    private WindowManager.LayoutParams windowParams;

    //  private int itemHeight, itemWidth;
    private ImageView iv_drag;

    private IDragGridAdapter mDragGridAdapter;

    public void setCurrentPage(int currentPage) {
        Configure.currentPage = currentPage;
    }


    public DragGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DragGridView(Context context) {
        super(context);
    }

    @Override
    @Deprecated
    public void setAdapter(ListAdapter adapter) {
        throw new RuntimeException("setDragGridAdapter(IDragAdapter adapter); must be called instead this");
    }

    public void setDragGridAdapter(IDragGridAdapter adapter) {
        mDragGridAdapter = adapter;
        super.setAdapter(adapter);
    }

    public boolean setOnItemLongClickListener(final MotionEvent ev) {
        this.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Configure.isDragging = true;
                DragGridView.this.onPage(EVENT_START_DRAG, PAGE_UNKNOWN);
                int x = (int) ev.getX();
                int y = (int) ev.getY();
                mLastX = x;
                mLastY = y;
                dragPosition = dropPosition = arg2;

                if (dragPosition == AdapterView.INVALID_POSITION) {
                    return false;
                }
                fromView = (ViewGroup) getChildAt(dragPosition - getFirstVisiblePosition());
                fromView.destroyDrawingCache();
                fromView.setDrawingCacheEnabled(true);
                fromView.setDrawingCacheBackgroundColor(0xff6DB7ED);
                Bitmap bm = Bitmap.createBitmap(fromView.getDrawingCache());

                Bitmap bitmap = Bitmap.createBitmap(bm, 8, 8, bm.getWidth() - 16, bm.getHeight() - 8);
                startDrag(bitmap, x, y);
                return false;
            };
        });
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d(TAG, "onInterceptTouchEvent ev.getAction() = " + ev.getAction() + ", isDragging = " + Configure.isDragging);
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            return setOnItemLongClickListener(ev);
        }
        return super.onInterceptTouchEvent(ev);
    }

    private void startDrag(final Bitmap bm, final int x, final int y) {

        windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);// "window"
        Animation disappear = AnimationUtils.loadAnimation(getContext(), R.anim.out);
        disappear.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub
                fromView.setVisibility(8);
                stopDrag();
                windowParams = new WindowManager.LayoutParams();
                windowParams.gravity = Gravity.TOP | Gravity.LEFT;
                windowParams.x = fromView.getLeft() + 28;
                windowParams.y = fromView.getTop() + (int) (40 * Configure.screenDensity) + 8;
                windowParams.alpha = 0.8f;
                windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                windowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;

                iv_drag = new ImageView(getContext());
                iv_drag.setImageBitmap(bm);
                windowManager.addView(iv_drag, windowParams);
                iv_drag.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.del_done));
                //dragImageView = iv_drag;
            }
        });
        fromView.startAnimation(disappear);
        DragGridView.this.onPage(EVENT_DEL_UP, -100);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        TLog.debug(TAG, "ididididid ===== %d movePageNum ===== %d onTouchEvent iv_drag =  " + iv_drag + ",eventAction=%d, x=%d,y=%d", getId(), movePageNum, ev.getAction(), (int) ev.getX(),
                (int) ev.getY());
        if (iv_drag != null && dragPosition != AdapterView.INVALID_POSITION) {
            int x = (int) ev.getX();
            int y = (int) ev.getY();
            switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (!isCountXY) {
                    xtox = x - mLastX;
                    ytoy = y - mLastY;
                    isCountXY = true;
                }
                onDrag(x, y);
                break;
            case MotionEvent.ACTION_UP:
                stopDrag();
                onDrop(x, y);
                break;
            }
        }
        return super.onTouchEvent(ev);
    }

    private void onDrag(int x, int y) {
        if (iv_drag != null) {
            windowParams.alpha = 0.8f;
            windowParams.x = (x - mLastX - xtox) + fromView.getLeft() + 28 - movePageNum * Configure.screenWidth;
            windowParams.y = (y - mLastY - ytoy) + fromView.getTop() + (int) (40 * Configure.screenDensity) + 8;
            TLog.debug(TAG, "onDrag(x=%d, y=%d)", windowParams.x, windowParams.y);
            windowManager.updateViewLayout(iv_drag, windowParams);
        }


        if ((x > (Configure.screenWidth / 2 - 100) && x < (Configure.screenWidth / 2 + 100)) && (y > Configure.screenHeight - 200)) {
            DragGridView.this.onPage(EVENT_DEL_COLOR_DEEP, -100);
            return;
        }
        if (Configure.isDelDark) {
            DragGridView.this.onPage(EVENT_DEL_COLOR_LIGHT, -200);
        }

        if (movePageNum > 0) {

            if ((x >= (movePageNum + 1) * Configure.screenWidth - iv_drag.getWidth() || x <= movePageNum * Configure.screenWidth) && !Configure.isChangingPage)
                stopCount++;
            else
                stopCount = 0;
            if (stopCount > 10) {
                stopCount = 0;
                if (x >= (movePageNum + 1) * Configure.screenWidth - iv_drag.getWidth() && Configure.currentPage < Configure.countPages - 1) {
                    Configure.isChangingPage = true;
                    DragGridView.this.onPage(EVENT_SLIDING_PAGE, ++Configure.currentPage);
                    movePageNum++;
                } else if (x <= movePageNum * Configure.screenWidth && Configure.currentPage > 0) {
                    Configure.isChangingPage = true;
                    DragGridView.this.onPage(EVENT_SLIDING_PAGE, --Configure.currentPage);
                    movePageNum--;
                }
            }
        } else {
            TLog.debug(TAG, "x = %d , xxx = %d  isChangingPage = %b", x, (movePageNum + 1) * Configure.screenWidth - iv_drag.getWidth(), Configure.isChangingPage);
            if ((x >= (movePageNum + 1) * Configure.screenWidth - iv_drag.getWidth() || x <= movePageNum * Configure.screenWidth) && !Configure.isChangingPage)
                stopCount++;
            else
                stopCount = 0;
            if (stopCount > 10) {
                stopCount = 0;
                if (x >= (movePageNum + 1) * Configure.screenWidth - iv_drag.getWidth() / 2 && Configure.currentPage < Configure.countPages - 1) {
                    Configure.isChangingPage = true;
                    DragGridView.this.onPage(EVENT_SLIDING_PAGE, ++Configure.currentPage);
                    movePageNum++;
                } else if (x <= movePageNum * Configure.screenWidth && Configure.currentPage > 0) {
                    Configure.isChangingPage = true;
                    DragGridView.this.onPage(EVENT_SLIDING_PAGE, --Configure.currentPage);
                    movePageNum--;
                }
            }
        }
        onDragDoAnimation(x, y);
    }

    public void setPageListener(G_PageListener pageListener) {
        this.pageListener = pageListener;
    }

    public interface G_PageListener {
        void page(int cases, int page);
    }

    public void setOnItemChangeListener(G_ItemChangeListener pageListener) {
        this.itemListener = pageListener;
    }

    public interface G_ItemChangeListener {
        void change(int from, int to, int count);
    }

    public void notifyDataSetChanged() {
        if (mDragGridAdapter != null) {
            mDragGridAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 
     * instruction。
     * 
     * @param cases
     * @param page
     */
    private void onPage(int cases, int page) {
        TLog.debug(TAG, "cases=(%d) , page=(%d)", cases, page);
        if (pageListener != null) {
            pageListener.page(cases, page);
        }
    }

    private int lastHitPostion = AdapterView.INVALID_POSITION;


    private int viewTag[] = new int[] { 0, 1, 2, 3, 4, 5, 6, 7 };


    public HashMap<Integer, Integer> getRealArrayHashMap() {
        HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();
        for (int i = 0; i < viewTag.length; i++) {
            hashMap.put(viewTag[i], i);
        }
        return hashMap;
    }

    private void onDragDoAnimationAcrossPage(int x, int y) {
        TLog.debug(TAG, "onDragDoAnimationAcrossPage=%d, dragPosition=%d", x, y);
        // 获取目标绝对位置
        int hitViewPosition = getAbsolutePostion(x, y);
        if (isSwapAniRunning) {
            TLog.debug(TAG, "isSwapAniRunning");
            return;
        }

        if (hitViewPosition == AdapterView.INVALID_POSITION) {
            TLog.debug(TAG, "INVALID_POSITION");
            return;
        }

        if (lastHitPostion == hitViewPosition) {
            TLog.debug(TAG, "INVALID_POSITION");
            return;
        }


        TLog.debug(TAG, "onDragDoAnimationAcrossPage viewTag[dragPosition]=%d, hitViewPosition=%d", viewTag[dragPosition], hitViewPosition);


        if (lastHitPostion == AdapterView.INVALID_POSITION) {
            dragPosition = 0;
        }

        final int realDragPostion = viewTag[dragPosition];

        HashMap<Integer, Integer> posToViewPos = getRealArrayHashMap();

        // 往后移
        if (realDragPostion > hitViewPosition) {
            // viewTag[Pos] 表示控件显示在界面上的位置。
            // posToViewPos.get(pos) 表示控件在内部逻辑上实际的位置。


            for (int i = hitViewPosition; i < realDragPostion; i++) {
                int pos = i - getFirstVisiblePosition();
                System.out.println("pos ===== " + pos);
                final View iView = getChildAt(posToViewPos.get(pos));
                if (iView != null)
                    iView.startAnimation(getMoveAnimation(true, pos, (ViewGroup) iView));
            }

            TLog.debug("", "hitViewPosition = %d, = %d", hitViewPosition, viewTag[dragPosition]);
            for (int j = hitViewPosition; j < realDragPostion; j++) {
                int pos = j - getFirstVisiblePosition();
                System.out.println("pospospospos = " + pos);
                System.out.println("bbbbbbbbbbbbbbbbb = " + pos);
                viewTag[posToViewPos.get(pos)] = viewTag[posToViewPos.get(pos)] + 1;
            }
        }

        // 往前移
        else {
            for (int i = hitViewPosition; i > realDragPostion; i--) {
                int pos = i - getFirstVisiblePosition();
                final View iView = getChildAt(posToViewPos.get(pos));
                if (iView != null)
                    iView.startAnimation(getMoveAnimation(false, pos, (ViewGroup) iView));
            }

            for (int i = hitViewPosition; i > realDragPostion; i--) {
                int pos = i - getFirstVisiblePosition();
                System.out.println("viewTag[" + pos + "] = " + viewTag[pos]);
                viewTag[posToViewPos.get(pos)] = viewTag[posToViewPos.get(pos)] - 1;
                System.out.println("viewTag[" + pos + "] = " + viewTag[pos]);
            }
        }

        lastHitPostion = hitViewPosition;

    }

    private void onDragDoAnimation(int x, int y) {
        if (movePageNum != 0) {
            onDragDoAnimationAcrossPage(x, y);
        }

        TLog.debug(TAG, "ididididid ===== %d  onDragDoAnimation=%d, dragPosition=%d", this.getId(), x, y);
        // 获取目标绝对位置
        int hitViewPosition = getAbsolutePostion(x, y);
        if (isSwapAniRunning) {
            TLog.debug(TAG, "isSwapAniRunning");
            return;
        }
        if (hitViewPosition == AdapterView.INVALID_POSITION) {
            TLog.debug(TAG, "INVALID_POSITION");
            return;
        }

        if (lastHitPostion == hitViewPosition) {
            TLog.debug(TAG, "INVALID_POSITION");
            return;
        }

        if (viewTag[dragPosition] == hitViewPosition) {
            TLog.debug(TAG, "hitViewPosition");
            return;
        }

        TLog.debug(TAG, "onDragDoAnimation=%d, dragPosition=%d", hitViewPosition, viewTag[dragPosition]);

        lastHitPostion = hitViewPosition;

        HashMap<Integer, Integer> posToViewPos = getRealArrayHashMap();

        if (movePageNum == 0) {
            TLog.debug(TAG, "viewTag[dragPosition]=%d, hitViewPosition=%d", viewTag[dragPosition], hitViewPosition);

            final int realDragPostion = viewTag[dragPosition];

            // 往后移
            if (realDragPostion > hitViewPosition) {
                // viewTag[Pos] 表示控件显示在界面上的位置。
                // posToViewPos.get(pos) 表示控件在内部逻辑上实际的位置。

                for (int i = hitViewPosition; i < realDragPostion; i++) {
                    int pos = i - getFirstVisiblePosition();
                    System.out.println("pos ===== " + pos);
                    final View iView = getChildAt(posToViewPos.get(pos));
                    if (iView != null)
                        iView.startAnimation(getMoveAnimation(true, pos, (ViewGroup) iView));
                }

                TLog.debug("", "hitViewPosition = %d, = %d", hitViewPosition, viewTag[dragPosition]);
                for (int j = hitViewPosition; j < realDragPostion; j++) {
                    int pos = j - getFirstVisiblePosition();
                    System.out.println("pospospospos = " + pos);
                    System.out.println("bbbbbbbbbbbbbbbbb = " + pos);
                    viewTag[posToViewPos.get(pos)] = viewTag[posToViewPos.get(pos)] + 1;
                }
            }

            // 往前移
            else {
                for (int i = hitViewPosition; i > realDragPostion; i--) {
                    int pos = i - getFirstVisiblePosition();
                    final View iView = getChildAt(posToViewPos.get(pos));
                    if (iView != null)
                        iView.startAnimation(getMoveAnimation(false, pos, (ViewGroup) iView));
                }

                for (int i = hitViewPosition; i > realDragPostion; i--) {
                    int pos = i - getFirstVisiblePosition();
                    System.out.println("viewTag[" + pos + "] = " + viewTag[pos]);
                    viewTag[posToViewPos.get(pos)] = viewTag[posToViewPos.get(pos)] - 1;
                    System.out.println("viewTag[" + pos + "] = " + viewTag[pos]);
                }
            }

            viewTag[dragPosition] = lastHitPostion;
            TLog.debug(TAG, "arr = %s", Arrays.toString(viewTag));
        }

    }

    private int width = 0;
    private int height = 0;
    private int left = 0;
    private int top = 0;

    /**
     * 碰撞检测
     */
    public int getAbsolutePostion(int x, int y) {
        if (width == 0) {
            View child = this.getChildAt(this.getFirstVisiblePosition());
            width = child.getWidth();
            height = child.getHeight();
            left = child.getLeft();
            top = child.getTop();
        }

        int xPos = (x - left) / width;
        int yPos = (y - top) / height;

        int pos = yPos * mNumColumns + xPos;
        if (getChildAt(pos - getFirstVisiblePosition()) == null) {
            return AdapterView.INVALID_POSITION;
        }
        return pos;
    }


    /**
     * 碰撞检测
     */
    public int getAbsPostion(int x, int y) {
        for (int pos = getFirstVisiblePosition(); pos < getChildCount(); pos++) {
            View childView = this.getChildAt(pos);

            final int left = childView.getLeft();
            final int top = childView.getTop();

            final int width = childView.getWidth();
            final int height = childView.getHeight();

            if ((left < x) && (x < left + width) && (top < y) && y < (top + height)) {
                TLog.debug(TAG, "checkViewRect position=%d", pos);
                return pos;
            }
        }
        return AdapterView.INVALID_POSITION;
    }

    private void onDrop(int x, int y) {

        isSwapAniRunning = false;

        final int[] arr = viewTag;

        // 改变表格数据
        mDragGridAdapter.sortByIntArray(arr);
        mDragGridAdapter.notifyDataSetChanged();
        viewTag = new int[] { 0, 1, 2, 3, 4, 5, 6, 7 };

        lastHitPostion = AdapterView.INVALID_POSITION;

        Configure.isDragging = false;

        DragGridView.this.onPage(EVENT_END_DRAG, PAGE_UNKNOWN);

        fromView.setVisibility(View.VISIBLE);


        //        // 执行删除操作t
        //        if (Configure.isDelDark) {
        //            DelDone = getDelAnimation(x, y);//AnimationUtils.loadAnimation(getContext(), R.anim.del_done);
        //            DelDone.setAnimationListener(new Animation.AnimationListener() {
        //                @Override
        //                public void onAnimationStart(Animation animation) {
        //                }
        //
        //                @Override
        //                public void onAnimationRepeat(Animation animation) {
        //                }
        //
        //                @Override
        //                public void onAnimationEnd(Animation animation) {
        //                    Configure.removeItem = dragPosition;
        //                    DragGridView.this.onPage(EVENT_DEL_DONE, -300);
        //                }
        //            });
        //            fromView.setVisibility(View.VISIBLE);
        //            fromView.startAnimation(DelDone);
        //            return;
        //        }
        //
        //        DragGridView.this.onPage(EVENT_DEL_CANCEL, -300);
        //
        //        /*
        //         * 找出（x, y）坐标在gridview中的位置
        //         */
        //        int tempPosition = pointToPosition(x - movePageNum * Configure.screenWidth, y);
        //        if (tempPosition != AdapterView.INVALID_POSITION) {
        //            dropPosition = tempPosition;
        //        }
        //
        //        // 跨页面拖动
        //        if (movePageNum != 0 && itemListener != null) {
        //            itemListener.change(dragPosition, dropPosition, movePageNum);
        //            movePageNum = 0;
        //            return;
        //        }
        //
        //        movePageNum = 0;
        //
        //        ViewGroup toView = (ViewGroup) getChildAt(dropPosition - getFirstVisiblePosition());
        //        if (dragPosition % 2 == 0) {
        //            AtoB = getDownAnimation((dropPosition % 2 == dragPosition % 2) ? 0 : 1, (dropPosition / 2 - dragPosition / 2));
        //            if (dropPosition != dragPosition)
        //                toView.startAnimation(getMyAnimation((dragPosition % 2 == dropPosition % 2) ? 0 : -1, (dragPosition / 2 - dropPosition / 2)));
        //        } else {
        //            AtoB = getDownAnimation((dropPosition % 2 == dragPosition % 2) ? 0 : -1, (dropPosition / 2 - dragPosition / 2));
        //            if (dropPosition != dragPosition)
        //                toView.startAnimation(getMyAnimation((dragPosition % 2 == dropPosition % 2) ? 0 : 1, (dragPosition / 2 - dropPosition / 2)));
        //        }
        //        fromView.startAnimation(AtoB);
        //
        //
        //        AtoB.setAnimationListener(new Animation.AnimationListener() {
        //            @Override
        //            public void onAnimationStart(Animation arg0) {
        //            }
        //
        //            @Override
        //            public void onAnimationRepeat(Animation arg0) {
        //            }
        //
        //            @Override
        //            public void onAnimationEnd(Animation arg0) {
        //                // TODO Auto-generated method stub
        //                DragGridView.this.exchangeItem();
        //            }
        //        });
    }

    private void stopDrag() {
        if (iv_drag != null) {
            windowManager.removeView(iv_drag);
            iv_drag = null;
        }
    }

    public Animation getMyAnimation(float x, float y) {
        TranslateAnimation go = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, x, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, y);
        go.setDuration(550);
        return go;
    }

    public Animation getDownAnimation(float x, float y) {
        AnimationSet set = new AnimationSet(true);
        TranslateAnimation go = new TranslateAnimation(Animation.RELATIVE_TO_SELF, x, Animation.RELATIVE_TO_SELF, x, Animation.RELATIVE_TO_SELF, y, Animation.RELATIVE_TO_SELF, y);
        go.setFillAfter(true);
        go.setDuration(1000);

        AlphaAnimation alpha = new AlphaAnimation(0.9f, 1.0f);
        alpha.setFillAfter(true);
        alpha.setDuration(1000);

        ScaleAnimation scale = new ScaleAnimation(1.2f, 1.0f, 1.2f, 1.0f);
        scale.setFillAfter(true);
        scale.setDuration(550);

        set.addAnimation(go);
        set.addAnimation(alpha);
        set.addAnimation(scale);

        set.setFillAfter(true);

        return set;
    }

    private boolean isSwapAniRunning = false;

    private int mNumColumns;

    @Override
    public void setNumColumns(int numColumns) {
        mNumColumns = numColumns;
        super.setNumColumns(numColumns);
    }

    public Animation getMoveAnimation(boolean isNext, int position, final View view) {
        Animation ani = null;
        int xSelfCount = 0;
        int ySelfCount = 0;

        if (isNext) {
            // 右边界的位置
            if ((position % mNumColumns) == mNumColumns - 1) {
                xSelfCount = -(mNumColumns - 1);
                ySelfCount = 1;
            } else {
                xSelfCount = 1;
                ySelfCount = 0;
            }
        } else {
            // 左边界的位置
            if ((position % mNumColumns) == 0) {
                xSelfCount = mNumColumns - 1;
                ySelfCount = -1;
            } else {
                xSelfCount = -1;
                ySelfCount = 0;
            }
        }


        ani = getMyAnimation(xSelfCount, ySelfCount);

        final int finalXSelfCount = xSelfCount;
        final int finalYSelfCount = ySelfCount;
        ani.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isSwapAniRunning = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                int left = view.getLeft() + finalXSelfCount * view.getWidth();
                int top = view.getTop() + finalYSelfCount * view.getHeight();
                view.clearAnimation();
                view.layout(left, top, left + view.getWidth(), top + view.getHeight());
                view.setVisibility(View.VISIBLE);
                isSwapAniRunning = false;
            }
        });

        return ani;
    }


    public Animation getDelAnimation(int x, int y) {
        AnimationSet set = new AnimationSet(true);
        //TranslateAnimation go = new TranslateAnimation(Animation.ABSOLUTE, x-itemWidth/2, Animation.ABSOLUTE, x-itemWidth/2, 
        //      Animation.ABSOLUTE, y-itemHeight/2, Animation.ABSOLUTE, y-itemHeight/2);
        //go.setFillAfter(true);go.setDuration(1550);
        RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setFillAfter(true);
        rotate.setDuration(550);
        AlphaAnimation alpha = new AlphaAnimation(1.0f, 0.0f);
        alpha.setFillAfter(true);
        alpha.setDuration(550);

        //  ScaleAnimation scale = new ScaleAnimation(1.0f,0.0f,1.0f,0.0f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        //  scale.setFillAfter(true);scale.setDuration(550);

        //set.addAnimation(rotate);
        set.addAnimation(alpha);
        set.addAnimation(rotate);
        return set;
    }
}
