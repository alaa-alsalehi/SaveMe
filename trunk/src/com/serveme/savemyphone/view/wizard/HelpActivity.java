package com.serveme.savemyphone.view.wizard;

import android.content.Intent;
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
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.serveme.savemyphone.R;
import com.serveme.savemyphone.view.AdminActivity;

public class HelpActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		String direction = getResources().getString(R.string.direction);
		TableRow choises = (TableRow) findViewById(R.id.choises_row);
		final TextView textView2 = (TextView) findViewById(R.id.textView2);
		setTextWithImage(textView2, R.string.use_help);
		textView2.setMovementMethod(ScrollingMovementMethod.getInstance());
		final Button previousButton = (Button) findViewById(R.id.previous);
		previousButton.setVisibility(View.GONE);
		final Button nextButton = (Button) findViewById(R.id.next);
		nextButton.setText(R.string.finish);
		if ("right".equals(direction)) {
			choises.setGravity(Gravity.RIGHT | Gravity.CENTER);
			textView2.setGravity(Gravity.RIGHT | Gravity.CENTER);
			// remove and added at last of the choices row to make it right
			choises.removeView(previousButton);
			choises.addView(previousButton);
		} else {
			choises.setGravity(Gravity.LEFT | Gravity.CENTER);
		}

		previousButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

			}
		});
		nextButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(HelpActivity.this,
						AdminActivity.class);
				intent.putExtra("first_time", true);
				startActivity(intent);
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

}
