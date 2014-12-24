package com.serveme.savemyphone.receivers;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

public class AdminReciver extends DeviceAdminReceiver {

    @Override
    public void onEnabled(Context context, Intent intent) {
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
    }
    
    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        return "This app will not work if you disable admin persmission";
    }

    @Override
    public void onPasswordChanged(Context context, Intent intent) {

    }
    

}