package org.tadpole.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.tadpole.app.BoardPageItem;
import org.tadpole.app.R;
import org.tadpole.common.TLog;
import org.tadpole.widget.BoardDataConfig;
import org.tadpole.widget.Configure;
import org.tadpole.widget.DragGridView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DragGridAdapter extends BaseAdapter implements IDragGridAdapter {

    private static final String TAG = "DragGridAdapter";
    private Context mContext;
    private BoardDataConfig<BoardPageItem> mBoardData;
    private LayoutInflater mInflater;
    private int mPage;

    public DragGridAdapter(Context context, int page, BoardDataConfig<BoardPageItem> boardData) {
        super();
        mContext = context;
        mBoardData = boardData;
        mInflater = LayoutInflater.from(mContext);
        mPage = page;
    }

    public List<BoardPageItem> getItemList() {
        List<BoardPageItem> list = mBoardData.getPageItemList(mPage);
        if (list == null) {
            list = new ArrayList<BoardPageItem>();
        }
        return list;
    }

    @Override
    public int getCount() {
        return getItemList().size();
    }

    public void exchange(int startPosition, int endPosition) {

        List<BoardPageItem> pageItemList = getItemList();

        TLog.debug(TAG, "exchange startPosition = %d, endPosition = %d", startPosition, endPosition);
        BoardPageItem start = (BoardPageItem) getItem(startPosition);
        BoardPageItem end = (BoardPageItem) getItem(endPosition);
        pageItemList.add(startPosition, end);
        pageItemList.remove(startPosition + 1);
        pageItemList.add(endPosition, start);
        pageItemList.remove(endPosition + 1);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        TLog.debug(TAG, "getView = " + parent);

        final DragGridView dragGridViewParent = (DragGridView) parent;

        List<BoardPageItem> pageItemList = getItemList();

        final View view = mInflater.inflate(org.tadpole.app.R.layout.board_page_griditem, null);
        TextView textView = (TextView) view.findViewById(R.id.pageItemText);
        View deleteBtnView = view.findViewById(R.id.pageItemDeleteBtn);
        final BoardPageItem item = pageItemList.get(position);
        textView.setText(item.title);

        final View bg = view.findViewById(R.id.pageItemBg);

        deleteBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dragGridViewParent.delete(position, item, view);
            }
        });

        if (BoardPageItem.COLOR_BLUE.equals(item.color)) {
            bg.setBackgroundResource(R.drawable.blue);
        } else {
            bg.setBackgroundResource(R.drawable.red);
        }

        if (Configure.isEditMode) {
            bg.getBackground().setAlpha(220);
            if (item.editable) {
                deleteBtnView.setVisibility(View.VISIBLE);
            } else {
                deleteBtnView.setVisibility(View.INVISIBLE);
            }
        } else {
            bg.getBackground().setAlpha(255);
            deleteBtnView.setVisibility(View.INVISIBLE);
        }

        if (item.hide) {
            view.setVisibility(View.INVISIBLE);
        }
        return view;
    }

    /**
     * 根据数据进行排序
     */
    public void sortByPositions(final int arr[]) {
        List<BoardPageItem> pageItemList = getItemList();
        for (int i = 0, len = pageItemList.size(); i < len; i++) {
            pageItemList.get(i).sortTag = arr[i];
        }
        Collections.sort(pageItemList, new Comparator<BoardPageItem>() {
            @Override
            public int compare(BoardPageItem lhs, BoardPageItem rhs) {
                return lhs.sortTag > rhs.sortTag ? 1 : -1;
            }
        });
    }

    @Override
    public void replace(int index, BoardPageItem data) {
        List<BoardPageItem> pageItemList = getItemList();
        pageItemList.remove(index);
        pageItemList.add(index, (BoardPageItem) data);
    }

    @Override
    public Object getItem(int position) {
        List<BoardPageItem> pageItemList = getItemList();
        return pageItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
