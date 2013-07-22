package com.itap.voiceemoticon.widget;

import java.util.ArrayList;

import android.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Adapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.Toast;
import android.widget.AdapterView;

import com.itap.voiceemoticon.VEApplication;
import com.itap.voiceemoticon.adapter.ArrayListAdapter;
import com.itap.voiceemoticon.api.PageList;
import com.itap.voiceemoticon.api.Voice;

public abstract class PageListView<T> extends XListView implements OnScrollListener, XListView.IXListViewListener {
    private LinearLayout mLoadLayout;
    private ArrayListAdapter<T> mAdapter;
    private int mTotalCount;
    private int mLastItem;
    private int mStartIndex;
    public int maxResult = 20;

    public PageListView(Context context) {
        super(context);
        init();
    }
    

    public PageListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public PageListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setDivider(getResources().getDrawable(org.tadpoleframework.R.drawable.divider));
        setSelector(new ColorDrawable(Color.TRANSPARENT));
    }
    
    @Override
    public void setAdapter(ListAdapter adapter) {
        mAdapter = (ArrayListAdapter<T>) adapter;
        this.setFooterDividersEnabled(false);
        this.setXListViewListener(this);
        super.setAdapter(adapter);
    }

    public void doLoad(String... args) {
        super.showFooterLoading();
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
        final PageListView me = this;

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
                            Toast.makeText(me.getContext(), "服务器木有数据", Toast.LENGTH_LONG);
                            me.stopRefreshOrLoad();
                        }
                    });
                    return;
                }
                mStartIndex += maxResult;
                final ArrayListAdapter adapter = mAdapter;
                final ArrayList list = adapter.getList();
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
                        me.stopRefreshOrLoad();
                    }
                });
            }
        }).start();
    }

    public abstract PageList<T> onLoadPageList(int startIndex, int maxResult);

    public void onRefresh() {
        doRefresh();
    }

    public void onLoadMore() {
        doLoad();
    }
    
}
