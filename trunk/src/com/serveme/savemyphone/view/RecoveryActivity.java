package com.serveme.savemyphone.view;

import group.pals.android.lib.ui.lockpattern.LockPatternActivity;
import java.math.BigInteger;
import java.security.SecureRandom;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.serveme.savemyphone.R;
import com.serveme.savemyphone.mail.MailSender;
import com.serveme.savemyphone.preferences.PrefEditor;
import com.serveme.savemyphone.util.MyTracker;
import com.serveme.savemyphone.view.utils.Authenticator;


public class RecoveryActivity extends Activity {

	private PrefEditor pe;
	private Authenticator auth;
	public static final int REQ_CREATE_PATTERN = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recovery_activity);
		String generatedPass = new SessionIdentifierGenerator().nextSessionId();
		MailSender m = new MailSender(RecoveryActivity.this,generatedPass);
		auth = new Authenticator(RecoveryActivity.this);
		pe = new PrefEditor(RecoveryActivity.this);
		pe.setTempPassword(generatedPass);
		m.execute("");
		
		final EditText input = (EditText) findViewById(R.id.editText);
		final Button okBtn = (Button) findViewById(R.id.ok);
		final Button cancelBtn = (Button) findViewById(R.id.cancel);
		
		okBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(input.getText().toString().equalsIgnoreCase(pe.getTempPassword())){
					if(pe.getLockMethod().equals("pattern")){
						auth.requestPatttern(REQ_CREATE_PATTERN);
					} else {
						// password
					}
				} else {
					Toast.makeText(RecoveryActivity.this, "Incorrect Password!", Toast.LENGTH_LONG).show();
				}
				
			}
		});
		
		cancelBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	public final class SessionIdentifierGenerator {
		@SuppressLint("TrulyRandom")
		private SecureRandom random = new SecureRandom();

		public String nextSessionId() {
			return new BigInteger(60, random).toString(32);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		pe.removeTempPassword();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
				case REQ_CREATE_PATTERN:
					char[] passCode = data.getCharArrayExtra(LockPatternActivity.EXTRA_PATTERN);
					pe.setPattern(passCode);
				break;
			}
			
			Intent intent = new Intent(RecoveryActivity.this, MainActivity.class);
			intent.putExtra("first_time", true);
			startActivity(intent); 
			finish();
			MyTracker.fireButtonPressedEvent(RecoveryActivity.this, "recover_pattern_done");
			
		} else {
			MyTracker.fireButtonPressedEvent(RecoveryActivity.this, "recover_pattern_cancelled");
		}
	}
	
}
