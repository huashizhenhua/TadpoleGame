package com.tadpolemusic.adapter;

import android.app.Activity;

import com.tadpolemusic.activity.fragment.AbsCenterContent;

public class MyMusicItem {
    public enum Action {
        NEW_ACTIVITY, REPLEACE_CENTER
    }

    public int iconDefaultResId = android.R.drawable.ic_menu_call;
    public String text = "set text please";
    public Action action = Action.REPLEACE_CENTER;
    public String contentKey = "default";
    public Class<? extends AbsCenterContent> centerContentClass;
    public Class<? extends Activity> activityClass;
    public int iconSelectedResId = android.R.drawable.ic_menu_camera;
}
