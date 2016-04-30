package com.cartemere.car.keymapper;

import com.cartemere.car.keymapper.model.AppAssociation;
import com.cartemere.car.keymapper.task.AppListTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class AppSelectionActivity extends Activity {

	private AppAssociation parentAssociation = null;
	private AppListTask task;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_selection);
		getParentAssociation();

		ProgressDialog progressDialog = new ProgressDialog(this);
		// detach thread to process Asynch loading...
		task = new AppListTask(this, progressDialog);

		// init progress bar
		progressDialog.setTitle("Loading list of installed apps");
		progressDialog.setCancelable(true);
		progressDialog.setProgress(0);
		progressDialog.setMax(100);
		progressDialog
				.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						// click on Cancel Button
						task.cancel(true);
					}
				});
		progressDialog.show();

		// run async task
		task.execute();
	}

	@Override
	public void onResume() {
		super.onResume();
		getParentAssociation();
		this.getActionBar().setTitle(
				"Select app for key : " + parentAssociation.getKeyName());
	}

	public AppAssociation getParentAssociation() {
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
		return parentAssociation;
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
