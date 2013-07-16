package view;


import java.util.List;

import service.AppsMonitor;
import model.DBOperations;
import com.serveme.savemyphone.R;
import control.AppsListAdapter;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class AdminActivity extends ListActivity {

	static Context context;
	static List<String> whitelist = null;
	static DBOperations db;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		db = DBOperations.getInstance(context);
		Log.v("Befor", "done");
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
	        	AlertDialog.Builder alert = new AlertDialog.Builder(this);

	        	alert.setTitle("Lock Phone");
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
	        		  context.startService(new Intent(context,AppsMonitor.class));
	        		  Toast.makeText(context, "done", Toast.LENGTH_LONG).show();
	        	  }
	        	  }
	        	});

	        	alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	        	  public void onClick(DialogInterface dialog, int whichButton) {
	        	    // Canceled.
	        	  }
	        	});

	        	alert.show();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public static DBOperations getDBOperator(){
		if(db == null){
			db = DBOperations.getInstance(context);
		}
		return db;
	}

	public static List<String> getWhiteList(){
		if(whitelist == null){
			whitelist = getDBOperator().getWhiteListApps();
		}
		return whitelist;
	}
}
