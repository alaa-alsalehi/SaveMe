package com.serveme.savemyphone.control;

import com.serveme.savemyphone.model.DBOperations;
import com.serveme.savemyphone.model.Launcher;
import com.serveme.savemyphone.preferences.PrefEditor;

import android.annotation.TargetApi;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationObserver extends NotificationListenerService {
	
    @Override
	public void onNotificationPosted(StatusBarNotification statusBarNotification) {
		DBOperations db = DBOperations.getInstance(this);
		Launcher launcher = new Launcher(
				statusBarNotification.getPackageName(), null);
		if (new PrefEditor(this).getStatus() == 1) {
			if (!db.getWhiteListPackages().contains(launcher)) {
				cancelNotification(statusBarNotification.getPackageName(),
						statusBarNotification.getTag(),
						statusBarNotification.getId());
			}
		}
	}

	@Override
	public void onNotificationRemoved(
			StatusBarNotification statusBarNotification) {
		// TODO Auto-generated method stub

	}

}
