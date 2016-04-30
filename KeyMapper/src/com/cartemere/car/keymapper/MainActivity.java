   package com.cartemere.car.keymapper;

import java.util.ArrayList;

import com.cartemere.car.keymapper.dao.AppAssociationDAO;
import com.cartemere.car.keymapper.model.AppAssociation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;


public class MainActivity extends Activity {

	public static final String PROPERTY_ASSOCIATION = "association";
	public static final String PROPERTY_APP_LIST = "appList";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // retrieve all datas
        AppAssociationDAO dao = AppAssociationDAO.getInstance();
        ArrayList<AppAssociation> associations = dao.loadAllAssociationWithInit(getApplicationContext());
        
        EfficientAdapter adapter = new EfficientAdapter(this, associations);
        // Attach the adapter to a ListView
        ListView appSelectionListView = (ListView) findViewById(R.id.list_key);
        appSelectionListView.setAdapter(adapter);
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
    
    public class EfficientAdapter extends ArrayAdapter<AppAssociation> {
        
    	final Activity referenceActivity;

        public EfficientAdapter(Activity activity, ArrayList<AppAssociation> associations) {
            super(activity, 0, associations);
            this.referenceActivity = activity;
         }
        
        /**
         * Make a view to hold each row.
         *
         * @see android.widget.ListAdapter#getView(int, android.view.View,
         *      android.view.ViewGroup)
         */
    	@Override
        public View getView(int position, View convertView, ViewGroup parent) {
        	// Get the data item for this position
        	final AppAssociation association = getItem(position);    
            // Check if an existing view is being reused, otherwise inflate the view
            ViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {
               viewHolder = new ViewHolder();
               LayoutInflater inflater = LayoutInflater.from(getContext());
               convertView = inflater.inflate(R.layout.app_main_fragment, parent, false);
               viewHolder.btnChooseApp = (Button) convertView.findViewById(R.id.button_choose_app);
               viewHolder.appTitle = (TextView) convertView.findViewById(R.id.label_app_title);
               viewHolder.appName = (TextView) convertView.findViewById(R.id.label_app_package);
               viewHolder.btnSwitch = (Switch) convertView.findViewById(R.id.button_app_enable);
               convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            // Populate the data into the template view using the data object
            
            viewHolder.appTitle.setText("Select app for key " + association.getKeyName());
            String appName = association.getAppName();
            if (appName == null) {
            	appName = "[no app selected]";
            }
            viewHolder.appName.setText(appName);
            
            viewHolder.btnChooseApp.setText(association.getKeyName());
            viewHolder.btnChooseApp.setOnClickListener(new View.OnClickListener() {
    			@Override
    			public void onClick(View v) {
    				//manager.actionChoose(referenceActivity);
    				// move to second view
    		    	Intent intent = new Intent();
    		    	intent.setClass(referenceActivity, AppSelectionActivity.class);
    		    	intent.putExtra(PROPERTY_ASSOCIATION, association);
    		    	startActivity(intent);
    			}
    		});

            viewHolder.btnSwitch.setChecked(association.getIsKeyMappingEnabled());
            viewHolder.btnSwitch.setEnabled(association.getAppPackageName() != null);
            viewHolder.btnSwitch
    				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
    					@Override
    					public void onCheckedChanged(CompoundButton buttonView,
    							boolean isChecked) {
    						association.setIsKeyMappingEnabled(isChecked);
    						AppAssociationDAO dao = AppAssociationDAO.getInstance();
    						dao.updateAssociation(referenceActivity, association);
    					}
    				});
    		
            return convertView;
        }
        
    	class ViewHolder {
        	Button btnChooseApp;
            TextView appTitle;
            TextView appName;
            Switch btnSwitch;
            
			@Override
			public String toString() {
				return "ViewHolder [btnChooseApp=" + btnChooseApp
						+ ", appTitle=" + appTitle + ", appName="
						+ appName + ", btnSwitch=" + btnSwitch + "]";
			}
        }
    }
}
