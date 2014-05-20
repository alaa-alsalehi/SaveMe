package com.serveme.savemyphone.mail;

import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.serveme.analytics.AnalyticsExceptionParser;
import com.serveme.savemyphone.R;
import com.serveme.savemyphone.preferences.PrefEditor;
import com.serveme.savemyphone.view.RecoveryActivity;

public class MailSender implements Response.Listener<String>,
		Response.ErrorListener {

	private RecoveryActivity recoveryActivity;

	public MailSender(RecoveryActivity recoveryActivity) {
		this.recoveryActivity = recoveryActivity;
		RequestQueue mRequestQueue;

		mRequestQueue = Volley.newRequestQueue(recoveryActivity);
		String email = new PrefEditor(recoveryActivity).getRecoveryEmail();
		StringRequest jr = new StringRequest(Request.Method.GET,
				"http://saveme-verfication.appspot.com/GetConfirmationCode?email="
						+ email, this, this);

		mRequestQueue.add(jr);
	}

	@Override
	public void onResponse(String result) {
		Toast.makeText(recoveryActivity, result, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		Toast.makeText(recoveryActivity, R.string.network_failure,
				Toast.LENGTH_LONG).show();
		recoveryActivity.finish();
		Tracker tracker = EasyTracker.getInstance(recoveryActivity);
		tracker.send(MapBuilder.createException(
				new AnalyticsExceptionParser().getDescription(Thread
						.currentThread().toString(), error.getCause()), false)
				.build());
	}

}
