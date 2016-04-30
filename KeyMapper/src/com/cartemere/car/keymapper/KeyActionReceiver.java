package com.cartemere.car.keymapper;

import com.cartemere.car.keymapper.action.KeyMapperAction;
import com.cartemere.car.keymapper.cache.AppAssociationCache;
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
    	AppAssociationCache cache = AppAssociationCache.getInstance(context);
    	AppAssociation appAssociation = cache.getAssociationFromEvent(context, eventName);
		if (appAssociation != null) {
			if (appAssociation.getIsKeyMappingEnabled()) {
				String packageName = appAssociation.getAppPackageName();
				KeyMapperAction.launchSelectedApp(context, packageName);
			} else {
				Log.w(LOG_KEY, "Mapping disabled for event : " + eventName);
			}
		} else {
			Log.e(LOG_KEY, "received unexpected event : " + eventName);
		}
		
    }
}