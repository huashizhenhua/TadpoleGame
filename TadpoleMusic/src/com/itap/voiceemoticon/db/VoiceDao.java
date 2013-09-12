package com.itap.voiceemoticon.db;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.itap.voiceemoticon.activity.NotificationCenter;
import com.itap.voiceemoticon.activity.NotificationID;
import com.itap.voiceemoticon.api.Voice;
import com.itap.voiceemoticon.util.StringUtil;

public class VoiceDao extends VoiceDb {
    public static final String CLASS_NAME = "VoiceDao";
    public static final String TABLE_NAME = "voice";

    public static String[] ALL_PROJECTION = { VoiceColumns._ID, VoiceColumns.VOICE_TITLE, VoiceColumns.VOICE_PATH, VoiceColumns.VOICE_TAGS, VoiceColumns.VOICE_CREATE_TIME };

    public static final class VoiceColumns implements BaseColumns {
        public static final String VOICE_TITLE = "title";
        public static final String VOICE_PATH = "path";
        public static final String VOICE_TAGS = "tags";
        public static final String VOICE_CREATE_TIME = "create_time";
    }

    /***
     * rex_node_id + path是唯一主键
     */
    public static final String UNIQUE_KEY_SELECTION = VoiceColumns.VOICE_PATH + " = ? ";

    /***
     * 保存或者更新 根据唯一键
     * 
     * @param voice
     * @return
     */
    public Long saveOrUpdate(Voice voice) {
        // rex_proj_id + rex_file_name 构成唯一key
        SQLiteDatabase db = this.getWriteDB();

        long voiceId = voice.id;
        String[] uniqueKeySelection = new String[] { String.valueOf(voice.url) };
        Cursor cursor = db.query(TABLE_NAME, new String[] { VoiceColumns.VOICE_PATH }, UNIQUE_KEY_SELECTION, uniqueKeySelection, null, null, null);
        ContentValues values = new ContentValues(5);
        values.put(VoiceColumns.VOICE_TITLE, voice.title);
        values.put(VoiceColumns.VOICE_PATH, voice.url);
        values.put(VoiceColumns.VOICE_TAGS, voice.tags);
        values.put(VoiceColumns.VOICE_CREATE_TIME, voice.creatTime);

        if (cursor.getCount() == 0) {
            voiceId = db.insert(TABLE_NAME, null, values);
        } else {
            db.update(TABLE_NAME, values, UNIQUE_KEY_SELECTION, uniqueKeySelection);
            cursor.moveToNext();
            voiceId = cursor.getLong(cursor.getColumnIndex(VoiceColumns._ID));
        }
        voice.id = voiceId;
        this.closeCursor(cursor);
        return voiceId;
    }

    public long insertOrUpdate(SQLiteDatabase db, Voice voice) {
        long voiceId = voice.id;
        String[] uniqueKeySelection = new String[] { String.valueOf(voice.url) };
        Cursor cursor = db.query(TABLE_NAME, new String[] { VoiceColumns._ID }, UNIQUE_KEY_SELECTION, uniqueKeySelection, null, null, null);
        ContentValues values = new ContentValues(5);
        values.put(VoiceColumns.VOICE_PATH, voiceId);
        values.put(VoiceColumns.VOICE_TITLE, voice.title);
        values.put(VoiceColumns.VOICE_PATH, voice.url);
        values.put(VoiceColumns.VOICE_TAGS, voice.tags);
        values.put(VoiceColumns.VOICE_CREATE_TIME, voice.creatTime);
        if (cursor.getCount() == 0) {
            voiceId = db.insert(TABLE_NAME, null, values);
        } else {
            db.update(TABLE_NAME, values, UNIQUE_KEY_SELECTION, uniqueKeySelection);
            cursor.moveToNext();
            voiceId = cursor.getLong(cursor.getColumnIndex(VoiceColumns._ID));
        }
        this.closeCursor(cursor);
        return voiceId;
    }

    /***
     * 保存或者更新 根据唯一键
     * 
     * @param voice
     * @return
     */
    public void insertOrUpdate(ArrayList<Voice> voiceList) {
        SQLiteDatabase db = this.getWriteDB();
        db.beginTransaction();
        Iterator<Voice> voiceIterator = voiceList.iterator();
        while (voiceIterator.hasNext()) {
            Voice voice = voiceIterator.next();
            this.insertOrUpdate(db, voice);
        }
        db.setTransactionSuccessful();
        db.endTransaction();

    }

    /**
     * 
     * @param rexProj
     * @param path
     * @return
     */
    public Voice findVoice(long voiceId, String path) {
        SQLiteDatabase db = this.getWriteDB();
        String[] uniqueKeySelection = new String[] { String.valueOf(voiceId), path };
        Cursor cursor = db.query(TABLE_NAME, ALL_PROJECTION, UNIQUE_KEY_SELECTION, uniqueKeySelection, null, null, null);
        Voice voice = null;
        if (cursor.moveToNext()) {
            voice = buildVoiceFromCursor(cursor);
        }
        this.closeCursor(cursor);

        return voice;
    }

