package com.serveme.savemyphone;

import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class GridAdapter extends BaseAdapter {

	private Context context;

	List<ApplicationInfo> aList = null;
	GridView gridview;

	// Constructor
	public GridAdapter(Context c, List<ApplicationInfo> aList, GridView gridview) {
		this.context = c;
		this.aList = aList;
		this.gridview = gridview;
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
		} else {
			txtView = (TextView) convertView;
		}

		ApplicationInfo appinfo = aList.get(position);
		txtView.setText(appinfo.loadLabel((context.getPackageManager())));
		txtView.setTextColor(Color.WHITE);
		txtView.setGravity(Gravity.CENTER_HORIZONTAL);
		txtView.setLines(2); // to make all text view in the same size
		Drawable img = appinfo.loadIcon(context.getPackageManager());
		// img.setBounds(0, 0, (int)(gridview.getColumnWidth()*0.60), (int)(gridview.getColumnWidth()*0.60));
		// txtView.setCompoundDrawables(null, img, null, null);
		txtView.setCompoundDrawablePadding(10);
		txtView.setCompoundDrawablesWithIntrinsicBounds(null, img, null, null);

		return txtView;
	}

}