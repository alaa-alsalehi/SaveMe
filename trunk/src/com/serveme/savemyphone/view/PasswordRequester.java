package com.serveme.savemyphone.view;

import group.pals.android.lib.ui.lockpattern.LockPatternActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.Toast;

public class PasswordRequester {

	public static final int REQ_CREATE_PATTERN = 1;

	public static void requestPatternPassword(Activity activity) {
		Intent intent = new Intent(LockPatternActivity.ACTION_CREATE_PATTERN,
				null, activity, LockPatternActivity.class);
		activity.startActivityForResult(intent, REQ_CREATE_PATTERN);
	}

	public static void onPatternPasswordRecived(int requestCode,
			int resultCode, Intent data, Activity activity) {
		switch (requestCode) {
		case REQ_CREATE_PATTERN:
			if (resultCode == Activity.RESULT_OK) {
				char[] passCode = data
						.getCharArrayExtra(LockPatternActivity.EXTRA_PATTERN);
				SharedPreferences preferences = activity.getSharedPreferences(
						"mypref", Context.MODE_PRIVATE);
				Editor edit = preferences.edit();
				edit.putString("pass_code", String.copyValueOf(passCode));
				edit.apply();
				Intent intent = new Intent(activity, AdminActivity.class);
				intent.putExtra("first_time", true);
				activity.startActivity(intent);
				activity.finish();
			} else {
				activity.finish();
			}
			break;

		}
	}

}
