package view;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AdminReciver extends DeviceAdminReceiver {

	@Override
	public void onEnabled(Context context, Intent intent) {
Log.v("rec", "rec");
	}

	@Override
	public void onDisabled(Context context, Intent intent) {
		Log.v("admin reciver", "admin disable");
	}

}