package com.serveme.savemyphone.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.codechimp.apprater.AppRater;
import org.codechimp.apprater.InCorrectMarketException;

import com.serveme.savemyphone.R;
import com.serveme.savemyphone.model.DBOperations;
import com.serveme.savemyphone.model.Launcher;
import com.serveme.savemyphone.util.MyTracker;
import com.serveme.savemyphone.view.MainActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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
	// private final ImageLoader imageloader = new ImageLoader();
	private List<ResolveInfo> appsList = null;
	private Set<Launcher> whitelist = null;
	private static boolean[] status;
	private LayoutInflater inflater = null;
	private DBOperations db;

	public AppsListAdapter(final Context context) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		appsList = new ArrayList<ResolveInfo>();
		new AsyncTask<String, Integer, List<ResolveInfo>>() {
		     protected List<ResolveInfo> doInBackground(String... urls) {
		    	Intent in = new Intent(Intent.ACTION_MAIN);
		 		in.addCategory(Intent.CATEGORY_LAUNCHER);
		 		List<ResolveInfo> aList = context.getPackageManager().queryIntentActivities(in, 0);
		 		status = new boolean[aList.size()]; // to keep enable/disable status
		 		Log.v("count", aList.size()+"");
		 		db = DBOperations.getInstance(context);
				whitelist = db.getWhiteListApps(); // allowed applications
				Collections.sort(aList,	new ResolveInfo.DisplayNameComparator(context.getPackageManager()));
				for (Iterator<ResolveInfo> it = aList.iterator(); it.hasNext();) {
					ResolveInfo rinfo = it.next();
					if (rinfo.activityInfo.packageName.equals("com.serveme.savemyphone")) {
						it.remove();
					}
				}
				return aList; 
		     }
		     @Override
		     protected void onPostExecute(List<ResolveInfo> result) {
			    	updateList(result);
		     }
		 }.execute("");
	}

	@Override
	public int getCount() {
		return appsList.size();
	}

	@Override
	public Object getItem(int position) {
		return appsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		final ResolveInfo appinfo = appsList.get(position);
		final Launcher launcher = new Launcher(
				appinfo.activityInfo.packageName, appinfo.activityInfo.name);

		// more performance for ListView to use Holder Pattern
		ViewHolder viewHolder;

		if (convertView == null) {
			// if it's not recycled, initialize some attributes
			convertView = inflater.inflate(R.layout.item_layout, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
			viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
			viewHolder.tg = (ToggleButton) convertView
					.findViewById(R.id.enable_disable);
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
						db.insertoApp(launcher);
						whitelist.add(launcher);
					}
				} else {
					status[position] = false;
					if (whitelist.contains(launcher)) {
						db.deleteLauncher(launcher);
						whitelist.remove(launcher);
					}
				}
				MyTracker.fireButtonPressedEvent(context, "enable_disable_app");
			}
		});

		viewHolder.name
				.setText(appinfo.loadLabel((context.getPackageManager())));
		try {
			Drawable img = appinfo.loadIcon(context.getPackageManager());
			int imagesize = (int) context.getResources().getDimension(
					R.dimen.image_size);
			img.setBounds(0, 0, imagesize, imagesize);
			viewHolder.icon.setImageDrawable(img);
		} catch (OutOfMemoryError e) {//large images
			// TODO: handle exception
		}
		// imageloader.load(viewHolder.icon, appinfo, context);
		if (whitelist.contains(launcher)) {
			status[position] = true;
		}
		viewHolder.tg.setChecked(status[position]);

		return convertView;
	}

	public static class ViewHolder {
		public TextView name;
		public ImageView icon;
		public ToggleButton tg;
	}
	
	public void updateList(List<ResolveInfo> result){
		appsList.addAll(result);
		this.notifyDataSetChanged();
	}

}
