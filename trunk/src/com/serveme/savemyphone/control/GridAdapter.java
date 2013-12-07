package com.serveme.savemyphone.control;

import java.util.List;

import com.serveme.savemyphone.R;
import com.serveme.savemyphone.model.Launcher;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GridAdapter extends BaseAdapter {

	private Context context;

	List<Launcher> aList = null;

	// Constructor
	public GridAdapter(Context c, List<Launcher> aList) {
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
		if (convertView == null) { // if it's not recycled, initialize some
									// attributes
			txtView = new TextView(context);
			txtView.setTextColor(Color.WHITE);
			txtView.setShadowLayer(5, 1, 1, Color.BLACK);
			txtView.setGravity(Gravity.CENTER_HORIZONTAL);
			txtView.setLines(2); // to make all text view in the same size
			int padding = context.getResources().getDimensionPixelSize(
					R.dimen.grid_item_padding);
			txtView.setPadding(0, padding, 0, 0);
			txtView.setCompoundDrawablePadding(10);
		} else {
			txtView = (TextView) convertView;
		}

		ActivityInfo appinfo = null;
		try {
			Launcher launcher = aList.get(position);
			appinfo = context.getPackageManager().getActivityInfo(new ComponentName(launcher.getPackageName(),launcher.getActivity()),
					PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		txtView.setText(appinfo.loadLabel((context.getPackageManager())));
		Drawable img = appinfo.loadIcon(context.getPackageManager());
		// GridView gridview = (GridView) parent;
		// img.setBounds(0, 0, (int)(gridview.getColumnWidth()/2),
		// (int)(gridview.getColumnWidth()/2));
		// txtView.setCompoundDrawables(null, img, null, null);
		// int imagesize = (int)
		// context.getResources().getDimensionPixelSize(R.dimen.image_size);
		// img.setBounds(0, 0, imagesize, imagesize);
		float scalefactor = context.getResources().getDisplayMetrics().density * 50;
		img.setBounds(0, 0, (int) (scalefactor), (int) (scalefactor));
		// ActivityManager am = (ActivityManager)
		// context.getSystemService(Context.ACTIVITY_SERVICE);
		// int iconSize = am.getLauncherLargeIconSize();
		// img.setBounds(0, 0, iconSize,iconSize);
		txtView.setCompoundDrawables(null, img, null, null);
		// txtView.setCompoundDrawablesWithIntrinsicBounds(null, img, null,
		// null);

		return txtView;
	}
}