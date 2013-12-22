package com.serveme.savemyphone.view.wizard;

//import org.omar.android.lib.ui.passwordlock.PasswordEntryActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.ExceptionReporter;
import com.serveme.analytics.AnalyticsExceptionParser;
import com.serveme.savemyphone.R;
import com.serveme.savemyphone.view.PasswordRequester;

public class PasswordRequest extends ActionBarActivity {

	public static final int REQ_CREATE_PATTERN = 1;

	private char[] passCode;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		String direction = getResources().getString(R.string.direction);
		TableRow choises = (TableRow) findViewById(R.id.choises_row);
		final TextView textView2 = (TextView) findViewById(R.id.textView2);
		textView2.setText(R.string.pattern_request_help);
		textView2.setMovementMethod(ScrollingMovementMethod.getInstance());
		final Button previousButton = (Button) findViewById(R.id.previous);
		previousButton.setVisibility(View.GONE);
		final Button nextButton = (Button) findViewById(R.id.next);
		nextButton.setText(R.string.create_pattern);
		if ("right".equals(direction)) {
			choises.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
			textView2.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
			// remove and added at last of the choices row to make it right
			choises.removeView(previousButton);
			choises.addView(previousButton);
		} else {
			choises.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
		}

		previousButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

			}
		});
		nextButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				PasswordRequester.requestPatternPassword(PasswordRequest.this);
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
