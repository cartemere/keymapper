   package com.cartemere.car.keymapper;

import com.cartemere.car.keymapper.action.ActionManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;


public class MainActivity extends Activity {

	public static final String KEY_TYPE = "key";
	private Switch btnSwitch;
	private Button btnChoose;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        final Activity activity = this;
        final ActionManager manager = new ActionManager();
        btnChoose = (Button) findViewById(R.id.button_choose_dvd);
        btnSwitch = (Switch) findViewById(R.id.button_switch_dvd);
        
        btnChoose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				manager.actionChoose(activity);
				// move to second view
		    	Intent intent = new Intent();
		    	intent.setClass(activity, AppSelectionActivity.class);
		    	intent.putExtra(KEY_TYPE, "DVD");
		    	startActivity(intent);
			}
		});
        
		btnSwitch
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						manager.actionSwitch(isChecked);
					}
				});
		
		
		// check the current state before we display the screen
		manager.actionSwitch(btnSwitch.isChecked());
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
}
