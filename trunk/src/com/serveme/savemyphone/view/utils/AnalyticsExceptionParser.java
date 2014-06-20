package com.serveme.savemyphone.view.utils;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.google.analytics.tracking.android.ExceptionParser;


public class AnalyticsExceptionParser implements ExceptionParser {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.analytics.tracking.android.ExceptionParser#getDescription(
	 * java.lang.String, java.lang.Throwable)
	 */
	public String getDescription(String threadName, Throwable exception) {
		List<Throwable> list = ExceptionUtil.getThrowableList(exception);
		StringBuilder builder = new StringBuilder();
		// delete all seen before lines
		ArrayList<StackTraceElement> djvu = new ArrayList<StackTraceElement>();
		for (Throwable throwable : list) {
			StackTraceElement[] stactTrace = throwable.getStackTrace();
			builder.append(throwable.getClass().getName() + "\n");
			int djvuCounter = 0;
			for (StackTraceElement stackTraceElement : stactTrace) {
				if (djvu.contains(stackTraceElement))
					djvuCounter++;
				else {
					if (djvuCounter != 0) {
						builder.append("... " + djvuCounter + " more\n");
						djvuCounter = 0;
					}
					String stackMethod = stackTraceElement.toString();
					builder.append(stackMethod + "\n");
					djvu.add(stackTraceElement);
				}
			}
			if (djvuCounter != 0) {
				builder.append("... " + djvuCounter + " more\n");
				djvuCounter = 0;
			}
			builder.append("caused by: ");
		}
		builder.delete(builder.lastIndexOf("\n"), builder.length());// delete
																	// last
																	// caused
																	// by:
		Log.d("stackMethod", builder.toString());
		return "Thread: " + threadName + ", ExceptionStack: "
				+ builder.toString();
	}
}
