package com.tadpolemusic.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.quickactionbar.QuickAction;
import com.quickactionbar.QuickActionBar;
import com.quickactionbar.QuickActionGrid;
import com.tadpolemusic.R;
import com.tadpolemusic.media.LocalMusicItem;

/**
 * <br>==========================
 * <br> author：Zenip
 * <br> email：lxyczh@gmail.com
 * <br> create：2013-1-31
 * <br>==========================
 */
public class LocalMusicAdapter extends ListViewAdapter<LocalMusicItem> implements SectionIndexer, OnScrollListener {

    private int mSelectedPostion = -1;

    public static interface OnSectionChangeListener {
        public void handle(char letter);
    }

    private OnSectionChangeListener mSectionChangeListener;

    public LocalMusicAdapter(Activity context) {
        super(context);
    }

    public void setSelectPostion(int position) {
        if (mSelectedPostion != position) {
            mSelectedPostion = position;
            this.notifyDataSetChanged();
        }
    }

    @SuppressLint("NewApi")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LocalMusicItem item = (LocalMusicItem) getItem(position);
        View view = convertView;
        ViewHolder viewHolder = null;
        if (view == null) {
            view = getLayoutInflater().inflate(R.layout.list_item_local_music, null);
            viewHolder = new ViewHolder();
            viewHolder.textViewSectionTitle = (TextView) view.findViewById(R.id.text_view_section_title);
            viewHolder.textViewMusicTitle = (TextView) view.findViewById(R.id.text_view_music_title);
            viewHolder.imageViewIcon = (ImageView) view.findViewById(R.id.image_view_icon);
            viewHolder.imageButtonOperation = (ImageButton) view.findViewById(R.id.image_button_operation);
            viewHolder.imageViewSelect = (ImageView) view.findViewById(R.id.image_view_select);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        String section = item.getFirstLetterInUpcase();
        // first item show the section
        if (position == 0) {
            viewHolder.textViewSectionTitle.setVisibility(View.VISIBLE);
            viewHolder.textViewSectionTitle.setText(section);
        }
        // if it is the new section , show too.
        else {
            String lastItemSection = mList.get(position - 1).getFirstLetterInUpcase();
            if (section.equals(lastItemSection)) {
                viewHolder.textViewSectionTitle.setVisibility(View.GONE);
            } else {
                viewHolder.textViewSectionTitle.setVisibility(View.VISIBLE);
                viewHolder.textViewSectionTitle.setText(section);
            }
        }

        if (position == mSelectedPostion) {
            viewHolder.imageViewSelect.setVisibility(View.VISIBLE);
        } else {
            viewHolder.imageViewSelect.setVisibility(View.INVISIBLE);
        }

        viewHolder.textViewMusicTitle.setText(item.getFileName());
        viewHolder.textViewSectionTitle.setText(item.getFirstLetterInUpcase());
        viewHolder.imageButtonOperation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Context ctx = v.getContext();

                QuickActionGrid quickActionGrid = new QuickActionGrid(ctx);
                quickActionGrid.setWidth(400);
                QuickAction quickActionDel = new QuickAction(ctx, android.R.drawable.ic_menu_delete, "删除");
                quickActionGrid.addQuickAction(quickActionDel);
                QuickAction quickActionShare = new QuickAction(ctx, android.R.drawable.ic_menu_share, "分享");
                quickActionGrid.addQuickAction(quickActionShare);
                quickActionGrid.show(v);

            }
        });

        return view;
    }


    public void showDeleteDialog(final LocalMusicItem item) {
        final LocalMusicAdapter me = this;
        AlertDialog.Builder ab = new AlertDialog.Builder(this.getContext());
        ab.setTitle("确定要删除?");
        ab.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mList.remove(item);
                item.delete(mContext);
                me.notifyDataSetChanged();
            }
        });
        ab.setNegativeButton("取消", null);
        ab.show();
    }

    /**
     * Class implementing holder pattern,
     * performance boost
     * 
     * @author Zenip
     */
    static class ViewHolder {
        ImageView imageViewIcon;
        ImageView imageViewSelect;
        TextView textViewMusicTitle;
        TextView textViewSectionTitle;
        ImageButton imageButtonOperation;
    }


    @Override
    public int getPositionForSection(int section) {
        LocalMusicItem item;
        String letter;
        for (int i = 0; i < getCount(); i++) {
            item = (LocalMusicItem) mList.get(i);
            letter = item.getFirstLetterInUpcase();
            char firstChar = letter.toUpperCase().charAt(0);
            if (letter.length() > 0 && firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    public void setOnSectionChangeListener(OnSectionChangeListener listener) {
        mSectionChangeListener = listener;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mList == null || firstVisibleItem == -1 || firstVisibleItem == 0) {
            return;
        }
        LocalMusicItem item = mList.get(firstVisibleItem);
        String firstLetter = item.getFirstLetterInUpcase();
        if (mSectionChangeListener != null) {
            mSectionChangeListener.handle(firstLetter.charAt(0));
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }
}
