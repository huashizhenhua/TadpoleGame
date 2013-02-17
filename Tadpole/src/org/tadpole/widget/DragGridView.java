package org.tadpole.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.tadpole.adapter.IDragGridAdapter;
import org.tadpole.app.BoardPageItem;
import org.tadpole.app.R;
import org.tadpole.common.TLog;
import org.w3c.dom.ls.LSException;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

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
    public static final int PAGE_UNKNOWN = 5;

    private static final String TAG = "DragGridView";
    private int dragPosition = AdapterView.INVALID_POSITION;
    private int dropPosition = AdapterView.INVALID_POSITION;
    private boolean mIsDragViewFromMe;
    private ArrayList<Runnable> runnableList = new ArrayList<Runnable>();
    Animation AtoB, BtoA, DelDone;
    int stopCount = 0;
    private G_PageListener pageListener;
    private G_ItemChangeListener itemListener;
    private IDragGridAdapter mDragGridAdapter;
    private boolean isSwapAniRunning = false;

    private int moveAnimationCount = 0;

    private int mNumColumns;
    private int lastHitPostion = AdapterView.INVALID_POSITION;
    private int viewTag[] = new int[] { 0, 1, 2, 3, 4, 5, 6, 7 };

    private OnItemClickListener mItemClickListener;

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


    protected void performLongClick(int x, int y) {
        int postion = pointToPosition((int) x, (int) y);
        if (postion != AdapterView.INVALID_POSITION) {
            TLog.debug(TAG, "onItemLongClick", "");
            PagedView pagedView = getParentPagedView();
            if (pagedView != null) {
                mIsDragViewFromMe = true;
                Configure.draggingPostion = postion;
                Configure.draggingPage = Configure.currentPage;

                dragPosition = dropPosition = postion;
                if (dragPosition == AdapterView.INVALID_POSITION) {
                }
                pagedView.startDrag(x, y, postion);

                IDragGridAdapter adapter = getDragGridAdapterByPage(Configure.currentPage);
                Configure.draggingItem = (BoardPageItem) adapter.getItem(dragPosition);
                Configure.draggingItem.hide = true;
            }
        }
    }

    public IDragGridAdapter getDragGridAdapter() {
        return mDragGridAdapter;
    }

    public DragGridView getDragGridView(int page) {
        View parentView = (View) DragGridView.this.getParent();
        System.out.println("DragGridView.this.getParent() = " + DragGridView.this.getParent());
        if (parentView != null) {
            PagedView pagedView = (PagedView) getParentPagedView();
            View pageV = pagedView.getChildAt(page);
            DragGridView gridview = (DragGridView) pageV.findViewById(org.tadpole.app.R.id.grid_view_board_page);
            return gridview;
        }
        return null;
    }

    public IDragGridAdapter getDragGridAdapterByPage(int page) {
        DragGridView gridview = getDragGridView(page);
        return gridview.getDragGridAdapter();
    }

    public PagedView getParentPagedView() {
        PagedView pagedView = null;
        View parentView = (View) DragGridView.this.getParent();
        System.out.println("DragGridView.this.getParent() = " + DragGridView.this.getParent());
        if (parentView != null) {
            pagedView = (PagedView) parentView.getParent().getParent();
        }
        return pagedView;
    }


    /**
     * @description if gridview is editting, disable on item click listener
     */
    @Override
    public void setOnItemClickListener(android.widget.AdapterView.OnItemClickListener listener) {
        mItemClickListener = listener;
        super.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (Configure.isEditMode == false) {
                    mItemClickListener.onItemClick(parent, view, position, id);
                }
            }
        });
    }

    /**
     * tranform @PagedView's motionEvent to @DragGridView motionEvent
     * and dispatch it in order to making click event available
     * 
     * @param event
     * @return
     */
    public boolean onParentTouchEvent(final MotionEvent event) {
        MotionEvent gridEvent = MotionEvent.obtain(event);
        TLog.debug(TAG, "onParentTouchEvent gridEvent = %s", gridEvent.toString());
        gridEvent.setLocation(event.getX() - getDragGridOffsetLeft(), event.getY() - getYDragGridOffsetTop());
        setupLongClickListener(gridEvent);
        this.dispatchTouchEvent(gridEvent);
        return true;
    }

    /**
     * 自定义长按事件
     * 
     * @param ev
     * @return
     */
    public boolean onParentInterceptTouchEvent(final MotionEvent event) {
        Log.d(TAG, "onParentInterceptTouchEvent ev.getAction() = " + event.getAction() + ", isDragging = " + Configure.isDragging);
        return true;
    }

    public boolean setupLongClickListener(final MotionEvent event) {
        this.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                performLongClick((int) event.getX(), (int) event.getY());
                return false;
            };
        });
        return true;
    }

    public int getYDragGridOffsetTop() {
        return this.getTop() + ((View) this.getParent()).getTop();
    }

    public int getDragGridOffsetLeft() {
        return this.getLeft() + ((View) this.getParent()).getLeft();
    }

    public boolean isMoveToNext(View iv_drag, int pageX) {
        return (pageX >= Configure.screenWidth - (iv_drag.getWidth() / 2)) && (pageX <= Configure.screenWidth) && !Configure.isChangingPage;
    }

    public boolean isMoveToPrev(View iv_drag, int pageX) {
        return (pageX >= 0) && (pageX <= 0 + iv_drag.getWidth() / 2) && !Configure.isChangingPage;
    }


    public void onDrag(View iv_drag, int pageX, int pageY, int gridX, int gridY) {
        if (isMoveToNext(iv_drag, pageX) || isMoveToPrev(iv_drag, pageX))
            stopCount++;
        else
            stopCount = 0;
        if (stopCount > 10) {
            stopCount = 0;
            if (isMoveToNext(iv_drag, pageX)) {
                Configure.isChangingPage = true;
                DragGridView.this.onPage(EVENT_SLIDING_PAGE, ++Configure.currentPage);
                Configure.movePageNum++;
            }

            if (isMoveToPrev(iv_drag, pageX)) {
                Configure.isChangingPage = true;
                DragGridView.this.onPage(EVENT_SLIDING_PAGE, --Configure.currentPage);
                Configure.movePageNum--;
            }
        }
        onDragDoAnimation(gridX, gridY);
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

    public HashMap<Integer, Integer> getRealArrayHashMap() {
        HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();
        for (int i = 0; i < viewTag.length; i++) {
            hashMap.put(viewTag[i], i);
        }
        return hashMap;
    }

    private void onDragDoAnimation(int x, int y) {
        if (Configure.isChangingPage) {
            return;
        }

        // 获取目标绝对位置
        int hitViewPosition = getAbsolutePostion(x, y);

        TLog.debug(TAG, "onDragDoAnimation lastHitPostion=%d hitViewPosition=%d, dragPosition=%d", lastHitPostion, hitViewPosition, dragPosition);

        if (isSwapAniRunning) {
            TLog.debug(TAG, "isSwapAniRunning");
            return;
        }

        if (hitViewPosition == AdapterView.INVALID_POSITION) {
            TLog.debug(TAG, "hitViewPosition == AdapterView.INVALID_POSITION");
            return;
        }

        if (lastHitPostion == hitViewPosition) {
            TLog.debug(TAG, "lastHitPostion == hitViewPosition");
            return;
        }

        /**
         * 如果是跨页面拖动
         */
        if (dragPosition == AdapterView.INVALID_POSITION) {
            int movePostion = 0;
            int xSelfOffset = -4;
            int ySelfOffset = 4;

            if (Configure.currentPage < Configure.draggingPage) {
                movePostion = Configure.PAGE_SIZE - 1;
                xSelfOffset = 4;
                ySelfOffset = -4;
            }

            final View view = this.getChildAt(movePostion - getFirstVisiblePosition());
            final View finalView = getParentPagedView().copyViewInAniLayer(view, view.getHeight(), view.getWidth());
            Animation ani = getTransRelaAnimation(0, 0, xSelfOffset, ySelfOffset);
            ani.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    view.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    ((ViewGroup) finalView.getParent()).removeView(finalView);
                }
            });
            finalView.startAnimation(ani);
            dragPosition = movePostion;
        }

        if (viewTag[dragPosition] == hitViewPosition) {
            TLog.debug(TAG, "viewTag[dragPosition] == hitViewPosition");
            // 当恰恰好偏移的是第一个
            dropPosition = hitViewPosition;
            return;
        }

        TLog.debug(TAG, "onDragDoAnimation=%d, dragPosition=%d", hitViewPosition, viewTag[dragPosition]);

        lastHitPostion = hitViewPosition;

        HashMap<Integer, Integer> posToViewPos = getRealArrayHashMap();

        TLog.debug(TAG, "viewTag[dragPosition]=%d, hitViewPosition=%d", viewTag[dragPosition], hitViewPosition);

        final int realDragPostion = viewTag[dragPosition];

        dropPosition = hitViewPosition;

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

    public int getDropPostion() {
        return dropPosition;
    }

    public Rect getDropPostionRectInindow() {
        int xPer = dropPosition % mNumColumns;
        int yPer = dropPosition / mNumColumns;

        int windowX = this.getLeft() + xPer * cellWidth;
        int windowY = this.getRight() + yPer * cellHeight;

        Rect rect = new Rect();
        rect.left = windowX;
        rect.top = windowY;

        rect.bottom = windowY + cellHeight;
        rect.right = windowY + cellWidth;

        return rect;
    }

    private int cellWidth = 0;
    private int cellHeight = 0;
    private int firstChildLeft = 0;
    private int firstChildRight = 0;

    /**
     * 碰撞检测
     */
    public int getAbsolutePostion(int x, int y) {
        if (cellWidth == 0) {
            View child = this.getChildAt(this.getFirstVisiblePosition());
            cellWidth = child.getWidth();
            cellHeight = child.getHeight();
            firstChildLeft = child.getLeft();
            firstChildRight = child.getTop();
        }

        int xPos = (x - firstChildLeft) / cellWidth;
        int yPos = (y - firstChildRight) / cellHeight;

        int pos = yPos * mNumColumns + xPos;
        if (getChildAt(pos - getFirstVisiblePosition()) == null) {
            return AdapterView.INVALID_POSITION;
        }
        return pos;
    }

    public void resetMemberValue() {
        mIsDragViewFromMe = false;
        isSwapAniRunning = false;
        viewTag = new int[] { 0, 1, 2, 3, 4, 5, 6, 7 };
        lastHitPostion = AdapterView.INVALID_POSITION;
        lastHitPostion = dragPosition = dropPosition = AdapterView.INVALID_POSITION;
    }

    public void onDrop() {
        mIsDragViewFromMe = false;
        final int[] arr = viewTag;
        Runnable runnable = null;
        if (Configure.draggingPage == Configure.currentPage) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    if (Configure.draggingItem != null) {
                        Configure.draggingItem.hide = false;
                        Configure.draggingItem = null;
                    }

                    // 改变表格数
                    mDragGridAdapter.sortByPositions(arr);
                    mDragGridAdapter.notifyDataSetChanged();


                    resetMemberValue();

                    Configure.draggingPage = Configure.DRAG_PAGE_INVALID;
                    Configure.draggingPostion = Configure.DRAG_POSITION_INVALID;

                }
            };
        } else {
            runnable = new Runnable() {
                @Override
                public void run() {
                    if (Configure.draggingItem != null) {
                        Configure.draggingItem.hide = false;
                        Configure.draggingItem = null;
                    }

                    final BoardDataConfig boardData = Configure.boardData;
                    final int draggingPostion = Configure.draggingPostion;
                    final int currentPage = Configure.currentPage;
                    final int draggingPage = Configure.draggingPage;


                    List<BoardPageItem> curList = boardData.getPageItemList(currentPage);
                    List<BoardPageItem> dragList = boardData.getPageItemList(draggingPage);
                    BoardPageItem dragItem = dragList.get(draggingPostion);
                    BoardPageItem curFirstItem = curList.get(dropPosition);


                    List<BoardPageItem> totalList = boardData.getBoardItemList();
                    int from = totalList.indexOf(dragItem);
                    int to = totalList.indexOf(curFirstItem);
                    boardData.moveFromTo(from, to);
                    getDragGridView(Configure.draggingPage).resetMemberValue();
                    resetMemberValue();
                    Configure.draggingPage = Configure.DRAG_PAGE_INVALID;
                    Configure.draggingPostion = Configure.DRAG_POSITION_INVALID;
                    getParentPagedView().refreshAllPageDragView();
                }
            };
        }

        TLog.debug(TAG, "DDDD moveAnimationCount=%d", moveAnimationCount);

        if (moveAnimationCount > 0) {
            runnableList.add(runnable);
        } else {
            runnable.run();
        }
    }

    protected void dragPosToTailAndSort() {
        for (int i = 7; i > viewTag[dragPosition]; i--) {
            viewTag[i] = viewTag[i] - 1;
        }
        viewTag[dragPosition] = 7;
        mDragGridAdapter.sortByPositions(viewTag);
    }

    public void onLeave() {
        isSwapAniRunning = false;
        if (!mIsDragViewFromMe) {
            resetMemberValue();
            if (moveAnimationCount > 0) {
                runnableList.add(new Runnable() {
                    @Override
                    public void run() {
                        mDragGridAdapter.notifyDataSetChanged();
                    }
                });
            } else {
                this.notifyDataSetChanged();
            }
        }
    }

    public Animation getTransRelaAnimation(float toX, float toY) {
        TranslateAnimation go = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, toX, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, toY);
        go.setDuration(450);
        return go;
    }

    public Animation getTransRelaAnimation(float fromX, float fromY, float toX, float toY) {
        TranslateAnimation go = new TranslateAnimation(Animation.RELATIVE_TO_SELF, fromX, Animation.RELATIVE_TO_SELF, toX, Animation.RELATIVE_TO_SELF, fromY, Animation.RELATIVE_TO_SELF, toY);
        go.setDuration(600);
        return go;
    }

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
        ani = getTransRelaAnimation(xSelfCount, ySelfCount);


        final int finalXSelfCount = xSelfCount;
        final int finalYSelfCount = ySelfCount;
        ani.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                moveAnimationCount++;
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
                moveAnimationCount--;

                TLog.debug(TAG, "moveAnimationCount = %d", moveAnimationCount);
                for (int i = 0; i < runnableList.size(); i++) {
                    runnableList.get(i).run();
                }
                runnableList.clear();
            }
        });
        return ani;
    }

    /**
     * 
     * instruction。
     * 
     * @param position
     * @param item
     * @param view
     */
    public void delete(final int position, final BoardPageItem item, View view) {
        view.setVisibility(View.INVISIBLE);
        int nextPage = Configure.currentPage + 1;
        List curPageItemList = Configure.boardData.getPageItemList(Configure.currentPage);

        // do move animation
        for (int i = curPageItemList.size() - 1; i > position; i--) {
            int pos = i - getFirstVisiblePosition();
            final View iView = getChildAt(pos);
            if (iView != null)
                iView.startAnimation(getMoveAnimation(false, pos, (ViewGroup) iView));
        }

        if ((nextPage < Configure.boardData.getPageCount())) {
            BoardPageItem nextPageFistItem = Configure.boardData.getPageItemList(nextPage).get(0);
            final View newGridItemView = createGridItemView(LayoutInflater.from(getContext()), nextPageFistItem);

            View transTargetView = view;
            if (curPageItemList.size() == Configure.PAGE_SIZE) {
                transTargetView = getChildAt(Configure.PAGE_SIZE - 1 - getFirstVisiblePosition());
            }

            int location[] = new int[2];
            transTargetView.getLocationInWindow(location);

            TLog.debug(TAG, "transTargetView.getHeight() = %d, transTargetView.getWidth() = %d", transTargetView.getHeight(), transTargetView.getWidth());

            getParentPagedView().addViewToAnimLayout(newGridItemView, location, transTargetView.getHeight(), transTargetView.getWidth());
            newGridItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TLog.debug(TAG, "newGridItemView.getHeight() = %d, newGridItemView.getWidth() = %d", newGridItemView.getHeight(), newGridItemView.getWidth());
                }
            });

            // move the first item of next page to cur page
            Animation transRelaAni = getTransRelaAnimation(2, -4, 0, 0);
            transRelaAni.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    List curPageItemList = Configure.boardData.getPageItemList(Configure.currentPage);
                    curPageItemList.remove(position);
                    getParentPagedView().refreshAllPageDragView();
                    ((ViewGroup) newGridItemView.getParent()).removeView(newGridItemView);
                }
            });
            newGridItemView.startAnimation(transRelaAni);
        } else {
            Runnable runable = new Runnable() {
                @Override
                public void run() {
                    List curPageItemList = Configure.boardData.getPageItemList(Configure.currentPage);
                    curPageItemList.remove(position);
                    getParentPagedView().refreshAllPageDragView();
                }
            };

            // has animtaion
            if (curPageItemList.size() - 1 > position) {
                runnableList.add(runable);
            }
            // no animation
            else {
                runable.run();
            }
        }
    }

    public static View createGridItemView(LayoutInflater inflater, BoardPageItem item) {
        final View view = inflater.inflate(org.tadpole.app.R.layout.board_page_griditem, null);
        TextView textView = (TextView) view.findViewById(R.id.pageItemText);
        View deleteBtnView = view.findViewById(R.id.pageItemDeleteBtn);
        textView.setText(item.title);
        if (BoardPageItem.COLOR_BLUE.equals(item.color)) {
            textView.setBackgroundResource(R.drawable.blue);
        } else {
            textView.setBackgroundResource(R.drawable.red);
        }
        if (Configure.isEditMode) {
            textView.getBackground().setAlpha(220);
            deleteBtnView.setVisibility(View.VISIBLE);
        } else {
            textView.getBackground().setAlpha(255);
            deleteBtnView.setVisibility(View.INVISIBLE);
        }
        return view;
    }

}
