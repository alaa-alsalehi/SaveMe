package com.serveme.savemyphone.view.utils;

import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.serveme.savemyphone.view.UserView;

public class AlertUtility {

	private static View thisView;

	public static WindowManager.LayoutParams getParam() {
		final WindowManager.LayoutParams param = new WindowManager.LayoutParams();
		param.flags = WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER;
		param.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		param.width = LayoutParams.MATCH_PARENT;
		param.height = LayoutParams.MATCH_PARENT;
		param.format = PixelFormat.TRANSLUCENT;
		return param;
	}

	public synchronized static View getView(Context context) {
		if (thisView == null) {
			UserView view = new UserView(context);
			thisView = view;
		}
		SharedPreferences preferences = thisView.getContext()
				.getSharedPreferences("mypref", Context.MODE_PRIVATE);
		try {
			thisView.setBackgroundDrawable(BackgroundUtility
					.getBitmapDrawableFromAsset(
							thisView.getContext(),
							preferences.getString("background",
									"background/"
											+ thisView.getContext().getAssets()
													.list("background")[3])));
		} catch (IOException e) {
			Tracker tracker = EasyTracker.getInstance(context);
			tracker.send(MapBuilder.createException(
					new AnalyticsExceptionParser().getDescription(Thread
							.currentThread().toString(), e), false).build());
		}
		return thisView;
	}

}