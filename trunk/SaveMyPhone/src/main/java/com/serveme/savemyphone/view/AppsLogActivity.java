package com.serveme.savemyphone.view;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.serveme.savemyphone.R;
import com.serveme.savemyphone.control.AppsLogListAdapter;
import com.serveme.savemyphone.model.DBOperations;
import com.serveme.savemyphone.model.DB_KEYS;
import com.serveme.savemyphone.view.utils.AdMobListener;
import com.serveme.savemyphone.view.utils.AnalyticsExceptionParser;

public class AppsLogActivity extends ActionBarActivity {

	private SQLiteDatabase db;
	private Cursor cursor;
	private long sessionId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_apps_log);
		// Show the Up button in the action bar.
		sessionId = getIntent().getLongExtra("sessionId", -1);
		if (sessionId == -1 && savedInstanceState != null) {
			sessionId = savedInstanceState.getLong("sessionId", -1);
		}
		intialize();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong("sessionId", sessionId);
	}

	@Override
	protected void onStart() {
		super.onStart();
		db = DBOperations.getInstance(this).getDatabase();
		if (sessionId == -1) {
			cursor = db.rawQuery("select sum(" + DB_KEYS.KEY_END_DATE + " - "
					+ DB_KEYS.KEY_START_DATE + ") all_time ,"
					+ DB_KEYS.KEY_PACKAGE_NAME + "," + DB_KEYS.KEY_ID
					+ " from " + DB_KEYS.APP_LOG_TABLE + " group by "
					+ DB_KEYS.KEY_PACKAGE_NAME, null);
		} else {
			cursor = db.rawQuery("select sum(" + DB_KEYS.KEY_END_DATE + " - "
					+ DB_KEYS.KEY_START_DATE + ") all_time ,"
					+ DB_KEYS.KEY_PACKAGE_NAME + "," + DB_KEYS.KEY_ID
					+ " from " + DB_KEYS.APP_LOG_TABLE + " where "
					+ DB_KEYS.KEY_LOG_SESSION_ID + " =" + sessionId
					+ " group by " + DB_KEYS.KEY_PACKAGE_NAME, null);
		}
		intialize();
	}

	@Override
	protected void onStop() {
		if (cursor != null) {
			try {
				cursor.close();
			} catch (Exception exception) {
				Tracker tracker = EasyTracker.getInstance(this);
				tracker.send(MapBuilder.createException(
						new AnalyticsExceptionParser().getDescription(Thread
								.currentThread().toString(), exception), false)
						.build());
			}
		}
		if (db != null) {
			try {
				db.close();
			} catch (Exception exception) {
				Tracker tracker = EasyTracker.getInstance(this);
				tracker.send(MapBuilder.createException(
						new AnalyticsExceptionParser().getDescription(Thread
								.currentThread().toString(), exception), false)
						.build());
			}
		}
		super.onStop();
	}

	protected void intialize() {
		ListView listView = (ListView) findViewById(R.id.app_list);
		AppsLogListAdapter adapter = new AppsLogListAdapter(this, cursor);
		listView.setSmoothScrollbarEnabled(true);
		listView.setAdapter(adapter);
		listView.setEmptyView(findViewById(R.id.empty));
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
}
