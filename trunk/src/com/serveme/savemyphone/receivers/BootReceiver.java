package com.serveme.savemyphone.receivers;

import com.serveme.savemyphone.preferences.PrefEditor;
import com.serveme.savemyphone.service.AppsMonitor;
import com.serveme.savemyphone.view.UserActivity;
import com.serveme.savemyphone.view.utils.AlertUtility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.WindowManager;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (new PrefEditor(context).getStatus() == 1) {
			if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)
					|| intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
				context.startService(new Intent(context, AppsMonitor.class));
			} else {
			}
		}
	}

}
