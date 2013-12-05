package com.serveme.savemyphone.view;

import com.serveme.savemyphone.receivers.AdminReciver;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.preference.DialogPreference;
import android.util.AttributeSet;

public class UninstallDialogPreference extends DialogPreference {

	private Context context;
    public UninstallDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        this.context = context;
    }
    
    @Override
    protected void onDialogClosed(boolean result) {
        // When the user selects "OK", persist the new value
        if (result) {
        	ComponentName devAdminReceiver = new ComponentName(context, AdminReciver.class);
        	DevicePolicyManager dpm = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        	dpm.removeActiveAdmin(devAdminReceiver);
        	Intent intent = new Intent(Intent.ACTION_DELETE);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        }
    }

} 