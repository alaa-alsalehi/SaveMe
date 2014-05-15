package com.serveme.savemyphone.view;

import group.pals.android.lib.ui.lockpattern.LockPatternActivity;

import java.util.ArrayList;
import java.util.List;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.InterstitialAd;
import com.serveme.savemyphone.R;
import com.serveme.savemyphone.control.GridAdapter;
import com.serveme.savemyphone.model.DBOperations;
import com.serveme.savemyphone.model.Launcher;
import com.serveme.savemyphone.preferences.PrefEditor;
import com.serveme.savemyphone.service.AppsMonitor;
import com.serveme.savemyphone.util.MyTracker;
import com.serveme.savemyphone.view.utils.Authenticator;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class UserActivity extends ActionBarActivity implements AdListener {

	private static final int REQ_ENTER_PATTERN = 2;

	private static final String MY_INTERSTITIAL_UNIT_ID = "2ac0657dacb4406b";

	private InterstitialAd interstitial;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_activity);
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
			finish();
			switch (resultCode) {
			case RESULT_OK:
				// Log.v("result", "passed");
				PrefEditor pe = new PrefEditor(UserActivity.this);
				pe.updateStatus(0);
				UserActivity.this.stopService(new Intent(UserActivity.this,
						AppsMonitor.class));
				// Create the interstitial
				interstitial = new InterstitialAd(this, MY_INTERSTITIAL_UNIT_ID);

				// Create ad request
				AdRequest adRequest = new AdRequest();

				// Begin loading your interstitial
				interstitial.loadAd(adRequest);
				interstitial.setAdListener(this);

				break;
			case RESULT_CANCELED:
				// Log.v("result", "canceled");
				break;
			case LockPatternActivity.RESULT_FAILED:
				// Log.v("result", "faild");
				break;
			case LockPatternActivity.RESULT_FORGOT_PATTERN:
				// The user forgot the pattern and invoked your recovery
				// Activity.
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

	private final BroadcastReceiver bcr = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			UserActivity.this.finish();
		}
	};

	@Override
	public void onDismissScreen(Ad arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
		// TODO Auto-generated method stub

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
