package com.serveme.savemyphone.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.serveme.savemyphone.model.DBOperations;
import com.serveme.savemyphone.view.UserActivity;


public class AppsMonitor extends Service {
	int counter = 1;
	static final int UPDATE_INTERVAL = 250;
	private Timer timer = new Timer();
	ActivityManager am;
	private DBOperations db;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
		db = new DBOperations(this);
		am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
		doGetRunningApp();
		return START_STICKY;
	}

	private void doGetRunningApp() {
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				// get the info from the currently running task
//				List<ActivityManager.RecentTaskInfo> taskInfo = am.getRecentTasks(1, ActivityManager.RECENT_WITH_EXCLUDED );
//				ComponentName componentInfo = taskInfo.get(0).origActivity;
				List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
				ComponentName componentInfo = taskInfo.get(0).topActivity;
				if (!db.getWhiteListApps().contains(componentInfo.getPackageName())) {
					Intent saveintent = new Intent(getBaseContext(), UserActivity.class);
					saveintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					getApplication().startActivity(saveintent);
				}
				Log.v("", componentInfo.getPackageName());
			}
		}, 1, UPDATE_INTERVAL);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (timer != null) {
			timer.cancel();
		}
		Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
		
//		Intent home = new Intent(Intent.ACTION_MAIN);
//		home.addCategory(Intent.CATEGORY_HOME);
//		home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//      getApplication().startActivity(home);
		
//        Process.killProcess(android.os.Process.myPid());
		
//        System.exit(0);
	}
}