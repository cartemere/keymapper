package com.cartemere.car.keymapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class AppSelectionActivity extends Activity {

	private List<Map<String, String>> appPropertyList = null;
	String iconKey = "icon";
	String logoKey = "logo";
	String nameKey = "name";
	String packageKey = "package";
	boolean cancelAppListLoad;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_selection);

		cancelAppListLoad = false;
		ListView appSelectionListView = (ListView) findViewById(R.id.list_select_app);
		appPropertyList = new ArrayList<Map<String, String>>();

		// retrieve list of installed apps
		List<ApplicationInfo> appInfoList = null;
		PackageManager pm = getPackageManager();
		appInfoList = pm.getInstalledApplications(PackageManager.GET_META_DATA);

		// init progress bar
		ProgressDialog progressDialog = new ProgressDialog(
				AppSelectionActivity.this);
		progressDialog.setTitle("Loading list of installed apps");
		progressDialog.setCancelable(true);
		progressDialog.setProgress(0);
		progressDialog.setMax(appInfoList.size());
		progressDialog
				.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						// click on Cancel Button
						cancelAppListLoad = true;
					}
				});
		progressDialog.show();

		// process elements
		Map<String, String> propertyToValue;
		for (ApplicationInfo appInfo : appInfoList) {
			if (cancelAppListLoad) {
				break;
			}
			// progressDialog.incrementProgressBy(1);
			propertyToValue = new HashMap<String, String>();
			propertyToValue.put(iconKey, String.valueOf(appInfo.icon));
			propertyToValue.put(logoKey, String.valueOf(appInfo.logo));
			propertyToValue.put(nameKey, appInfo.loadLabel(pm).toString());
			propertyToValue.put(packageKey, appInfo.packageName);
			appPropertyList.add(propertyToValue);
		}

		progressDialog.dismiss();

		// reorder elements to display
		sortAppPropertyList();

		// Map structure to display
		SimpleAdapter mSchedule = new SimpleAdapter(this.getBaseContext(),
				appPropertyList, R.layout.app_selection_fragment, new String[] {
						iconKey, logoKey, nameKey }, new int[] {
						R.id.app_selection_img, R.id.app_selection_logo,
						R.id.app_selection_name });

		appSelectionListView.setAdapter(mSchedule);

		appSelectionListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						final Map<String, String> propertyMap = appPropertyList
								.get(position);
						// TODO : attach selected app to related action
						AlertDialog.Builder adb = new AlertDialog.Builder(
								AppSelectionActivity.this);
						adb.setTitle("Item selection");
						adb.setMessage("App : " + propertyMap.get(nameKey)
								+ "\nPackage : " + propertyMap.get(packageKey));
						adb.setPositiveButton("Launch selected app",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										launchSelectedApp(propertyMap
												.get(packageKey));
									}
								});
						adb.setNegativeButton("OK", null);
						adb.show();
					}
				});
	}

	private void launchSelectedApp(String packageName) {
		// check if target activity is already running
		Intent launchIntent = null;
		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		String className = null;
		for (ActivityManager.RunningTaskInfo info : am.getRunningTasks(9999)) {
			if (packageName.equals(info.baseActivity.getPackageName())) {
				className = info.baseActivity.getClassName();
				launchIntent = new Intent();
				launchIntent.setClassName(packageName, className);
				launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				break;
			}
		}
		if (className == null) {
			// launch target activity
			launchIntent = getPackageManager().getLaunchIntentForPackage(
					packageName);
		}
		startActivity(launchIntent);
	}

	private void sortAppPropertyList() {
		Comparator<Map<String, String>> comparator = new Comparator<Map<String, String>>() {

			@Override
			public int compare(Map<String, String> lhs, Map<String, String> rhs) {
				if (lhs == null) {
					return 0;
				} else if (rhs == null) {
					return 1;
				} else {
					String lhName = lhs.get(nameKey);
					String rhName = rhs.get(nameKey);
					if (lhName == null) {
						return 0;
					} else if (rhName == null) {
						return 1;
					} else {
						return lhName.compareToIgnoreCase(rhName);
					}
				}
			}
		};
		Collections.sort(appPropertyList, comparator);
	}

	@Override
	public void onResume() {
		super.onResume();
		Intent intent = getIntent();
		if (intent != null) {
			Bundle extras = intent.getExtras();
			if (extras != null) {
				String keyType = extras.getString(MainActivity.KEY_TYPE);
				if (keyType != null) {
					this.getActionBar().setTitle(
							"Select app for key : " + keyType);
				}
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.app_selection, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
