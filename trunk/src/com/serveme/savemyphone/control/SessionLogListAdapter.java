package com.serveme.savemyphone.control;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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

public class SessionLogListAdapter extends SimpleCursorAdapter {

	public SessionLogListAdapter(final Context context, Cursor appsLog) {
		super(context, R.layout.session_item_layout, appsLog, new String[0],
				new int[0], 0);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View newView = super.newView(context, cursor, parent);
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.sessionTime = (TextView) newView
				.findViewById(R.id.session_time);
		newView.setTag(viewHolder); // first you set Tag to get it later
		return newView;
	}

	@Override
	public void bindView(View convertView, Context context, Cursor cursor) {
		super.bindView(convertView, context, cursor);
		Date sessionDate = new Date(cursor.getLong(cursor
				.getColumnIndex(DB_KEYS.KEY_SESSION_DATE)));
		ViewHolder viewHolder = (ViewHolder) convertView.getTag();

		viewHolder.sessionTime.setText(String.format("%tr %tF", sessionDate,
				sessionDate));
		viewHolder.sessionId = cursor.getLong(cursor
				.getColumnIndex(DB_KEYS.KEY_ID));
	}

	public static class ViewHolder {
		public TextView sessionTime;
		public long sessionId;
	}

}
