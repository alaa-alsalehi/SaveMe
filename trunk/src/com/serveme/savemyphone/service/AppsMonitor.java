package com.serveme.savemyphone.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.serveme.savemyphone.R;
import com.serveme.savemyphone.model.DBOperations;
import com.serveme.savemyphone.model.Launcher;
import com.serveme.savemyphone.view.UserActivity;

public class AppsMonitor extends Service {
	private static final int UPDATE_INTERVAL = 200;
	private Timer timer = new Timer();
	private ActivityManager am;
	private DBOperations db;
	// private ComponentName lastallowedapp;
	int counter = 1;
	private Handler handler;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		db = new DBOperations(this);
		am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
		handler = new Handler() {
			Toast toast = Toast.makeText(AppsMonitor.this, R.string.prevent_message,
					Toast.LENGTH_LONG);
			@Override
			public void handleMessage(Message msg) {
				toast.show();
			}
		};
		doGetRunningApp();
		return START_STICKY; // continue running until it is explicitly stopped,
								// so return sticky
	}

	private void doGetRunningApp() {
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {

				List<ActivityManager.RunningTaskInfo> taskInfo = am
						.getRunningTasks(2);
				ComponentName componentInfo = taskInfo.get(0).topActivity;
				Launcher launcher = new Launcher(
						componentInfo.getPackageName(), null);
				// Log.d("test", taskInfo.get(1).baseActivity.toString());
				// Log.d("activity", taskInfo.get(1).topActivity.toString());
				if (!db.getWhiteListPackages().contains(launcher)
						&& !componentInfo.getPackageName().equals("android")
						&& !componentInfo.getClassName().equals(
								"com.serveme.savemyphone.view.UserActivity")
						&& !componentInfo
								.getClassName()
								.equals("group.pals.android.lib.ui.lockpattern.LockPatternActivity")) {
					// ActivityManager manager =
					// (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
					// List<RunningAppProcessInfo> services =
					// manager.getRunningAppProcesses();
					// for(RunningAppProcessInfo rpi : services){
					// if(rpi.processName.startsWith(componentInfo.getPackageName())){
					// android.os.Process.killProcess(rpi.pid);
					// }
					// }
					// am.killBackgroundProcesses(componentInfo.getPackageName());

					if (db.getWhiteListPackages().contains(launcher)) {
						// Intent intent = new Intent(Intent.ACTION_MAIN);
						// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
						// Intent.FLAG_ACTIVITY_NEW_TASK);
						// intent.setComponent(new
						// ComponentName(lastallowedapp.getPackageName(),lastallowedapp.getClassName()));
						// startActivity(intent);

						Intent saveintent = AppsMonitor.this
								.getPackageManager().getLaunchIntentForPackage(
										taskInfo.get(0).baseActivity
												.getPackageName());
						saveintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
								| Intent.FLAG_ACTIVITY_NEW_TASK);
						getApplication().startActivity(saveintent);
					} else {
						Intent saveintent = new Intent(getBaseContext(),
								UserActivity.class);
						saveintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						getApplication().startActivity(saveintent);
						//Log.d("test", "test");
						handler.sendEmptyMessage(0);
					}
					//
				} else if (!componentInfo.getPackageName().equals("android")) {
					// lastallowedapp = taskInfo.get(0).topActivity;
				} else {
					handler.removeMessages(0);
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