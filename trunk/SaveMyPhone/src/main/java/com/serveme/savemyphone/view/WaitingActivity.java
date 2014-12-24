package com.serveme.savemyphone.view;

import com.serveme.savemyphone.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class WaitingActivity extends Activity {

	private String packageName;
	private String activity;

	protected void onNewIntent(Intent start) {
		String packageName = start.getStringExtra("package");
		this.packageName = packageName;
		String activity = start.getStringExtra("activity");
		this.activity = activity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base);
		if (savedInstanceState == null) {
			Intent start = getIntent();
			if (start == null) {
				return;
			}
			String packageName = start.getStringExtra("package");
			this.packageName = packageName;
			String activity = start.getStringExtra("activity");
			this.activity = activity;
		} else {
			this.packageName = savedInstanceState.getString("package");
			this.activity = savedInstanceState.getString("activity");
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("package", packageName);
		outState.putString("activity", activity);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Intent appIntent = getPackageManager().getLaunchIntentForPackage(
				packageName);
		// i.setAction(Intent.ACTION_MAIN);
		// i.addCategory(Intent.CATEGORY_LAUNCHER);
		appIntent.setClassName(packageName, activity);
		appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TOP);
		try {
			startActivity(appIntent);
		} catch (SecurityException exception) {
			appIntent = getPackageManager().getLaunchIntentForPackage(
					packageName);
			appIntent.setAction(Intent.ACTION_MAIN);
			appIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			startActivity(appIntent);
		}
	}

	@Override
	protected void onPause() {
		finish();
		super.onPause();
	}

	@Override
	public void onBackPressed() {

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
