package com.serveme.savemyphone.view.wizard;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.serveme.savemyphone.R;
import com.serveme.savemyphone.preferences.PrefEditor;
import com.serveme.savemyphone.view.utils.ActivitiesController;


public class RecoveryEmailRequest extends Activity {

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recovery_activity);
		final TextView tv = (TextView) findViewById(R.id.textView);
		tv.setText(getResources().getString(R.string.email_request_textview));
		final EditText input = (EditText) findViewById(R.id.editText);
		input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		input.setText(getFirstEmailAddress());
		final Button okBtn = (Button) findViewById(R.id.ok);
		okBtn.setText(getResources().getString(R.string.confirm));
		final Button cancelBtn = (Button) findViewById(R.id.cancel);
		
		okBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new PrefEditor(RecoveryEmailRequest.this).setRecoveryEmail(input.getText().toString());
				new ActivitiesController(RecoveryEmailRequest.this).getActivitiesFlow();
			}
		});
		
		cancelBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	private String getFirstEmailAddress(){
		if(getGoogleAccountNames().length > 0){
			return getGoogleAccountNames()[0];
		} else {
			return null;
		}
	}
	

	private String[] getGoogleAccountNames() {
		AccountManager mAccountManager = AccountManager.get(RecoveryEmailRequest.this);
		Account[] accounts = mAccountManager.getAccountsByType("com.google");
		String[] names = new String[accounts.length];
		for (int i = 0; i < names.length; i++) {
			names[i] = accounts[i].name;
		}
		return names;
	}
}
