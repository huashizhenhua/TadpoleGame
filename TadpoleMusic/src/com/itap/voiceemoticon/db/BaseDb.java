package com.itap.voiceemoticon.db;

import java.io.File;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

/**
 * 基础db类根据是否有Sd卡选择数据库的存储位置。 通过集成基础Db类，我们只需要在子类构造函数传入相应的配置参数即可。
 */

public class BaseDb {
    public static final String CLASS_NAME = "BaseDb";

    /**
     * 使用在存储位置再内部存储的db
     */
    public final static int USEDB_INTERNAL = 0;
    /**
     * 如果有sd卡则使用sd卡，否则使用内部存储
     */
    public final static int USEDB_SDCARD_PRIORITY = 1;

    // base db 私有成员变量
    private String _dbPath;
    // private SQLiteDatabase _db;
    private boolean _usingSdCard;

    // 内部存储的路径
    private String _dbInternalPath;

    // 子类所传入的配置参数
    protected String tag = "BaseDb";
    protected ArrayList<CreateUpdateSql> tableSqls;
    protected int databaseVersion;
    protected String databaseName;
    protected String sdcardDbPostiveDir;
    protected Context context;
    protected boolean sdcardPrefer = true; // 是否优先使用sd卡

    // 锁
    private byte[] _lock;

    protected BaseDb(String tag, ArrayList<CreateUpdateSql> sqls, int dbVersion, String dbName, String positiveDir, byte[] lock) {
        _lock = lock;
        synchronized (_lock) {
            this.tag = tag;
            this.databaseVersion = dbVersion;
            this.databaseName = dbName;
            this.sdcardDbPostiveDir = positiveDir;
            this.tableSqls = sqls;

        }
    }

    protected BaseDb(String tag, ArrayList<CreateUpdateSql> sqls, int dbVersion, String dbName, String positiveDir, byte[] lock, boolean sdcardPrefer) {
        _lock = lock;
        synchronized (_lock) {
            this.tag = tag;
            this.databaseVersion = dbVersion;
            this.databaseName = dbName;
            this.sdcardDbPostiveDir = positiveDir;
            this.tableSqls = sqls;
            this.sdcardPrefer = sdcardPrefer;

        }
    }

    public void feedAndCreate(Context context) {
        this.context = context;
        checkSDCfg();
    }

    private void onCreateDB(SQLiteDatabase db) {
        //         System.out.println("sqls seq length:" + tableSqls.size());
        for (int i = 0; i < tableSqls.size(); i++) {
            boolean tableExisted = false;//表明该表是否已存在
            if (tableSqls.get(i).isNeedCheckTableExistedFlag()) {
                String table = tableSqls.get(i).getTableName();
                if (table != null && table.length() > 0) {
                    if (isTableExisted(table, db)) {
                        tableExisted = true;//如果已存在,则不会创建这个表.这样做是为了对以往记录的兼容.
                        continue;
                    }
                }
            }
            if (tableSqls.get(i).getCreateVer() >= db.getVersion() && !tableExisted) {
                Log.d(tag, "onCreateDB:" + tableSqls.get(i).getCreateSql());
                db.execSQL(tableSqls.get(i).getCreateSql());
            }
        }
    }

    private void onUpdateDB(SQLiteDatabase db) {
        for (int i = 0; i < tableSqls.size(); i++) {
            Log.d(tag, "onUpdateDB:" + tableSqls.get(i).getUpdateSql());
            Log.d(tag, "current version:" + db.getVersion() + ", update version:" + tableSqls.get(i).getUpdateVer());
            if (tableSqls.get(i).getUpdateVer() == db.getVersion()) {
                //检查该表是否存在要新添加列,再进行处理,为了兼容以前旧版本对数据库版本号设置的bug,跟创建表类似
                if (!isColumnExisted(tableSqls.get(i).getTableName(), tableSqls.get(i).getUpdateColumnName(), db)) {
                    db.execSQL(tableSqls.get(i).getUpdateSql());
                    if (tableSqls.get(i).isCreateAfterUpdateFlag()) {
                        Log.d(tag, "proceed UpdateSql:" + tableSqls.get(i).getCreateSql());
                        db.execSQL(tableSqls.get(i).getCreateSql());
                    }
                }
            }
        }
    }

