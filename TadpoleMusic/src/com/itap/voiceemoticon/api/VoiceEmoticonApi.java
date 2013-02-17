package com.itap.voiceemoticon.api;

import java.util.ArrayList;


public interface VoiceEmoticonApi {
    public PageList<Voice> getHostVoicesList(int startIndex, int count);

    public PageList<Voice> searchHostVoices(String searchKey, int startIndex, int maxResult);

    public void statistics(ArrayList<String> urlList);
}
