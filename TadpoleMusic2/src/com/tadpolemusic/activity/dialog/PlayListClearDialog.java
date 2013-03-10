package com.tadpolemusic.activity.dialog;


import android.content.Context;

import com.tadpolemusic.R;


/**
 * 
 * <br>==========================
 * <br> author：Zenip
 * <br> email：lxyczh@gmail.com
 * <br> create：2013-3-8
 * <br>==========================
 */
public class PlayListClearDialog extends BaseAlertDialog {
    public PlayListClearDialog(final Context context) {
        super(context);
        setContent(R.layout.dialog_alert_play_list_clear);
        setTitle("清空队列");
    }
}
