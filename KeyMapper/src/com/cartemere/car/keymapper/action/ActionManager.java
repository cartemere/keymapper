package com.cartemere.car.keymapper.action;

import java.util.List;

import com.cartemere.car.keymapper.AppSelectionActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class ActionManager {

	private static final String LOG_KEY = "ActionManager";
	
    public void actionChoose(Activity activity) {
    	
    	
    }

	public void actionSwitch(boolean isChecked) {
		Log.i(LOG_KEY, "switch check status=" + isChecked);
		if (isChecked) {
			// enable notif
		} else {
			// disable notif
		}
	}
	
}
