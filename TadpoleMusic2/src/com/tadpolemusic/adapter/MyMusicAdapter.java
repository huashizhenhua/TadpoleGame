package com.tadpolemusic.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tadpolemusic.R;

public class MyMusicAdapter extends GridViewAdapter<MyMusicItem> {

    public MyMusicAdapter(Activity context) {
        super(context);
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        MyMusicItem item = (MyMusicItem) getItem(position);
        View view = convertView;
        ViewHolder viewHolder = null;
        if (view == null) {
            view = getLayoutInflater().inflate(R.layout.sliding_menu_left_grid_item, null);
            viewHolder = new ViewHolder();
            viewHolder.textViewText = (TextView) view.findViewById(R.id.text_view_text);
            viewHolder.imageViewIcon = (ImageView) view.findViewById(R.id.image_view_icon);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textViewText.setText(item.text);
        viewHolder.imageViewIcon.setBackgroundResource(item.iconDrawableId);
        return view;
    }

    /**
     * Class implementing holder pattern,
     * performance boost
     * 
     * @author Zenip
     */
    static class ViewHolder {
        ImageView imageViewIcon;
        TextView textViewText;
    }
}
