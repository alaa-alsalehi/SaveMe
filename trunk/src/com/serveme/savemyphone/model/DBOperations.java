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
	private List<String> whitelist;

	public DBOperations(Context context) {
		dbhandler = getInstance(context);
	}

	public static DBHandler getInstance(Context c) {
		return dbhandler != null ? dbhandler : (dbhandler = new DBHandler(c));
	}

	public void insertöApp(String packagename) {
		ContentValues values = new ContentValues();
		values.put(DB_KEYS.KEY_PKGNAME, packagename);
		SQLiteDatabase database = dbhandler.getWritableDatabase();
		database.insert(DB_KEYS.WHITE_LIST_TABLE, null, values);
		database.close();
	}

	public void deleteApp(String packagename) {
		SQLiteDatabase db = dbhandler.getWritableDatabase();
		db.delete(DB_KEYS.WHITE_LIST_TABLE, DB_KEYS.KEY_PKGNAME + " = ?",
				new String[] { packagename });
		db.close();
	}

	public List<String> getWhiteListApps() {
		if (whitelist == null) {
			whitelist = new ArrayList<String>();
			SQLiteDatabase database = dbhandler.getReadableDatabase();
			cursor = database.query(DB_KEYS.WHITE_LIST_TABLE, null, null, null,
					null, null, DB_KEYS.KEY_PKGNAME);
			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					whitelist.add((cursor.getString((cursor
							.getColumnIndexOrThrow(DB_KEYS.KEY_PKGNAME)))));
				} while (cursor.moveToNext());
			}
			database.close();
		}
		return whitelist;
	}

	public void updateStatus(int status) {
		ContentValues values = new ContentValues();
		values.put(DB_KEYS.KEY_STATUS, status);
		SQLiteDatabase database = dbhandler.getWritableDatabase();
		database.update(DB_KEYS.STATUS_TABLE, values, null, null);
		database.close();
	}

	public int getStatus() {
		SQLiteDatabase database = dbhandler.getReadableDatabase();
		cursor = database.query(DB_KEYS.STATUS_TABLE, null, null, null, null,
				null, null);
		cursor.moveToFirst();
		return cursor
				.getInt((cursor.getColumnIndexOrThrow(DB_KEYS.KEY_STATUS)));
	}


}
