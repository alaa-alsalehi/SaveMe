package com.serveme.savemyphone.view.utils;

import android.content.Context;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Log;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.android.gms.ads.AdListener;

public class AdMobListener extends AdListener {

	private Context context;

	public AdMobListener(Context context) {
		this.context = context;
	}

	@Override
	public void onAdFailedToLoad(int errorCode) {
		// TODO Auto-generated method stub
		super.onAdFailedToLoad(errorCode);
	}

	@Override
	public void onAdLeftApplication() {
		EasyTracker.getInstance(context).send(
				MapBuilder.createEvent("ads", "ads", "leave", Long.valueOf(1))
						.build());
	}

	@Override
	public void onAdLoaded() {
		EasyTracker.getInstance(context).send(
				MapBuilder.createEvent("ads", "ads", "recive", Long.valueOf(1))
						.build());
	}

}
