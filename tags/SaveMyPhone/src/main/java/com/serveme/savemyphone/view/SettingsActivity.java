package com.serveme.savemyphone.view;

import com.haibison.android.lockpattern.LockPatternActivity;
import com.serveme.savemyphone.paid.R;
import com.serveme.savemyphone.preferences.PrefEditor;
import com.serveme.savemyphone.receivers.AdminReciver;
import com.serveme.savemyphone.util.MyTracker;
import com.serveme.savemyphone.util.Utility;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;

public class SettingsActivity extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {

	private static final int REQ_CHANGE_PATTERN = 1;
	private static final int REQ_CHANGE_BACKGROUND = 2;
	private static final int REQ_CHANGE_LOCK_PATTERN = 3;
	private static final String KEY_LOCK_METHOD = "lock_method";
	private PrefEditor pe;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		// ListPreference lock_Preference = (ListPreference)
		// findPreference(KEY_LOCK_METHOD);
		// lock_Preference.setSummary(lock_Preference.getEntry());
		pe = new PrefEditor(SettingsActivity.this);
		Preference changePatternPreference = (Preference) findPreference("change_pattern");
		CheckBoxPreference stealthModePreference = (CheckBoxPreference) findPreference("stealth_mode");
		Preference uninstallPref = (Preference) findPreference("uninstall");
		changePatternPreference
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						Intent intent = new Intent(
								LockPatternActivity.ACTION_CREATE_PATTERN,
								null, SettingsActivity.this,
								LockPatternActivity.class);
						startActivityForResult(intent, REQ_CHANGE_PATTERN);
						MyTracker.fireButtonPressedEvent(SettingsActivity.this,
								"request_change_pattern");
						return true;
					}
				});

		stealthModePreference
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						CheckBoxPreference cbPreference = (CheckBoxPreference) preference;
						if (cbPreference.isChecked()) {
							pe.setStealthMode(false);
							cbPreference.setChecked(false);
							return false;
						} else {
							pe.setStealthMode(true);
							cbPreference.setChecked(true);
							return true;
						}
					}
				});
		CheckBoxPreference hideLockPreference = (CheckBoxPreference) findPreference("hide_lock");
		SharedPreferences preferences = getSharedPreferences("mypref",
				Context.MODE_PRIVATE);
		if (preferences.getBoolean("hidden_lock_active", false)) {
			hideLockPreference.setChecked(true);
		} else {
			hideLockPreference.setEnabled(false);
		}
		hideLockPreference
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						if (Utility.isPaid(SettingsActivity.this)) {
							CheckBoxPreference cbPreference = (CheckBoxPreference) preference;
							if (cbPreference.isChecked()) {
								SharedPreferences preferences = getSharedPreferences(
										"mypref", Context.MODE_PRIVATE);
								Editor edit = preferences.edit();
								edit.putBoolean("hidden_lock_active", false);
								edit.apply();
								cbPreference.setChecked(false);
								return false;
							} else {
								SharedPreferences preferences = getSharedPreferences(
										"mypref", Context.MODE_PRIVATE);
								Editor edit = preferences.edit();
								edit.putBoolean("hidden_lock_active", true);
								edit.apply();
								cbPreference.setChecked(true);
								return true;
							}
						} else {
							Utility.buyFull(SettingsActivity.this);
							return false;
						}
					}
				});
		CheckBoxPreference fastLockPreference = (CheckBoxPreference) findPreference("fast_lock");

		/*
		 * if (preferences.getBoolean("hidden_lock_active", false)) {
		 * hideLockPreference.setChecked(true); } else {
		 * hideLockPreference.setEnabled(false); }
		 */
		fastLockPreference
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						Log.d("change", newValue + "");
						CheckBoxPreference cbPreference = (CheckBoxPreference) preference;
						if ((Boolean) newValue) {
							getPackageManager()
									.setComponentEnabledSetting(
											new ComponentName(
													SettingsActivity.this,
													UserLauncherActivity.class),
											PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
											PackageManager.DONT_KILL_APP);
							cbPreference.setChecked(true);
							return true;
						} else {

							getPackageManager()
									.setComponentEnabledSetting(
											new ComponentName(
													SettingsActivity.this,
													UserLauncherActivity.class),
											PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
											PackageManager.DONT_KILL_APP);
							cbPreference.setChecked(false);
							return false;
						}
					}
				});
		Preference changeLockPatternPreference = (Preference) findPreference("change_lock_pattern");
		changeLockPatternPreference
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						if (Utility.isPaid(SettingsActivity.this)) {
							Intent intent = new Intent(SettingsActivity.this,
									UnlockSettingsActivity.class);
							startActivityForResult(intent,
									REQ_CHANGE_LOCK_PATTERN);
							MyTracker.fireButtonPressedEvent(
									SettingsActivity.this,
									"change_lock_pattern");
						} else {
							Utility.buyFull(SettingsActivity.this);
						}
						return true;
					}
				});

		uninstallPref
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					public boolean onPreferenceClick(Preference preference) {
						Utility.uninstallSaveMe(SettingsActivity.this);
						return true;
					}
				});

		Preference changeBackgroundPreference = (Preference) findPreference("change_background");
		changeBackgroundPreference
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						Intent intent = new Intent(SettingsActivity.this,
								BackGroundActivity.class);
						startActivityForResult(intent, REQ_CHANGE_BACKGROUND);
						MyTracker.fireButtonPressedEvent(SettingsActivity.this,
								"request_change_pattern");
						return true;
					}
				});
		Preference controlNotificationsPreference = (Preference) findPreference("control_notifications");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
			controlNotificationsPreference
					.setOnPreferenceClickListener(new OnPreferenceClickListener() {

						@Override
						public boolean onPreferenceClick(Preference preference) {
							startActivity(new Intent(
									"android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
							return true;
						}
					});
		} else {
			Preference controlNotificationsPreferenceCategoty = (Preference) findPreference("notifications_category");
			getPreferenceScreen().removePreference(
					controlNotificationsPreferenceCategoty);
			controlNotificationsPreference.setEnabled(false);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		MyTracker.fireActivityStartEvent(SettingsActivity.this);
		MyTracker.setUncaughtExceptionHandler(this);
	}

	@Override
	protected void onStop() {
		MyTracker.fireActivityStopevent(SettingsActivity.this);
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
				char[] passCode = data
						.getCharArrayExtra(LockPatternActivity.EXTRA_PATTERN);
				SharedPreferences preferences = getSharedPreferences("mypref",
						Context.MODE_PRIVATE);
				Editor edit = preferences.edit();
				edit.putString("saved_pattern", String.copyValueOf(passCode));
				edit.apply();
				MyTracker.fireButtonPressedEvent(SettingsActivity.this,
						"change_pattern_done");
			}
			break;
		case REQ_CHANGE_BACKGROUND:
			if (resultCode == Activity.RESULT_OK) {
				String background = data.getStringExtra("background");
				SharedPreferences preferences = getSharedPreferences("mypref",
						Context.MODE_PRIVATE);
				Editor edit = preferences.edit();
				edit.putString("background", background);
				edit.apply();
			}
			break;
		case REQ_CHANGE_LOCK_PATTERN:
			if (resultCode == Activity.RESULT_OK) {
				CheckBoxPreference hideLockPreference = (CheckBoxPreference) findPreference("hide_lock");
				// OnPreferenceChangeListener changelistener =
				// hideLockPreference.getOnPreferenceChangeListener();
				// hideLockPreference.setOnPreferenceChangeListener(null);
				hideLockPreference.setEnabled(true);
				hideLockPreference.setChecked(true);

			}
		}
	}
}
