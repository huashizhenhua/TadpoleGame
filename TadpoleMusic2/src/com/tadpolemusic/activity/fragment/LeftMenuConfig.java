package com.tadpolemusic.activity.fragment;

import java.util.ArrayList;

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
    public static ArrayList<MyMusicItem> myMusicItems = new ArrayList<MyMusicItem>();

    public static MyMusicItem localMusicItem = new MyMusicItem();

    static {
        // local music 
        MyMusicItem item = localMusicItem;
        item.text = "本地音乐";
        item.centerContentClass = LocalMusicFragment.class;
        item.action = MyMusicItem.Action.REPLEACE_CENTER;
        item.contentKey = "local_music";
        myMusicItems.add(localMusicItem);


        // hot voice
        item = new MyMusicItem();
        item.text = "热门语音";
        item.contentKey = "hot_voice";
        item.centerContentClass = HotVoiceFragment.class;
        item.action = MyMusicItem.Action.REPLEACE_CENTER;
        myMusicItems.add(item);

        // hot voice
        item = new MyMusicItem();
        item.text = "关于我们(Activity)";
        item.contentKey = "hot_voice";
        item.activityClass = AboutActivity.class;
        item.action = MyMusicItem.Action.NEW_ACTIVITY;
        myMusicItems.add(item);
        
        item = new MyMusicItem();
        item.text = "关于我们(Activity)";
        item.contentKey = "hot_voice";
        item.activityClass = AboutActivity.class;
        item.action = MyMusicItem.Action.NEW_ACTIVITY;
        myMusicItems.add(item);
    }
}
