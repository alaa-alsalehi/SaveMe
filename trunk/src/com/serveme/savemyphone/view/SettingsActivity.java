package com.serveme.savemyphone.view;

import com.serveme.savemyphone.R;
import com.serveme.savemyphone.receivers.AdminReciver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {

	public static final String KEY_LOCK_METHOD = "lock_method";

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		ListPreference lock_Preference = (ListPreference) findPreference(KEY_LOCK_METHOD);
		lock_Preference.setSummary(lock_Preference.getEntry());
		Preference myPref = (Preference) findPreference("uninstall");
		myPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
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
}
