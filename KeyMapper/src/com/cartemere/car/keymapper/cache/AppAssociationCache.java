package com.cartemere.car.keymapper.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;

import com.cartemere.car.keymapper.dao.AppAssociationDAO;
import com.cartemere.car.keymapper.model.AppAssociation;
import com.cartemere.car.keymapper.properties.PropertiesReader;

/**
 * Local cache to prevent from calling DB on each call...
 * On update, this cache is sync to the related DB
 * @author cartemere
 *
 */
public class AppAssociationCache {

	private static AppAssociationCache instance = null;
	
	// use ArrayList has we have few elements to manage. It is faster and use less memory than HashMap
	private List<AppAssociation> associations = new ArrayList<AppAssociation>();
	
	public static synchronized AppAssociationCache getInstance(Context context) {
		if (instance == null) {
			instance = new AppAssociationCache();
			instance.init(context);
		}
		return instance;
	}
	
	
	private void init(Context context) {
		// connect to DB and load all existing entries
		AppAssociationDAO dao = new AppAssociationDAO();
        associations = dao.loadAllAssociationsFromDB(context);
        if (associations.isEmpty()) {
        	// no record found : need to init
			createDefaultAssociations(context, dao);
    		associations = dao.loadAllAssociationsFromDB(context);
        }
	}


	private void createDefaultAssociations(Context context,
			AppAssociationDAO dao) {
		PropertiesReader pReader = new PropertiesReader(context);
		Collection<AppAssociation> associations = pReader.getDefaultAssociations();
		for (AppAssociation association : associations) {
			dao.createAssociationInDB(context, association);
		}
	}
	
	public List<AppAssociation> getAllAssociations() {
		return associations;
	}
	
	public void updateAssociationByKey(Context context, AppAssociation association) {
		int location = 0;
		for (AppAssociation existingAssociation : associations) {
			if (existingAssociation.getKeyName().equals(association.getKeyName())) {
				// update in DB
				AppAssociationDAO dao = new AppAssociationDAO();
				dao.deleteAssociationInDB(context, existingAssociation);
				dao.createAssociationInDB(context, association);
				// update local List
				associations.set(location, association);
				break;
			}
			location++;
		}
	}
	
	public AppAssociation getAssociationFromEvent(Context context, String eventName) {
		for (AppAssociation existingAssociation : associations) {
			if (existingAssociation.getEvent().equals(eventName)) {
				return existingAssociation;
			}
		}
		return null;
	}
	
	
}
