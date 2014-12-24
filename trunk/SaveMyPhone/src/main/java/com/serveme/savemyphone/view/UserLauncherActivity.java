package com.serveme.savemyphone.view;

import com.serveme.savemyphone.R;
import com.serveme.savemyphone.R.layout;
import com.serveme.savemyphone.R.menu;
import com.serveme.savemyphone.preferences.PrefEditor;
import com.serveme.savemyphone.service.AppsMonitor;
import com.serveme.savemyphone.util.MyTracker;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class UserLauncherActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_launcher);
		lock();
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_launcher, menu);
		return true;
	}

	protected void lock() {
		PrefEditor pe = new PrefEditor(UserLauncherActivity.this);
		pe.updateStatus(1);
		Intent saveintent = new Intent(UserLauncherActivity.this,
				AppsMonitor.class);
		startService(saveintent);
	}
	
	

}
