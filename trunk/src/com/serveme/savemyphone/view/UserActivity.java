package com.serveme.savemyphone.view;

import group.pals.android.lib.ui.lockpattern.LockPatternActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.InterstitialAd;
import com.serveme.savemyphone.R;
import com.serveme.savemyphone.preferences.PrefEditor;
import com.serveme.savemyphone.service.AppsMonitor;
import com.serveme.savemyphone.util.MyTracker;
import com.serveme.savemyphone.view.utils.Authenticator;

public class UserActivity extends Activity implements AdListener {

	private static final int REQ_ENTER_PATTERN = 2;

	private static final String MY_INTERSTITIAL_UNIT_ID = "2ac0657dacb4406b";

	private InterstitialAd interstitial;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.user_activity);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().hide();
		}
		
		new Authenticator(this).checkPattern(REQ_ENTER_PATTERN);
		MyTracker.fireButtonPressedEvent(UserActivity.this, "unlock");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.user, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_unlock:

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQ_ENTER_PATTERN: {
			switch (resultCode) {
			case RESULT_OK:
				// Log.v("result", "passed");
				PrefEditor pe = new PrefEditor(UserActivity.this);
				pe.updateStatus(0);
				UserActivity.this.stopService(new Intent(UserActivity.this,
						AppsMonitor.class));
				// Create the interstitial
				//interstitial = new InterstitialAd(this, MY_INTERSTITIAL_UNIT_ID);

				// Create ad request
				//AdRequest adRequest = new AdRequest();

				// Begin loading your interstitial
				//interstitial.loadAd(adRequest);
				//interstitial.setAdListener(this);

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

	@Override
	public void onDismissScreen(Ad arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
		finish();
	}

	@Override
	public void onLeaveApplication(Ad arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPresentScreen(Ad arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReceiveAd(Ad arg0) {
		if (interstitial != null) {
			interstitial.show();
		}
	}

}
