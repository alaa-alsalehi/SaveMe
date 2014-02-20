package com.serveme.savemyphone.receivers;

import com.serveme.savemyphone.model.DBOperations;
import com.serveme.savemyphone.preferences.PrefEditor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class SDCardReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {     
    	String action = intent.getAction();
    	PrefEditor pe = new PrefEditor(context);
        if(action.equals(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE) || action.equals(Intent.ACTION_MEDIA_UNMOUNTED) || action.equals(Intent.ACTION_MEDIA_REMOVED) || action.equals(Intent.ACTION_MEDIA_BAD_REMOVAL) || action.equals(Intent.ACTION_MEDIA_SHARED) ){  
        	Log.d("SDCard Receiver", "removed");
        	pe.setSDCardMounted(false);
        	DBOperations.getInstance(context).reCreateWhiteList();
        	context.sendBroadcast(new Intent("refresh_white_list"));
        } else if(action.equals(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE) || action.equals(Intent.ACTION_MEDIA_MOUNTED)){
        	Log.d("SDCard Receiver", "added");
        	pe.setSDCardMounted(true);
        	DBOperations.getInstance(context).reCreateWhiteList();
        	context.sendBroadcast(new Intent("refresh_white_list"));
        }
    }

}