package com.cartemere.car.keymapper.dao;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cartemere.car.keymapper.model.AppAssociation;

/**
 * Provide accessor to the Association persistence layer
 * @author cartemere
 */
public class AppAssociationDAO {
	
	private static String LOG_KEY = "AppAssociationDAO";
	public static String TABLE_NAME = "T_APP_ASSOCIATION";
	public static String COLUMN_NAME_KEY = "keyName";
	public static String COLUMN_EVENT = "event";
	public static String COLUMN_APP_NAME_KEY = "appName";
	public static String COLUMN_NAME_PACKAGE = "packageName";
	public static String COLUMN_NAME_ENABLED = "isEnabled";
	
	public static String SQL_INIT_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" 
	 + COLUMN_NAME_KEY + " CHAR(20) NOT NULL,"
	 + COLUMN_EVENT + " CHAR(50) NOT NULL,"
	 + COLUMN_APP_NAME_KEY + " CHAR(20), "
	 + COLUMN_NAME_PACKAGE + " CHAR(50), "
	 + COLUMN_NAME_ENABLED + " INT NOT NULL" + ")";
	
	public static String SQL_DELETE_TABLE = "DROP TABLE " + TABLE_NAME;
	
	public static String SQL_INIT_POPULATE_TABLE = "";
	
	private String[] projection = { 
			COLUMN_NAME_KEY,
			COLUMN_EVENT,
			COLUMN_APP_NAME_KEY,
			COLUMN_NAME_PACKAGE,
			COLUMN_NAME_ENABLED };
	
	public AppAssociation loadAssociationFromDBByEvent(Context context,
			String eventKey) {
		KeyMapperDbHelper dbHelper = new KeyMapperDbHelper(context);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		AppAssociation association = null;
		Log.i(LOG_KEY, "load association for event : " + eventKey);
		try {
			String whereClause = COLUMN_EVENT + "=?";

			// query DB
			Cursor cursor = db.query(TABLE_NAME, projection, whereClause,
					new String[] { eventKey }, null, null, null);

			// parse result
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				association = createAssociationFromCursor(cursor);
				

			}
			return association;
		} finally {
			db.close();
		}
	}

	
	public ArrayList<AppAssociation> loadAllAssociationsFromDB(Context context) {
		ArrayList<AppAssociation> result = new ArrayList< AppAssociation>();
		KeyMapperDbHelper dbHelper = new KeyMapperDbHelper(context);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		try {
			String sortOrder = COLUMN_NAME_KEY + " DESC";

			// query DB
			Cursor cursor = db.query(TABLE_NAME, projection, null,
					null, null, null, sortOrder);

			// parse result
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				AppAssociation association = createAssociationFromCursor(cursor);
				result.add(association);
				cursor.moveToNext();
			}
			return result;
		} finally {
			db.close();
		}
	}
	
	public void deleteAssociationInDB(Context context, AppAssociation association) {
		KeyMapperDbHelper dbHelper = new KeyMapperDbHelper(context);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		try {
			Log.i(LOG_KEY, "delete association : " + association);
			String selection = COLUMN_NAME_KEY + " = ?";
			String[] selectionArgs = { association.getKeyName() };
			db.delete(TABLE_NAME, selection, selectionArgs);
		} finally {
			db.close();
		}
	}
	
	public void createAssociationInDB(Context context, AppAssociation association) {
		KeyMapperDbHelper dbHelper = new KeyMapperDbHelper(context);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		try {
			Log.i(LOG_KEY, "insert association : " + association);
			ContentValues values = new ContentValues();
			values.put(COLUMN_NAME_KEY, association.getKeyName());
			values.put(COLUMN_EVENT, association.getEvent());
			values.put(COLUMN_APP_NAME_KEY, association.getAppName());
			values.put(COLUMN_NAME_PACKAGE, association.getAppPackageName());
			values.put(COLUMN_NAME_ENABLED, association.getIsKeyMappingEnabled().booleanValue() ? 1 : 0);
			
			db.insert(
			         TABLE_NAME,
			         null,
			         values);
		} finally {
			db.close();
		}
	}
	
	public AppAssociation updateAssociationInDB(Context context,AppAssociation association) {
		deleteAssociationInDB(context, association);
		createAssociationInDB(context, association);
		return association;
	}
	

	private AppAssociation createAssociationFromCursor(Cursor cursor) {
		AppAssociation association;
		String keyName = cursor.getString(cursor
				.getColumnIndexOrThrow(COLUMN_NAME_KEY));
		String event = cursor.getString(cursor
				.getColumnIndexOrThrow(COLUMN_EVENT));
		String appName = cursor.getString(cursor
				.getColumnIndexOrThrow(COLUMN_APP_NAME_KEY));
		String appPackageName = cursor.getString(cursor
				.getColumnIndexOrThrow(COLUMN_NAME_PACKAGE));
		Integer isEnabledAsInt = cursor.getInt(cursor
				.getColumnIndexOrThrow(COLUMN_NAME_ENABLED));
		
		// build output structure
		association = new AppAssociation(keyName,event,
				appName, appPackageName, isEnabledAsInt == 1);
		return association;
	}
	
}
