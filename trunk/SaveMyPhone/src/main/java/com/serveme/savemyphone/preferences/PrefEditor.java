package com.serveme.savemyphone.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PrefEditor {

	SharedPreferences preferences;

	public PrefEditor(Context context) {
		preferences = context.getSharedPreferences("mypref",
				Context.MODE_PRIVATE);
	}

	public boolean isNewUser() {
		if (preferences.contains("installed")) {
			return false;
		} else {
			Editor edit = preferences.edit();
			edit.putBoolean("installed", true);
			edit.apply();
			return true;
		}
	}

	public void updateStatus(int status) {

		Editor edit = preferences.edit();
		edit.putInt("status", status);
		edit.apply();
	}

	public boolean isLocked() {
		return preferences.getInt("status", -1) == 1;
		// 1 locked
		// 0 unlocked
		// -1 unknown
	}

	public void setSDCardMounted(boolean flag) {
		Editor edit = preferences.edit();
		edit.putBoolean("sdcardFlag", flag);
		edit.apply();
	}

	public void setLockMethod(String lockMethod) {
		Editor edit = preferences.edit();
		edit.putString("lock_method", lockMethod);
		edit.apply();
	}

	public void setPattern(char[] passCode) {
		Editor edit = preferences.edit();
		edit.putString("saved_pattern", String.copyValueOf(passCode));
		edit.apply();
	}

	public void setStealthMode(boolean isStealthModeEnables) {
		Editor edit = preferences.edit();
		edit.putBoolean("stealth_mode", isStealthModeEnables);
		edit.apply();
	}

	public void setRecoveryEmail(String email) {
		Editor edit = preferences.edit();
		edit.putString("recovery_email", email);
		edit.apply();
	}

	public void setTempPassword(String tempPass) {
		Editor edit = preferences.edit();
		edit.putString("temp_pass", tempPass);
		edit.apply();
	}

	public void removeTempPassword() {
		Editor edit = preferences.edit();
		edit.remove("temp_pass");
		edit.apply();
	}

	public int getStatus() {
		return preferences.getInt("status", 0);
	}

	public boolean isSDCardMounted() {
		return preferences.getBoolean("sdcardFlag", true);
	}

	public String getLockMethod() {
		return preferences.getString("lock_method", "password");
	}

	public boolean isPatternExist() {
		if (preferences != null && preferences.contains("saved_pattern")) {
			if (getLockMethod().equals("password"))
				setLockMethod("pattern");
			return true;
		}
		if (preferences != null && preferences.contains("pass_code")) {
			if (preferences.getString("pass_code", null) != null) {
				Editor edit = preferences.edit();
				edit.putString("saved_pattern",
						preferences.getString("pass_code", null));
				setLockMethod("pattern");
				edit.apply();
				return true;
			}
		}
		return false;
	}

	public String getSavedPattern() {
		return preferences.getString("saved_pattern", null);
	}

	public boolean isStealthModeEnabled() {
		return preferences.getBoolean("stealth_mode", false);
	}

	public boolean isEmailExist() {
		if (preferences != null && preferences.contains("recovery_email")) {
			return true;
		}
		return false;
	}

	public String getRecoveryEmail() {
		return preferences.getString("recovery_email", null);

	}

	public String getTempPassword() {
		return preferences.getString("temp_pass", null);
	}

}
