package com.itap.voiceemoticon.db;

import java.util.HashMap;
import java.util.Iterator;

import android.database.sqlite.SQLiteDatabase;

import com.itap.voiceemoticon.util.FileUtil;

/**
 * 缓存数据库链接，避免多线程操作导致崩溃的问题
 * 
 * 一个应用，应确保一个数据库只有一个写链接。 这样可以避免多线程中的产生的各种问题。<br>
 * 目前 碰到的问题列表如下：<br>
 * 
 * 1 问题关键字：database is locked. 问题产生原因：多个数据库链接在多线程的环境同时写数据库。
 * 
 * 
 * @author chenzh
 * 
 */
public class DbCacheMgr {

    /**
     * 数据库读链接缓存
     */
    private static final HashMap<String, SQLiteDatabase> CACHE_READ_DB = new HashMap<String, SQLiteDatabase>(2);

    /**
     * 数据库读写链接缓存
     */
    private static final HashMap<String, SQLiteDatabase> CACHE_WRITE_DB = new HashMap<String, SQLiteDatabase>(2);

    /**
     * 获取缓存中的读链接
     * 
     * @param dbPath
     *            数据库路径，缓存的key
     * @return
     */
    public static synchronized SQLiteDatabase getCacheReadDb(String dbPath) {
        if (CACHE_READ_DB.containsKey(dbPath)) {
            return CACHE_READ_DB.get(dbPath);
        } else {
            SQLiteDatabase db = null;
            try {
                db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.CREATE_IF_NECESSARY);
                CACHE_READ_DB.put(dbPath, db);
            } catch (Exception e) {
                FileUtil.delete(dbPath);
                db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.CREATE_IF_NECESSARY);
                CACHE_READ_DB.put(dbPath, db);
            }
            return db;
        }
    }

    /**
     * 获取缓存中的读写数据库链接
     * 
     * @param dbPath
     *            数据库路径，缓存的key
     * @return
     */
    public static synchronized SQLiteDatabase getCacheWriteDb(String dbPath) {
        SQLiteDatabase db = null;
        if (CACHE_WRITE_DB.containsKey(dbPath)) {
            db = CACHE_WRITE_DB.get(dbPath);
        } else {
            try {
                db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.CREATE_IF_NECESSARY);
                CACHE_WRITE_DB.put(dbPath, db);
            } catch (Exception e) {
                // SQLiteDiskIOException
                FileUtil.delete(dbPath);
                db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.CREATE_IF_NECESSARY);
                CACHE_WRITE_DB.put(dbPath, db);
            }
        }
        return db;
    }

    /**
     * 关闭数据库
     * 
     * @param db
     */
    public static void closeDataBase(SQLiteDatabase db) {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    /**
     * 清空数据库链接缓存
     * 
     * @param db
     */
    public synchronized static void clearDbCache() {
        clearDbCache(CACHE_READ_DB);
        clearDbCache(CACHE_WRITE_DB);
    }

    private static void clearDbCache(HashMap<String, SQLiteDatabase> dbCache) {
        try {
            if (dbCache == null) {
                return;
            }
            Iterator<String> keyIter = dbCache.keySet().iterator();
            while (keyIter.hasNext()) {
                SQLiteDatabase db = dbCache.get(keyIter.next());
                if (db != null) {
                    db.close();
                }
            }
            dbCache.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
