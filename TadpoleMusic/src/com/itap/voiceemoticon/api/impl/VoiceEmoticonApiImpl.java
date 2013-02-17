package com.itap.voiceemoticon.api.impl;

import java.util.ArrayList;

import android.os.Bundle;
import android.util.Log;

import com.itap.voiceemoticon.VEApplication;
import com.itap.voiceemoticon.api.PageList;
import com.itap.voiceemoticon.api.VEHttpCaller;
import com.itap.voiceemoticon.api.VEResponse;
import com.itap.voiceemoticon.api.Voice;
import com.itap.voiceemoticon.api.VoiceEmoticonApi;
import com.itap.voiceemoticon.common.ConstValues;

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
}
