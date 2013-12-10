package com.serveme.savemyphone.view;

//import org.omar.android.lib.ui.passwordlock.PasswordEntryActivity;

import group.pals.android.lib.ui.lockpattern.LockPatternActivity;

import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.ExceptionReporter;
import com.google.analytics.tracking.android.MapBuilder;
import com.serveme.ads.AdMobListener;
import com.serveme.analytics.AnalyticsExceptionParser;
import com.serveme.savemyphone.R;
import com.serveme.savemyphone.control.AppsListAdapter;
import com.serveme.savemyphone.preferences.PrefEditor;
import com.serveme.savemyphone.receivers.AdminReciver;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class AdminActivity extends ActionBarActivity {

	private DevicePolicyManager devicePolicyManager;
	private ComponentName adminComponent;
	private static final int REQ_ENTER_PATTERN = 2;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Calling this to ensures that your application is properly initialized
		// with default settings
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

		checkAdminAccess();

		checkPassCode();

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		AppsListAdapter adapter = new AppsListAdapter(this);
		ListView listView = (ListView) findViewById(R.id.app_list);
		listView.setAdapter(adapter);
		adsStuff();
	}

	private void adsStuff() {
		final AdView adView = (AdView) findViewById(R.id.adView);
		// Create the adView

		adView.setGravity(Gravity.CENTER);
		adView.setAdListener(new AdMobListener(this));
		// Initiate a generic request to load it with an ad
		final AdRequest adRequest = new AdRequest();
		adRequest.addTestDevice(AdRequest.TEST_EMULATOR);
		// adRequest.addTestDevice("8E7864D6D7911778659788D0B39F99E8");
		Thread thread = new Thread(new Runnable() {

			public void run() {
				Looper.prepare();
				adView.loadAd(adRequest);

			}
		});
		thread.start();
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
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.admin, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_lock:
			PrefEditor pe = new PrefEditor(AdminActivity.this);
			pe.updateStatus(1);
			Intent saveintent = new Intent(getBaseContext(), UserActivity.class);
			startActivity(saveintent);
			EasyTracker.getInstance(this).send(
					MapBuilder.createEvent("ui_action", "button_press", "lock",
							Long.valueOf(1)).build());
			/*
			 * Intent saveintent = new
			 * Intent(getBaseContext(),PasswordEntryActivity.class);
			 * startActivity(saveintent); context.startService(new
			 * Intent(context, AppsMonitor.class)); finish();
			 */
			return true;
		case R.id.action_settings:
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			EasyTracker.getInstance(this).send(
					MapBuilder.createEvent("ui_action", "button_press",
							"settings", Long.valueOf(1)).build());
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void checkAdminAccess() {
		devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		adminComponent = new ComponentName(AdminActivity.this,
				AdminReciver.class);
		if (!devicePolicyManager.isAdminActive(adminComponent)) {
			finish();
			Intent intent = new Intent(getBaseContext(), AdminRequest.class);
			startActivity(intent);
		}
	}

	private void checkPassCode() {
		if (devicePolicyManager.isAdminActive(adminComponent)) {
			SharedPreferences mySharedPreferences = getSharedPreferences(
					"mypref", Context.MODE_PRIVATE);
			if (mySharedPreferences != null
					&& mySharedPreferences.contains("pass_code")) {
				if (getIntent() != null) {
					if (!getIntent().getBooleanExtra("first_time", false)) {
						// if(pattern){
						new Checker(this).checkPattern(REQ_ENTER_PATTERN);
						// }else{

						// }
					}
				} else {
					new Checker(this).checkPattern(REQ_ENTER_PATTERN);
				}
			} else {
				// finish();
				PasswordRequester.requestPatternPassword(this);
				/*
				 * Intent intent = new
				 * Intent(getBaseContext(),PasswordRequest.class);
				 * startActivity(intent);
				 */
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQ_ENTER_PATTERN: {
			switch (resultCode) {
			case RESULT_OK:
				Log.v("result", "passed");
				break;
			case RESULT_CANCELED:
				finish();
				break;
			case LockPatternActivity.RESULT_FAILED:
				finish();
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
		PasswordRequester.onPatternPasswordRecived(requestCode, resultCode,
				data, this);
	}

}
