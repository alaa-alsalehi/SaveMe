package com.serveme.savemyphone.view;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.serveme.savemyphone.R;
import com.serveme.savemyphone.control.AppsLogListAdapter;
import com.serveme.savemyphone.model.DBOperations;
import com.serveme.savemyphone.model.DB_KEYS;
import com.serveme.savemyphone.view.utils.AdMobListener;
import com.serveme.savemyphone.view.wizard.HelpActivity;

public class AppsLogActivity extends ActionBarActivity {

	private SQLiteDatabase db;
	private Cursor cursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_apps_log);
		// Show the Up button in the action bar.
		setupActionBar();
		intialize();
	}

	@Override
	protected void onStart() {
		super.onStart();
		db = DBOperations.getInstance(this).getDatabase();
		cursor = db.rawQuery("select sum(" + DB_KEYS.KEY_END_DATE + " - "
				+ DB_KEYS.KEY_START_DATE + ") all_time ,"
				+ DB_KEYS.KEY_PACKAGE_NAME + "," + DB_KEYS.KEY_ID + " from "
				+ DB_KEYS.APP_LOG_TABLE + " group by "
				+ DB_KEYS.KEY_PACKAGE_NAME, null);
		intialize();
	}

	@Override
	protected void onStop() {
		cursor.close();
		db.close();
		super.onStop();
	}

	protected void intialize() {
		ListView listView = (ListView) findViewById(R.id.app_list);
		AppsLogListAdapter adapter = new AppsLogListAdapter(this, cursor);
		listView.setSmoothScrollbarEnabled(true);
		listView.setAdapter(adapter);
		adsStuff();
	}

	private void adsStuff() {
		final AdView adView = (AdView) findViewById(R.id.adView);
		// Create the adView
		// adView.setGravity(Gravity.CENTER);
		adView.setAdListener(new AdMobListener(this));
		// Initiate a generic request to load it with an ad
		final AdRequest adRequest = new AdRequest.Builder().addTestDevice(
				AdRequest.DEVICE_ID_EMULATOR).build();
		// adRequest.addTestDevice("8E7864D6D7911778659788D0B39F99E8");
		adView.loadAd(adRequest);
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	private void setupActionBar() {
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			Intent intent = new Intent(this, HelpActivity.class);
			intent.putExtra("first_time", true);
			NavUtils.navigateUpTo(this, intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
