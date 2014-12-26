package com.serveme.savemyphone.mail;

import java.net.UnknownHostException;

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
import com.serveme.savemyphone.paid.R;
import com.serveme.savemyphone.preferences.PrefEditor;
import com.serveme.savemyphone.view.RecoveryActivity;
import com.serveme.savemyphone.view.utils.AnalyticsExceptionParser;

public class CodeVerifier implements Response.Listener<String>,
		Response.ErrorListener {

	private RecoveryActivity recoveryActivity;

	public CodeVerifier(RecoveryActivity recoveryActivity, String code) {
		this.recoveryActivity = recoveryActivity;
		RequestQueue mRequestQueue;

		mRequestQueue = Volley.newRequestQueue(recoveryActivity);
		String email = new PrefEditor(recoveryActivity).getRecoveryEmail();
		StringRequest jr = new StringRequest(Request.Method.GET,
				"http://saveme-verfication.appspot.com/VerifyConfirmationCode?email="
						+ email + "&code=" + code, this, this);

		mRequestQueue.add(jr);
	}

	@Override
	public void onResponse(String result) {
		String message = null;
		if (result.equals("success")) {
			message = recoveryActivity.getResources().getString(
					R.string.recovery_activity_success_dialoge);
			recoveryActivity.changePassword();
		} else if (result.equals("fail")) {
			message = recoveryActivity.getResources().getString(
					R.string.recovery_activity_failure_dialoge);
		} else {
			message = "Oooops " + result;
		}

		Toast.makeText(recoveryActivity, message, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		Toast.makeText(recoveryActivity, R.string.network_failure,
				Toast.LENGTH_LONG).show();

		if (!(error.getCause() instanceof UnknownHostException)) {
			Tracker tracker = EasyTracker.getInstance(recoveryActivity);
			tracker.send(MapBuilder.createException(
					new AnalyticsExceptionParser().getDescription(Thread
							.currentThread().toString(), error.getCause()),
					false).build());
		}
	}

}
