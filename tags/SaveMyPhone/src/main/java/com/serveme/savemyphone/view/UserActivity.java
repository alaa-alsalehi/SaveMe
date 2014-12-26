package com.serveme.savemyphone.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.google.android.gms.ads.InterstitialAd;
import com.haibison.android.lockpattern.LockPatternActivity;
import com.serveme.savemyphone.paid.R;
import com.serveme.savemyphone.preferences.PrefEditor;
import com.serveme.savemyphone.service.AppsMonitor;
import com.serveme.savemyphone.util.MyTracker;
import com.serveme.savemyphone.view.utils.Authenticator;

public class UserActivity extends Activity {

	private static final int REQ_ENTER_PATTERN = 2;

	private static final String MY_INTERSTITIAL_UNIT_ID = "ca-app-pub-5487351232333966/9316100059 ";

	private InterstitialAd interstitial;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.user_activity);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// getActionBar().hide();
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
				/*interstitial = new InterstitialAd(this);
				interstitial.setAdUnitId(MY_INTERSTITIAL_UNIT_ID);
				// Create ad request
				AdRequest adRequest = new AdRequest.Builder().build();

				// Begin loading your interstitial
				interstitial.loadAd(adRequest);
				interstitial.setAdListener(new AdListener() {
					@Override
					public void onAdFailedToLoad(int errorCode) {
						// TODO Auto-generated method stub
						finish();
					}

					@Override
					public void onAdLoaded() {
						if (interstitial != null) {
							interstitial.show();
						}
					}
				});*/

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
