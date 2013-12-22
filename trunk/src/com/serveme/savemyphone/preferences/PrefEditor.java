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
	
/*	public void updateAdminPermission(int adminPermission){
		Editor edit = preferences.edit();
        edit.putInt("status", adminPermission);
        edit.apply();
	}*/

	public int getStatus() {
		return preferences.getInt("status", 0);
	}
	
	public String getPassCode(){
		return preferences.getString("pass_code","null");
	}
}
