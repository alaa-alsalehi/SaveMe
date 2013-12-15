package com.serveme.savemyphone.view;

import android.app.Activity;
import android.app.admin.DeviceAdminInfo;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.ExceptionReporter;
import com.google.analytics.tracking.android.MapBuilder;
import com.serveme.analytics.AnalyticsExceptionParser;
import com.serveme.savemyphone.R;
import com.serveme.savemyphone.receivers.AdminReciver;

public class AdminRequest extends Activity {

	private final int REQUEST_ENABLE = 1;
	private DevicePolicyManager devicePolicyManager;
	private ComponentName adminComponent;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.admin_request);
		Button btn = (Button) findViewById(R.id.admin_btn);

		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
				adminComponent = new ComponentName(AdminRequest.this,
						AdminReciver.class);
				if (!devicePolicyManager.isAdminActive(adminComponent)) {
					Intent intent = new Intent(
							DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
					intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
							adminComponent);
					intent.putExtra(
							DevicePolicyManager.EXTRA_ADD_EXPLANATION,
							getResources().getString(
									R.string.lock_permission_request));
					intent.putExtra("force-locked",
							DeviceAdminInfo.USES_POLICY_FORCE_LOCK);
					intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
					startActivityForResult(intent, REQUEST_ENABLE);
					EasyTracker.getInstance(AdminRequest.this).send(
							MapBuilder
									.createEvent("ui_action", "button_press",
											"request_admin_permission",
											Long.valueOf(1)).build());
				}
			}
		});

	}

	@Override
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
		Thread.UncaughtExceptionHandler uncaughtExceptionHandler = Thread
				.getDefaultUncaughtExceptionHandler();
		if (uncaughtExceptionHandler instanceof ExceptionReporter) {
			ExceptionReporter exceptionReporter = (ExceptionReporter) uncaughtExceptionHandler;
			exceptionReporter
					.setExceptionParser(new AnalyticsExceptionParser());
		}
	}

	@Override
	protected void onStop() {
		EasyTracker.getInstance(this).activityStop(this);
		super.onStop();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_ENABLE:
			if (resultCode == Activity.RESULT_OK) {
				Log.v("DeviceAdminSample", "Administration enabled!");
				finish();
				devicePolicyManager.lockNow();
				Intent intent = new Intent(getBaseContext(),
						AdminActivity.class);
				startActivity(intent);
				EasyTracker.getInstance(this).send(
						MapBuilder.createEvent("ui_action", "button_press",
								"admin_permission_done", Long.valueOf(1))
								.build());
			} else {
				EasyTracker.getInstance(this).send(
						MapBuilder.createEvent("ui_action", "button_press",
								"admin_permission_cancelled", Long.valueOf(1))
								.build());
			}
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
