package com.tadpolemusic.api;

import java.util.ArrayList;

import com.tadpolemusic.media.MusicData;


public interface VoiceEmoticonApi {
    public PageList<Voice> getHostVoicesList(int startIndex, int maxResults);

    public PageList<Voice> searchHostVoices(String searchKey, int startIndex, int maxResult);

    public void statistics(ArrayList<String> urlList);

    public PageList<MusicData> getMusicListFromtHostVoices(int startIndex, int maxResults);
}
