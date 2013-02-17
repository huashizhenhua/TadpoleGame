package com.itap.voiceemoticon.db;


/**
 * Create Sql and Update Sql for a table in the database
 * 
 * @author liangyc@ucweb.com
 *         <br> Create: 2012-06-19
 */
public class CreateUpdateSql {

    private String _updateSql;

    private String _createSql;

    /**
     * above this version, will exec the updateSql
     */
    private int _updateVer;

    /**
     * above this version, will exec the createSql,default is 0.
     */
    private int _createVer = 0;

    /**
     * a boolean flag, indicate whether it should call createSql after exec the updateSql
     * <br>
     * default is false
     */
    private boolean _createAfterUpdateFlag = false;

    /**
     * table name
     */
    private String _tableName = "";

    /**
     * columnName needed to be added
     */
    private String _updateColumnName = "";

    /**
     * Check whether the table exists before implementing the create sql
     */
    private boolean _needCheckTableExistedFlag = false;

    public CreateUpdateSql(String _createSql, String _updateSql, int _createVer, int _updateVer) {
        this._createSql = _createSql;
        this._updateSql = _updateSql;
        this._createVer = _createVer;
        this._updateVer = _updateVer;
    }

    public CreateUpdateSql(String _createSql, String _updateSql, int _createVer, int _updateVer, boolean flag) {
        this(_createSql, _updateSql, _createVer, _updateVer);
        _createAfterUpdateFlag = flag;
    }

    public CreateUpdateSql(String _createSql, String _updateSql, int _createVer, int _updateVer, boolean _createAfterUpdate, boolean _needCheckTableExisted, String _tableName) {
        this(_createSql, _updateSql, _createVer, _updateVer);
        _createAfterUpdateFlag = _createAfterUpdate;
        _needCheckTableExistedFlag = _needCheckTableExisted;
        this._tableName = _tableName;
    }

    public CreateUpdateSql(String _createSql, String _updateSql, int _createVer, int _updateVer, boolean _createAfterUpdate, String _tableName, String _columnName) {
        this(_createSql, _updateSql, _createVer, _updateVer, _createAfterUpdate);
        this._tableName = _tableName;
        this._updateColumnName = _columnName;
    }

    public String getUpdateSql() {
        return _updateSql;
    }

    public String getCreateSql() {
        return _createSql;
    }

    public int getUpdateVer() {
        return _updateVer;
    }

    public int getCreateVer() {
        return _createVer;
    }

    public boolean isCreateAfterUpdateFlag() {
        return _createAfterUpdateFlag;
    }

    public boolean isNeedCheckTableExistedFlag() {
        return _needCheckTableExistedFlag;
    }

    public String getTableName() {
        return _tableName;
    }

    public String getUpdateColumnName() {
        return _updateColumnName;
    }

}
