package com.itap.voiceemoticon.widget;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tadpolemusic.VEApplication;
import com.tadpolemusic.adapter.PullToRefreshListViewAdapter;
import com.tadpolemusic.api.PageList;

public abstract class PageListView<T> extends PullToRefreshListView implements OnScrollListener {
    private PullToRefreshListViewAdapter<T> mAdapter;
    private int mTotalCount;
    private int mStartIndex;
    public int maxResult = 20;

    public PageListView(Context context) {
        super(context);

        // default layout is fill_parent
        ViewGroup.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
        this.setLayoutParams(lp);
    }


    public PageListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        mAdapter = (PullToRefreshListViewAdapter<T>) adapter;
        super.setAdapter(adapter);
        final PageListView<T> me = this;
        me.setMode(Mode.BOTH);
        // 下拉刷新
        this.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                me.doRefresh();

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                me.doLoad(false);
            }
        });
    }

    public void doLoad(String... args) {
        this.loadData(false);
    }

    public void doLoad(boolean isRefresh) {
        if (isRefresh) {
            mStartIndex = 0;
        }

        if (mStartIndex > mTotalCount) {
            return;
        }
        this.loadData(isRefresh);
    }


    public void doRefresh() {
        this.doLoad(true);
    }

    private void loadData(final boolean isRefresh) {
        Log.d(VEApplication.TAG, "loadData startIndex = " + mStartIndex + ", mTotalCount = " + mTotalCount);
        final PageListView<T> me = this;

        if (isRefresh) {
            mStartIndex = 0;
        }

        final int toLoadStartIndex = mStartIndex;
        new Thread(new Runnable() {
            @Override
            public void run() {
                final PageList<T> pageList = me.onLoadPageList(toLoadStartIndex, maxResult);
                if (pageList == null) {
                    Log.d(VEApplication.TAG, "page list is null");
                    me.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(me.getContext(), "服务器木有数据", Toast.LENGTH_LONG).show();
                            me.onRefreshComplete();
                        }
                    });
                    return;
                }
                mStartIndex += maxResult;
                final PullToRefreshListViewAdapter adapter = mAdapter;
                final List list = adapter.getList();
                mTotalCount = pageList.totalCount;
                me.post(new Runnable() {
                    @Override
                    public void run() {
                        if (isRefresh && list != null) {
                            list.clear();
                            adapter.notifyDataSetChanged();
                        }
                        if (list == null) {
                            adapter.setList(pageList.records);
                        } else {
                            list.addAll(pageList.records);
                        }
                        adapter.notifyDataSetChanged();
                        me.onRefreshComplete();
                    }
                });
            }
        }).start();
    }

    public abstract PageList<T> onLoadPageList(int startIndex, int maxResult);

}
