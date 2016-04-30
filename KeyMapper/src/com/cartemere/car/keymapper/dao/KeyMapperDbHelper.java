package com.cartemere.car.keymapper.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Low level DB management
 * @author cartemere
 */
public class KeyMapperDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 28042016;
    public static final String DATABASE_NAME = "KeyMapper.db";
    
    public KeyMapperDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(AppAssociationDAO.SQL_INIT_CREATE_TABLE);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	db.execSQL(AppAssociationDAO.SQL_DELETE_TABLE);
        onCreate(db);
    }
    
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}