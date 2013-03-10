package com.tadpolemusic.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class BaseListAdapter<T, V extends View> extends BaseAdapter {
    public static final int INVALID_POSITION = -1;
    protected List<T> mList;
    protected Activity mContext;
    protected V mListView;
    protected int mSelectedPosition = INVALID_POSITION;

    public BaseListAdapter(Activity context) {
        this.mContext = context;
    }

    public void setSelectedPostion(int i) {
        if (mSelectedPosition != i) {
            mSelectedPosition = i;
            this.notifyDataSetChanged();
        }
    }

    public int getSelectedPostion() {
        return mSelectedPosition;
    }

    @Override
    public int getCount() {
        if (mList != null)
            return mList.size();
        else
            return 0;
    }

    @Override
    public Object getItem(int position) {
        return mList == null ? null : mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    abstract public View getView(int position, View convertView, ViewGroup parent);

    public void setList(List<T> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    public List<T> getList() {
        return mList;
    }

    public void setList(T[] list) {
        ArrayList<T> arrayList = new ArrayList<T>(list.length);
        for (T t : list) {
            arrayList.add(t);
        }
        setList(arrayList);
    }

    public V getListView() {
        return mListView;
    }

    public void setListView(V listView) {
        mListView = listView;
    }

    protected Context getContext() {
        return mContext;
    }

    protected LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(mContext);
    }
}
