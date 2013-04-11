package com.itap.voiceemoticon.widget;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.tadpolemusic.R;

/**
 * <br>==========================
 * <br> author：Zenip
 * <br> email：lxyczh@gmail.com
 * <br> create：2013-1-26下午12:02:43
 */
public class LoadingUtil {

    public static View getLoadingWidget(Activity activity) {
        View v = activity.getLayoutInflater().inflate(R.layout.loading_blue, null);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
        v.setLayoutParams(lp);
        return v;
    }
}