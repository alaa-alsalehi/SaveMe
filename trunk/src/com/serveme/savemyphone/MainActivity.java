package com.serveme.savemyphone;

import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class MainActivity extends Activity {

	List<ResolveInfo> appsinfolist;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

//		startService(new Intent(this, AppsMonitor.class));
		DBHandler db = new DBHandler(this);
		SQLiteDatabase sqldb = db.getWritableDatabase();
		
		Intent in = new Intent(Intent.ACTION_MAIN);
		in.addCategory(Intent.CATEGORY_LAUNCHER);
		appsinfolist = getPackageManager().queryIntentActivities(in, 0);

		GridView gridView = (GridView) findViewById(R.id.grid_view);
		gridView.setAdapter(new GridAdapter(this, appsinfolist, gridView));

		gridView.setStretchMode( GridView.STRETCH_COLUMN_WIDTH );
		gridView.setNumColumns( GridView.AUTO_FIT );
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Intent i = getPackageManager().getLaunchIntentForPackage(appsinfolist.get(position).activityInfo.packageName);
//				stopService(new Intent(getBaseContext(), AppsMonitor.class));
				startActivity(i);
			}
		});

	}
	
	@Override
	public void onBackPressed() {
	    // your code.
	}

}
