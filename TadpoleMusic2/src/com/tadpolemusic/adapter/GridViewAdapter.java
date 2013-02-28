package com.tadpolemusic.adapter;

import android.app.Activity;
import android.widget.GridView;

public abstract class GridViewAdapter<T> extends BaseListAdapter<T, GridView> {

    public GridViewAdapter(Activity context) {
        super(context);
    }
}
