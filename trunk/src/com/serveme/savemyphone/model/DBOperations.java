package com.serveme.savemyphone.model;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBOperations {

	private static DBHandler dbhandler;
	private Cursor cursor;
	private List<Launcher> whitelist;

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
		SQLiteDatabase db = dbhandler.getWritableDatabase();
		db.delete(DB_KEYS.WHITE_LIST_TABLE, DB_KEYS.KEY_PKGNAME + " = ?",
				new String[] { packageName });
		db.close();
	}
	
	public void deleteLauncher(Launcher launcher) {
		SQLiteDatabase db = dbhandler.getWritableDatabase();
		db.delete(DB_KEYS.WHITE_LIST_TABLE, DB_KEYS.KEY_PKGNAME + " = ? and " + DB_KEYS.KEY_ACTIVITY + " = ?",
				new String[] { launcher.getPackageName(),launcher.getActivity() });
		db.close();
	}

	public List<Launcher> getWhiteListApps() {
		if (whitelist == null) {
			whitelist = new ArrayList<Launcher>();
			SQLiteDatabase database = dbhandler.getReadableDatabase();
			cursor = database.query(DB_KEYS.WHITE_LIST_TABLE, null, null, null, null, null, DB_KEYS.KEY_PKGNAME);
			// loop through all rows and add it to white list
			if (cursor.moveToFirst()) {
				do {
					String packageName = cursor.getString((cursor.getColumnIndexOrThrow(DB_KEYS.KEY_PKGNAME)));
					String activity = cursor.getString((cursor.getColumnIndexOrThrow(DB_KEYS.KEY_ACTIVITY)));
					Launcher lanucher = new Launcher(packageName, activity);
					whitelist.add(lanucher);
				} while (cursor.moveToNext());
			}
			database.close();
		}
		return whitelist;
	}

}
