package com.serveme.savemyphone.view.utils;

import com.serveme.savemyphone.preferences.PrefEditor;
import com.serveme.savemyphone.receivers.AdminReciver;
import com.serveme.savemyphone.view.wizard.AdminRequest;
import com.serveme.savemyphone.view.wizard.HelpActivity;
import com.serveme.savemyphone.view.wizard.PasswordRequest;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class ActivitiesController {

	private static final int REQ_ENTER_PATTERN = 2;
	
	private Activity activity;
	private DevicePolicyManager devicePolicyManager;
	private ComponentName adminComponent;
	private Authenticator auth;
	private PrefEditor pe;

	public ActivitiesController(Activity activity) {
		this.activity = activity;
		devicePolicyManager = (DevicePolicyManager) activity.getSystemService(Context.DEVICE_POLICY_SERVICE);
		adminComponent = new ComponentName(activity, AdminReciver.class);
		auth = new Authenticator(activity);
		pe = new PrefEditor(activity);
	}

	public void getActivitiesFlow() {
		if (!devicePolicyManager.isAdminActive(adminComponent)) {
			activity.finish();
			Intent intent = new Intent(activity, AdminRequest.class);
			activity.startActivity(intent);
		} else if (!pe.isPatternExist()) {
			activity.finish();
			Intent intent = new Intent(activity, PasswordRequest.class);
			activity.startActivity(intent);
		} else if (pe.isNewUser()){
			activity.finish();
			Intent intent = new Intent(activity, HelpActivity.class);
			intent.putExtra("first_time", true);
			activity.startActivity(intent); 
		} else {
			if (activity.getIntent() == null || !activity.getIntent().getBooleanExtra("first_time",	false)) {
				if (pe.getLockMethod().equals("pattern")) {
					auth.checkPattern(REQ_ENTER_PATTERN);
				} else {

				}
			}
		}
	}

}
