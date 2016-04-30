package com.cartemere.car.keymapper.dao;

import java.util.ArrayList;
import java.util.Collection;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cartemere.car.keymapper.model.AppAssociation;
import com.cartemere.car.keymapper.properties.PropertiesReader;

/**
 * Provide accessor to the Association persistence layer
 * @author cartemere
 */
public class AppAssociationDAO {
	
	
	private static String LOG_KEY = "DAO";
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
	
	private static AppAssociationDAO instance = null;
	
	public static AppAssociationDAO getInstance() {
		if (instance == null)  {
			instance = new AppAssociationDAO();
		}
		return instance;
	}

	public ArrayList<AppAssociation> loadAllAssociationWithInit(Context context) {
		ArrayList<AppAssociation> allResult = loadAllAssociations(context);
		if (allResult.size() == 0) {
			Log.i(LOG_KEY, "DB is empty, init content...");
			initDBContent(null, context);
			allResult = loadAllAssociations(context);
		}
		return allResult;
	}
	/**
	 * populate the DB. To be called only on the first launch
	 * @param context
	 */
	public void initDBContent(KeyMapperDbHelper dbHelper, Context context) {
		
		PropertiesReader pReader = new PropertiesReader(context);
		Collection<AppAssociation> associations = pReader.getDefaultAssociations();
		for (AppAssociation association : associations) {
			createAssociation(context, association);
		}
	}
	
	
	public AppAssociation loadAssociationFromEvent(Context context,
			String eventKey) {
		KeyMapperDbHelper dbHelper = new KeyMapperDbHelper(context);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		try {
			// declare projection
			String[] projection = { 
					COLUMN_NAME_KEY,
					COLUMN_EVENT,
					COLUMN_APP_NAME_KEY,
					COLUMN_NAME_PACKAGE,
					COLUMN_NAME_ENABLED };
			String whereClause = COLUMN_EVENT + "=?";

			// query DB
			Cursor cursor = db.query(TABLE_NAME, projection, whereClause,
					new String[] { eventKey }, null, null, null);

			// parse result
			cursor.moveToFirst();
			if (cursor.getCount() < 1) {
				String errorMessage = "No record found for key = " + eventKey ;
				Log.e(LOG_KEY, errorMessage);
				throw new IllegalArgumentException(errorMessage);
			}
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
			AppAssociation association = new AppAssociation(keyName,event,
					appName, appPackageName, isEnabledAsInt == 1);

			return association;
		} finally {
			db.close();
		}
	}
	
	public ArrayList<AppAssociation> loadAllAssociations(Context context) {
		ArrayList<AppAssociation> result = new ArrayList< AppAssociation>();
		KeyMapperDbHelper dbHelper = new KeyMapperDbHelper(context);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		try {
			// declare projection
			String[] projection = { 
					COLUMN_NAME_KEY,
					COLUMN_EVENT,
					COLUMN_APP_NAME_KEY,
					COLUMN_NAME_PACKAGE,
					COLUMN_NAME_ENABLED };
			String sortOrder = COLUMN_NAME_KEY + " DESC";

			// query DB
			Cursor cursor = db.query(TABLE_NAME, projection, null,
					null, null, null, sortOrder);

			// parse result
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
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
				AppAssociation association = new AppAssociation(keyName, event, 
						appName, appPackageName, isEnabledAsInt == 1);
				result.add(association);
				cursor.moveToNext();
			}
			return result;
		} finally {
			db.close();
		}
	}
	
	public void deleteAssociation(Context context, AppAssociation association) {
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
	
	public void createAssociation(Context context, AppAssociation association) {
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
	
	public AppAssociation updateAssociation(Context context,AppAssociation association) {
		deleteAssociation(context, association);
		createAssociation(context, association);
		return association;
	}
	
}
