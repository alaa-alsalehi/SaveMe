package com.serveme.savemyphone.util;

import android.app.Activity;
import android.content.Context;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.ExceptionReporter;
import com.google.analytics.tracking.android.MapBuilder;
import com.serveme.savemyphone.view.utils.AnalyticsExceptionParser;


public class MyTracker {

	public static void fireButtonPressedEvent(Context context, String label){
		EasyTracker.getInstance(context).send(
					MapBuilder.createEvent(
						"ui_action",
						"button_press",
						label,
						Long.valueOf(1)
					).build()
		);
	}
	
	public static void fireActivityStartEvent(Activity context){
		EasyTracker.getInstance(context).activityStart(context);
	}
	
	public static void fireActivityStopevent(Activity context){
		EasyTracker.getInstance(context).activityStop(context);
	}
	
	public static void getUncaughtExceptionHandler(){
		Thread.UncaughtExceptionHandler uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		if (uncaughtExceptionHandler instanceof ExceptionReporter) {
			ExceptionReporter exceptionReporter = (ExceptionReporter) uncaughtExceptionHandler;
			exceptionReporter.setExceptionParser(new AnalyticsExceptionParser());
		}
	}
}
