package com.serveme.savemyphone.view;

//import org.omar.android.lib.ui.passwordlock.PasswordEntryActivity;

import group.pals.android.lib.ui.lockpattern.LockPatternActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.ExceptionReporter;
import com.serveme.analytics.AnalyticsExceptionParser;
import com.serveme.savemyphone.R;

public class PasswordRequest extends Activity {

	public static final int REQ_CREATE_PATTERN = 1;

	private char[] passCode;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.password_request);
		Button patternButton = (Button) findViewById(R.id.pattern_ptn);
		Button passwordButton = (Button) findViewById(R.id.password_btn);

		patternButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				PasswordRequester.requestPatternPassword(PasswordRequest.this);
			}
		});

		passwordButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Intent intent = new
				// Intent(LockPatternActivity.ACTION_CREATE_PATTERN, null,
				// getBaseContext(), PasswordEntryActivity.class);
				// startActivityForResult(intent, REQ_CREATE_PATTERN);
			}
		});

	}
	
		@Override
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
		Thread.UncaughtExceptionHandler uncaughtExceptionHandler = Thread
				.getDefaultUncaughtExceptionHandler();
		if (uncaughtExceptionHandler instanceof ExceptionReporter) {
			ExceptionReporter exceptionReporter = (ExceptionReporter) uncaughtExceptionHandler;
			exceptionReporter
					.setExceptionParser(new AnalyticsExceptionParser());
		}
	}
	
	@Override
	protected void onStop() {
		EasyTracker.getInstance(this).activityStop(this);
		super.onStop();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		PasswordRequester.onPatternPasswordRecived(requestCode, resultCode,
				data, this);
		super.onActivityResult(requestCode, resultCode, data);
	}

}
