package com.tadpolemusic.api;

import java.io.UnsupportedEncodingException;

import android.util.Log;

import com.itap.voiceemoticon.api.util.HttpCaller;
import com.itap.voiceemoticon.api.util.WSError;
import com.tadpolemusic.VEApplication;

public class VEHttpCaller {

    public static VEResponse doGet(String url) {
        String respContent = null;
        try {
            Log.d(VEApplication.TAG, "VEHttpCaller.doGet'request'url=" + url + "");
            respContent = HttpCaller.doGet(url);
            Log.d(VEApplication.TAG, "VEHttpCaller.doGet'response'content=" + respContent + "");
        } catch (WSError e) {
            e.printStackTrace();
            Log.d(VEApplication.TAG, "VEHttpCaller.doGet'response'errMsg=" + e.getMessage() + "");
        }
        return VEResponse.buildFromJSONString(respContent);
    }

    //TODO?
    public static VEResponse doPost(String url, String body) {
        String respContent = null;
        try {
            Log.d(VEApplication.TAG, "VEHttpCaller.doGet'request'url=" + url + "");
            Log.d(VEApplication.TAG, "VEHttpCaller.doGet'request'body=" + body + "");
            respContent = HttpCaller.doPost(url, body);
            Log.d(VEApplication.TAG, "VEHttpCaller.doGet'response'content=" + respContent + "");
        } catch (UnsupportedEncodingException e) {
            Log.d(VEApplication.TAG, "VEHttpCaller.doGet'response'errMsg=" + e.getMessage() + "");
            e.printStackTrace();
        }
        return VEResponse.buildFromJSONString(respContent);
    }
}
