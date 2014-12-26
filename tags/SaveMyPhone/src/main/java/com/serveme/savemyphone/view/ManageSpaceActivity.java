package com.serveme.savemyphone.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.haibison.android.lockpattern.LockPatternActivity;
import com.serveme.savemyphone.view.utils.Authenticator;

public class ManageSpaceActivity extends Activity {
	private static final int REQ_ENTER_PATTERN = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new Authenticator(this).checkPattern(REQ_ENTER_PATTERN);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQ_ENTER_PATTERN: {
			switch (resultCode) {
			case RESULT_OK:
				Intent setting = new Intent(this, SettingsActivity.class);
				startActivity(setting);

			case RESULT_CANCELED:

			case LockPatternActivity.RESULT_FAILED:

			case LockPatternActivity.RESULT_FORGOT_PATTERN:
				// The user forgot the pattern and invoked your recovery
				// Activity.
			default:
				finish();
				break;
			}

			/*
			 * In any case, there's always a key EXTRA_RETRY_COUNT, which holds
			 * the number of tries that the user did.
			 */
			/*
			 * In any case, there's always a key EXTRA_RETRY_COUNT, which holds
			 * the number of tries that the user did.
			 */
			// int retryCount =
			// data.getIntExtra(LockPatternActivity.EXTRA_RETRY_COUNT, 0);

			break;
		}// REQ_ENTER_PATTERN
		}
	}

}
