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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		PasswordRequester.onPatternPasswordRecived(requestCode, resultCode,
				data, this);
		super.onActivityResult(requestCode, resultCode, data);
	}

}
