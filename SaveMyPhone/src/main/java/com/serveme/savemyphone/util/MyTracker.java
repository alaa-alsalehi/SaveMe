package com.serveme.savemyphone.util;

import android.app.Activity;
import android.content.Context;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.ExceptionReporter;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.serveme.savemyphone.service.AppsMonitor;
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
	
	public static void setUncaughtExceptionHandler(final Context context){
		final Thread.UncaughtExceptionHandler uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		if (uncaughtExceptionHandler instanceof ExceptionReporter) {
			ExceptionReporter exceptionReporter = (ExceptionReporter) uncaughtExceptionHandler;
			exceptionReporter.setExceptionParser(new AnalyticsExceptionParser());
		}
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
		        @Override
		        public void uncaughtException(Thread thread, Throwable ex) {
		            if (thread.getName().startsWith("AdWorker")) {
		            	Tracker tracker = EasyTracker
								.getInstance(context);
						tracker.send(MapBuilder.createException(
								new AnalyticsExceptionParser()
										.getDescription(Thread
												.currentThread()
												.toString(), ex),
								false).build());
		            } else if (uncaughtExceptionHandler != null) {
		            	uncaughtExceptionHandler.uncaughtException(thread, ex);
		            } else {
		                throw new RuntimeException("No default uncaught exception handler.", ex);
		            }
		        }
		});
	}
}
