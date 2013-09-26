package com.itap.voiceemoticon.api;

import java.util.ArrayList;
import java.util.HashMap;

import com.itap.voiceemoticon.db.UserVoice;


public interface VoiceEmoticonApi {
    public PageList<Voice> getHostVoicesList(int startIndex, int count);

    public PageList<Voice> searchHostVoices(String searchKey, int startIndex, int maxResult);

    public void statistics(ArrayList<String> urlList);
    
    public ArrayList<UserVoice> getList(String uid, String platform);
    
    public void delete(long[] idArr);
    
    public VEResponse uploadVoice(HashMap<String, String> params, String mp3FilePath, String fileName);
}
