package com.serveme.savemyphone.view.wizard;

//import org.omar.android.lib.ui.passwordlock.PasswordEntryActivity;

import group.pals.android.lib.ui.lockpattern.LockPatternActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.serveme.savemyphone.R;
import com.serveme.savemyphone.util.MyTracker;

public class PasswordRequest extends ActionBarActivity {

	public static final int REQ_CREATE_PATTERN = 1;
	public static final int REQ_CREATE_PASSWORD = 2;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guide_activity);
//		String direction = getResources().getString(R.string.direction);
//		LinearLayout buttonsPane = (LinearLayout) findViewById(R.id.buttons_pane);
		final TextView textView = (TextView) findViewById(R.id.textView);
		textView.setText(R.string.pattern_request_help);
		textView.setMovementMethod(ScrollingMovementMethod.getInstance());
		final Button firstButton = (Button) findViewById(R.id.previous);
		firstButton.setText(R.string.create_password);
		// Temporarily ******************************************************
		((LinearLayout) findViewById(R.id.buttons_pane)).setWeightSum(1.0f); 
		firstButton.setVisibility(View.GONE);
		/* *************************************************************** */
		final Button secondButton = (Button) findViewById(R.id.next);
		secondButton.setText(R.string.create_pattern);

		firstButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

			}
		});
		
		secondButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(LockPatternActivity.ACTION_CREATE_PATTERN, null, PasswordRequest.this, LockPatternActivity.class);
				startActivityForResult(intent, REQ_CREATE_PATTERN);
				MyTracker.fireButtonPressedEvent(PasswordRequest.this, "request_pattern");
			}
		});

	}

	@Override
	protected void onStart() {
		super.onStart();
		MyTracker.fireActivityStartEvent(PasswordRequest.this);
		MyTracker.getUncaughtExceptionHandler();
	}
;
	@Override
	protected void onStop() {
		MyTracker.fireActivityStopevent(PasswordRequest.this);
		super.onStop();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case REQ_CREATE_PATTERN:
				if (resultCode == Activity.RESULT_OK) {
					char[] passCode = data.getCharArrayExtra(LockPatternActivity.EXTRA_PATTERN);
					SharedPreferences preferences = getSharedPreferences("mypref", Context.MODE_PRIVATE);
					Editor edit = preferences.edit();
					edit.putString("saved_pattern", String.copyValueOf(passCode)); 
					edit.apply();
					Intent intent = new Intent(PasswordRequest.this, HelpActivity.class);
					startActivity(intent); 
					finish();
					MyTracker.fireButtonPressedEvent(PasswordRequest.this, "request_pattern_done");
				} else {
					MyTracker.fireButtonPressedEvent(PasswordRequest.this, "request_pattern_cancelled");
				}
			break;

		}
	}

}
