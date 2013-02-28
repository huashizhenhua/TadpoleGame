package com.itap.voiceemoticon;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Notification;
import android.content.Context;

import com.itap.voiceemoticon.api.Voice;
import com.itap.voiceemoticon.api.VoiceEmoticonApi;
import com.itap.voiceemoticon.api.impl.VoiceEmoticonApiImpl;
import com.itap.voiceemoticon.db.DaoFactory;
import com.itap.voiceemoticon.db.VoiceDao;
import com.itap.voiceemoticon.media.MusicPlayer;

public class VEApplication {
    public static final String TAG = "VEApplication";

    public static VoiceEmoticonApi getVoiceEmoticonApi() {
        return new VoiceEmoticonApiImpl();
    }

    private static MusicPlayer mMusicPlayer;

    public static synchronized MusicPlayer getMusicPlayer(Context context) {
        if (mMusicPlayer == null) {
            mMusicPlayer = new MusicPlayer(context.getApplicationContext());
        }
        return mMusicPlayer;
    }

    public static void runOnThread(Runnable runnable) {
        new Thread(runnable).start();

    }


    //-------------------------------------------------------------
    //Music Collected Cache
    //-------------------------------------------------------------

    private static ArrayList<String> sMusicCollectedCached = null;

    /**
     * 是否已经被收藏
     * instruction。
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
