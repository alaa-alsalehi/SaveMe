package com.serveme.savemyphone.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PrefEditor {
	//public static final int ADMIN_PERMISSION_OK = 1;
	//public static final int ADMIN_PERMISSION_IGNORED = 2;
	
	SharedPreferences preferences;
	
	public PrefEditor(Context context){
		 preferences = context.getSharedPreferences("mypref", Context.MODE_PRIVATE);
	}
	
	public void updateStatus(int status) {
       
        Editor edit = preferences.edit();
        edit.putInt("status", status);
        edit.apply();
	}
	
	public void setSDCardMounted(boolean flag) {
	       
        Editor edit = preferences.edit();
        edit.putBoolean("sdcardFlag", flag);
        edit.apply();
	}
	
	public void setLockMethod(String lockMethod){
		Editor edit = preferences.edit();
		edit.putString("lock_method", lockMethod); 
		edit.apply();	
	}
	
	public void setPattern(char[] passCode) {
		Editor edit = preferences.edit();
		edit.putString("saved_pattern", String.copyValueOf(passCode)); 
		edit.apply();	
	}

	public int getStatus() {
		return preferences.getInt("status", 0);
	}
	
	public boolean isSDCardMounted() {
		return preferences.getBoolean("sdcardFlag", true);
	}
	
	public String getSavedPattern(){
		return preferences.getString("saved_pattern","null");
	}



}
