package com.serveme.savemyphone.receivers;

import java.util.ArrayList;
import java.util.List;

import com.serveme.savemyphone.model.DBOperations;
import com.serveme.savemyphone.model.Launcher;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;


public class UpdatePackageReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		DBOperations dbOperations = DBOperations.getInstance(context);
		Uri data = intent.getData();
		if (data != null) {
			String packageName = data.getEncodedSchemeSpecificPart();
			int counter = dbOperations.getLaunchersCount(packageName);
			if (counter != 0) {
				Intent in = new Intent(Intent.ACTION_MAIN);
				in.addCategory(Intent.CATEGORY_LAUNCHER);
				in.setPackage(packageName);
				List<ResolveInfo> aList = context.getPackageManager().queryIntentActivities(in, 0);
				if (counter == aList.size()) {
					ArrayList<Launcher> newLaunchers = new ArrayList<Launcher>();
					for (ResolveInfo resolveInfo : aList) {
						Launcher launcher = new Launcher(packageName, resolveInfo.activityInfo.name);
						newLaunchers.add(launcher);
					}
					dbOperations.replaceApp(newLaunchers);
				} else {
					dbOperations.deleteApp(packageName);
				}
			}
		}
	}
}