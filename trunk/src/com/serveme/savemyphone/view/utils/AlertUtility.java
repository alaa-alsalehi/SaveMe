package com.serveme.savemyphone.view.utils;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;

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
		return thisView;
	}

}