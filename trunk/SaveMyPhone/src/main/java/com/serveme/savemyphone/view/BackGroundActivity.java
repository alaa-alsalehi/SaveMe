package com.serveme.savemyphone.view;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.serveme.savemyphone.R;
import com.serveme.savemyphone.control.StanderListAdapter;
import com.serveme.savemyphone.util.MyTracker;
import com.serveme.savemyphone.view.utils.AnalyticsExceptionParser;
import com.serveme.savemyphone.view.utils.BackgroundUtility;

import org.lucasr.twowayview.TwoWayView;

public class BackGroundActivity extends ActionBarActivity {
	private StanderListAdapter adapter;

	private static class Result {
		int position;
		Drawable drawable;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.background_activity);
		final TwoWayView imagesPreview = (TwoWayView) this
				.findViewById(R.id.images_preview);
		final ImageView backgroundView = (ImageView) findViewById(R.id.background_image);
		final FrameLayout progress = (FrameLayout) findViewById(R.id.progress);
		adapter = new StanderListAdapter(this);
		imagesPreview.setAdapter(adapter);
		imagesPreview.setOrientation(TwoWayView.Orientation.HORIZONTAL);
		new AsyncTask<Void, Void, Result>() {
			@Override
			protected void onPreExecute() {
				progress.setVisibility(View.VISIBLE);
			}

			@Override
			protected Result doInBackground(Void... params) {
				SharedPreferences preferences = getSharedPreferences("mypref",
						Context.MODE_PRIVATE);

				try {
					String backgroundValue = preferences.getString(
							"background", null);
					Result result;
					if (backgroundValue != null) {
						String[] assets = getAssets().list("background");
						int position = 0;
						String backgroundFile = backgroundValue.split("/")[1];
						for (int i = 0; i < assets.length; i++) {
							if (backgroundFile.equals(assets[i])) {
								position = i;
								break;
							}
						}
						result = new Result();
						result.position = position;
						result.drawable = BackgroundUtility
								.getBitmapDrawableFromAsset(
										BackGroundActivity.this,
										backgroundValue);
					} else {
						String defaultValue = "background/"
								+ getAssets().list("background")[0];
						result = new Result();
						result.position = 0;
						result.drawable = BackgroundUtility
								.getBitmapDrawableFromAsset(
										BackGroundActivity.this, defaultValue);
					}
					return result;
				} catch (IOException e) {
					Tracker tracker = EasyTracker
							.getInstance(BackGroundActivity.this);
					tracker.send(MapBuilder.createException(
							new AnalyticsExceptionParser().getDescription(
									Thread.currentThread().toString(), e),
							false).build());
				}
				return null;
			}

			@Override
			protected void onPostExecute(Result result) {
				if (result != null) {
					imagesPreview.setSelection(result.position);
					backgroundView.setBackgroundDrawable(result.drawable);
				}
				progress.setVisibility(View.GONE);
			}
		}.execute();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.background, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_done:

			Intent data = new Intent();
			data.putExtra("background", adapter.getChoosedBacground());
			setResult(Activity.RESULT_OK, data);
			finish();
			MyTracker.fireButtonPressedEvent(BackGroundActivity.this,
					"change_background");
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
