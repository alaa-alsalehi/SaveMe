package com.serveme.savemyphone.service;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.serveme.analytics.AnalyticsExceptionParser;
import com.serveme.savemyphone.R;
import com.serveme.savemyphone.model.DBOperations;
import com.serveme.savemyphone.model.Launcher;
import com.serveme.savemyphone.view.UserActivity;
import com.serveme.savemyphone.view.utils.AlertUtility;

public class AppsMonitor extends Service {
	private static final int UPDATE_INTERVAL = 200;
	private Timer timer = new Timer();
	private ActivityManager am;
	private DBOperations db;
	// private ComponentName lastallowedapp;
	int counter = 1;
	private Handler handler;
	private View view;

	private enum MobileState {
		START_APP, ALLOW_APP, UNALLOW_APP, ANDROID, USER_ACTIVITY, UNALLOW_APP_STARTED_BY_ALLOW_APP, START_ALERT_MESSAGE, END_ALERT_MESSAGE
	}

	private volatile MobileState currentState;
	private volatile MobileState previousState;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@SuppressLint("HandlerLeak")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		currentState = MobileState.USER_ACTIVITY;
		previousState = MobileState.START_APP;
		db = DBOperations.getInstance(this);
		am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
		// view = LayoutInflater.from(AppsMonitor.this).inflate(
		// R.layout.password_request, null);
		final WindowManager.LayoutParams param = AlertUtility.getParam();
		view = AlertUtility.getView(AppsMonitor.this);
		handler = new Handler() {
			Toast toast = Toast.makeText(AppsMonitor.this,
					R.string.prevent_message, Toast.LENGTH_LONG);

			@Override
			public void handleMessage(Message msg) {
				final WindowManager wmgr = (WindowManager) getApplicationContext()
						.getSystemService(Context.WINDOW_SERVICE);
				if (msg.what == 0) {
					synchronized (view) {
						if (currentState == MobileState.UNALLOW_APP) {
							try {
								wmgr.addView(view, param);
							} catch (Exception e) {
								Tracker tracker = EasyTracker
										.getInstance(AppsMonitor.this);
								tracker.send(MapBuilder
										.createException(
												new AnalyticsExceptionParser()
														.getDescription(
																Thread.currentThread()
																		.toString()
																		+ " "
																		+ previousState
																		+ " "
																		+ currentState,
																e), false)
										.build());
							}
							toast.show();
							setCurrentState(MobileState.START_ALERT_MESSAGE);
						}
					}
				} else if (msg.what == 1) {
					synchronized (view) {
						if (currentState == MobileState.START_ALERT_MESSAGE) {
							try {
								setCurrentState(MobileState.END_ALERT_MESSAGE);
								wmgr.removeView(view);
							} catch (Exception e) {
								Tracker tracker = EasyTracker
										.getInstance(AppsMonitor.this);
								tracker.send(MapBuilder
										.createException(
												new AnalyticsExceptionParser()
														.getDescription(
																Thread.currentThread()
																		.toString()
																		+ " "
																		+ previousState
																		+ " "
																		+ currentState,
																e), false)
										.build());
							}
						}
					}
				}

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
				Log.d("state", previousState + " " + currentState.toString()
						+ " " + componentInfo.getClassName());
				// Log.d("test", taskInfo.get(1).baseActivity.toString());
				// Log.d("activity", taskInfo.get(1).topActivity.toString());
				if (!db.getWhiteListPackages().contains(launcher)
						&& !componentInfo.getPackageName().equals("android")
						&& !componentInfo.getClassName().equals(
								"com.serveme.savemyphone.view.UserActivity")
						&& !componentInfo
								.getClassName()
								.equals("com.serveme.savemyphone.view.RecoveryActivity")
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
					/*
					 * componentInfo = taskInfo.get(0).baseActivity; launcher =
					 * new Launcher( componentInfo.getPackageName(), null);
					 */
					componentInfo = taskInfo.get(0).baseActivity;// «·»—‰«„Ã
																	// «·√’·Ì
					launcher = new Launcher(componentInfo.getPackageName(),
							null);
					if (db.getWhiteListPackages().contains(launcher)) {
						// Intent intent = new Intent(Intent.ACTION_MAIN);
						// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
						// Intent.FLAG_ACTIVITY_NEW_TASK);
						// intent.setComponent(new
						// ComponentName(lastallowedapp.getPackageName(),lastallowedapp.getClassName()));
						// startActivity(intent);
						// ≈–« ﬂ«‰ √”«” «·⁄„·Ì… »—‰«„Ã „”„ÊÕ ›ÌÂ ÌÃ» «·⁄Êœ… ≈·Ï
						// «·»—‰«„Ã «·√’·Ì

						synchronized (view) {
							if (currentState == MobileState.START_ALERT_MESSAGE) {

							} else if (currentState != MobileState.UNALLOW_APP_STARTED_BY_ALLOW_APP) {
								handler.sendEmptyMessage(0);
								setCurrentState(MobileState.UNALLOW_APP_STARTED_BY_ALLOW_APP);
							}
						}
					} else {
						// Log.d("test", "test");
						synchronized (view) {
							if (currentState == MobileState.START_ALERT_MESSAGE) {
								// ≈–« ⁄‰« —”«·…  Õ–Ì— ‘€«·… „« »·⁄» ≈ÿ·«ﬁ«
							} else if (currentState != MobileState.UNALLOW_APP) {
								setCurrentState(MobileState.UNALLOW_APP);
								handler.sendEmptyMessage(0);
							}
						}
					}
					//
				} else if (componentInfo.getPackageName().equals("android")) {
					// lastallowedapp = taskInfo.get(0).topActivity;
					synchronized (view) {
						if (currentState != MobileState.START_ALERT_MESSAGE) {
							setCurrentState(MobileState.ANDROID);
						} else {
							handler.sendEmptyMessage(1);
						}
					}
				} else if (componentInfo.getClassName().equals(
						"com.serveme.savemyphone.view.UserActivity")) {
					synchronized (view) {
						if (currentState == MobileState.START_ALERT_MESSAGE) {
							handler.sendEmptyMessage(1);
						} else {
							setCurrentState(MobileState.USER_ACTIVITY);
						}
					}
				} else {
					synchronized (view) {
						if (currentState != MobileState.START_ALERT_MESSAGE) {
							setCurrentState(MobileState.ALLOW_APP);
						} else {
							handler.sendEmptyMessage(1);
						}
					}
				}

			}
		}, 1, UPDATE_INTERVAL);
	}

	private void setCurrentState(MobileState newState) {
		previousState = currentState;
		currentState = newState;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (timer != null) {
			timer.cancel();
		}
		sendBroadcast(new Intent("finish_user_activity"));
		handler.removeMessages(0);
		handler.removeMessages(1);
		// ≈“«·… «·‘«‘… «·„”«⁄œ… ·÷„«‰ ⁄œ„ ÊÃÊœ „‘«ﬂ·
		final WindowManager wmgr = (WindowManager) getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE);
		try {
			if (view != null)
				wmgr.removeView(view);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}