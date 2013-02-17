package org.tadpole.adapter;

import java.util.Map;

import org.tadpole.app.BoardPageItem;
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

    public void notifyDataSetChanged();

    public void sortByPositions(int[] viewTag);

    public void replace(int position, BoardPageItem data);
}
