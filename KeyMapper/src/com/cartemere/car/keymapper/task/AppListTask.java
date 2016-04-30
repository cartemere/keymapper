package com.cartemere.car.keymapper.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cartemere.car.keymapper.AppSelectionActivity;
import com.cartemere.car.keymapper.R;
import com.cartemere.car.keymapper.action.KeyMapperAction;
import com.cartemere.car.keymapper.cache.AppAssociationCache;
import com.cartemere.car.keymapper.model.AppAssociation;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class AppListTask extends AsyncTask<Void, Integer, SimpleAdapter> {

	public static final String LOG_KEY = "AppListTask";
	private AppSelectionActivity activity;
	private ProgressDialog dialog;

	private List<Map<String, String>> appPropertyList = new ArrayList<Map<String, String>>();
	public static String TAG_ICON_KEY = "icon";
	public static String TAG_APP_NAME = "name";
	public static String TAG_APP_PACKAGE = "package";
	boolean cancelAppListLoad = false;
	
	private static int PROGRESS_GET_APP_LIST = 20;
	private static int PROGRESS_PROCESS_APP = 1;
	private static int PROGRESS_SORT_LIST = 10;
	private static int PROGRESS_CREATE_ADAPTER = 20;
	private static int PROGRESS_SET_ADAPTER = 20;
	private static int PROGRESS_RENDER = 20;
	
	
	private int progressCurrent = 0;
	private int progressMaxValue = 0;

	public AppListTask(AppSelectionActivity parentActivity,
			ProgressDialog progressDialog) {
		super();
		// keep parent activity to access context & attach result
		this.activity = parentActivity;
		// keep progressDialog to update it...
		this.dialog = progressDialog;
	}

	public void cancel() {
		cancelAppListLoad = true;
	}

	@Override
	protected SimpleAdapter doInBackground(Void... params) {
		SimpleAdapter mSchedule = null;
		try {
			// retrieve list of installed apps
			List<ApplicationInfo> appInfoList = null;
			PackageManager pm = activity.getPackageManager();
			appInfoList = pm
					.getInstalledApplications(PackageManager.GET_META_DATA);
			if (!cancelAppListLoad) {

				// we have list of all items, can estimate progress bar behavior
				computeProgressMaxValue(appInfoList);
				publishProgress(PROGRESS_GET_APP_LIST);
				// process elements
				Map<String, String> propertyToValue;
				for (ApplicationInfo appInfo : appInfoList) {
					if (cancelAppListLoad) {
						break;
					}
					// progressDialog.incrementProgressBy(1);
					propertyToValue = new HashMap<String, String>();
					propertyToValue.put(TAG_ICON_KEY,
							String.valueOf(appInfo.icon));
					propertyToValue.put(TAG_APP_NAME, appInfo.loadLabel(pm)
							.toString());
					propertyToValue.put(TAG_APP_PACKAGE, appInfo.packageName);
					appPropertyList.add(propertyToValue);
					publishProgress(PROGRESS_PROCESS_APP);
				}

				if (!cancelAppListLoad) {

					// reorder elements to display
					sortAppPropertyList();
					publishProgress(PROGRESS_SORT_LIST);

					if (!cancelAppListLoad) {
						mSchedule = new SimpleAdapter(
								activity.getBaseContext(), appPropertyList,
								R.layout.app_selection_fragment, new String[] {
										AppListTask.TAG_ICON_KEY,
										AppListTask.TAG_APP_NAME }, new int[] {
										R.id.app_selection_img,
										R.id.app_selection_name });
						publishProgress(PROGRESS_CREATE_ADAPTER);
					}
				}
			}
		} catch (Exception ex) {
			dialog.dismiss();
			Log.e(LOG_KEY, "unexpected error while processing", ex);
		}
		return mSchedule;
	}


	@Override
	protected void onPostExecute(SimpleAdapter mSchedule) {

		try {
			// attach adapter to List
			ListView appSelectionListView = (ListView) activity
					.findViewById(R.id.list_select_app);
			appSelectionListView.setAdapter(mSchedule);
			publishProgress(PROGRESS_SET_ADAPTER);

			// set entry behavior
			appSelectionListView
					.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							final Map<String, String> propertyMap = appPropertyList
									.get(position);
							final AppAssociation parentAssociation = activity
									.getParentAssociation();
							AlertDialog.Builder adb = new AlertDialog.Builder(
									activity);


							adb.setTitle(activity.getString(R.string.label_confirm_title) + " " 
									+ parentAssociation.getKeyName());
							String message = activity.getString(R.string.label_confirm_app_name) + " : "
									+ propertyMap.get(AppListTask.TAG_APP_NAME)
									+ "\n" + activity.getString(R.string.label_confirm_package_name) +  " : "
									+ propertyMap
											.get(AppListTask.TAG_APP_PACKAGE);
							if (parentAssociation.getAppName() != null) {
								message += "\n\n" + activity.getString(R.string.label_confirm_app_old_name) + " : "
										+ parentAssociation.getAppName();
							}
							adb.setMessage(message);
							adb.setPositiveButton(activity.getString(R.string.label_confirm_action_set_launch),
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											parentAssociation.setAppName(propertyMap
													.get(AppListTask.TAG_APP_NAME));
											parentAssociation
													.setAppPackageName(propertyMap
															.get(AppListTask.TAG_APP_PACKAGE));
											AppAssociationCache cache = AppAssociationCache.getInstance(activity
													.getApplicationContext());
											cache.updateAssociationByKey(
													activity.getApplicationContext(),
													parentAssociation);
											// launch selected app
											KeyMapperAction.launchSelectedApp(
													activity,
													propertyMap
															.get(AppListTask.TAG_APP_PACKAGE));
										}
									});
							adb.setNeutralButton(activity.getString(R.string.label_confirm_action_set),
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											parentAssociation.setAppName(propertyMap
													.get(AppListTask.TAG_APP_NAME));
											parentAssociation
													.setAppPackageName(propertyMap
															.get(AppListTask.TAG_APP_PACKAGE));
											AppAssociationCache cache = AppAssociationCache.getInstance(activity
													.getApplicationContext());
											cache.updateAssociationByKey(
													activity.getApplicationContext(),
													parentAssociation);
											// close current activity (come back
											// to main activity)
											activity.finish();
										}
									});
							adb.setNegativeButton(activity.getString(R.string.label_confirm_action_cancel), null);
							adb.show();
						}
					});
		} finally {
			// release
			dialog.dismiss();
		}
	}

	@Override
	protected void onPreExecute() {
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		if (values.length > 0) {
		progressCurrent += values[0];
		dialog.setProgress(progressCurrent * 100 / progressMaxValue);
		}
	}
	
	private void computeProgressMaxValue(List<ApplicationInfo> appInfoList) {
		progressMaxValue = PROGRESS_GET_APP_LIST + PROGRESS_SORT_LIST + PROGRESS_CREATE_ADAPTER + PROGRESS_SET_ADAPTER + PROGRESS_RENDER + (PROGRESS_PROCESS_APP * appInfoList.size());
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
					String lhName = lhs.get(TAG_APP_NAME);
					String rhName = rhs.get(TAG_APP_NAME);
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
}