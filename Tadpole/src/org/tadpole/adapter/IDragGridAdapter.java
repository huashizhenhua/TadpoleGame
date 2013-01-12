package org.tadpole.adapter;

import org.tadpole.widget.DragGridView;

import android.widget.ListAdapter;

/**
 * when use {@link DragGridView}, adapter must implement {@link IDragGridAdapter}
 * 
 * <br>==========================
 * <br> author：Zenip
 * <br> email：lxyczh@gmail.com
 * <br> create：2013-1-1上午10:18:16
 * <br>==========================
 */
public interface IDragGridAdapter extends ListAdapter {
    /**
     * 
     * exchange item by position
     * 
     * @param startPosition
     * @param endPosition
     */
    public void exchange(int startPosition, int endPosition);

    /**
     * to update new data
     * instruction。
     */
    public void notifyDataSetChanged();
}
