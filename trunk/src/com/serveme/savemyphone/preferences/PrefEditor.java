package com.serveme.savemyphone.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PrefEditor {
	private Context context;
	
	public PrefEditor(Context context){
		this.context = context;
	}
	public void updateStatus(int status) {
        SharedPreferences preferences = context.getSharedPreferences("mypref", Context.MODE_PRIVATE);
        Editor edit = preferences.edit();
        edit.putInt("status", status);
        edit.apply();
	}

	public int getStatus() {
		SharedPreferences mySharedPreferences = context.getSharedPreferences("mypref", Context.MODE_PRIVATE);
		return mySharedPreferences.getInt("status", 1);
	}
}
