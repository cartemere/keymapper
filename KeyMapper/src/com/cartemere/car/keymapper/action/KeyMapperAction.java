package com.cartemere.car.keymapper.action;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class KeyMapperAction {

	private static String LOG_KEY = "KeyMapperAction";

	public static void launchSelectedApp(Context context, String packageName) {
		// check if target activity is already running
		Intent launchIntent = null;
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		String className = null;
		for (ActivityManager.RunningTaskInfo info : am.getRunningTasks(9999)) {
			if (packageName.equals(info.baseActivity.getPackageName())) {
				className = info.baseActivity.getClassName();
				launchIntent = new Intent();
				launchIntent.setClassName(packageName, className);
				launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				Log.i(LOG_KEY, "found app " + packageName + " running in background");
				break;
			}
		}
		if (className == null) {
			// launch target activity
			launchIntent = context.getPackageManager().getLaunchIntentForPackage(
					packageName);
			Log.i(LOG_KEY, "launch app " + packageName + " from scratch");
		}
		context.startActivity(launchIntent);
	}
}
