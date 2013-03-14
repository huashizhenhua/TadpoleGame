package com.tadpolemusic.activity.fragment.menu;

import com.tadpolemusic.activity.LeftAndRightActivity.ActivityMusicListenter;
import com.tadpolemusic.adapter.MyMusicItem;

public interface IMenuControl {
    public void scrollToCenter();

    public void scrollToLeft();

    void setCenterContent(MyMusicItem item);

    public void scrollToRight();

    public void registerMusicListener(ActivityMusicListenter listener);

    public void unRegisterMusicListenr(ActivityMusicListenter listener);
}
