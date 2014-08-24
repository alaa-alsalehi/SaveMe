package com.serveme.savemyphone.view;

import com.serveme.savemyphone.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

public class BaseActivity extends Activity {

	private String packageName;
	private String activity;
	private boolean isStart;

	private BroadcastReceiver stopBaseActivity = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			finish();
		}

	};

	protected void onNewIntent(Intent start) {
		String packageName = start.getStringExtra("package");
		this.packageName = packageName;
		String activity = start.getStringExtra("activity");
		this.activity = activity;
		isStart = false;
		startWaitingActivity();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base);
		Intent start = getIntent();
		if (start == null) {
			return;
		}
		String packageName = start.getStringExtra("package");
		this.packageName = packageName;
		String activity = start.getStringExtra("activity");
		this.activity = activity;
		isStart = false;
		registerReceiver(stopBaseActivity, new IntentFilter(
				"finish_user_activity"));
		startWaitingActivity();
	}

	private void startWaitingActivity() {
		if (!isStart) {
			Intent i = new Intent(this, WaitingActivity.class);
			i.putExtra("package", packageName);
			i.putExtra("activity", activity);
			startActivity(i);
			isStart = true;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean("isStart", isStart);
		outState.putString("packageName", packageName);
		outState.putString("activity", activity);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		isStart = savedInstanceState.getBoolean("isStart");
		packageName = savedInstanceState.getString("packageName");
		activity = savedInstanceState.getString("activity");
	}

	@Override
	public void onBackPressed() {

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(stopBaseActivity);
	}
}
