package com.serveme.savemyphone.util;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.serveme.savemyphone.receivers.AdminReciver;
import com.serveme.savemyphone.view.SettingsActivity;

public class Utility {
	public static void uninstallSaveMe(Context context) {
		MyTracker.fireButtonPressedEvent(context, "uninstall");
		ComponentName devAdminReceiver = new ComponentName(context,
				AdminReciver.class);
		DevicePolicyManager dpm = (DevicePolicyManager) context
				.getSystemService(Context.DEVICE_POLICY_SERVICE);
		dpm.removeActiveAdmin(devAdminReceiver);
		Intent intent = new Intent(Intent.ACTION_DELETE);
		intent.setData(Uri.parse("package:" + context.getPackageName()));
		context.startActivity(intent);
	}
}
