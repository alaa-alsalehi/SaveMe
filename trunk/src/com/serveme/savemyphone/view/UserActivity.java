package com.serveme.savemyphone.view;

import group.pals.android.lib.ui.lockpattern.LockPatternActivity;
import group.pals.android.lib.ui.lockpattern.prefs.DisplayPrefs;

import java.util.List;

import com.serveme.savemyphone.R;
import com.serveme.savemyphone.control.GridAdapter;
import com.serveme.savemyphone.model.DBOperations;
import com.serveme.savemyphone.service.AppsMonitor;
import android.app.Activity;
import android.app.WallpaperManager;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class UserActivity extends Activity {

	final Context context = this;
	List<String> appsinfolist;
	private DBOperations db;

	private static final int REQ_ENTER_PATTERN = 2;

	DevicePolicyManager devicePolicyManager;
	ComponentName adminComponent;

	/** Called when the activity is first created. */
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.user_activity);
		db = new DBOperations(this);
		appsinfolist = db.getWhiteListApps();
		registerReceiver(bcr, new IntentFilter("finish_user_activity"));
		startService(new Intent(this, AppsMonitor.class));

		GridView gridView = (GridView) findViewById(R.id.grid_view);
		gridView.setBackgroundDrawable(WallpaperManager.getInstance(context)
				.getDrawable());
		gridView.setAdapter(new GridAdapter(this, appsinfolist));
		gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
		gridView.setNumColumns(GridView.AUTO_FIT);

		devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		adminComponent = new ComponentName(UserActivity.this,
				AdminReciver.class);

		// float scalefactor = getResources().getDisplayMetrics().density * 80;
		// Point size = new Point();
		// this.getWindowManager().getDefaultDisplay().getSize(size);
		// int screenWidth = size.x;
		// int columns = (int) ((float) screenWidth / (float) scalefactor);
		// gridView.setNumColumns(columns);

		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Intent i = getPackageManager().getLaunchIntentForPackage(
						appsinfolist.get(position));
				startActivity(i);
			}
		});

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(bcr);
	}

	@Override
	public void onBackPressed() {
		// your code.
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
			SharedPreferences mySharedPreferences = getSharedPreferences(
					"mypref", Context.MODE_PRIVATE);
			String pass_code = mySharedPreferences.getString("pass_code",
					"null");
			char[] savedPattern = pass_code.toCharArray();
			DisplayPrefs.setStealthMode(context, false);
			Intent intent = new Intent(
					LockPatternActivity.ACTION_COMPARE_PATTERN, null, context,
					LockPatternActivity.class);
			intent.putExtra(LockPatternActivity.EXTRA_PATTERN, savedPattern);
			startActivityForResult(intent, REQ_ENTER_PATTERN);
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
				Log.v("result", "passed");
				db.updateStatus(0);
				context.stopService(new Intent(context, AppsMonitor.class));
				break;
			case RESULT_CANCELED:
				Log.v("result", "canceled");
				break;
			case LockPatternActivity.RESULT_FAILED:
				Log.v("result", "faild");
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

	// @Override
	// public void onAttachedToWindow()
	// {
	// Log.i("TESTE", "onAttachedToWindow");
	// this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
	// super.onAttachedToWindow();
	// }

	private final BroadcastReceiver bcr = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			finish();
		}
	};

	// private void showUnlockDialog(){
	// AlertDialog.Builder alert = new AlertDialog.Builder(this);
	//
	// alert.setTitle("Unlock Phone");
	// alert.setMessage("Enter the password");
	//
	// // Set an EditText view to get user input
	// final EditText input = new EditText(this);
	// input.setInputType(InputType.TYPE_CLASS_TEXT |
	// InputType.TYPE_TEXT_VARIATION_PASSWORD);
	// alert.setView(input);
	//
	// alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int whichButton) {
	// String value = input.getText().toString();
	// Log.v("text", value);
	// if(value.equals("omar")){
	// db.updateStatus(0);
	// context.stopService(new Intent(context,AppsMonitor.class));
	// Toast.makeText(context, "done", Toast.LENGTH_LONG).show();
	// finish();
	// }
	// }
	// });
	//
	// alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int whichButton) {
	// // Canceled.
	// }
	// });
	//
	// alert.show();
	// }
}
