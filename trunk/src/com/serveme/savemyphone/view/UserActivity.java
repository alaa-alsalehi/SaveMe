package com.serveme.savemyphone.view;

import group.pals.android.lib.ui.lockpattern.LockPatternActivity;
import group.pals.android.lib.ui.lockpattern.prefs.DisplayPrefs;

import java.util.List;

import com.serveme.savemyphone.R;
import com.serveme.savemyphone.control.GridAdapter;
import com.serveme.savemyphone.model.DBOperations;
import com.serveme.savemyphone.preferences.PrefEditor;
import com.serveme.savemyphone.receivers.AdminReciver;
import com.serveme.savemyphone.service.AppsMonitor;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.WallpaperManager;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
@SuppressLint("NewApi")
public class UserActivity extends ActionBarActivity {

	final Context context = this;
	List<String> appsinfolist;
	private DBOperations db;

	private static final int REQ_ENTER_PATTERN = 2;

	DevicePolicyManager devicePolicyManager;
	ComponentName adminComponent;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB){
			requestWindowFeature(Window.FEATURE_NO_TITLE);
		}
//		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
//		ActionBar actionBar = getActionBar();
//		actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#33ffffff")));
//		actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#55ffffff")));
//		actionBar.setDisplayShowHomeEnabled(false);
//		actionBar.setDisplayShowTitleEnabled(false);
		
		setContentView(R.layout.user_activity);
		db = new DBOperations(this);
		appsinfolist = db.getWhiteListApps();
		registerReceiver(bcr, new IntentFilter("finish_user_activity"));
		startService(new Intent(this, AppsMonitor.class));

		GridView gridView = (GridView) findViewById(R.id.grid_view);
		gridView.setBackgroundDrawable(WallpaperManager.getInstance(context).getDrawable());
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
			new Checker(this).checkPattern(REQ_ENTER_PATTERN);
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
				PrefEditor pe = new PrefEditor(UserActivity.this);
				pe.updateStatus(0);
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
