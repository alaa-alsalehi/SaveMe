package com.serveme.savemyphone.view;

import com.serveme.savemyphone.R;

import android.content.Context;
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
		param.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		param.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		param.width = LayoutParams.MATCH_PARENT;
		param.height = LayoutParams.MATCH_PARENT;
		return param;
	}

	public synchronized static View getView(Context context) {
		if (thisView == null) {
			TextView view = new TextView(context);
			view.setText(R.string.prevent_message);
			view.setGravity(Gravity.CENTER);
			view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
			view.setBackgroundColor(context.getResources().getColor(R.color.actionbar_background));
			thisView = view;
			return view;
		} else {
			return thisView;
		}
	}

}