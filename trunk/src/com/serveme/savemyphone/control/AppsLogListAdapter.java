package com.serveme.savemyphone.control;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.codechimp.apprater.AppRater;
import org.codechimp.apprater.InCorrectMarketException;

import com.serveme.savemyphone.R;
import com.serveme.savemyphone.model.DBOperations;
import com.serveme.savemyphone.model.DB_KEYS;
import com.serveme.savemyphone.model.Launcher;
import com.serveme.savemyphone.util.MyTracker;
import com.serveme.savemyphone.view.MainActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.support.v4.widget.SimpleCursorAdapter;
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

public class AppsLogListAdapter extends SimpleCursorAdapter {

	public AppsLogListAdapter(final Context context, Cursor appsLog) {
		super(context, R.layout.applog_item_layout, appsLog, new String[0],
				new int[0], 0);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View newView = super.newView(context, cursor, parent);
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.name = (TextView) newView.findViewById(R.id.name);
		viewHolder.icon = (ImageView) newView.findViewById(R.id.icon);
		viewHolder.timeInMinutes = (TextView) newView
				.findViewById(R.id.time_in_minutes);
		newView.setTag(viewHolder); // first you set Tag to get it later
		return newView;
	}

	@Override
	public void bindView(View convertView, Context context, Cursor cursor) {
		super.bindView(convertView, context, cursor);
		String packageName = cursor.getString(cursor
				.getColumnIndex(DB_KEYS.KEY_PACKAGE_NAME));
		ViewHolder viewHolder = (ViewHolder) convertView.getTag();
		ImageView appicon = viewHolder.icon;
		appicon.setImageDrawable(null);
		ApplicationInfo applicationInfo = null;
		try {
			applicationInfo = context.getPackageManager().getApplicationInfo(
					packageName, 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (applicationInfo == null) {
			viewHolder.name.setText(packageName);
		} else {
			viewHolder.name.setText(applicationInfo.loadLabel(context
					.getPackageManager()));
		}

		double timeInMinutes = cursor.getDouble(cursor
				.getColumnIndex("all_time")) / 60000.0;
		viewHolder.timeInMinutes.setText(String.format("%.2f", timeInMinutes));
		if (viewHolder.imgLoader != null)
			viewHolder.imgLoader.cancel(true);
		viewHolder.imgLoader = new ImageLoader(context,
				(ImageView) convertView.findViewById(R.id.icon));
		viewHolder.imgLoader.execute(packageName);
	}

	public static class ViewHolder {
		public TextView name;
		public ImageView icon;
		public TextView timeInMinutes;
		public ImageLoader imgLoader;
	}

	private class ImageLoader extends AsyncTask<String, Integer, Drawable> {
		private Context con;
		private WeakReference<ImageView> wrimageView;

		public ImageLoader(Context con, ImageView imageView) {
			this.wrimageView = new WeakReference<ImageView>(imageView);
			this.con = con;
		}

		protected Drawable doInBackground(String... packageName) {
			Drawable img = null;
			// if(imageView.getDrawingCache() != null) {
			// img = con.getResources().getDrawable(R.drawable.ic_launcher);
			try {
				img = con.getPackageManager()
						.getApplicationIcon(packageName[0]);
				int imagesize = (int) con.getResources().getDimension(
						R.dimen.image_size);
				img.setBounds(0, 0, imagesize, imagesize);
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

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
