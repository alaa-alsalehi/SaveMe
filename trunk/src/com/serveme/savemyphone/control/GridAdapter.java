package com.serveme.savemyphone.control;

import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GridAdapter extends BaseAdapter {

	private Context context;

	List<String> aList = null;

	// Constructor
	public GridAdapter(Context c, List<String> aList) {
		this.context = c;
		this.aList = aList;
	}

	@Override
	public int getCount() {
		return aList.size();
	}

	@Override
	public Object getItem(int position) {
		return aList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView txtView;
		if (convertView == null) { // if it's not recycled, initialize some attributes
			txtView = new TextView(context);
//			txtView.setTextColor(Color.WHITE);
			txtView.setGravity(Gravity.CENTER_HORIZONTAL);
			txtView.setLines(2); // to make all text view in the same size
			txtView.setPadding(0, 30, 0, 0);
			txtView.setCompoundDrawablePadding(10);
		} else {
			txtView = (TextView) convertView;
		}

		ApplicationInfo appinfo = null;
		try {
			appinfo = context.getPackageManager().getApplicationInfo(aList.get(position),PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		txtView.setText(appinfo.loadLabel((context.getPackageManager())));
		Drawable img = appinfo.loadIcon(context.getPackageManager());
//		GridView gridview = (GridView) parent;
//		img.setBounds(0, 0, (int)(gridview.getColumnWidth()/2), (int)(gridview.getColumnWidth()/2));
//		txtView.setCompoundDrawables(null, img, null, null);
		float scalefactor = context.getResources().getDisplayMetrics().density * 50;
		img.setBounds(0, 0, (int)(scalefactor), (int)(scalefactor));
//		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//		int iconSize = am.getLauncherLargeIconSize();
//		img.setBounds(0, 0, iconSize,iconSize);
		txtView.setCompoundDrawables(null, img, null, null);
//		txtView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);
		
		return txtView;
	}

}