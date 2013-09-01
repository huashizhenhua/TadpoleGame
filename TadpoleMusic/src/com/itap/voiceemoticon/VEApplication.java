
package com.itap.voiceemoticon;

import com.itap.voiceemoticon.api.Voice;
import com.itap.voiceemoticon.api.VoiceEmoticonApi;
import com.itap.voiceemoticon.api.impl.VoiceEmoticonApiImpl;
import com.itap.voiceemoticon.db.DaoFactory;
import com.itap.voiceemoticon.db.UserVoiceModel;
import com.itap.voiceemoticon.db.VoiceDao;
import com.itap.voiceemoticon.media.MusicPlayer;
import com.itap.voiceemoticon.third.WeiboHelper;
import com.sina.weibo.sdk.WeiboSDK;
import com.sina.weibo.sdk.api.IWeiboAPI;
import com.sina.weibo.sdk.api.IWeiboHandler;
import com.tencent.tauth.Tencent;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.ArrayList;

public class VEApplication extends Application {

    public static Context sContext = null;

    public static final String TAG = "VEApplication";

    public static VoiceEmoticonApi getVoiceEmoticonApi() {
        return new VoiceEmoticonApiImpl();
    }

    public static final String PREF_NAME = "voiceemoticon";

    public static final String PREF_KEY_HIDE_TITLE = "hidetitle";

    private static SharedPreferences sPrefs;

//    public static IWeiboAPI sWeiboApi = null;

    @Override
    public void onCreate() {
        super.onCreate();
        sPrefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        // 1 初始化SDK
//        sWeiboApi = WeiboSDK.createWeiboAPI(this, WeiboHelper.APP_KEY);
//        // 2 注册到新浪微博
//        sWeiboApi.registerApp();

        sContext = this;
    }
    

    public static boolean getHideTitle() {
        return sPrefs.getBoolean(PREF_KEY_HIDE_TITLE, false);
    }

    public static void setHideTitle(boolean flag) {
        Editor editor = sPrefs.edit();
        editor.putBoolean(PREF_KEY_HIDE_TITLE, flag);
        editor.commit();
    }

    private static MusicPlayer mMusicPlayer;

    private static Tencent sInstance;

    public static synchronized MusicPlayer getMusicPlayer(Context context) {
        if (mMusicPlayer == null) {
            mMusicPlayer = new MusicPlayer(context.getApplicationContext());
        }
        return mMusicPlayer;
    }

    public static void runOnThread(Runnable runnable) {
        new Thread(runnable).start();

    }

    // -------------------------------------------------------------
    // Music Collected Cache
    // -------------------------------------------------------------

    private static ArrayList<String> sMusicCollectedCached = null;

    /**
     * 是否已经被收藏 instruction。
     */
    public static boolean isCollected(Context context, String path) {
        if (sMusicCollectedCached == null) {
            reloadVoiceCollectedCache(context);
        }
        return sMusicCollectedCached.contains(path);
    }

    public static void reloadVoiceCollectedCache(Context context) {
        sMusicCollectedCached = new ArrayList<String>();
        VoiceDao dao = DaoFactory.getInstance(context).getVoiceDao();
        ArrayList<Voice> list = dao.allVoices();
        for (int i = 0, len = list.size(); i < len; i++) {
            Voice voice = list.get(i);
            sMusicCollectedCached.add(voice.url);
        }
    }

}
