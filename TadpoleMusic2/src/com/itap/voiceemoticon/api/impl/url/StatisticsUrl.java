package com.itap.voiceemoticon.api.impl.url;

import android.os.Bundle;

import com.itap.voiceemoticon.api.util.MD5Util;
import com.itap.voiceemoticon.common.ConstValues;



public class StatisticsUrl extends RequestUrl {
    Bundle mdata;

    public Bundle generateUrlAndBody(Bundle data) {
        mdata = data;
        String serverUrl = getServerUrl();
        String url = serverUrl + "hot_stat";
        Bundle result = new Bundle();
        result.putString("url", url);
        result.putString("body", generateBody());
        return result;
    }

    public String generateBody() {
        String[] hotUrls = mdata.getStringArray(ConstValues.HOTSTAT);
        String body = "{\"hot_stat\":[" + generateHostList(hotUrls) + "]}";
        return body;
    }

    private String generateHostList(String[] hotUrls) {
        String result = "";
        for (int i = 0; i < hotUrls.length; i++) {
            String url = hotUrls[i];
            String md5 = MD5Util.getMD5(url.getBytes());
            String item = "\"" + url + "_" + md5 + "\"";
            if (i != hotUrls.length - 1)
                item += ",";

            result += item;
        }
        return result;
    }


}