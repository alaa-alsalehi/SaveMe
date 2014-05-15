package com.serveme.savemyphone.view.utils;

import group.pals.android.lib.ui.lockpattern.LockPatternActivity;
import group.pals.android.lib.ui.lockpattern.prefs.DisplayPrefs;
import com.serveme.savemyphone.preferences.PrefEditor;
import com.serveme.savemyphone.view.RecoveryActivity;

import android.app.Activity;
import android.content.Intent;

public class Authenticator {
	
	private Activity context;
	private PrefEditor pe;
	public Authenticator(Activity context){
		this.context = context;
		pe = new PrefEditor(context);
	}
	
	public void requestPatttern(int requestCode){
		Intent intent = new Intent(LockPatternActivity.ACTION_CREATE_PATTERN, null, context, LockPatternActivity.class);
		context.startActivityForResult(intent, requestCode);
	}
	
	public void checkPattern(int requestCode){
		char[] savedPattern = new PrefEditor(context).getSavedPattern().toCharArray();
		if(pe.isStealthModeEnabled()){
			DisplayPrefs.setStealthMode(context, true);
		} else {
			DisplayPrefs.setStealthMode(context, false);
		}

		Intent intent = new Intent(LockPatternActivity.ACTION_COMPARE_PATTERN, null, context,LockPatternActivity.class);
		intent.putExtra(LockPatternActivity.EXTRA_PATTERN, savedPattern);
		intent.putExtra(LockPatternActivity.EXTRA_INTENT_ACTIVITY_FORGOT_PATTERN, new Intent(context, RecoveryActivity.class));
		context.startActivityForResult(intent,requestCode);
	}
	
}
