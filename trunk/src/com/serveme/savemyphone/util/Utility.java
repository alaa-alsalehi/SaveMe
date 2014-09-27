package com.serveme.savemyphone.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import com.serveme.savemyphone.R;
import com.serveme.savemyphone.receivers.AdminReciver;
import com.serveme.util.PackageUtil;

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

	public static boolean isPaid(Context context) {
		return context.getPackageName().indexOf("paid") != -1;
	}

	public static void buyFull(final Activity context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(R.string.buy_quesition);
		builder.setPositiveButton(
				context.getResources().getString(android.R.string.yes),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						PackageUtil.openMyApp((Context) context,
								"com.serveme.savemyphone.paid");
						MyTracker.fireButtonPressedEvent(context,
								"Ok_buy_dialog");
					}
				});
		builder.setNegativeButton(
				context.getResources().getString(android.R.string.no),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						MyTracker.fireButtonPressedEvent(context,
								"Cancel_buy_dialog");
					}
				});
		builder.show();
	}
}
