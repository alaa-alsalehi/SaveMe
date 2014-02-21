package com.serveme.savemyphone.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.serveme.savemyphone.model.DBOperations;

public class RemovePackageReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		DBOperations dbOperations = DBOperations.getInstance(context);
		Uri data = intent.getData();
		if (data != null) {
			String packageName = data.getEncodedSchemeSpecificPart();
			dbOperations.deleteApp(packageName);
			dbOperations.reCreateWhiteList();
		}
	}

}
