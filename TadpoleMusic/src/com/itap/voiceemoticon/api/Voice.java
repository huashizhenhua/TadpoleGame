
package com.itap.voiceemoticon.api;

import com.itap.voiceemoticon.VEApplication;
import com.itap.voiceemoticon.db.DaoFactory;
import com.itap.voiceemoticon.third.WeixinHelper;
import com.itap.voiceemoticon.util.StringUtil;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.webkit.URLUtil;

import java.net.URLEncoder;
import java.util.ArrayList;

public class Voice {
    public long id;

    public String title;

    public String url;

    public String tags;

    public int creatTime;

    public String getFirstLetter() {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        String str = PinyinHelper.toHanyuPinyinString(title, format, "");
        if (str != null && str.length() > 0) {
            if (Character.isLetter(str.toCharArray()[0])) {
                return (String)str.subSequence(0, 1);
            }
        }
        return "?";
    }

    public static Voice buildFromJSON(JSONObject jsonObject) {
        Voice hotVoice = new Voice();
        hotVoice.title = jsonObject.optString("title", "");
        hotVoice.url = jsonObject.optString("url", "");
        hotVoice.tags = jsonObject.optString("metas", "");
        return hotVoice;
    }

    public static PageList<Voice> buildPageListFromJSON(JSONObject jsonObject) {
        PageList<Voice> pageList = new PageList<Voice>();
        pageList.totalCount = jsonObject.optInt("total_results");

        ArrayList<Voice> hostVoiceList = new ArrayList<Voice>();
        JSONArray jsonArr = jsonObject.optJSONArray("voice");
        for (int i = 0, len = jsonArr.length(); i < len; i++) {
            JSONObject jsonObj;
            try {
                jsonObj = jsonArr.getJSONObject(i);
                hostVoiceList.add(Voice.buildFromJSON(jsonObj));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        pageList.records = hostVoiceList;
        return pageList;
    }

    public void sendToWeixin(final Context context) {
        new WeixinHelper(context).sendMusic(title, tags, url);
        VEApplication.runOnThread(new Runnable() {

            @Override
            public void run() {
                sendStatisticsUrl(context);
            }
        });
    }

    public void sendStatisticsUrl(Context context) {
        ArrayList<String> list = new ArrayList<String>();
        list.add(url);
        VEApplication.getVoiceEmoticonApi().statistics(list);
    }

    /**
     * add to my collection
     * 
     * @param context
     */
    public void saveToCollect(Context context) {
        try {
            DaoFactory.getInstance(context).getVoiceDao().saveOrUpdate(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * remove from collect
     * 
     * @param context
     */
    public void delete(Context context) {
        try {
            DaoFactory.getInstance(context).getVoiceDao().delete(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendToQQ(Context context, boolean isHideTitle) {

        
        String targetUrl = "http://voiceemoticon.sinaapp.com/static/play.htm?";
        targetUrl += "title=" + URLEncoder.encode(title);
        targetUrl += "&tags=" + URLEncoder.encode(tags);
        targetUrl += "&voiceUrl=" + URLEncoder.encode(url);
        
        Bundle bundle = new Bundle();
        bundle.putString("title", isHideTitle ? "这是条语音表情" : title);
        bundle.putString("targetUrl", targetUrl);
        bundle.putString("summary", isHideTitle ? "" : tags);
        // bundle.putString("site", siteUrl.getText() + "");
        bundle.putString("appName", "语音表情-微信QQ聊天必备");

        System.out.println(Tencent.createInstance("100497165", context));
        
        Tencent.createInstance("100497165", context).shareToQQ((Activity)context, bundle,
                new IUiListener() {
                    
                    @Override
                    public void onError(UiError e) {
                        System.out.println("shareToQQ:" + "onError code:" + e.errorCode + ", msg:"
                                + e.errorMessage + ", detail:" + e.errorDetail);
                    }
                    
                    @Override
                    public void onComplete(JSONObject arg0) {
                        System.out.println("shareToQQ:" + "onComplete");
                    }
                    
                    @Override
                    public void onCancel() {
                        System.out.println("shareToQQ" + "onCancel");
                        
                    }
                });
    }

    public void sendToFriends(Context context) {
    }
}
