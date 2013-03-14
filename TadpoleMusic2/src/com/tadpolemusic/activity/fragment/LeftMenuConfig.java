package com.tadpolemusic.activity.fragment;

import java.util.ArrayList;

import com.tadpolemusic.R;
import com.tadpolemusic.activity.AboutActivity;
import com.tadpolemusic.activity.fragment.center.HotVoiceFragment;
import com.tadpolemusic.activity.fragment.center.LocalMusicFragment;
import com.tadpolemusic.adapter.MyMusicItem;

/**
 * 
 * NOTE!! please keey content key unique
 * 
 * 
 * <br>==========================
 * <br> author：Zenip
 * <br> email：lxyczh@gmail.com
 * <br> create：2013-3-2
 * <br>==========================
 */
public class LeftMenuConfig {


    public static MyMusicItem getLocalMusicItem() {
        MyMusicItem localMusicItem = new MyMusicItem();
        localMusicItem.text = "本地音乐";
        localMusicItem.centerContentClass = LocalMusicFragment.class;
        localMusicItem.action = MyMusicItem.Action.REPLEACE_CENTER;
        localMusicItem.contentKey = "local_music";
        localMusicItem.iconDefaultResId = R.drawable.icon_navigation_local_music;
        localMusicItem.iconSelectedResId = R.drawable.icon_navigation_local_music;
        return localMusicItem;

    }

    public static ArrayList<MyMusicItem> getMyMusicItem() {

        // local music 
        ArrayList<MyMusicItem> myMusicItems = new ArrayList<MyMusicItem>();
        myMusicItems.add(getLocalMusicItem());


        MyMusicItem item = new MyMusicItem();
        item.text = "热门语音";
        item.contentKey = "hot_voice";
        item.centerContentClass = HotVoiceFragment.class;
        item.action = MyMusicItem.Action.REPLEACE_CENTER;
        item.iconDefaultResId = R.drawable.icon_navigation_listen_playlist;
        item.iconSelectedResId = R.drawable.icon_navigation_listen_playlist;
        myMusicItems.add(item);

        item = new MyMusicItem();
        item.text = "独立Activity";
        item.contentKey = "hot_voice";
        item.activityClass = AboutActivity.class;
        item.action = MyMusicItem.Action.NEW_ACTIVITY;
        item.iconDefaultResId = R.drawable.icon_navigation_collect_playlist;
        item.iconSelectedResId = R.drawable.icon_navigation_collect_playlist;
        myMusicItems.add(item);


        return myMusicItems;
    }
}
