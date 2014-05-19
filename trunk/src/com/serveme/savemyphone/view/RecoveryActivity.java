package com.serveme.savemyphone.view;

import group.pals.android.lib.ui.lockpattern.LockPatternActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.serveme.savemyphone.R;
import com.serveme.savemyphone.mail.CodeVerifier;
import com.serveme.savemyphone.mail.MailSender;
import com.serveme.savemyphone.preferences.PrefEditor;
import com.serveme.savemyphone.util.MyTracker;
import com.serveme.savemyphone.view.utils.Authenticator;

public class RecoveryActivity extends Activity {

	private PrefEditor pe;
	public static final int REQ_CREATE_PATTERN = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recovery_activity);

		final EditText input = (EditText) findViewById(R.id.editText);
		final Button okBtn = (Button) findViewById(R.id.ok);
		final Button cancelBtn = (Button) findViewById(R.id.cancel);

		okBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new CodeVerifier(RecoveryActivity.this, input.getText()
						.toString());
			}
		});

		cancelBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		new MailSender(RecoveryActivity.this);
	}

	public void changePassword() {
		pe = new PrefEditor(RecoveryActivity.this);
		if (pe.getLockMethod().equals("pattern")) {
			Authenticator auth = new Authenticator(this);
			auth.requestPatttern(REQ_CREATE_PATTERN);
		} else {
			// password
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case REQ_CREATE_PATTERN:
				char[] passCode = data
						.getCharArrayExtra(LockPatternActivity.EXTRA_PATTERN);
				pe = new PrefEditor(RecoveryActivity.this);
				pe.setPattern(passCode);
				break;
			}

			Intent intent = new Intent(RecoveryActivity.this,
					MainActivity.class);
			intent.putExtra("first_time", true);
			startActivity(intent);
			finish();
			MyTracker.fireButtonPressedEvent(RecoveryActivity.this,
					"recover_pattern_done");

		} else {
			MyTracker.fireButtonPressedEvent(RecoveryActivity.this,
					"recover_pattern_cancelled");
		}
	}

}
