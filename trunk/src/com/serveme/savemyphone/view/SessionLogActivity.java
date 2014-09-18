package com.serveme.savemyphone.view;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.serveme.savemyphone.R;
import com.serveme.savemyphone.control.AppsLogListAdapter;
import com.serveme.savemyphone.control.SessionLogListAdapter;
import com.serveme.savemyphone.control.SessionLogListAdapter.ViewHolder;
import com.serveme.savemyphone.model.DBOperations;
import com.serveme.savemyphone.model.DB_KEYS;
import com.serveme.savemyphone.view.utils.AdMobListener;
import com.serveme.savemyphone.view.utils.AnalyticsExceptionParser;

public class SessionLogActivity extends ActionBarActivity {

	private SQLiteDatabase db;
	private Cursor cursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_session_log);
		// Show the Up button in the action bar.
		intialize();
	}

	@Override
	protected void onStart() {
		super.onStart();
		db = DBOperations.getInstance(this).getDatabase();
		cursor = db.rawQuery("select " + DB_KEYS.KEY_SESSION_DATE + ","
				+ DB_KEYS.KEY_ID + " from " + DB_KEYS.APP_LOG_SESSION_TABLE,
				null);
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
		ListView listView = (ListView) findViewById(R.id.session_list);
		SessionLogListAdapter adapter = new SessionLogListAdapter(this, cursor);
		listView.setSmoothScrollbarEnabled(true);
		listView.setAdapter(adapter);
		listView.setEmptyView(findViewById(R.id.empty));
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View item, int arg2,
					long arg3) {
				ViewHolder tag = (ViewHolder) item.getTag();
				Intent intent = new Intent(SessionLogActivity.this,
						AppsLogActivity.class);
				intent.putExtra("sessionId", tag.sessionId);
				startActivity(intent);
			}
		});
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