    /**
     * Init Db operation
     */
    private void InitDb(SQLiteDatabase db) {
        onCreateDB(db);
        onUpdateDB(db);
        if (db.getVersion() < databaseVersion) {//加一个判断，如果版本不一样的时候才设置数据库version，这个判断可以每次在xt502减少200毫秒
            db.setVersion(databaseVersion);//这个判断原来是不等于,或者不判断而更改数据库version字段,导致升级后的兼容性问题,例如先装了version是2的数据库,再安装旧版本version为1的数据库,下次再启动version为2的数据库就以为需要升级此数据库,导致重复创建表的问题
        }
    }

    /**
     * 检查SD卡配置
     */
    private void checkSDCfg() {
        this._usingSdCard = false;
        String rootDir = context.getFilesDir().getParent() + "/ucgamesdk/db/";
        File rootFile = new File(rootDir);
        if (!rootFile.exists())
            rootFile.mkdirs();
        this._dbPath = rootDir + this.databaseName;
        this._dbInternalPath = this._dbPath;

        File sdcard = Environment.getExternalStorageDirectory();
        String dbSdCardAbsolutePath = sdcard.getAbsolutePath() + this.sdcardDbPostiveDir;

        // sdcardPrefer true＝检查sd卡 false＝不使用sd卡
        if (this.sdcardPrefer && sdcard != null && sdcard.canWrite()) {
            File sdDirFile = new File(dbSdCardAbsolutePath);
            if (!sdDirFile.exists()) {
                sdDirFile.mkdirs();
            }
            this._usingSdCard = true;
            this._dbPath = dbSdCardAbsolutePath + File.separator + this.databaseName;
        }

        // 创建或更新内部存储使用的db
        if (_usingSdCard) {
            SQLiteDatabase internalDb = DbCacheMgr.getCacheWriteDb(_dbInternalPath);
            if (internalDb != null) {
                try {
                    InitDb(internalDb);
                } catch (Exception e) {
                    Log.e(CLASS_NAME, "checkSDCfg" + "数据库创建或更新失败,辅助db", e);
                }
            }
        }
        SQLiteDatabase mainDb = DbCacheMgr.getCacheWriteDb(_dbPath);
        if (mainDb != null) {
            try {
                InitDb(mainDb);
            } catch (Exception e) {
                Log.e(CLASS_NAME, "checkSDCfg" + "数据库创建或更新失败，主db", e);
            }
        }
    }

    protected SQLiteDatabase getWriteDB() {
        return DbCacheMgr.getCacheWriteDb(_dbPath);
    }

    /**
     * 返回内部存储写数据库的sqlite实例
     * 
     * @return
     */
    protected synchronized SQLiteDatabase getInternalWriteDB() {
        return DbCacheMgr.getCacheWriteDb(_dbInternalPath);
    }

    protected synchronized SQLiteDatabase getReadDB() {
        return DbCacheMgr.getCacheReadDb(_dbPath);
    }

    protected SQLiteDatabase getInternalReadDB() {
        return DbCacheMgr.getCacheReadDb(_dbInternalPath);
    }

    /**
     * Basic Insert Methods
     * 
     * @param values
     * @param tableName
     * @return
     */
    protected boolean insert(ContentValues values, String tableName) {
        return insert(values, tableName, USEDB_SDCARD_PRIORITY);
    }

    /**
     * Basic Insert Methods
     * 
     * @param values
     * @param tableName
     * @param dbType
     * @return
     */
    protected boolean insert(ContentValues values, String tableName, int dbType) {
        boolean flag = false;// insert result boolean value
        synchronized (_lock) {
            // if the values doesn't contain username,return null
            SQLiteDatabase db = null;
            try {
                if (dbType == USEDB_INTERNAL)
                    db = getInternalWriteDB();
                else
                    db = getWriteDB();
                long rowId = db.insert(tableName, null, values);
                flag = true;
                Log.d(CLASS_NAME, "insert" + "insert tables " + tableName + "@" + rowId + ",new records , using getWriteDb methods");
            } catch (Exception e) {
                Log.e(CLASS_NAME, "Insert" + "插入操作异常@" + tag);
                e.printStackTrace();
            }
            return flag;
        }

    }

