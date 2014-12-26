package com.serveme.savemyphone.control;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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

import com.serveme.savemyphone.paid.R;
import com.serveme.savemyphone.model.DBOperations;
import com.serveme.savemyphone.model.Launcher;
import com.serveme.savemyphone.util.MyTracker;

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
				List<ResolveInfo> aList = context.getPackageManager()
						.queryIntentActivities(in, 0);
				status = new boolean[aList.size()]; // to keep enable/disable
													// status
				Log.v("count", aList.size() + "");
				db = DBOperations.getInstance(context);
				whitelist = db.getWhiteListApps(); // allowed applications
				Collections.sort(aList, new ResolveInfo.DisplayNameComparator(
						context.getPackageManager()));
				for (Iterator<ResolveInfo> it = aList.iterator(); it.hasNext();) {
					ResolveInfo rinfo = it.next();
					if (rinfo.activityInfo.packageName
							.equals("com.serveme.savemyphone")) {
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

		if (viewHolder.imgLoader != null) {
			viewHolder.imgLoader.cancel(true);
		}

		viewHolder.tg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					status[position] = true;
					if (!whitelist.contains(launcher)) {
						db.insertoApp(launcher);
					}
				} else {
					status[position] = false;
					if (whitelist.contains(launcher)) {
						db.deleteLauncher(launcher);
					}
				}
				MyTracker.fireButtonPressedEvent(context, "enable_disable_app");
			}
		});

		viewHolder.name
				.setText(appinfo.loadLabel((context.getPackageManager())));

		ImageView appicon = viewHolder.icon;
		appicon.setImageDrawable(null);

		viewHolder.imgLoader = new ImageLoader(context, appicon);
		viewHolder.imgLoader.execute(appinfo);

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
		public ImageLoader imgLoader;
	}

	public void updateList(List<ResolveInfo> result) {
		appsList.addAll(result);
		this.notifyDataSetChanged();
	}

	private class ImageLoader extends AsyncTask<ResolveInfo, Integer, Drawable> {
		private Context con;
		private WeakReference<ImageView> wrimageView;

		public ImageLoader(Context con, ImageView imageView) {
			this.wrimageView = new WeakReference<ImageView>(imageView);
			this.con = con;
		}

		protected Drawable doInBackground(ResolveInfo... appinfo) {
			Drawable img = null;
			// if(imageView.getDrawingCache() != null) {
			// img = con.getResources().getDrawable(R.drawable.ic_launcher);
			img = appinfo[0].loadIcon(con.getPackageManager());
			int imagesize = (int) con.getResources().getDimension(
					R.dimen.image_size);
			img.setBounds(0, 0, imagesize, imagesize);
			// } else {
			//
			// }

			return img;
		}

		protected void onPostExecute(Drawable img) {
			ImageView imageView = wrimageView.get();
			if (imageView != null)
				imageView.setImageDrawable(img);
		}

	}

}
