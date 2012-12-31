package org.tadpole.adapter;

import java.util.ArrayList;

import org.tadpole.widget.PagedAdapter;

import android.view.View;
import android.view.ViewGroup;

public class BoardPagedAdapter extends PagedAdapter {

    private ArrayList<View> mPageViews;

    public BoardPagedAdapter(ArrayList<View> pageViews) {
        if (pageViews != null) {
            mPageViews = pageViews;
        } else {
            mPageViews = new ArrayList<View>();
        }
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mPageViews.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mPageViews.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return mPageViews.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        return mPageViews.get(position);
    }

}