    /**
     * Basic Update Method
     * 
     * @param values
     * @param whereKey
     * @param whereValue
     * @return
     */
    protected int update(ContentValues values, String where, String tableName, int dbType) {
        SQLiteDatabase db = null;
        int count = 0;
        synchronized (_lock) {
            try {
                if (dbType == USEDB_INTERNAL)
                    db = getInternalWriteDB();
                else
                    db = getWriteDB();
                if (where.length() > 0) {
                    // means that it's to update desired id
                    count = db.update(tableName, values, where, null);
                } else {
                    // means that it's to update all the records
                    count = db.update(tableName, values, null, null);
                }
                Log.d(CLASS_NAME, "update" + "update table " + tableName + ",records in codition:" + where + ",records count:" + count);
            } catch (Exception e) {
                Log.e(CLASS_NAME, "checkSDCfg" + "更新操作异常@" + tag);
                e.printStackTrace();
            }
            return count;
        }
    }

    protected int update(ContentValues values, String where, String tableName) {
        return update(values, where, tableName, USEDB_SDCARD_PRIORITY);
    }

    /**
     * Delte record with the given id
     * 
     * @param id
     * @return
     */
    protected int delete(String where, String tableName, int dbType) {
        SQLiteDatabase db = null;
        int count = 0;
        synchronized (_lock) {
            try {
                if (dbType == USEDB_INTERNAL)
                    db = getInternalWriteDB();
                else
                    db = getWriteDB();
                if (where != null && where.length() > 0) {
                    count = db.delete(tableName, where, null);
                    Log.d(CLASS_NAME, "delete" + "delete records @ condition:" + where + ",records count:" + count, null);
                } else {
                    count = db.delete(tableName, null, null);
                    Log.d(CLASS_NAME, "delete" + "delete all records @ " + tableName + " ,records count:" + count);
                }
                return count;
            } catch (Exception e) {
                Log.e(CLASS_NAME, "delete " + "删除操作异常@" + tag);
                e.printStackTrace();
            }
            return count;
        }

    }

    protected int delete(String where, String tableName) {
        return delete(where, tableName, USEDB_SDCARD_PRIORITY);
    }

    public void closeCursor(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
    }

    public boolean is_UsingSdCard() {
        return _usingSdCard;
    }

    /**
     * 按照sqllite关键词转义规则进行转义,避免出现sql语法错误
     * 
     * / -> //
     * ' -> ''
     * [ -> /[
     * ] -> /]
     * % -> /%
     * & -> /&
     * _ -> /_
     * ( -> /(
     * ) -> /)
     * 
     * @param value
     * @return
     */
    public static String rectifySqliteEscapeChar(String value) {
        String ret = value;
        ret = ret.replace("/", "//");
        ret = ret.replace("'", "''");
        ret = ret.replace("[", "/[");
        ret = ret.replace("]", "/]");
        ret = ret.replace("%", "/%");
        ret = ret.replace("&", "/&");
        ret = ret.replace("_", "/_");
        ret = ret.replace("(", "/(");
        ret = ret.replace(")", "/)");
        return ret;
    }

    /**
     * 检查该table是否已经存在
     * 
     * @param tableName
     * @return
     */
    private boolean isTableExisted(String tableName, SQLiteDatabase db) {
        String sql = "select count(*) as c from Sqlite_master  where type ='table' and name = ? ";
        Cursor resultCursor = null;
        boolean existed = false;
        try {
            resultCursor = db.rawQuery(sql, new String[] { String.valueOf(tableName) });
            while (resultCursor.moveToNext()) {
                int tableCount = resultCursor.getInt(resultCursor.getColumnIndex("c"));
                if (tableCount == 1)
                    existed = true;
            }
        } catch (Exception e) {
        } finally {
            closeCursor(resultCursor);
        }
        return existed;
    }

    /**
     * 
     * @param tableName
     * @param columnName
     * @param db
     * @return
     */
    private boolean isColumnExisted(String tableName, String columnName, SQLiteDatabase db) {
        String sql = "select sql from Sqlite_master  where type ='table' and name = ? ";
        Cursor resultCursor = null;
        boolean existed = false;
        if (tableName != null && tableName.length() > 0 && columnName != null && columnName.length() > 0) {
            try {
                resultCursor = db.rawQuery(sql, new String[] { String.valueOf(tableName) });
                while (resultCursor.moveToNext()) {
                    String tableCreateSql = resultCursor.getString(resultCursor.getColumnIndex("sql"));//获取建表语句
                    //然后通过建表语句检查是否有对应的字段确认该表是否存在.

                    if (tableCreateSql.indexOf(columnName) > 0)
                        existed = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeCursor(resultCursor);
            }
        }
        return existed;
    }
}
