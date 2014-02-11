package com.serveme.savemyphone.receivers;

import com.serveme.savemyphone.model.DBOperations;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class SDCardMountReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {     
        String action = intent.getAction();
        if(action.equals(Intent.ACTION_MEDIA_UNMOUNTED) || action.equals(Intent.ACTION_MEDIA_REMOVED) || action.equals(Intent.ACTION_MEDIA_BAD_REMOVAL) || action.equals(Intent.ACTION_MEDIA_SHARED) ){  
        	Log.d("unmounted", "unmounted");
        	DBOperations.sdcard_mounted = false;
        } else if(action.equals(Intent.ACTION_MEDIA_MOUNTED)){
        	Log.d("mounted", "mounted");
        	DBOperations.sdcard_mounted = true;
        }
    }

}