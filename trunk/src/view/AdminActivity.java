package view;

import java.util.List;

import service.AppsMonitor;
import model.DBOperations;
import com.serveme.savemyphone.R;
import control.AppsListAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.admin.DeviceAdminInfo;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

public class AdminActivity extends ListActivity {

	static Context context;
	static List<String> whitelist = null;
	static DBOperations db;
	private final int REQUEST_ENABLE = 0;
	private DevicePolicyManager devicePolicyManager;
	private ComponentName adminComponent;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AdminActivity.setContext(this);
		db = DBOperations.getInstance(context);
		AppsListAdapter adapter = new AppsListAdapter(context);
		this.setListAdapter(adapter);
		checkAdminAccess();
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
			AdminActivity.getDBOperator().updateStatus(1);
			Intent saveintent = new Intent(getBaseContext(), UserActivity.class);
			saveintent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivity(saveintent);
			context.startService(new Intent(context, AppsMonitor.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public static DBOperations getDBOperator() {
		if (db == null) {
			db = DBOperations.getInstance(context);
		}
		return db;
	}

	public static List<String> getWhiteList() {
		if (whitelist == null) {
			whitelist = getDBOperator().getWhiteListApps();
		}
		return whitelist;
	}

	public static void setContext(Context c) {
		context = c;
	}

	private void checkAdminAccess() {
		devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		adminComponent = new ComponentName(AdminActivity.this,
				AdminReciver.class);
		if (!devicePolicyManager.isAdminActive(adminComponent)) {
			finish();
			Intent intent = new Intent(getBaseContext(), AdminRequest.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivity(intent);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (REQUEST_ENABLE == requestCode) {
			if (resultCode != Activity.RESULT_OK) {
				Intent intent = new Intent(getBaseContext(), AdminRequest.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				startActivity(intent);
			} 
		}
	}

	private void showPasswordDialog() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Lock Phone");
		alert.setMessage("Enter the password");

		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
				if (value.equals("omar")) {
					AdminActivity.getDBOperator().updateStatus(1);
					Intent saveintent = new Intent(getBaseContext(),UserActivity.class);
					startActivity(saveintent);
					context.startService(new Intent(context, AppsMonitor.class));
					finish();
				}
			}
		});

		alert.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
					}
				});

		alert.show();
	}

}
