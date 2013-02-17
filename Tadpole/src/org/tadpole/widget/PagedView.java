package org.tadpole.widget;


import org.tadpole.app.R;
import org.tadpole.common.TLog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Scroller;

/**
 * 仿Launcher中的WorkSapce，可以左右滑动切换屏幕的类
 */
public class PagedView extends ViewGroup {
    private static final String TAG = "PagedView";

    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;
    private int mCurScreen;
    private int mDefaultScreen = 0;
    private static final int TOUCH_STATE_REST = 0;
    private static final int TOUCH_STATE_SCROLLING = 1;
    private static final int SNAP_VELOCITY = 600;


    private int mTouchState = TOUCH_STATE_REST;
    private int mTouchSlop;
    private float mLastMotionX;
    private PageListener pageListener;


    private View mViewDragging;
    private View fromView;

    private int startDragX;
    private int startDragY;

    boolean isTouchOffsetCounted = false;

    private int touchOffsetX;
    private int touchOffsetY;

    /**
     * 用于放置具有全屏效果动画的动画层
     */
    private ViewGroup aniViewGroup;


    private WindowManager.LayoutParams windowParams = new WindowManager.LayoutParams();
    private WindowManager mWindowManager;


    public PagedView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);// "window"
    }

    public PagedView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mScroller = new Scroller(context);
        mCurScreen = mDefaultScreen;
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);// "window"
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childLeft = 0;
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View childView = getChildAt(i);
            if (childView.getVisibility() != View.GONE) {
                final int childWidth = childView.getMeasuredWidth();
                childView.layout(childLeft, 0, childLeft + childWidth, childView.getMeasuredHeight());
                childLeft += childWidth;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode != MeasureSpec.EXACTLY) {
            throw new IllegalStateException("ScrollLayout only canmCurScreen run at EXACTLY mode!");
        }
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode != MeasureSpec.EXACTLY) {
            throw new IllegalStateException("ScrollLayout only can run at EXACTLY mode!");
        }
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
        }
        scrollTo(mCurScreen * width, 0);
    }

    /**
     * According to the position of current layout scroll to the destination
     * page.
     */
    public void snapToDestination() {
        final int screenWidth = getWidth();
        final int destScreen = (getScrollX() + screenWidth / 2) / screenWidth;
        snapToScreen(destScreen);
    }

    public void snapToScreen(int whichScreen) {
        Configure.lastPage = this.getCurScreen();
        // get the valid layout page
        whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
        if (getScrollX() != (whichScreen * getWidth())) {

            final int delta = whichScreen * getWidth() - getScrollX();
            mScroller.startScroll(getScrollX(), 0, delta, 0, Math.abs(delta) * 2);
            final DragGridView gv = (DragGridView) PagedView.this.getChildAt(Configure.lastPage).findViewById(R.id.grid_view_board_page);
            this.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Configure.isChangingPage = false;
                    if (Configure.isDragging) {
                        gv.onLeave();
                    }
                }
            }, Math.abs(delta) * 2);

            mCurScreen = whichScreen;
            if (mCurScreen > Configure.currentPage) {
                Configure.currentPage = whichScreen;
                PagedView.this.onPage(Configure.currentPage);
            } else if (mCurScreen < Configure.currentPage) {
                Configure.currentPage = whichScreen;
                PagedView.this.onPage(Configure.currentPage);
            }
            invalidate(); // Redraw the layout
        }
    }

    public void setToScreen(int whichScreen) {
        whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
        mCurScreen = whichScreen;
        scrollTo(whichScreen * getWidth(), 0);
    }

    /**
     * 获得当前页码
     */
    public int getCurScreen() {
        return mCurScreen;
    }

    /**
     * 获取当前view对象
     */
    public DragGridView getCurGridView() {
        return (DragGridView) getDragGridView(this.mCurScreen);
    }

    /**
     * 当滑动后的当前页码
     */
    public int getPage() {
        return Configure.currentPage;
    }

    public DragGridView getDragGridView(int page) {
        return (DragGridView) PagedView.this.getChildAt(page).findViewById(R.id.grid_view_board_page);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("ScrollLayout", "ScrollLayout onTouchEvent action=" + event.getAction() + "getCurPage() = " + getCurGridView() + ", Configure.isDragging = " + Configure.isDragging);

        getCurGridView().onParentTouchEvent(event);

        if (Configure.isDragging) {
            this.handleDragTouchEvent(event);
            return true;
        }

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        final int action = event.getAction();
        final float x = event.getX();
        //	final float y = event.getY();

        switch (action) {
        case MotionEvent.ACTION_DOWN:
            if (!mScroller.isFinished()) {
                mScroller.abortAnimation();
            }
            mLastMotionX = x;
            break;
        case MotionEvent.ACTION_MOVE:
            int deltaX = (int) (mLastMotionX - x);
            mLastMotionX = x;
            scrollBy(deltaX, 0);
            break;
        case MotionEvent.ACTION_UP:
            final VelocityTracker velocityTracker = mVelocityTracker;
            velocityTracker.computeCurrentVelocity(1000);
            int velocityX = (int) velocityTracker.getXVelocity();

            if (velocityX > SNAP_VELOCITY && mCurScreen > 0) {
                // Fling enough to move left
                snapToScreen(mCurScreen - 1);

            } else if (velocityX < -SNAP_VELOCITY && mCurScreen < getChildCount() - 1) {
                // Fling enough to move right
                snapToScreen(mCurScreen + 1);
            } else {
                snapToDestination();
            }
            if (mVelocityTracker != null) {
                mVelocityTracker.recycle();
                mVelocityTracker = null;
            }
            mTouchState = TOUCH_STATE_REST;
            break;
        case MotionEvent.ACTION_CANCEL:
            mTouchState = TOUCH_STATE_REST;
            break;
        }
        return true;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d("ScrollLayout", "onInterceptTouchEvent action=" + ev.getAction());
        getCurGridView().onParentInterceptTouchEvent(ev);

        if (Configure.isDragging) {
            return true;
        }

        final int action = ev.getAction();
        if ((action == MotionEvent.ACTION_MOVE) && (mTouchState != TOUCH_STATE_REST)) {
            return true;
        }

        final float x = ev.getX();

        switch (action) {
        case MotionEvent.ACTION_MOVE:
            final int xDiff = (int) Math.abs(mLastMotionX - x);
            if (xDiff > mTouchSlop) {
                mTouchState = TOUCH_STATE_SCROLLING;
            }
            break;
        case MotionEvent.ACTION_DOWN:
            mLastMotionX = x;
            mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
            break;

        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_UP:
            mTouchState = TOUCH_STATE_REST;
            break;
        }
        return true;

    }

    public void onPage(int page) {
        this.pageListener.page(page);
    }

    public void setPageListener(PageListener pageListener) {
        this.pageListener = pageListener;
    }


    public interface PageListener {
        void page(int page);
    }


    public View copyViewInAniLayer(View view, int height, int width) {
        if (aniViewGroup == null) {
            aniViewGroup = createAnimLayout();
        }
        int location[] = new int[2];
        view.getLocationInWindow(location);
        ImageView imageView = new ImageView(getContext());
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(true);
        Bitmap bm = Bitmap.createBitmap(view.getDrawingCache());
        imageView.setImageBitmap(bm);
        addViewToAnimLayout(imageView, location, height, width);
        return imageView;
    }

    /**
     * @Description: 创建动画层
     * @param
     * @return void
     * @throws
     */
    private ViewGroup createAnimLayout() {
        ViewGroup rootView = (ViewGroup) ((Activity) getContext()).getWindow().getDecorView();
        RelativeLayout animLayout = new RelativeLayout(getContext());
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
        animLayout.setLayoutParams(lp);
        animLayout.setBackgroundResource(android.R.color.transparent);
        rootView.addView(animLayout);
        return animLayout;
    }

    /**
     * @Description: 添加视图到动画层
     * @param @param vg
     * @param @param view
     * @param @param location
     * @param @return
     * @return View
     * @throws
     */
    public View addViewToAnimLayout(final View view, int[] location, int height, int width) {
        int x = location[0];
        int y = location[1];
        if (aniViewGroup == null) {
            aniViewGroup = createAnimLayout();
        }
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, height);
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        lp.leftMargin = x;
        lp.topMargin = y;

        view.setLayoutParams(lp);
        aniViewGroup.addView(view);
        return view;
    }

    public void onDrag(int x, int y) {
        if (mViewDragging != null) {
            windowParams.alpha = 0.8f;
            windowParams.x = (x - startDragX - touchOffsetX) + startDragX;
            windowParams.y = (y - startDragY - touchOffsetY) + startDragY;
            mWindowManager.updateViewLayout(mViewDragging, windowParams);
        }

        if (getCurGridView() != null) {
            // 将PagedView的坐标转化为gridView的坐标
            int gridViewX = x - getCurGridView().getDragGridOffsetLeft();
            int gridViewY = y - getCurGridView().getYDragGridOffsetTop();

            int pageX = x;
            int pageY = y;

            if (x > 0 && y > 0 && Configure.isChangingPage == false) {
                getCurGridView().onDrag(mViewDragging, pageX, pageY, gridViewX, gridViewY);
            }
        }
    }


    public boolean startDrag(final int x, final int y, int arg2) {
        Configure.isDragging = true;

        int dragPosition = arg2;
        final DragGridView gridView = getCurGridView();

        // copy fromView  as a bitmap
        fromView = (ViewGroup) gridView.getChildAt(dragPosition - gridView.getFirstVisiblePosition());

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
                windowParams.gravity = Gravity.TOP | Gravity.LEFT;

                startDragX = fromView.getLeft() + gridView.getDragGridOffsetLeft() + (int) (3 * Configure.screenDensity);
                startDragY = fromView.getTop() + gridView.getYDragGridOffsetTop() + (int) (3 * Configure.screenDensity);

                windowParams.x = startDragX;
                windowParams.y = startDragY;

                windowParams.alpha = 0.8f;
                windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                windowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;

                fromView.setVisibility(View.GONE);

                mViewDragging = DragGridView.createGridItemView(LayoutInflater.from(getContext()), Configure.draggingItem);
                mWindowManager.addView(mViewDragging, windowParams);
                mViewDragging.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.del_done));

                startEdit();
            }
        });
        fromView.startAnimation(disappear);
        return false;
    }

    public void startEdit() {
        Configure.isEditMode = true;
        refreshAllPageDragView();
    }

    public void endEdit() {
        Configure.isEditMode = false;
        refreshAllPageDragView();
    }

    public void refreshAllPageDragView() {
        int pageCount = Configure.boardData.getPageCount();
        int childCount = getChildCount();
        if (pageCount > childCount) {
            //增加一个页面VIEW
            //            this.addView(child);
        }

        if (pageCount < childCount && childCount != 1) {
            if (childCount - 1 == Configure.currentPage) {
                snapToScreen(--Configure.currentPage);
            }
            this.removeViewAt(childCount - 1);
        }

        for (int i = 0, len = getChildCount(); i < len; i++) {
            getDragGridView(i).notifyDataSetChanged();
        }
    }

    private void handleDragTouchEvent(MotionEvent ev) {
        TLog.debug(TAG, "ididididid ===== " + this + ", onBranchTouchEvent iv_drag =  " + mViewDragging + ",eventAction=%d, x=%d,y=%d", ev.getAction(), (int) ev.getX(), (int) ev.getY());
        if (mViewDragging != null) {
            int x = (int) ev.getX();
            int y = (int) ev.getY();
            switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (!isTouchOffsetCounted) {
                    touchOffsetX = x - startDragX;
                    touchOffsetY = y - startDragY;
                    isTouchOffsetCounted = true;
                }
                onDrag(x, y);
                break;
            case MotionEvent.ACTION_UP:
                TLog.debug(TAG, "ACTION_UP", "");
                PagedView.this.onDrop();
                break;
            }
        }
    }

    private void onDrop() {
        if (mViewDragging != null) {
            this.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mWindowManager.removeView(mViewDragging);
                    mViewDragging = null;
                }
            }, 100);
        }
        Configure.isDragging = false;
        getCurGridView().onDrop();
    }

    public boolean isEditing() {
        return Configure.isEditMode;
    }
}