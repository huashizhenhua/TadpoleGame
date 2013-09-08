
package com.itap.voiceemoticon.adapter;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.itap.voiceemoticon.R;
import com.itap.voiceemoticon.VEApplication;
import com.itap.voiceemoticon.activity.MainActivity;
import com.itap.voiceemoticon.api.Voice;
import com.itap.voiceemoticon.widget.WeixinAlert;

import org.tadpoleframework.widget.adapter.BaseListAdapter;

/**
 * usage。 <br>=
 * ========================= <br>
 * author：Zenip <br>
 * email：lxyczh@gmail.com <br>
 * create：2013-1-31上午9:33:58 <br>=
 * =========================
 */
public class VoiceAdapter extends BaseListAdapter<Voice, ListView> implements OnClickListener {

    public static final int CMD_SHARE = 1;
    public static final int CMD_COLLECT = 2;
    public static final int CMD_DELETE = 3;
    
    public VoiceAdapter(Activity context) {
        super(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Voice item = (Voice)getItem(position);
        View view = convertView;
        ViewHolder viewHolder = null;
        if (view == null) {
            view = getLayoutInflater().inflate(R.layout.list_item_hot_voice, null);
            viewHolder = new ViewHolder();
            viewHolder.textViewTitle = (TextView)view.findViewById(R.id.text_view_title);
            viewHolder.textViewTags = (TextView)view.findViewById(R.id.text_view_tags);
            viewHolder.btnShare = view.findViewById(R.id.btn_share);
            viewHolder.btnCollect = (ImageButton)view.findViewById(R.id.btn_collect);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.textViewTitle.setText(item.title);
        viewHolder.textViewTags.setText(item.tags);

        if (VEApplication.isCollected(mContext, item.url)) {
            viewHolder.btnCollect.setImageResource(R.drawable.btn_hasfavorite_default);
        } else {
            viewHolder.btnCollect.setImageResource(R.drawable.btn_favorite_default);
        }

        viewHolder.btnShare.setTag(position);
        viewHolder.btnShare.setOnClickListener(this);

        final ImageButton finalBtnCollect = viewHolder.btnCollect;
        viewHolder.btnCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mList.get(position).saveToCollect(v.getContext());
                VEApplication.reloadVoiceCollectedCache(mContext);
                finalBtnCollect.setImageResource(R.drawable.btn_hasfavorite_default);
                MainActivity mainActivity = (MainActivity)mContext;
                if (mainActivity.myCollectVoiceFragment != null) {
                    mainActivity.myCollectVoiceFragment.reloadData();
                }
            }
        });

        view.setBackgroundResource(R.drawable.wb_retweet_bg);

        return view;
    }

    /**
     * Class implementing holder pattern, performance boost
     * 
     * @author Zenip
     */
    static class ViewHolder {
        TextView textViewTitle;

        TextView textViewTags;

        View btnShare;

        ImageButton btnCollect;
    }

    @Override
    public void onClick(View v) {
        
        if(v.getTag() == null) {
            return;
        }
        
        int position = (Integer)v.getTag();
        Voice data = getItemData(position);
        switch (v.getId()) {
            case R.id.btn_share:
                callback(v, getItemData(position), CMD_SHARE, position);
                break;
            case R.id.btn_collect:
                callback(v, getItemData(position), CMD_COLLECT, position);
                break;
            default:
                break;
        }
    }

}