    /**
     * 获取所有资源项目
     * 
     * @param beUsingSDCard
     * @return
     */
    public ArrayList<Voice> allVoices() {
        SQLiteDatabase db = this.getReadDB();
        ArrayList<Voice> retList = new ArrayList<Voice>();
        Cursor cursor = db.query(TABLE_NAME, ALL_PROJECTION, null, null, null, null, null);
        while (cursor.moveToNext()) {
            retList.add(buildVoiceFromCursor(cursor));
        }
        this.closeCursor(cursor);

        return retList;
    }

    /***
     * @param rexProj
     * @return
     */
    public ArrayList<Voice> findRexNodeFiles(Voice voice) {
        SQLiteDatabase db = this.getReadDB();
        long voiceId = voice.id;
        String selection = VoiceColumns.VOICE_PATH + " = ?";
        String[] selectionArgs = new String[] { String.valueOf(voiceId) };
        Cursor cursor = db.query(TABLE_NAME, ALL_PROJECTION, selection, selectionArgs, null, null, null);
        ArrayList<Voice> retList = new ArrayList<Voice>();
        while (cursor.moveToNext()) {
            Voice Voice = buildVoiceFromCursor(cursor);
            retList.add(Voice);
        }
        this.closeCursor(cursor);
        return retList;
    }

    /***
     * return rexfile list match condition like "is_valid=true"
     */
    public ArrayList<Voice> findRexNodeValidFiles(Voice voice) {
        SQLiteDatabase db = this.getReadDB();
        long voiceId = voice.id;
        String clause = VoiceColumns.VOICE_PATH + " = ? and " + VoiceColumns.VOICE_TAGS + " = 1 ";
        String[] selection = new String[] { String.valueOf(voiceId) };
        Cursor cursor = db.query(TABLE_NAME, ALL_PROJECTION, clause, selection, null, null, null);
        ArrayList<Voice> retList = new ArrayList<Voice>();
        while (cursor.moveToNext()) {
            Voice Voice = buildVoiceFromCursor(cursor);
            retList.add(Voice);
        }

        this.closeCursor(cursor);

        return retList;
    }

    /**
     * @param Voice
     * @return
     */
    public int delete(Voice voice) {
        SQLiteDatabase db = this.getWriteDB();
        String voicePath = voice.url;
        String[] uniqueKeySelection = new String[] { voicePath };
        int count = db.delete(TABLE_NAME, UNIQUE_KEY_SELECTION, uniqueKeySelection);
        
        NotificationCenter.obtain(NotificationID.N_MY_COLLECT_CHANGE, null).notifyToTarget();
        
        return count;
    }

    /**
     * 读取游标数据创建Voice
     * 
     * @param cursor
     * @return
     */
    private Voice buildVoiceFromCursor(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(VoiceColumns._ID));
        String title = cursor.getString(cursor.getColumnIndex(VoiceColumns.VOICE_TITLE));
        String path = cursor.getString(cursor.getColumnIndex(VoiceColumns.VOICE_PATH));
        String tags = cursor.getString(cursor.getColumnIndex(VoiceColumns.VOICE_TAGS));
        long creatTime = cursor.getLong(cursor.getColumnIndex(VoiceColumns.VOICE_CREATE_TIME));
        Voice voice = new Voice();
        voice.id = id;
        voice.creatTime = (int) creatTime;
        voice.title = title;
        voice.url = path;
        voice.tags = tags;
        return voice;
    }

    /**
     * 删除全部记录
     * 
     * @return 返回删除的行数
     */
    public int deleleAll() {
        SQLiteDatabase db = this.getWriteDB();
        int linesCount = db.delete(TABLE_NAME, null, null);

        return linesCount;
    }

    /**
     * 根据rexProjId删除资源文件
     * 
     * @param rexProjId
     * @return
     */
    public int deleteVoices(long voiceId) {
        SQLiteDatabase db = this.getWriteDB();
        String selection = VoiceColumns.VOICE_PATH + "= ?";
        String[] selectionArgs = new String[] { String.valueOf(voiceId) };
        int linesCount = db.delete(TABLE_NAME, selection, selectionArgs);

        return linesCount;
    }

    /**
     * 删除多个资源文件，当资源节点id在voiceIds数组里。
     * 
     * @param voiceIds
     * @return
     */
    public int deleteVoices(ArrayList<Long> voiceIds) {
        SQLiteDatabase db = this.getWriteDB();
        String selection = VoiceColumns.VOICE_PATH + " in (" + StringUtil.join(voiceIds, ",") + ")";
        int linesCount = db.delete(TABLE_NAME, selection, null);

        return linesCount;
    }

}
