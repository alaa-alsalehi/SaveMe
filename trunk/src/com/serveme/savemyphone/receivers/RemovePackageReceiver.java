package com.serveme.savemyphone.receivers;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.serveme.savemyphone.R;
import com.serveme.savemyphone.model.DBOperations;
import com.serveme.savemyphone.model.Launcher;
import com.serveme.savemyphone.preferences.PrefEditor;
import com.serveme.savemyphone.util.MyTracker;
import com.serveme.savemyphone.view.MainActivity;

public class RemovePackageReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent intent) {
		DBOperations dbOperations = DBOperations.getInstance(context);
		Uri data = intent.getData();
		if (data != null
				&& !intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)) {
			String packageName = data.getEncodedSchemeSpecificPart();
			dbOperations.deleteApp(packageName);
			HiddenLockUtility.checkAndHandleHiddenLock(context, packageName);
		}
	}



}
