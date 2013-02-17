package org.tadpole.app;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.tadpole.adapter.DragGridAdapter;
import org.tadpole.aidl.IPluginCallback;
import org.tadpole.aidl.PluginServiceConnect;
import org.tadpole.common.TLog;
import org.tadpole.util.ListUtil;
import org.tadpole.widget.BoardDataConfig;
import org.tadpole.widget.Configure;
import org.tadpole.widget.DragGridView;
import org.tadpole.widget.DragGridView.G_ItemChangeListener;
import org.tadpole.widget.PagedView;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class MainActivity extends BaseActvity {
    private static final String TAG = "MainActivity";
    private PagedView mBoardPagedView;
    private View mMoveBackgroundView;
    private TextView mPageCountTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board);

        mBoardPagedView = (PagedView) this.findViewById(R.id.page_view_board);
        mMoveBackgroundView = (View) this.findViewById(R.id.move_background);
        new MoveBackground(mMoveBackgroundView).startMove();

        Configure.init(this);
        initPage();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "--------------onDestroy------------");
        super.onDestroy();
    }

    //----------------ui logic-----------------
    private ArrayList<ArrayList> mPageDataList = new ArrayList<ArrayList>();
    private ArrayList<DragGridView> mPageViewsList = new ArrayList<DragGridView>();

    public static final int PAGE_SIZE = 8;

    public ArrayList<BoardPageItem> loadBoardDataItems() {
        String[] tStrings = new String[20];
        for (int i = 0, len = tStrings.length; i < len; i++) {
            tStrings[i] = "weco" + i;
        }

        TLog.debug(TAG, "loadBoardDataItems itemCount=%d", tStrings.length);
        ArrayList<BoardPageItem> itemList = new ArrayList<BoardPageItem>();
        for (int i = 0, len = tStrings.length; i < len; i++) {
            BoardPageItem item = new BoardPageItem();
            item.title = tStrings[i];
            item.id = tStrings[i];
            item.color = (i % 3 == 1) ? BoardPageItem.COLOR_RED : BoardPageItem.COLOR_BLUE;
            itemList.add(item);
        }
        return itemList;
    }


    private void initPage() {
        mPageCountTextView = (TextView) this.findViewById(R.id.tv_page);
        mBoardPagedView.setPageListener(new PagedView.PageListener() {
            @Override
            public void page(int page) {
                setCurPage(page);
            }
        });

        ArrayList<BoardPageItem> itemList = loadBoardDataItems();
        Configure.boardData = new BoardDataConfig(itemList, Configure.PAGE_SIZE);

        try {
            InputStream is = this.getAssets().open("board_data.json");
            byte[] contentBytes = new byte[is.available()];
            is.read(contentBytes);
            String content = new String(contentBytes);
            JSONArray jsonArray = new JSONArray(content);
            Configure.boardData = BoardDataConfig.fromJSON(jsonArray, Configure.PAGE_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        int pageCount = Configure.boardData.getPageCount();
        for (int pageIndex = 0; pageIndex < pageCount; pageIndex++) {
            View v = null;
            v = buildPageView(Configure.boardData, pageIndex);
            mBoardPagedView.addView(v);
        }
    }

    private View buildPageView(BoardDataConfig<BoardPageItem> boardData, int page) {
        LinearLayout.LayoutParams LP = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        View rl = LayoutInflater.from(this).inflate(R.layout.board_page, null);
        rl.setLayoutParams(LP);

        // disable gridview content scroll
        final DragGridView dragGridView = (DragGridView) rl.findViewById(R.id.grid_view_board_page);
        mPageViewsList.add(dragGridView);


        dragGridView.setPageListener(new DragGridView.G_PageListener() {
            @Override
            public void page(int cases, int page) {
                TLog.debug(TAG, "G_PageListener cases = %d, page = %d", cases, page);
                switch (cases) {
                case DragGridView.EVENT_START_DRAG:
                    break;
                case DragGridView.EVENT_END_DRAG:
                    break;
                case DragGridView.EVENT_SLIDING_PAGE:
                    MainActivity.this.mBoardPagedView.snapToScreen(page);
                    dragGridView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Configure.isChangingPage = false;
                        }
                    }, 600);
                    break;
                default:
                    break;
                }
            }
        });

        //生成适配器的ImageItem <====> 动态数组的元素，两者一一对应  
        DragGridAdapter dgaAdapter = new DragGridAdapter(this, //没什么解释  
                page, boardData);

        dragGridView.setDragGridAdapter(dgaAdapter);
        dragGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Log.i(TAG, "onItemClick");
            }
        });

        dragGridView.setOnItemChangeListener(new G_ItemChangeListener() {
            @Override
            public void change(int from, int to, int count) {
                ArrayList<Object> fromList = (ArrayList<Object>) mPageDataList.get(Configure.currentPage - count);
                ArrayList<Object> toList = (ArrayList<Object>) mPageDataList.get(Configure.currentPage);

                ListUtil.swapListItem(fromList, toList, from, to);

                mPageViewsList.get(Configure.currentPage).notifyDataSetChanged();
                mPageViewsList.get(Configure.currentPage - count).notifyDataSetChanged();
            }
        });


        return rl;
    }

    public void setCurPage(final int page) {
        Animation a = AnimationUtils.loadAnimation(MainActivity.this, R.anim.scale_in);
        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mPageCountTextView.setText((page + 1) + "");
                mPageCountTextView.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.scale_out));
            }
        });
        mPageCountTextView.startAnimation(a);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        TLog.debug(TAG, "onKeyDown keyCode = %d ", keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK && mBoardPagedView.isEditing()) {
            mBoardPagedView.endEdit();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("刷新");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
