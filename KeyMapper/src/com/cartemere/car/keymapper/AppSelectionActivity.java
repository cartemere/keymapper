package com.cartemere.car.keymapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cartemere.car.keymapper.action.KeyMapperAction;
import com.cartemere.car.keymapper.cache.AppAssociationCache;
import com.cartemere.car.keymapper.model.AppAssociation;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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

	public static final String PERSISTENCE_PACKAGE_PREFIX = "key_package_name_";
	public static final String PERSISTENCE_ENABLE_PREFIX = "key_enable_";
	private List<Map<String, String>> appPropertyList = null;
	String iconKey = "icon";
	String logoKey = "logo";
	String appName = "name";
	String appPackage = "package";
	boolean cancelAppListLoad;
	private AppAssociation parentAssociation = null;
	private Activity instance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		setContentView(R.layout.activity_app_selection);

		getParentAssociation();
		
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
			propertyToValue.put(appName, appInfo.loadLabel(pm).toString());
			propertyToValue.put(appPackage, appInfo.packageName);
			appPropertyList.add(propertyToValue);
		}
		
		// reorder elements to display
		sortAppPropertyList();

		progressDialog.dismiss();

		// Map structure to display
		SimpleAdapter mSchedule = new SimpleAdapter(this.getBaseContext(),
				appPropertyList, R.layout.app_selection_fragment, new String[] {
						iconKey, logoKey, appName }, new int[] {
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
						AlertDialog.Builder adb = new AlertDialog.Builder(
								AppSelectionActivity.this);
						
						adb.setTitle("App selection for action " + parentAssociation.getKeyName());
						String message = "App : " + propertyMap.get(appName)
								+ "\nPackage : " + propertyMap.get(appPackage);
						if (parentAssociation.getAppName() != null) {
							message +=  "\n" + "\nPrevious App : " + parentAssociation.getAppName();
						}
						adb.setMessage(message);
						adb.setPositiveButton("Set & Launch",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										parentAssociation.setAppName(propertyMap.get(appName));
										parentAssociation.setAppPackageName(propertyMap.get(appPackage));
								        AppAssociationCache cache = AppAssociationCache.getInstance(getApplicationContext());
								        cache.updateAssociationByKey(getApplicationContext(), parentAssociation);
										KeyMapperAction.launchSelectedApp(instance, propertyMap.get(appPackage));
									}
								});
						adb.setNeutralButton("Set",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										parentAssociation.setAppName(propertyMap.get(appName));
										parentAssociation.setAppPackageName(propertyMap.get(appPackage));
										AppAssociationCache cache = AppAssociationCache.getInstance(getApplicationContext());
								        cache.updateAssociationByKey(getApplicationContext(), parentAssociation);
									}
								});
						adb.setNegativeButton("Cancel", null);
						adb.show();
					}
				});
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
					String lhName = lhs.get(appName);
					String rhName = rhs.get(appName);
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
		getParentAssociation();
		this.getActionBar().setTitle(
				"Select app for key : " + parentAssociation.getKeyName());
	}

	public void getParentAssociation() {
		if (parentAssociation == null) {
			Intent intent = getIntent();
			if (intent != null) {
				Bundle extras = intent.getExtras();
				if (extras != null) {
					parentAssociation = (AppAssociation) extras
							.getSerializable(MainActivity.PROPERTY_ASSOCIATION);
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
