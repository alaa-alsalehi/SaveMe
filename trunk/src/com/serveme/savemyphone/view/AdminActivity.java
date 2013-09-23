package com.serveme.savemyphone.view;

import java.util.List;

import com.serveme.savemyphone.R;
import com.serveme.savemyphone.control.AppsListAdapter;
import com.serveme.savemyphone.model.DBOperations;
import com.serveme.savemyphone.service.AppsMonitor;


import android.app.ListActivity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class AdminActivity extends ListActivity {

	static Context context;
	static List<String> whitelist = null;
	static DBOperations db;
	private DevicePolicyManager devicePolicyManager;
	private ComponentName adminComponent;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		checkAdminAccess();
		super.onCreate(savedInstanceState);
		context = this;
		db = new DBOperations(context);
		AppsListAdapter adapter = new AppsListAdapter(context);
		this.setListAdapter(adapter);
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
			db.updateStatus(1);
			Intent saveintent = new Intent(getBaseContext(), UserActivity.class);
			startActivity(saveintent);
			context.startService(new Intent(context, AppsMonitor.class));
			finish();
			return true;
		case R.id.action_settings:

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}


	private void checkAdminAccess() {
		devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		adminComponent = new ComponentName(AdminActivity.this,
				AdminReciver.class);
		if (!devicePolicyManager.isAdminActive(adminComponent)) {
			finish();
			Intent intent = new Intent(getBaseContext(), AdminRequest.class);
			startActivity(intent);
		}
	}

//	private void showConfirmationDialog() {
//		AlertDialog.Builder alert = new AlertDialog.Builder(this);
//
//		alert.setTitle("Confirmation");
//		alert.setMessage("Are you sure you want to uninstall the app ?");
//
//		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int whichButton) {
//
//			}
//		});
//
//		alert.setNegativeButton("Cancel",
//				new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int whichButton) {
//
//					}
//				});
//
//		alert.show();
//	}

//	 private void showPasswordDialog() {
//	 AlertDialog.Builder alert = new AlertDialog.Builder(this);
//	
//	 alert.setTitle("Lock Phone");
//	 alert.setMessage("Enter the password");
//	
//	 // Set an EditText view to get user input
//	 final EditText input = new EditText(this);
//	 input.setInputType(InputType.TYPE_CLASS_TEXT
//	 | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//	 alert.setView(input);
//	
//	 alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//	 public void onClick(DialogInterface dialog, int whichButton) {
//	 String value = input.getText().toString();
//	 if (value.equals("omar")) {
//	 AdminActivity.getDBOperator().updateStatus(1);
//	 Intent saveintent = new Intent(getBaseContext(),UserActivity.class);
//	 startActivity(saveintent);
//	 context.startService(new Intent(context, AppsMonitor.class));
//	 finish();
//	 }
//	 }
//	 });
//	
//	 alert.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
//	 public void onClick(DialogInterface dialog, int whichButton) {
//	 // Canceled.
//	 }
//	 });
//	
//	 alert.show();
//	 }

}
