package com.serveme.savemyphone.view.utils;

import com.serveme.savemyphone.R;
import com.serveme.savemyphone.view.UserView;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.PixelFormat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.TextView;

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
			return view;
		} else {
			return thisView;
		}
	}

}