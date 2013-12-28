package com.serveme.savemyphone.view.wizard;

import android.app.Activity;
import android.app.admin.DeviceAdminInfo;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.ExceptionReporter;
import com.google.analytics.tracking.android.MapBuilder;
import com.serveme.analytics.AnalyticsExceptionParser;
import com.serveme.savemyphone.R;
import com.serveme.savemyphone.preferences.PrefEditor;
import com.serveme.savemyphone.receivers.AdminReciver;
import com.serveme.savemyphone.view.AdminActivity;

public class AdminRequest extends ActionBarActivity {

	private final int REQUEST_ENABLE = 1;
	
	private ComponentName adminComponent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		String direction = getResources().getString(R.string.direction);
		TableRow choises = (TableRow) findViewById(R.id.choises_row);
		final TextView textView2 = (TextView) findViewById(R.id.textView2);
		textView2.setText(R.string.lock_permission_request_help);
		textView2.setMovementMethod(ScrollingMovementMethod.getInstance());
		final Button previousButton = (Button) findViewById(R.id.previous);
		final Button nextButton = (Button) findViewById(R.id.next);
		nextButton.setText(R.string.get_permission);
		if ("right".equals(direction)) {
			choises.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
			textView2.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
			// remove and added at last of the choices row to make it right
			choises.removeView(previousButton);
			choises.addView(previousButton);
		} else {
			choises.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
		}
		previousButton.setVisibility(View.GONE);
		previousButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				/*PrefEditor prefEditor = new PrefEditor(AdminRequest.this);
				prefEditor
						.updateAdminPermission(PrefEditor.ADMIN_PERMISSION_IGNORED);*/
			}
		});
		nextButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
				adminComponent = new ComponentName(AdminRequest.this,
						AdminReciver.class);
				EasyTracker.getInstance(AdminRequest.this).send(
						MapBuilder
								.createEvent("ui_action", "button_press",
										"request_admin_permission",
										Long.valueOf(1)).build());
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
				}else{
					finish();			
					devicePolicyManager.lockNow();//·· √ﬂœ „‰ √‰ «·„” Œœ„ ÂÊ ’«Õ» «·ÃÂ«“
					Intent intent = new Intent(getBaseContext(),
							PasswordRequest.class);
					startActivity(intent);
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
				DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
				devicePolicyManager.lockNow();
				Intent intent = new Intent(getBaseContext(),
						PasswordRequest.class);
				startActivity(intent);
				EasyTracker.getInstance(this).send(
						MapBuilder.createEvent("ui_action", "button_press",
								"admin_permission_done", Long.valueOf(1))
								.build());
				/*PrefEditor prefEditor = new PrefEditor(this);
				prefEditor
						.updateAdminPermission(PrefEditor.ADMIN_PERMISSION_OK);*/
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
