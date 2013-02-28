package com.itap.voiceemoticon.db;

import java.io.File;
import java.util.ArrayList;

/**
 * ucgame_private 私有db
 * 
 * 有sd卡时路径：sd卡/ucgamesdk/db/ucgame_private.db<br>
 * 无sd卡时路径：私有文件夹/db/ucgame_private.db<br>
 * 
 * @author chenzh@ucweb.com <br>
 *         Create: 2012-06-12
 */
public class VoiceDb extends BaseDb {
    private static final String CLASS_NAME = "VoiceDb";
    private static final boolean SDCARD_PREFER = false; //强制不使用sd卡

    /**
     * 锁对象，避免多线操作数据库的时候出现锁的问题
     */
    public static byte[] _locks = new byte[0];

    // ucgame_private 数据库配置
    private static final String SDCARD_DB_POSITIVE_DIR = "/ucgamesdk/db/";
    private static final String DATABASE_NAME = "ucgame_private.db";
    private static final int DATABASE_VERSION = 2;
    private static final ArrayList<CreateUpdateSql> sqls = new ArrayList<CreateUpdateSql>();
    static {
        sqls.add(new CreateUpdateSql(SqlSets.Voice.CREATE_SQL, SqlSets.Voice.UPDATE_SQL, SqlSets.Voice.CREATE_SQL_VERSION, SqlSets.Voice.UPDATE_SQL_VERSION));
    }

    public VoiceDb() {
        super(CLASS_NAME, sqls, DATABASE_VERSION, DATABASE_NAME, SDCARD_DB_POSITIVE_DIR + File.separator, _locks, SDCARD_PREFER);
    }
}