package com.serveme.savemyphone.view.utils;

import android.app.Activity;
import android.content.Intent;
import com.haibison.android.lockpattern.util.Settings;
import com.haibison.android.lockpattern.LockPatternActivity;
import com.serveme.savemyphone.preferences.PrefEditor;
import com.serveme.savemyphone.view.RecoveryActivity;

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
			Settings.Display.setStealthMode(context, true);
		} else {
			Settings.Display.setStealthMode(context, false);
		}

		Intent intent = new Intent(LockPatternActivity.ACTION_COMPARE_PATTERN, null, context,LockPatternActivity.class);
		intent.putExtra(LockPatternActivity.EXTRA_PATTERN, savedPattern);
		intent.putExtra(LockPatternActivity.EXTRA_PENDING_INTENT_FORGOT_PATTERN, new Intent(context, RecoveryActivity.class));
		context.startActivityForResult(intent,requestCode);
	}
	
}
