package com.serveme.savemyphone.view.wizard;

//import org.omar.android.lib.ui.passwordlock.PasswordEntryActivity;
import group.pals.android.lib.ui.lockpattern.LockPatternActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.serveme.savemyphone.R;
import com.serveme.savemyphone.preferences.PrefEditor;
import com.serveme.savemyphone.util.MyTracker;

public class PasswordRequest extends ActionBarActivity {

	public static final int REQ_CREATE_PATTERN = 1;
	public static final int REQ_CREATE_PASSWORD = 2;
	private PrefEditor pe;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.password_request);
		pe = new PrefEditor(PasswordRequest.this);
		final TextView textView = (TextView) findViewById(R.id.textView);
		textView.setText(R.string.password_request_help);
		textView.setMovementMethod(ScrollingMovementMethod.getInstance());
		final Button passwordBtn = (Button) findViewById(R.id.password);
		final Button patternBtn = (Button) findViewById(R.id.pattern);

		LinearLayout buttonsPane = (LinearLayout) findViewById(R.id.buttons_pane);
		String direction = getResources().getString(R.string.direction);
		if (direction.equals("right")) {
			buttonsPane.removeView(patternBtn);
			buttonsPane.addView(patternBtn);
		}

		// Temporarily ******************************************************
		buttonsPane.setWeightSum(1.0f); 
		passwordBtn.setVisibility(View.GONE);
		/* *************************************************************** */

		passwordBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				MyTracker.fireButtonPressedEvent(PasswordRequest.this, "request_password");
			}
		});
		
		patternBtn.setOnClickListener(new OnClickListener() {

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
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
				case REQ_CREATE_PATTERN:
					pe.setLockMethod("pattern");
					char[] passCode = data.getCharArrayExtra(LockPatternActivity.EXTRA_PATTERN);
					pe.setPattern(passCode);
				break;
			}
			
			Intent intent = new Intent(PasswordRequest.this, HelpActivity.class);
			intent.putExtra("first_time", true);
			startActivity(intent); 
			finish();
			MyTracker.fireButtonPressedEvent(PasswordRequest.this, "request_pattern_done");
			
		} else {
			MyTracker.fireButtonPressedEvent(PasswordRequest.this, "request_pattern_cancelled");
		}
	}

}
