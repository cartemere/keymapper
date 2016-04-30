package com.cartemere.car.keymapper.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.cartemere.car.keymapper.model.AppAssociation;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

public class PropertiesReader {
	
	public static final String LOG_TAG = "KeyMapperProp";
	public static String PROPERTIES_FILE_NAME = "key_definition.properties";
	public static String PROPERTIES_KEY_LIST = "key.list";
	public static String PROPERTIES_KEY_PREFIX = "key.event.";
	
	public static String PROPERTIES_KEY_SEPARATOR = ";";
	
	
	private Context context;
	private Properties properties;

	public PropertiesReader(Context context) {
		this.context = context;
		properties = new Properties();
	}

	public Properties getProperties(String fileName) {
		try {
			AssetManager am = context.getAssets();
			InputStream inputStream = am.open(fileName);
			properties.load(inputStream);
		} catch (IOException e) {
			Log.e(LOG_TAG, "Error while loading Properties file : " + fileName + "\n" +  e.toString());
		}
		return properties;
	}
	
	/**
	 * Init AppAssociations by loading default keys in properties file 
	 * @return List of key AppAssociation to handle
	 */
	public List<AppAssociation> getDefaultAssociations() {
		List<AppAssociation> associations = new ArrayList<AppAssociation>();
		Properties properties = getProperties(PROPERTIES_FILE_NAME);
		String keysAsString = (String)properties.get(PROPERTIES_KEY_LIST);
		if (keysAsString == null || keysAsString.isEmpty()) {
			throw new IllegalArgumentException("entry " + PROPERTIES_KEY_LIST + " not found in " + PROPERTIES_FILE_NAME);
		}
		String[] keysAsArray = keysAsString.split(PROPERTIES_KEY_SEPARATOR);
		Log.i(LOG_TAG, "found " + keysAsArray.length + " Keys to map");
		for (String keyName : keysAsArray) {
			String event = (String)properties.get(PROPERTIES_KEY_PREFIX + keyName);
			if (event == null || event.isEmpty()) {
				throw new IllegalArgumentException("entry " + PROPERTIES_KEY_PREFIX + keyName + " not found in " + PROPERTIES_FILE_NAME);
			}
			Log.i(LOG_TAG, "association " + keyName + "=" + event);
			AppAssociation association = new AppAssociation(keyName, event, null, null, false);
			associations.add(association);
		}
		
		return associations;
	}


}