package com.serveme.savemyphone.model;

import java.util.HashSet;
import java.util.Set;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.serveme.analytics.AnalyticsExceptionParser;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBOperations {

	private static DBHandler dbhandler;
	private Set<Launcher> whitelist;
	private Set<Launcher> whitelistPackages;
	private Context context;

	public DBOperations(Context context) {
		dbhandler = getInstance(context);
	}

	public static DBHandler getInstance(Context c) {
		return dbhandler != null ? dbhandler : (dbhandler = new DBHandler(c));
	}

	public void insertöApp(Launcher launcher) {
		ContentValues values = new ContentValues();
		values.put(DB_KEYS.KEY_PKGNAME, launcher.getPackageName());
		values.put(DB_KEYS.KEY_ACTIVITY, launcher.getActivity());
		SQLiteDatabase database = dbhandler.getWritableDatabase();
		database.insert(DB_KEYS.WHITE_LIST_TABLE, null, values);
		database.close();
	}

	public void deleteApp(String packageName) {
		SQLiteDatabase database = dbhandler.getWritableDatabase();
		database.delete(DB_KEYS.WHITE_LIST_TABLE, DB_KEYS.KEY_PKGNAME + " = ?", new String[] { packageName });
		database.close();
	}

	public void deleteLauncher(Launcher launcher) {
		SQLiteDatabase database = dbhandler.getWritableDatabase();
		database.delete(
				DB_KEYS.WHITE_LIST_TABLE,
				DB_KEYS.KEY_PKGNAME + " = ? and " + DB_KEYS.KEY_ACTIVITY
						+ " = ?",
				new String[] { launcher.getPackageName(),
						launcher.getActivity() });
		database.close();
	}

	public boolean isThereEnabledApps() {
		SQLiteDatabase database = null;
		Cursor cursor = null;
		try {
			database = dbhandler.getReadableDatabase();
			cursor = database.query(DB_KEYS.WHITE_LIST_TABLE, null, null, null,
					null, null, DB_KEYS.KEY_PKGNAME);
			if (cursor.moveToFirst()) {
				return true;
			} else {
				return false;
			}
		} finally {
			if (cursor != null) {
				try {
					cursor.close();
				} catch (Exception exception) {
					Tracker tracker = EasyTracker.getInstance(context);
					tracker.send(MapBuilder.createException(
							new AnalyticsExceptionParser().getDescription(
									Thread.currentThread().toString(),
									exception), false).build());
				}
			}
			if (database != null) {
				try {
					database.close();
				} catch (Exception exception) {
					Tracker tracker = EasyTracker.getInstance(context);
					tracker.send(MapBuilder.createException(
							new AnalyticsExceptionParser().getDescription(
									Thread.currentThread().toString(),
									exception), false).build());
				}
			}
		}
	}

	public Set<Launcher> getWhiteListApps() {
		if (whitelist == null) {
			whitelist = new HashSet<Launcher>();
			SQLiteDatabase database = dbhandler.getReadableDatabase();
			Cursor cursor = database.query(DB_KEYS.WHITE_LIST_TABLE, null,
					null, null, null, null, DB_KEYS.KEY_PKGNAME);
			// loop through all rows and add it to white list
			if (cursor.moveToFirst()) {
				do {
					String packageName = cursor.getString((cursor
							.getColumnIndexOrThrow(DB_KEYS.KEY_PKGNAME)));
					String activity = cursor.getString((cursor
							.getColumnIndexOrThrow(DB_KEYS.KEY_ACTIVITY)));
					Launcher lanucher = new Launcher(packageName, activity);
					whitelist.add(lanucher);
				} while (cursor.moveToNext());
			}
			database.close();
		}
		return whitelist;
	}

	public Set<Launcher> getWhiteListPackages() {
		if (whitelistPackages == null) {
			whitelistPackages = new HashSet<Launcher>();
			SQLiteDatabase database = dbhandler.getReadableDatabase();
			Cursor cursor = database.query(DB_KEYS.WHITE_LIST_TABLE, null,
					null, null, null, null, DB_KEYS.KEY_PKGNAME);
			// loop through all rows and add it to white list
			if (cursor.moveToFirst()) {
				do {
					String packageName = cursor.getString((cursor
							.getColumnIndexOrThrow(DB_KEYS.KEY_PKGNAME)));
					Launcher lanucher = new Launcher(packageName, null);
					whitelistPackages.add(lanucher);
				} while (cursor.moveToNext());
			}
			database.close();
		}
		return whitelistPackages;
	}

}
