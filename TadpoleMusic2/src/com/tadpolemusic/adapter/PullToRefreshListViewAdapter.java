package com.tadpolemusic.adapter;

import java.util.ArrayList;

import com.handmark.pulltorefresh.library.PullToRefreshListView;

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
public abstract class PullToRefreshListViewAdapter<T> extends BaseListAdapter<T, PullToRefreshListView> {

    public PullToRefreshListViewAdapter(Activity context) {
        super(context);
    }
}
