package com.itap.voiceemoticon.db;

import com.itap.voiceemoticon.db.VoiceDao.VoiceColumns;

/**
 * 
 * usage。
 * 
 * <br>==========================
 * <br> author：Zenip
 * <br> email：lxyczh@gmail.com
 * <br> create：2013-1-25上午11:55:42
 * <br>==========================
 */
public class SqlSets {

    public class Voice {
        public static final String TABLE_NAME = "voice";
        public final static String CREATE_SQL = "CREATE TABLE " + TABLE_NAME + " (" + VoiceColumns._ID + " INTEGER PRIMARY KEY, " + VoiceColumns.VOICE_TITLE + " TEXT, " + VoiceColumns.VOICE_TAGS
                + " TEXT, " + VoiceColumns.VOICE_PATH + " TEXT, " + VoiceColumns.VOICE_CREATE_TIME + " INTEGER " + ");";
        public final static String UPDATE_SQL = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
        public static final int CREATE_SQL_VERSION = 0;
        public static final int UPDATE_SQL_VERSION = -1;
    }
}
