package com.tadpolemusic.activity.fragment;

import android.support.v4.app.Fragment;


public abstract class AbsCenterContent extends Fragment {
    public abstract String geTitle();
    public abstract String getUniqueId();
    
    /**
     * when this fragment is showing and the playing index of Music Player is changedF
     * 
     * @param index
     * @return
     */
    public abstract void onMusicPlayingIndexChange(int index);
}
