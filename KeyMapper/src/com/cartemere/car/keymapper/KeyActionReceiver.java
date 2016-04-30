package com.cartemere.car.keymapper;

import com.cartemere.car.keymapper.action.KeyMapperAction;
import com.cartemere.car.keymapper.dao.AppAssociationDAO;
import com.cartemere.car.keymapper.model.AppAssociation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class KeyActionReceiver extends BroadcastReceiver {
	
	private static String LOG_KEY = "KeyMapperReceiver";
	
    public KeyActionReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        	
    	String eventName = intent.getAction();
		// retrieve related AppAssociation
    	Log.i(LOG_KEY, "received event : " + eventName);
		AppAssociation appAssociation = AppAssociationDAO.getInstance()
				.loadAssociationFromEvent(context, eventName);
		if (appAssociation.getIsKeyMappingEnabled()) {
			String packageName = appAssociation.getAppPackageName();
			KeyMapperAction.launchSelectedApp(context, packageName);
		}
    }
}