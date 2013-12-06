package com.serveme.savemyphone.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.serveme.savemyphone.model.DBOperations;

public class RemovePackageReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		DBOperations dbOperations=new DBOperations(context);
		dbOperations.deleteApp(intent.getData().getEncodedSchemeSpecificPart());
	}

}
