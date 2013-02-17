package com.itap.voiceemoticon.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.itap.voiceemoticon.R;
import com.itap.voiceemoticon.api.Voice;

/**
 * <br>==========================
 * <br> author：Zenip
 * <br> email：lxyczh@gmail.com
 * <br> create：2013-1-31
 * <br>==========================
 */
public class MyCollectAdapter extends VoiceAdapter implements SectionIndexer {

    public MyCollectAdapter(Activity context) {
        super(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder = null;
        if (view == null) {
            view = getLayoutInflater().inflate(R.layout.list_item_hot_voice, null);
            viewHolder = new ViewHolder();
            viewHolder.textViewTitle = (TextView) view.findViewById(R.id.text_view_title);
            viewHolder.textViewTags = (TextView) view.findViewById(R.id.text_view_tags);
            viewHolder.btnShare = view.findViewById(R.id.btn_share);
            viewHolder.btnDelete = view.findViewById(R.id.btn_delete);
            viewHolder.btnCollect = view.findViewById(R.id.btn_collect);
            viewHolder.textViewLetter = (TextView) view.findViewById(R.id.text_view_section_title);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Voice item = (Voice) getItem(position);

        viewHolder.textViewTitle.setText(item.title);
        viewHolder.textViewTags.setText(item.tags);

        viewHolder.btnDelete.setVisibility(View.VISIBLE);
        viewHolder.btnCollect.setVisibility(View.GONE);

        String section = item.getFirstLetter();
        // first item show the section
        if (position == 0) {
            viewHolder.textViewLetter.setVisibility(View.VISIBLE);
            viewHolder.textViewLetter.setText(section);
        }
        // if it is the new section , show too.
        else {
            String lastItemSection = mList.get(position - 1).getFirstLetter();
            if (section.equals(lastItemSection)) {
                viewHolder.textViewLetter.setVisibility(View.GONE);
            } else {
                viewHolder.textViewLetter.setVisibility(View.VISIBLE);
                viewHolder.textViewLetter.setText(section);
            }
        }

        // click events
        viewHolder.btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mList.get(position).sendToWeixin(v.getContext());
            }
        });
        viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog(mList.get(position));
            }
        });
        return view;
    }

    public void showDeleteDialog(final Voice voice) {
        final MyCollectAdapter me = this;
        AlertDialog.Builder ab = new AlertDialog.Builder(this.getContext());
        ab.setTitle("确定要删除?");
        ab.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mList.remove(voice);
                voice.delete(mContext);
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
        TextView textViewTitle;
        TextView textViewTags;
        View btnShare;
        View btnDelete;
        View btnCollect;
        TextView textViewLetter;
    }

    @Override
    public int getPositionForSection(int section) {
        Voice voice;
        String letter;
        for (int i = 0; i < getCount(); i++) {
            voice = (Voice) mList.get(i);
            letter = voice.getFirstLetter();
            char firstChar = letter.toUpperCase().charAt(0);
            if (letter.length() > 0 && firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getSectionForPosition(int arg0) {
        return 0;
    }

    @Override
    public Object[] getSections() {
        return null;
    }
}
