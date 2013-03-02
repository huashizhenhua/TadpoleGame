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
public abstract class ListViewAdapter<T> extends BaseListAdapter<T, ListView> {

    public ListViewAdapter(Activity context) {
        super(context);
    }
}
