package com.tadpolemusic.activity;

import android.app.Activity;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.GridView;

import com.tadpolemusic.R;

public class ImageGallareyActivity extends Activity {

    private GridView mGridView;
    private static final String[] STORE_IMAGES = { MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.LATITUDE, MediaStore.Images.Media.LONGITUDE, MediaStore.Images.Media._ID };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_gallery);
        mGridView = (GridView) findViewById(R.id.gridview);
        getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, STORE_IMAGES, null, null, null);

    }
}
