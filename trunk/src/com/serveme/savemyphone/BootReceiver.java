package com.serveme.savemyphone;

import com.serveme.savemyphone.model.DBOperations;
import com.serveme.savemyphone.service.AppsMonitor;
import com.serveme.savemyphone.view.AdminActivity;
import com.serveme.savemyphone.view.UserActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (new DBOperations(context).getStatus() == 1) {
			if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)
					|| intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
				Intent saveintent = new Intent(context, UserActivity.class);
				saveintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(saveintent);
				context.startService(new Intent(context, AppsMonitor.class));
			}
			else {
			}
		}
	}

}
