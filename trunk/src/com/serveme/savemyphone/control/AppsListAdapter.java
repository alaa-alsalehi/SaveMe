package com.serveme.savemyphone.control;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.serveme.savemyphone.R;
import com.serveme.savemyphone.model.DBOperations;
import com.serveme.savemyphone.model.Launcher;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class AppsListAdapter extends BaseAdapter {

	private Context context;
	//private final ImageLoader imageloader = new ImageLoader();
	private List<ResolveInfo> aList = null;
	private Set<Launcher> whitelist = null;
	private static boolean[] status;
	private LayoutInflater inflater = null;
	private DBOperations db;

	public AppsListAdapter(Context context) {
		this.context = context;
		Intent in = new Intent(Intent.ACTION_MAIN);
		in.addCategory(Intent.CATEGORY_LAUNCHER);
		aList = context.getPackageManager().queryIntentActivities(in, 0);
		db = new DBOperations(context);
		whitelist = db.getWhiteListApps(); // allowed applications
		status = new boolean[aList.size()]; // to keep enable/disable status
		inflater = LayoutInflater.from(context);
		Collections.sort(aList,new ResolveInfo.DisplayNameComparator(context.getPackageManager()));
		for (Iterator<ResolveInfo> it = aList.iterator(); it.hasNext();) {
			ResolveInfo rinfo = it.next();
			if (rinfo.activityInfo.packageName.equals("com.serveme.savemyphone")) {
				it.remove();
			}
		}
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		final ResolveInfo appinfo = aList.get(position);
		final Launcher launcher = new Launcher(appinfo.activityInfo.packageName, appinfo.activityInfo.name);

		// more performance for ListView to use Holder Pattern
		ViewHolder viewHolder;

		if (convertView == null) {
			// if it's not recycled, initialize some attributes
			convertView = inflater.inflate(R.layout.item_layout, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
			viewHolder.tg = (ToggleButton) convertView.findViewById(R.id.enable_disable);
			convertView.setTag(viewHolder); // first you set Tag to get it later
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					status[position] = true;
					if (!whitelist.contains(launcher)) {
						db.insertöApp(launcher);
						whitelist.add(launcher);
					}
				} else {
					status[position] = false;
					if (whitelist.contains(launcher)) {
						db.deleteLauncher(launcher);
						whitelist.remove(launcher);
					}
				}
				EasyTracker.getInstance(context).send(
						MapBuilder.createEvent("ui_action", "button_press",
								"enable_disable_app", Long.valueOf(1))
								.build());
			}
		});

		viewHolder.name.setText(appinfo.loadLabel((context.getPackageManager())));
		Drawable img = appinfo.loadIcon(context.getPackageManager());
    	int imagesize = (int) context.getResources().getDimension(R.dimen.image_size);
		img.setBounds(0, 0, imagesize, imagesize);
		 viewHolder.icon.setImageDrawable(img);
		//imageloader.load(viewHolder.icon, appinfo, context);
		if (whitelist.contains(launcher)) {	status[position] = true; }
		viewHolder.tg.setChecked(status[position]);

		return convertView;
	}

	public static class ViewHolder {
		public TextView name;
		public ImageView icon;
		public ToggleButton tg;
	}

}
