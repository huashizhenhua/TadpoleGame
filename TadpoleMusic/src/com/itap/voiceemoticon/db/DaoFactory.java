package com.itap.voiceemoticon.db;

import android.content.Context;

public class DaoFactory {

    private Context mContext;
    private static DaoFactory sFactory;

    public static synchronized DaoFactory getInstance(Context context) {
        if (sFactory == null) {
            sFactory = new DaoFactory(context);
        }
        return sFactory;
    }


    private DaoFactory(Context context) {
        mContext = context.getApplicationContext();
    }

    public VoiceDao getVoiceDao() {
        VoiceDao voiceDao = new VoiceDao();
        voiceDao.feedAndCreate(mContext);
        return voiceDao;
    }
}
