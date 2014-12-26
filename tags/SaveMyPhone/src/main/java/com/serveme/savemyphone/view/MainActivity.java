package com.serveme.savemyphone.view;

//import org.omar.android.lib.ui.passwordlock.PasswordEntryActivity;

import org.codechimp.apprater.AppRater;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.haibison.android.lockpattern.LockPatternActivity;
import com.serveme.savemyphone.paid.R;
import com.serveme.savemyphone.control.AppsListAdapter;
import com.serveme.savemyphone.model.DBOperations;
import com.serveme.savemyphone.preferences.PrefEditor;
import com.serveme.savemyphone.service.AppsMonitor;
import com.serveme.savemyphone.util.MyTracker;
import com.serveme.savemyphone.util.Utility;
import com.serveme.savemyphone.view.utils.ActivitiesController;
import com.serveme.savemyphone.view.utils.AdMobListener;
import com.serveme.savemyphone.view.wizard.HelpActivity;

public class MainActivity extends ActionBarActivity {

	private ActivitiesController ac;
	private PrefEditor pe;
	private static final int REQ_ENTER_PATTERN = 2;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Calling this to ensures that your application is properly initialized
		// with default settings
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		ac = new ActivitiesController(MainActivity.this);
		pe = new PrefEditor(MainActivity.this);
		if (savedInstanceState == null) {
			ac.getActivitiesFlow();
		}
		setContentView(R.layout.main_activity);
		if (getIntent().getBooleanExtra("first_time", false)
				|| savedInstanceState != null) {
			intialize();
		}
	}

	protected void intialize() {
		ListView listView = (ListView) findViewById(R.id.app_list);
		if (listView.getHeaderViewsCount() == 0) {
			AppsListAdapter adapter = new AppsListAdapter(this);

			LinearLayout headerLayout = createListHeader();
			listView.addHeaderView(headerLayout);
			listView.setSmoothScrollbarEnabled(true);
			listView.setAdapter(adapter);
		}
		adsStuff();

		AppRater.app_launched(MainActivity.this);

	}

	protected LinearLayout createListHeader() {
		TextView header = new TextView(this);
		header.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
		header.setText(R.string.admin_list_header);
		String direction = getResources().getString(R.string.direction);
		if (direction.equals("right")) {
			header.setGravity(Gravity.RIGHT);
		} else {
			header.setGravity(Gravity.LEFT);
		}
		LinearLayout headerLayout = new LinearLayout(this);
		header.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		headerLayout.setBackgroundColor(getResources().getColor(
				R.color.listview_header));
		// int padding = (int) ConverterUtil.convertDpToPixel(15, this);
		int padding = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 15, getResources()
						.getDisplayMetrics());
		headerLayout.setPadding(padding, padding, padding, padding);
		headerLayout.addView(header);
		return headerLayout;
	}

	private void adsStuff() {
		final AdView adView = (AdView) findViewById(R.id.adView);
		// Create the adView
		// adView.setGravity(Gravity.CENTER);
		adView.setAdListener(new AdMobListener(this));
		// Initiate a generic request to load it with an ad
		final AdRequest adRequest = new AdRequest.Builder().addTestDevice(
				AdRequest.DEVICE_ID_EMULATOR).build();
		// adRequest.addTestDevice("8E7864D6D7911778659788D0B39F99E8");
		adView.loadAd(adRequest);
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
							MyTracker.fireButtonPressedEvent(MainActivity.this,
									"Ok_lock_dialog");
							finish();
						}
					});
			builder.setNegativeButton(
					getResources().getString(android.R.string.no),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							MyTracker.fireButtonPressedEvent(MainActivity.this,
									"Cancel_lock_dialog");
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
		MyTracker.setUncaughtExceptionHandler(this);
	}

	@Override
	protected void onStop() {
		MyTracker.fireActivityStopevent(MainActivity.this);
		super.onStop();
	}

	@Override
	protected void onPause() {
		final AdView adView = (AdView) findViewById(R.id.adView);
		adView.pause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		final AdView adView = (AdView) findViewById(R.id.adView);
		adView.resume();
	}

	@Override
	protected void onDestroy() {
		final AdView adView = (AdView) findViewById(R.id.adView);
		adView.destroy();
		super.onDestroy();
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
		case R.id.action_app_log:
			if (Utility.isPaid(this)) {
				Intent appLogIntent = new Intent(this, AppsLogActivity.class);
				startActivity(appLogIntent);
				MyTracker.fireButtonPressedEvent(MainActivity.this, "app_log");
			} else {
				Utility.buyFull(this);
			}
			return true;
		case R.id.action_session_log:
			if (Utility.isPaid(this)) {
				Intent sessionLogIntent = new Intent(this,
						SessionLogActivity.class);
				startActivity(sessionLogIntent);
				MyTracker.fireButtonPressedEvent(MainActivity.this, "app_log");
			} else {
				Utility.buyFull(this);
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	protected void lock() {
		pe.updateStatus(1);
		Intent saveintent = new Intent(MainActivity.this, AppsMonitor.class);
		startService(saveintent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQ_ENTER_PATTERN: {
			switch (resultCode) {
			case RESULT_OK:
				intialize();
				break;
			case RESULT_CANCELED:
				finish();
				break;
			case LockPatternActivity.RESULT_FAILED:
				finish();
				break;
			case LockPatternActivity.RESULT_FORGOT_PATTERN:
				finish();
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
