package com.itap.voiceemoticon.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ListView.FixedViewInfo;

/**
 * Nice wrapper-abstraction around ArrayList
 * 
 * @author Zenip
 * 
 * @param <T>
 */
public abstract class ArrayListAdapter<T> extends BaseAdapter {


    protected ArrayList<T> mList;
    protected Activity mContext;
    protected ListView mListView;

    public ArrayListAdapter(Activity context) {
        this.mContext = context;
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

    public void setList(ArrayList<T> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    public ArrayList<T> getList() {
        return mList;
    }

    public void setList(T[] list) {
        ArrayList<T> arrayList = new ArrayList<T>(list.length);
        for (T t : list) {
            arrayList.add(t);
        }
        setList(arrayList);
    }

    public ListView getListView() {
        return mListView;
    }

    public void setListView(ListView listView) {
        mListView = listView;
    }

    protected Context getContext() {
        return mContext;
    }

    protected LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(mContext);
    }
}
