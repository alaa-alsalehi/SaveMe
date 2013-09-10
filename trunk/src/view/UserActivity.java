package view;

import java.util.List;

import model.DBOperations;

import service.AppsMonitor;

import com.serveme.savemyphone.R;
import control.GridAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

public class UserActivity extends Activity {

	final Context context = this;
	List<String> appsinfolist;
	private DBOperations db;

	protected static final int REQUEST_ENABLE = 0;
	DevicePolicyManager devicePolicyManager;
	ComponentName adminComponent;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_main);

		db = new DBOperations(this);

		appsinfolist = db.getWhiteListApps();

		startService(new Intent(this, AppsMonitor.class));

		for (String s : appsinfolist) {
			Log.v("Item ", s);
		}

		GridView gridView = (GridView) findViewById(R.id.grid_view);
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
			db.updateStatus(0);
			context.stopService(new Intent(context, AppsMonitor.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	// @Override
	// public void onAttachedToWindow()
	// {
	// Log.i("TESTE", "onAttachedToWindow");
	// this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
	// super.onAttachedToWindow();
	// }

	private void showUnlockDialog(){
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);

    	alert.setTitle("Unlock Phone");
    	alert.setMessage("Enter the password");

    	// Set an EditText view to get user input 
    	final EditText input = new EditText(this);
    	input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    	alert.setView(input);

    	alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
    	public void onClick(DialogInterface dialog, int whichButton) {
    	  String value = input.getText().toString();
    	  Log.v("text", value);
    	  if(value.equals("omar")){
    		  db.updateStatus(0);
    		  context.stopService(new Intent(context,AppsMonitor.class));
    		  Toast.makeText(context, "done", Toast.LENGTH_LONG).show();
    		  finish();
    	  }
    	  }
    	});

    	alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    	  public void onClick(DialogInterface dialog, int whichButton) {
    	    // Canceled.
    	  }
    	});

    	alert.show();
	}
}
