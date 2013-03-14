package com.tadpolemusic.activity;

import java.util.zip.Inflater;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tadpolemusic.R;
import com.tadpolemusic.adapter.GridViewAdapter;

public class ImageGalleryGridAdapter extends GridViewAdapter<ImageGallareyItem>{

    public ImageGalleryGridAdapter(Activity context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       View view =  getLayoutInflater().inflate(R.layout.grid_item_image_gallery, null);
        
       
       ViewHolder holder =new ViewHolder();
       holder.imageView = (ImageView) view.findViewById(R.id.imageview);
       
       
       ImageGallareyItem item = (ImageGallareyItem) getItem(position);
       
        
        return view;
    }
   
    static class ViewHolder {
       public ImageView imageView;
    }
}
