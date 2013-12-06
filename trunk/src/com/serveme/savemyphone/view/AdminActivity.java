package com.serveme.savemyphone.view;

//import org.omar.android.lib.ui.passwordlock.PasswordEntryActivity;

import group.pals.android.lib.ui.lockpattern.LockPatternActivity;

import com.serveme.savemyphone.R;
import com.serveme.savemyphone.control.AppsListAdapter;
import com.serveme.savemyphone.preferences.PrefEditor;
import com.serveme.savemyphone.receivers.AdminReciver;
import com.serveme.savemyphone.service.AppsMonitor;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

public class AdminActivity extends ActionBarActivity {


	private DevicePolicyManager devicePolicyManager;
	private ComponentName adminComponent;
	private static final int REQ_ENTER_PATTERN = 2;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Calling this to ensures that your application is properly initialized
		// with default settings
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

		checkAdminAccess();

		checkPassCode();
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		AppsListAdapter adapter = new AppsListAdapter(this);
		ListView listView = (ListView) findViewById(R.id.app_list);
		listView.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.admin, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_lock:
			PrefEditor pe = new PrefEditor(AdminActivity.this);
			pe.updateStatus(1);
			Intent saveintent = new Intent(getBaseContext(), UserActivity.class);
			startActivity(saveintent);
			/*
			 * Intent saveintent = new
			 * Intent(getBaseContext(),PasswordEntryActivity.class);
			 * startActivity(saveintent); context.startService(new
			 * Intent(context, AppsMonitor.class)); finish();
			 */
			return true;
		case R.id.action_settings:
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void checkAdminAccess() {
		devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		adminComponent = new ComponentName(AdminActivity.this,	AdminReciver.class);
		if (!devicePolicyManager.isAdminActive(adminComponent)) {
			finish();
			Intent intent = new Intent(getBaseContext(), AdminRequest.class);
			startActivity(intent);
		}
	}

	private void checkPassCode() {
		if (devicePolicyManager.isAdminActive(adminComponent)) {
			SharedPreferences mySharedPreferences = getSharedPreferences(
					"mypref", Context.MODE_PRIVATE);
			if (mySharedPreferences != null	&& mySharedPreferences.contains("pass_code")) {

//				if(pattern){
				new Checker(this).checkPattern(REQ_ENTER_PATTERN);
//				}else{
					
//				}
			} else {
				finish();
				Intent intent = new Intent(getBaseContext(),
						PasswordRequest.class);
				startActivity(intent);
			}
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQ_ENTER_PATTERN: {
			switch (resultCode) {
			case RESULT_OK:
				Log.v("result", "passed");
				break;
			case RESULT_CANCELED:
				finish();
				break;
			case LockPatternActivity.RESULT_FAILED:
				finish();
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

	// private void showConfirmationDialog() {
	// AlertDialog.Builder alert = new AlertDialog.Builder(this);
	//
	// alert.setTitle("Confirmation");
	// alert.setMessage("Are you sure you want to uninstall the app ?");
	//
	// alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int whichButton) {
	//
	// }
	// });
	//
	// alert.setNegativeButton("Cancel",
	// new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int whichButton) {
	//
	// }
	// });
	//
	// alert.show();
	// }

	// private void showPasswordDialog() {
	// AlertDialog.Builder alert = new AlertDialog.Builder(this);
	//
	// alert.setTitle("Lock Phone");
	// alert.setMessage("Enter the password");
	//
	// // Set an EditText view to get user input
	// final EditText input = new EditText(this);
	// input.setInputType(InputType.TYPE_CLASS_TEXT
	// | InputType.TYPE_TEXT_VARIATION_PASSWORD);
	// alert.setView(input);
	//
	// alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int whichButton) {
	// String value = input.getText().toString();
	// if (value.equals("omar")) {
	// AdminActivity.getDBOperator().updateStatus(1);
	// Intent saveintent = new Intent(getBaseContext(),UserActivity.class);
	// startActivity(saveintent);
	// context.startService(new Intent(context, AppsMonitor.class));
	// finish();
	// }
	// }
	// });
	//
	// alert.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int whichButton) {
	// // Canceled.
	// }
	// });
	//
	// alert.show();
	// }

}
