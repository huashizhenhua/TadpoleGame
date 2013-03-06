package com.tadpolemusic.api.impl;

import java.util.ArrayList;

import android.os.Bundle;
import android.util.Log;

import com.itap.voiceemoticon.common.ConstValues;
import com.tadpolemusic.VEApplication;
import com.tadpolemusic.api.PageList;
import com.tadpolemusic.api.VEHttpCaller;
import com.tadpolemusic.api.VEResponse;
import com.tadpolemusic.api.Voice;
import com.tadpolemusic.api.VoiceEmoticonApi;
import com.tadpolemusic.media.MusicData;

public class VoiceEmoticonApiImpl implements VoiceEmoticonApi {

    private RequestURLGenerator mRequestURLGenerator;

    private ServerRequestController mServerRequestController;

    public VoiceEmoticonApiImpl() {
        mRequestURLGenerator = RequestURLGenerator.getInstance();
    }

    @Override
    public PageList<Voice> getHostVoicesList(int startIndex, int maxResults) {
        Log.e(VEApplication.TAG, "getHostVoicesList called");
        Bundle data = mRequestURLGenerator.generateRequestUrl(ConstValues.GETPOPULAR, null);
        String url = data.getString("url");
        url = url + "?start_index=" + startIndex + "&max_results=" + maxResults;
        VEResponse resp = VEHttpCaller.doGet(url);
        if (resp.isSuccess()) {
            return Voice.buildPageListFromJSON(resp.data);
        }
        return null;
    }

    @Override
    public PageList<Voice> searchHostVoices(String searchKey, int startIndex, int maxResults) {
        Log.e(VEApplication.TAG, "searchHostVoices called");
        Bundle bundle = new Bundle();
        bundle.putString(ConstValues.KEY, searchKey);
        bundle.putInt(ConstValues.STARTINDEX, startIndex);
        bundle.putInt(ConstValues.MAXRESULT, maxResults);
        Bundle data = mRequestURLGenerator.generateRequestUrl(ConstValues.SEARCH, bundle);
        String url = data.getString("url");
        url = url + "?start_index=" + startIndex + "&max_results=" + maxResults;
        VEResponse resp = VEHttpCaller.doGet(url);
        if (resp.isSuccess()) {
            return Voice.buildPageListFromJSON(resp.data);
        }
        return null;
    }

    @Override
    public void statistics(ArrayList<String> urlList) {
        Log.e(VEApplication.TAG, "statistics called");
        Bundle bundle = new Bundle();
        String[] strArr = (String[]) urlList.toArray(new String[urlList.size()]);
        bundle.putStringArray(ConstValues.HOTSTAT, strArr);
        Bundle data = mRequestURLGenerator.generateRequestUrl(ConstValues.SENDSTATISTICS, bundle);
        String url = data.getString("url");
        String body = data.getString("body");
        VEResponse resp = VEHttpCaller.doPost(url, body);
        return;
    }



    @Override
    public PageList<MusicData> getMusicListFromtHostVoices(int startIndex, int maxResults) {
        Log.e(VEApplication.TAG, "getHostVoicesList called");
        Bundle data = mRequestURLGenerator.generateRequestUrl(ConstValues.GETPOPULAR, null);
        String url = data.getString("url");
        url = url + "?start_index=" + startIndex + "&max_results=" + maxResults;
        VEResponse resp = VEHttpCaller.doGet(url);
        if (resp.isSuccess()) {
            PageList<Voice> voicePageList = Voice.buildPageListFromJSON(resp.data);

            ArrayList<Voice> voiceList = voicePageList.records;
            int totalCount = voicePageList.totalCount;

            PageList<MusicData> dataPageList = new PageList<MusicData>();
            dataPageList.totalCount = voicePageList.totalCount;

            ArrayList<MusicData> dataList = new ArrayList<MusicData>();
            for (int i = 0, N = voiceList.size(); i < N; i++) {
                MusicData music = new MusicData();
                Voice voice = voiceList.get(i);
                music.musicName = voice.musicName;
                music.musicPath = voice.musicPath;
                music.musicAritst = voice.tags;
            }
            return dataPageList;
        }
        return null;
    }

}
