package com.serveme.savemyphone;

import service.AppsMonitor;
import view.AdminActivity;
import view.UserActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		AdminActivity.setContext(context);
		if(AdminActivity.getDBOperator().getStatus() == 1){
		Intent saveintent = new Intent(context, UserActivity.class);
		saveintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(saveintent);
		context.startService(new Intent(context, AppsMonitor.class));
		}
	}

}
