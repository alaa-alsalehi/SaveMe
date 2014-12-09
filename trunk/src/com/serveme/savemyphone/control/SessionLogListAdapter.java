package com.serveme.savemyphone.control;

import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.serveme.savemyphone.R;
import com.serveme.savemyphone.model.DB_KEYS;

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
