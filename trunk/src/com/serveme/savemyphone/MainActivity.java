package com.serveme.savemyphone;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class MainActivity extends Activity {

	List<ApplicationInfo> appsinfolist;
	List<ApplicationInfo> newList;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		appsinfolist = getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
		newList = new ArrayList<ApplicationInfo>();
		for (ApplicationInfo appinfo : appsinfolist) {
			if (getPackageManager().getLaunchIntentForPackage(appinfo.packageName) != null && !getPackageManager().getLaunchIntentForPackage(appinfo.packageName).equals("")) {
				newList.add(appinfo);
			}
		}

		GridView gridView = (GridView) findViewById(R.id.grid_view);
		gridView.setAdapter(new GridAdapter(this, newList, gridView));

		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Intent i = getPackageManager().getLaunchIntentForPackage(newList.get(position).packageName);
				startActivity(i);
			}
		});

	}

}
