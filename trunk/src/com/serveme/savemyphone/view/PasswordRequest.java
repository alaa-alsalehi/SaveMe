package com.serveme.savemyphone.view;

import group.pals.android.lib.ui.lockpattern.LockPatternActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.serveme.savemyphone.R;

public class PasswordRequest extends Activity {

	// This is preferred flag for pattern lib
	private static final int REQ_CREATE_PATTERN = 1;
	private char[] pattern;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.password_request);
		Button btn = (Button) findViewById(R.id.pattern_ptn);
		
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LockPatternActivity.ACTION_CREATE_PATTERN, null,getBaseContext(), LockPatternActivity.class);
				startActivityForResult(intent, REQ_CREATE_PATTERN);
			}
		});

	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
	    case REQ_CREATE_PATTERN: {
	        if (resultCode == RESULT_OK) {
	            pattern = data.getCharArrayExtra(LockPatternActivity.EXTRA_PATTERN);
	            SharedPreferences preferences = getSharedPreferences("mypref", Context.MODE_PRIVATE);
	            Editor edit = preferences.edit();
	            Log.v("hi",String.copyValueOf(pattern));
	            edit.putString("pass_code", String.copyValueOf(pattern));
	            edit.apply(); 
	            finish();
				Intent intent = new Intent(getBaseContext(), AdminActivity.class);
				startActivity(intent);
	        }
	        break;
	    }// REQ_CREATE_PATTERN
	    }
	    super.onActivityResult(requestCode, resultCode, data);
	}
	
}
