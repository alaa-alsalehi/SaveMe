package com.serveme.savemyphone.view;

import group.pals.android.lib.ui.lockpattern.LockPatternActivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.serveme.savemyphone.R;
import com.serveme.savemyphone.control.GridAdapter;
import com.serveme.savemyphone.model.DBOperations;
import com.serveme.savemyphone.model.Launcher;
import com.serveme.savemyphone.preferences.PrefEditor;
import com.serveme.savemyphone.receivers.AdminReciver;
import com.serveme.savemyphone.service.AppsMonitor;
import com.serveme.savemyphone.util.MyTracker;

import android.app.admin.DevicePolicyManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class UserActivity extends ActionBarActivity {

	private static final int REQ_ENTER_PATTERN = 2;

	private final Context context = this;
	private List<Launcher> appsinfolist;
	private DBOperations db;
	private PrefEditor pe;
	private GridAdapter ga;
	private DevicePolicyManager devicePolicyManager;
	private ComponentName adminComponent;

	private GridView gridView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
		}
		setContentView(R.layout.user_activity);
		devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		adminComponent = new ComponentName(UserActivity.this,
				AdminReciver.class);
		db = DBOperations.getInstance(context);
		pe = new PrefEditor(UserActivity.this);
		appsinfolist = new ArrayList<Launcher>();

		if (pe.isSDCardMounted()) {
			DBOperations.sdcard_mounted = true;
		} else {
			DBOperations.sdcard_mounted = false;
		}

		gridView = (GridView) findViewById(R.id.grid_view);
		appsinfolist.addAll(db.getWhiteListApps());
		ga = new GridAdapter(this, appsinfolist);
		gridView.setAdapter(ga);
		gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
		gridView.setNumColumns(GridView.AUTO_FIT);

		registerReceiver(bcr, new IntentFilter("finish_user_activity"));
		registerReceiver(refresh_list, new IntentFilter("refresh_white_list"));

		startService(new Intent(this, AppsMonitor.class));

		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				try {
					Launcher launcher = appsinfolist.get(position);
					Intent i = new Intent();
					i.setAction(Intent.ACTION_MAIN);
					i.addCategory(Intent.CATEGORY_LAUNCHER);
					i.setClassName(launcher.getPackageName(),
							launcher.getActivity());
					startActivity(i);
					EasyTracker.getInstance(context).send(
							MapBuilder.createEvent("ui_action", "button_press",
									"run_app", Long.valueOf(1)).build());
				} catch (ActivityNotFoundException e) {
					Toast.makeText(context, "Application not Installed",
							Toast.LENGTH_LONG).show();
				}
			}
		});

	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		ga.notifyDataSetChanged();
	}

	@Override
	protected void onStart() {
		super.onStart();
		MyTracker.fireActivityStartEvent(UserActivity.this);
		MyTracker.getUncaughtExceptionHandler();
	}

	@Override
	protected void onStop() {
		MyTracker.fireActivityStopevent(UserActivity.this);
		super.onStop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(bcr);
		unregisterReceiver(refresh_list);
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
			MyTracker.fireButtonPressedEvent(UserActivity.this, "unlock");
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
				// Log.v("result", "passed");
				pe.updateStatus(0);
				context.stopService(new Intent(context, AppsMonitor.class));
				break;
			case RESULT_CANCELED:
				// Log.v("result", "canceled");
				break;
			case LockPatternActivity.RESULT_FAILED:
				// Log.v("result", "faild");
				break;
			case LockPatternActivity.RESULT_FORGOT_PATTERN:
				// The user forgot the pattern and invoked your recovery
				// Activity.
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
			// int retryCount =
			// data.getIntExtra(LockPatternActivity.EXTRA_RETRY_COUNT, 0);

			break;
		}// REQ_ENTER_PATTERN
		}
	}

	private final BroadcastReceiver bcr = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			finish();
		}
	};

	private final BroadcastReceiver refresh_list = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			appsinfolist.clear();
			appsinfolist.addAll(db.getWhiteListApps());
			ga.notifyDataSetInvalidated();
			Log.v("recived", "recived");
		}
	};

}
