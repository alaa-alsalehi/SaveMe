package com.serveme.savemyphone.view;

//import org.omar.android.lib.ui.passwordlock.PasswordEntryActivity;

import org.codechimp.apprater.AppRater;
import org.codechimp.apprater.InCorrectMarketException;

import group.pals.android.lib.ui.lockpattern.LockPatternActivity;

import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.ExceptionReporter;
import com.serveme.ads.AdMobListener;
import com.serveme.analytics.AnalyticsExceptionParser;
import com.serveme.savemyphone.R;
import com.serveme.savemyphone.control.AppsListAdapter;
import com.serveme.savemyphone.model.DBOperations;
import com.serveme.savemyphone.preferences.PrefEditor;
import com.serveme.savemyphone.receivers.AdminReciver;
import com.serveme.savemyphone.util.ConverterUtil;
import com.serveme.savemyphone.util.MyTracker;
import com.serveme.savemyphone.view.wizard.AdminRequest;
import com.serveme.savemyphone.view.wizard.HelpActivity;
import com.serveme.savemyphone.view.wizard.PasswordRequest;

import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

	private DevicePolicyManager devicePolicyManager;
	private ComponentName adminComponent;
	private static final int REQ_ENTER_PATTERN = 2;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Calling this to ensures that your application is properly initialized with default settings
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		if (savedInstanceState == null) {
			checkAdminAccess();
			checkPassCode();
		}
		setContentView(R.layout.main_activity);
		PrefEditor pe = new PrefEditor(MainActivity.this);
		Log.v("sdcard flag", pe.isSDCardMounted()+ "");
		if (pe.isSDCardMounted()) {
			DBOperations.sdcard_mounted = true;
		} else {
			DBOperations.sdcard_mounted = false;
		}

		AppsListAdapter adapter = new AppsListAdapter(this);
		ListView listView = (ListView) findViewById(R.id.app_list);
		LinearLayout headerLayout = createListHeader();
		listView.addHeaderView(headerLayout);
		listView.setAdapter(adapter);
		adsStuff();

		try {
			AppRater.app_launched(this);
		} catch (InCorrectMarketException e) {

		}
	}

	protected LinearLayout createListHeader() {
		TextView header = new TextView(this);
//		header.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getDimension(R.dimen.apps_names));
		header.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
		header.setText(R.string.admin_list_header);
		LinearLayout headerLayout = new LinearLayout(this);
		headerLayout.setBackgroundColor(getResources().getColor(R.color.listview_header));
//		int padding = (int) ConverterUtil.convertDpToPixel(15, this);
		int padding = (int) TypedValue.applyDimension (TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics());
		headerLayout.setPadding(padding, padding, padding, padding);
		headerLayout.addView(header);
		return headerLayout;
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
	public void onBackPressed() {
		DBOperations db = DBOperations.getInstance(this);
		boolean activecount = db.isThereEnabledApps();
		if (activecount) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.activiate_lock_quesition);
			builder.setPositiveButton(
					getResources().getString(android.R.string.yes),
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							lock();
							MyTracker.fireButtonPressedEvent(MainActivity.this, "Ok_lock_dialog");
							finish();
						}
					});
			builder.setNegativeButton(
					getResources().getString(android.R.string.no),
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							MyTracker.fireButtonPressedEvent(MainActivity.this, "Cancel_lock_dialog");
							finish();
						}
					});
			builder.show();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		MyTracker.fireActivityStartEvent(MainActivity.this);
		Thread.UncaughtExceptionHandler uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		if (uncaughtExceptionHandler instanceof ExceptionReporter) {
			ExceptionReporter exceptionReporter = (ExceptionReporter) uncaughtExceptionHandler;
			exceptionReporter.setExceptionParser(new AnalyticsExceptionParser());
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
			lock();
			MyTracker.fireButtonPressedEvent(MainActivity.this, "lock");
			finish();
			/*
			 * Intent saveintent = new
			 * Intent(getBaseContext(),PasswordEntryActivity.class);
			 * startActivity(saveintent); context.startService(new
			 * Intent(context, AppsMonitor.class)); finish();
			 */
			return true;
		case R.id.action_settings:
			Intent settingIntent = new Intent(this, SettingsActivity.class);
			startActivity(settingIntent);
			MyTracker.fireButtonPressedEvent(MainActivity.this, "settings");
			return true;
		case R.id.action_help:
			Intent helpIntent = new Intent(this, HelpActivity.class);
			startActivity(helpIntent);
			MyTracker.fireButtonPressedEvent(MainActivity.this, "help");
			return true;			
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	protected void lock() {
		PrefEditor pe = new PrefEditor(MainActivity.this);
		pe.updateStatus(1);
		Intent saveintent = new Intent(MainActivity.this, UserActivity.class);
		startActivity(saveintent);
	}

	private void checkAdminAccess() {
		devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		adminComponent = new ComponentName(MainActivity.this, AdminReciver.class);
		if (!devicePolicyManager.isAdminActive(adminComponent)) {
			finish();
			Intent intent = new Intent(this, AdminRequest.class);
			startActivity(intent);
		}
	}

	private void checkPassCode() {
		if (devicePolicyManager.isAdminActive(adminComponent)) {
			SharedPreferences mySharedPreferences = getSharedPreferences("mypref", Context.MODE_PRIVATE);
			if (mySharedPreferences != null	&& mySharedPreferences.contains("saved_pattern")) {
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
				finish();
				Intent intent = new Intent(MainActivity.this, PasswordRequest.class);
				startActivity(intent);
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
			// int retryCount =
			// data.getIntExtra(LockPatternActivity.EXTRA_RETRY_COUNT, 0);

			break;
		}// REQ_ENTER_PATTERN
		}
	}

}
