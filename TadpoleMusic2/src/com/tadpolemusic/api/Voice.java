package com.tadpolemusic.api;

import java.util.ArrayList;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.itap.voiceemoticon.db.DaoFactory;
import com.itap.voiceemoticon.third.WeixinHelper;
import com.itap.voiceemoticon.util.StringUtil;
import com.tadpolemusic.VEApplication;
import com.tadpolemusic.media.MusicData;

public class Voice extends MusicData {
    public long id;
    public String tags;
    public int creatTime;

    public String getFirstLetter() {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        String str = PinyinHelper.toHanyuPinyinString(musicName, format, "");
        if (str != null && str.length() > 0) {
            if (Character.isLetter(str.toCharArray()[0])) {
                return (String) str.subSequence(0, 1);
            }
        }
        return "?";
    }


    public static Voice buildFromJSON(JSONObject jsonObject) {
        Voice hotVoice = new Voice();
        hotVoice.musicName = jsonObject.optString("title", "");
        hotVoice.musicPath = jsonObject.optString("url", "");
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
        new WeixinHelper(context).sendMusic(musicName, tags, musicPath);
        VEApplication.runOnThread(new Runnable() {

            @Override
            public void run() {
                sendStatisticsUrl(context);
            }
        });
    }

    public void sendStatisticsUrl(Context context) {
        ArrayList<String> list = new ArrayList<String>();
        list.add(musicPath);
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
}
