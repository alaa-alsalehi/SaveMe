package com.serveme.ads;

import android.content.Context;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest.ErrorCode;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Log;
import com.google.analytics.tracking.android.MapBuilder;

public class AdMobListener implements AdListener {
	
	private Context context ;

	public AdMobListener(Context context) {
		this.context =context;
	}

	@Override
	public void onDismissScreen(Ad arg0) {

	}

	@Override
	public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
		Log.v(arg1.toString());
	}

	@Override
	public void onLeaveApplication(Ad arg0) {
		EasyTracker.getInstance(context).send(
				MapBuilder.createEvent("ads",
						"ads", "leave",
						Long.valueOf(1)).build());
	}

	@Override
	public void onPresentScreen(Ad arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReceiveAd(Ad arg0) {
		EasyTracker.getInstance(context).send(
				MapBuilder.createEvent("ads",
						"ads", "recive",
						Long.valueOf(1)).build());		
	}

}
