package com.serveme.savemyphone;

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

public class AppsMonitor extends Service {
	int counter = 1;
	static final int UPDATE_INTERVAL = 1000;
	private Timer timer = new Timer();
	ActivityManager am;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
		am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
		doGetRunningApp();
		return START_STICKY;
	}

	private void doGetRunningApp() {
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {

				// get the info from the currently running task
				List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
				ComponentName componentInfo = taskInfo.get(0).topActivity;
				if (!componentInfo.getPackageName().equals("com.serveme.savemyphone")) {
					Intent saveintent = new Intent(getBaseContext(), MainActivity.class);
					saveintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					getApplication().startActivity(saveintent);
				}
				Log.v("MyService", String.valueOf(++counter));
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
	}
}