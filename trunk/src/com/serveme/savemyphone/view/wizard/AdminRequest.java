package com.serveme.savemyphone.view.wizard;

import android.app.Activity;
import android.app.admin.DeviceAdminInfo;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.serveme.savemyphone.R;
import com.serveme.savemyphone.receivers.AdminReciver;
import com.serveme.savemyphone.util.MyTracker;
import com.serveme.savemyphone.view.utils.ActivitiesController;

public class AdminRequest extends ActionBarActivity {

	private final int REQUEST_ENABLE = 1;
	
	private ActivitiesController ac;
	private DevicePolicyManager devicePolicyManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guide_activity);
		ac = new ActivitiesController(AdminRequest.this);
		final TextView textView = (TextView) findViewById(R.id.textView);
		textView.setText(R.string.lock_permission_request_help);
		textView.setMovementMethod(ScrollingMovementMethod.getInstance());
		final Button grantPermissionButton = (Button) findViewById(R.id.btn);
		grantPermissionButton.setText(R.string.grant_permission);
		grantPermissionButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
				ComponentName adminComponent = new ComponentName(AdminRequest.this, AdminReciver.class);
				MyTracker.fireButtonPressedEvent(AdminRequest.this, "request_admin_permission");
				if (!devicePolicyManager.isAdminActive(adminComponent)) {
					Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
					intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,	adminComponent);
					intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getResources().getString(R.string.lock_permission_request));
					intent.putExtra("force-locked",	DeviceAdminInfo.USES_POLICY_FORCE_LOCK);
					intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
					startActivityForResult(intent, REQUEST_ENABLE);
				} else {
					finish();			
					devicePolicyManager.lockNow(); //·· √ﬂœ „‰ √‰ «·„” Œœ„ ÂÊ ’«Õ» «·ÃÂ«“
					ac.getActivitiesFlow();
				}
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		MyTracker.fireActivityStartEvent(AdminRequest.this);
		MyTracker.getUncaughtExceptionHandler();
	}

	@Override
	protected void onStop() {
		MyTracker.fireActivityStopevent(AdminRequest.this);
		super.onStop();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_ENABLE:
			if (resultCode == Activity.RESULT_OK) {
				finish();			
				devicePolicyManager.lockNow(); //·· √ﬂœ „‰ √‰ «·„” Œœ„ ÂÊ ’«Õ» «·ÃÂ«“
				Intent intent = new Intent(AdminRequest.this, PasswordRequest.class);
				startActivity(intent);
				MyTracker.fireButtonPressedEvent(AdminRequest.this, "admin_permission_done");
			} else {
				MyTracker.fireButtonPressedEvent(AdminRequest.this, "admin_permission_cancelled");
			}
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
