package com.itap.voiceemoticon.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.itap.voiceemoticon.R;
import com.itap.voiceemoticon.VEApplication;
import com.itap.voiceemoticon.activity.MainActivity;
import com.itap.voiceemoticon.api.Voice;

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
public class VoiceAdapter extends ArrayListAdapter<Voice> {

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
        viewHolder.textViewTitle.setText(item.title);
        viewHolder.textViewTags.setText(item.tags);

        if (VEApplication.isCollected(mContext, item.url)) {
            viewHolder.btnCollect.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            viewHolder.btnCollect.setImageResource(android.R.drawable.btn_star_big_off);
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
                finalBtnCollect.setImageResource(android.R.drawable.btn_star_big_on);
                MainActivity mainActivity = (MainActivity) mContext;
                if (mainActivity.myCollectVoiceFragment != null) {
                    mainActivity.myCollectVoiceFragment.reloadData();
                }
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