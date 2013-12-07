package com.serveme.savemyphone.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.serveme.savemyphone.model.DBOperations;
import com.serveme.savemyphone.model.Launcher;
import com.serveme.savemyphone.view.UserActivity;

public class AppsMonitor extends Service {
	private static final int UPDATE_INTERVAL = 200;
	private Timer timer = new Timer();
	private ActivityManager am;
	private DBOperations db;
//	private ComponentName lastallowedapp;
	int counter = 1;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		db = new DBOperations(this);
		am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
		doGetRunningApp();
		return START_STICKY; // continue running until it is explicitly stopped, so return sticky
	}

	private void doGetRunningApp() {
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
				ComponentName componentInfo = taskInfo.get(0).topActivity;
				Launcher launcher = new Launcher(componentInfo.getPackageName(), componentInfo.getClassName());
				if (!db.getWhiteListApps().contains(launcher)
						&& !componentInfo.getPackageName().equals("android")
						&& !componentInfo.getClassName().equals("com.serveme.savemyphone.view.UserActivity")
						&& !componentInfo.getClassName().equals("group.pals.android.lib.ui.lockpattern.LockPatternActivity")) {
//					 ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
//					 List<RunningAppProcessInfo> services = manager.getRunningAppProcesses();
//					 for(RunningAppProcessInfo rpi : services){
//						 if(rpi.processName.startsWith(componentInfo.getPackageName())){
//								android.os.Process.killProcess(rpi.pid);
//						 }
//					 }
//					am.killBackgroundProcesses(componentInfo.getPackageName());
					if (db.getWhiteListApps().contains(taskInfo.get(0).baseActivity.getPackageName())) {
						// Intent intent = new Intent(Intent.ACTION_MAIN);
						// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
						// intent.setComponent(new ComponentName(lastallowedapp.getPackageName(),lastallowedapp.getClassName()));
						// startActivity(intent);

						Intent saveintent = AppsMonitor.this.getPackageManager().getLaunchIntentForPackage(taskInfo.get(0).baseActivity.getPackageName());
						saveintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
						getApplication().startActivity(saveintent);
					} else {
						Intent saveintent = new Intent(getBaseContext(),UserActivity.class);
						saveintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						getApplication().startActivity(saveintent);
					}
					//
				} else if (!componentInfo.getPackageName().equals("android")) {
//					lastallowedapp = taskInfo.get(0).topActivity;
				}

			}
		}, 1, UPDATE_INTERVAL);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (timer != null) {
			timer.cancel();
		}
		sendBroadcast(new Intent("finish_user_activity"));
	}
}