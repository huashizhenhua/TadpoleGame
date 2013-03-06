package com.tadpolemusic;

import java.util.ArrayList;

import android.content.Context;

import com.itap.voiceemoticon.db.DaoFactory;
import com.itap.voiceemoticon.db.VoiceDao;
import com.tadpolemusic.api.Voice;
import com.tadpolemusic.api.VoiceEmoticonApi;
import com.tadpolemusic.api.impl.VoiceEmoticonApiImpl;
import com.tadpolemusic.media.service.MusicPlayerProxy;

public class VEApplication {
    public static final String APPLICATION_NAME = "TadpoleMusic2";

    public static final String TAG = "VEApplication";

    private static MusicPlayerProxy mMusicPlayerProxy;

    public static VoiceEmoticonApi getVoiceEmoticonApi() {
        return new VoiceEmoticonApiImpl();
    }

    public static synchronized MusicPlayerProxy getMusicPlayer(Context context) {
        if (mMusicPlayerProxy == null) {
            mMusicPlayerProxy = new MusicPlayerProxy(context.getApplicationContext());
        }
        return mMusicPlayerProxy;
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
            sMusicCollectedCached.add(voice.musicPath);
        }
    }

}
