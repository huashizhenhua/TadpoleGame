package com.tadpolemusic.activity.fragment.menu;

import com.tadpolemusic.activity.fragment.AbsCenterContent;
import com.tadpolemusic.adapter.MyMusicItem;

public interface ILeftMenuControl {
    public void scrollToCenter();

    void setCenterContent(MyMusicItem item);
}
