package com.tadpolemusic.activity.fragment.menu;

import com.tadpolemusic.adapter.MyMusicItem;

public interface ILeftMenuControl {
    public void scrollToCenter();

    public void scrollToLeft();

    void setCenterContent(MyMusicItem item);

}
