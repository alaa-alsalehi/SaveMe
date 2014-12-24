package com.serveme.savemyphone.service;

import java.util.Iterator;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;

public class ServiceUtils {

	private Context mContext;
	private ActivityManager mActivityManager;

	public ServiceUtils(Context context, ActivityManager activityManager) {
		this.mContext = context;
		this.mActivityManager = activityManager;
	}

	public boolean isRunningService(String processname) {
		if (processname == null || processname.isEmpty()) {
			return false;
		}

		RunningServiceInfo service;

		if (mActivityManager == null) {
			mActivityManager = (ActivityManager) mContext
					.getSystemService(Context.ACTIVITY_SERVICE);
		}

		List<RunningServiceInfo> list = mActivityManager
				.getRunningServices(Integer.MAX_VALUE);
		Iterator<RunningServiceInfo> i = list.iterator();
		while (i.hasNext()) {
			service = i.next();
			if (service.process.equals(processname))
				return true;
		}

		return false;
	}

	public ComponentName getActivityForApp(RunningAppProcessInfo target) {
		ComponentName result = null;
		ActivityManager.RunningTaskInfo info;

		if (target == null)
			return null;

		if (mActivityManager == null) {
			mActivityManager = (ActivityManager) mContext
					.getSystemService(Context.ACTIVITY_SERVICE);
		}

		List<ActivityManager.RunningTaskInfo> list = mActivityManager
				.getRunningTasks(9999);
		Iterator<ActivityManager.RunningTaskInfo> i = list.iterator();

		while (i.hasNext()) {
			info = i.next();
			if (info.baseActivity.getPackageName().equals(target.processName)) {
				result = info.topActivity;
				break;
			}
		}

		return result;
	}
	

}
