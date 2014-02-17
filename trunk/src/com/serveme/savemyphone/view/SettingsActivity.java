package com.serveme.savemyphone.view;

import group.pals.android.lib.ui.lockpattern.LockPatternActivity;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.ExceptionReporter;
import com.google.analytics.tracking.android.MapBuilder;
import com.serveme.analytics.AnalyticsExceptionParser;
import com.serveme.savemyphone.R;
import com.serveme.savemyphone.receivers.AdminReciver;
import com.serveme.savemyphone.util.MyTracker;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {

	public static final int REQ_CHANGE_PATTERN = 1;
	public static final String KEY_LOCK_METHOD = "lock_method";

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		//ListPreference lock_Preference = (ListPreference) findPreference(KEY_LOCK_METHOD);
		//lock_Preference.setSummary(lock_Preference.getEntry());
		Preference changePatternPreference = (Preference) findPreference("change_pattern");
		Preference uninstallPref = (Preference) findPreference("uninstall");
		changePatternPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(LockPatternActivity.ACTION_CREATE_PATTERN, null, SettingsActivity.this, LockPatternActivity.class);
				startActivityForResult(intent, REQ_CHANGE_PATTERN);
				MyTracker.fireButtonPressedEvent(SettingsActivity.this, "request_change_pattern");
				return true;
			}
		});
		uninstallPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				EasyTracker.getInstance(SettingsActivity.this).send(MapBuilder.createEvent("ui_action", "button_press",
								"uninstall", Long.valueOf(1))
								.build());
				Context context = SettingsActivity.this; 
				ComponentName devAdminReceiver = new ComponentName(context,	AdminReciver.class);
				DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
				dpm.removeActiveAdmin(devAdminReceiver);
				Intent intent = new Intent(Intent.ACTION_DELETE);
				intent.setData(Uri.parse("package:" + context.getPackageName()));
				context.startActivity(intent);
				return true;
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

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
		if (key.equals(KEY_LOCK_METHOD)) {
			@SuppressWarnings("deprecation")
			Preference lock_preference = findPreference(key);
			// Set summary to be the user-description for the selected value
			lock_preference.setSummary(sharedPreferences.getString(key, ""));

		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case REQ_CHANGE_PATTERN:
				if (resultCode == Activity.RESULT_OK) {
					char[] passCode = data.getCharArrayExtra(LockPatternActivity.EXTRA_PATTERN);
					SharedPreferences preferences = getSharedPreferences("mypref", Context.MODE_PRIVATE);
					Editor edit = preferences.edit();
					edit.putString("saved_pattern", String.copyValueOf(passCode)); 
					edit.apply();
					MyTracker.fireButtonPressedEvent(SettingsActivity.this, "change_pattern_done");
				} 
			break;

		}
	}
}
