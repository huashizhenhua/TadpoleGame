package com.tadpolemusic.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tadpolemusic.R;
import com.tadpolemusic.VEApplication;
import com.tadpolemusic.api.Voice;

/**
 * 
 * usage。
 * 
 * <br>==========================
 * <br> author：Zenip
 * <br> email：lxyczh@gmail.com
 * <br> create：2013-1-31上午9:33:58
 * <br>==========================
 */
public class VoiceAdapter extends PullToRefreshListViewAdapter<Voice> {

    public VoiceAdapter(Activity context) {
        super(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Voice item = (Voice) getItem(position);
        View view = convertView;
        ViewHolder viewHolder = null;
        if (view == null) {
            view = getLayoutInflater().inflate(R.layout.list_item_hot_voice, null);
            viewHolder = new ViewHolder();
            viewHolder.textViewTitle = (TextView) view.findViewById(R.id.text_view_title);
            viewHolder.textViewTags = (TextView) view.findViewById(R.id.text_view_tags);
            viewHolder.btnShare = view.findViewById(R.id.btn_share);
            viewHolder.btnCollect = (ImageButton) view.findViewById(R.id.btn_collect);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textViewTitle.setText(item.musicName);
        viewHolder.textViewTags.setText(item.tags);

        if (VEApplication.isCollected(mContext, item.musicPath)) {
            viewHolder.btnCollect.setImageResource(R.drawable.btn_hasfavorite_default);
        } else {
            viewHolder.btnCollect.setImageResource(R.drawable.btn_favorite_default);
        }

        viewHolder.btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mList.get(position).sendToWeixin(v.getContext());
            }
        });

        final ImageButton finalBtnCollect = viewHolder.btnCollect;
        viewHolder.btnCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mList.get(position).saveToCollect(v.getContext());
                VEApplication.reloadVoiceCollectedCache(mContext);
                finalBtnCollect.setImageResource(R.drawable.btn_hasfavorite_default);
            }
        });

        return view;
    }

    /**
     * Class implementing holder pattern,
     * performance boost
     * 
     * @author Zenip
     */
    static class ViewHolder {
        TextView textViewTitle;
        TextView textViewTags;
        View btnShare;
        ImageButton btnCollect;
    }

}