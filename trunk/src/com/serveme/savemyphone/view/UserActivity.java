package com.serveme.savemyphone.view;

import group.pals.android.lib.ui.lockpattern.LockPatternActivity;
import java.util.List;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.ExceptionReporter;
import com.google.analytics.tracking.android.MapBuilder;
import com.serveme.analytics.AnalyticsExceptionParser;
import com.serveme.savemyphone.R;
import com.serveme.savemyphone.control.GridAdapter;
import com.serveme.savemyphone.model.DBOperations;
import com.serveme.savemyphone.model.Launcher;
import com.serveme.savemyphone.preferences.PrefEditor;
import com.serveme.savemyphone.receivers.AdminReciver;
import com.serveme.savemyphone.service.AppsMonitor;
import android.app.WallpaperManager;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class UserActivity extends ActionBarActivity {

	final Context context = this;
	List<Launcher> appsinfolist;
	private DBOperations db;

	private static final int REQ_ENTER_PATTERN = 2;

	DevicePolicyManager devicePolicyManager;
	ComponentName adminComponent;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
		}
		// getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		// ActionBar actionBar = getActionBar();
		// actionBar.setBackgroundDrawable(new
		// ColorDrawable(Color.parseColor("#33ffffff")));
		// actionBar.setStackedBackgroundDrawable(new
		// ColorDrawable(Color.parseColor("#55ffffff")));
		// actionBar.setDisplayShowHomeEnabled(false);
		// actionBar.setDisplayShowTitleEnabled(false);

		setContentView(R.layout.user_activity);
		db = new DBOperations(this);
		appsinfolist = db.getWhiteListApps();
		registerReceiver(bcr, new IntentFilter("finish_user_activity"));
		startService(new Intent(this, AppsMonitor.class));

		GridView gridView = (GridView) findViewById(R.id.grid_view);
		gridView.setBackgroundDrawable(WallpaperManager.getInstance(context).getDrawable());
		gridView.setAdapter(new GridAdapter(this, appsinfolist));
		gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
		gridView.setNumColumns(GridView.AUTO_FIT);

		devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		adminComponent = new ComponentName(UserActivity.this,AdminReciver.class);

		// float scalefactor = getResources().getDisplayMetrics().density * 80;
		// Point size = new Point();
		// this.getWindowManager().getDefaultDisplay().getSize(size);
		// int screenWidth = size.x;
		// int columns = (int) ((float) screenWidth / (float) scalefactor);
		// gridView.setNumColumns(columns);

		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,int position, long id) {
				Launcher launcher = appsinfolist.get(position);
				Intent i = new Intent();
				i.setAction(Intent.ACTION_MAIN);
				i.addCategory(Intent.CATEGORY_LAUNCHER);
				i.setClassName(launcher.getPackageName(),launcher.getActivity());
				startActivity(i);
				EasyTracker.getInstance(context).send(
						MapBuilder.createEvent("ui_action", "button_press",
								"run_app", Long.valueOf(1))
								.build());
			}
		});

	}
	
		@Override
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
		Thread.UncaughtExceptionHandler uncaughtExceptionHandler = Thread
				.getDefaultUncaughtExceptionHandler();
		if (uncaughtExceptionHandler instanceof ExceptionReporter) {
			ExceptionReporter exceptionReporter = (ExceptionReporter) uncaughtExceptionHandler;
			exceptionReporter
					.setExceptionParser(new AnalyticsExceptionParser());
		}
	}
	
	@Override
	protected void onStop() {
		EasyTracker.getInstance(this).activityStop(this);
		super.onStop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(bcr);
	}

	@Override
	public void onBackPressed() {
		// your code.
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.user, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_unlock:
			new Checker(this).checkPattern(REQ_ENTER_PATTERN);
			EasyTracker.getInstance(context).send(
					MapBuilder.createEvent("ui_action", "button_press",
							"unlock", Long.valueOf(1))
							.build());
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQ_ENTER_PATTERN: {
			switch (resultCode) {
			case RESULT_OK:
//				Log.v("result", "passed");
				PrefEditor pe = new PrefEditor(UserActivity.this);
				pe.updateStatus(0);
				context.stopService(new Intent(context, AppsMonitor.class));
				break;
			case RESULT_CANCELED:
//				Log.v("result", "canceled");
				break;
			case LockPatternActivity.RESULT_FAILED:
//				Log.v("result", "faild");
				break;
			case LockPatternActivity.RESULT_FORGOT_PATTERN:
				// The user forgot the pattern and invoked your recovery Activity.
				break;
			}

			/*
			 * In any case, there's always a key EXTRA_RETRY_COUNT, which holds
			 * the number of tries that the user did.
			 */
			/*
			 * In any case, there's always a key EXTRA_RETRY_COUNT, which holds
			 * the number of tries that the user did.
			 */
			// int retryCount = data.getIntExtra(LockPatternActivity.EXTRA_RETRY_COUNT, 0);

			break;
		}// REQ_ENTER_PATTERN
		}
	}

	// @Override
	// public void onAttachedToWindow()
	// {
	// Log.i("TESTE", "onAttachedToWindow");
	// this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
	// super.onAttachedToWindow();
	// }

	private final BroadcastReceiver bcr = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			finish();
		}
	};

}
