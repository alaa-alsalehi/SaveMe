package com.serveme.savemyphone.view.wizard;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import com.serveme.savemyphone.R;
import com.serveme.savemyphone.util.MyTracker;
import com.serveme.savemyphone.view.MainActivity;
import com.serveme.savemyphone.view.UserLauncherActivity;

public class HelpActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guide_activity);
		final TextView textView = (TextView) findViewById(R.id.textView);
		setTextWithImage(textView, R.string.usage_help_text);
		textView.setMovementMethod(ScrollingMovementMethod.getInstance());
		Button okButton = (Button) findViewById(R.id.btn);
		okButton.setText(android.R.string.ok);
		String direction = getResources().getString(R.string.direction);
		if (direction.equals("right")) {
			textView.setGravity(Gravity.RIGHT);
		} else {
			textView.setGravity(Gravity.LEFT);
		}

		okButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (getIntent() != null) {
					if (getIntent().getBooleanExtra("first_time", false)) {
						Intent intent = new Intent(HelpActivity.this,
								MainActivity.class);
						intent.putExtra("first_time", true);
						startActivity(intent);
						getPackageManager().setComponentEnabledSetting(
								new ComponentName(HelpActivity.this,
										UserLauncherActivity.class),
								PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
								PackageManager.DONT_KILL_APP);
					}
				}
				MyTracker.fireButtonPressedEvent(HelpActivity.this,
						"finish_wizard");
				finish();
			}
		});
	}

	private void setTextWithImage(TextView textView, int resourceId) {
		String useHelp = getResources().getString(resourceId);
		int index = useHelp.indexOf("{1}");
		if (index < 0) {
			throw new UnknownError();
		}
		SpannableStringBuilder ssb = new SpannableStringBuilder(useHelp);
		ImageSpan imageSpan = new ImageSpan(HelpActivity.this, R.drawable.lock,
				ImageSpan.ALIGN_BASELINE);
		ssb.setSpan(imageSpan, index, index + 3,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		index = useHelp.indexOf("{2}");
		if (index < 0) {
			throw new UnknownError();
		}
		imageSpan = new ImageSpan(HelpActivity.this, R.drawable.unlock,
				ImageSpan.ALIGN_BASELINE);
		ssb.setSpan(imageSpan, index, index + 3,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		textView.setText(ssb, BufferType.SPANNABLE);
	}

	@Override
	protected void onStart() {
		super.onStart();
		MyTracker.fireActivityStartEvent(HelpActivity.this);
		MyTracker.getUncaughtExceptionHandler();
	}

	@Override
	protected void onStop() {
		MyTracker.fireActivityStopevent(HelpActivity.this);
		super.onStop();
	}

}
