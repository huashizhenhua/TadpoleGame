package com.itap.voiceemoticon.widget;

import com.markupartist.android.widget.ScrollingTextView;
import com.tadpolemusic.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

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