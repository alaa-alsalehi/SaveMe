package com.serveme.savemyphone.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHandler extends SQLiteOpenHelper {

	// Database Version
	private static final int DATABASE_VERSION = 2;

	// Database Name
	private static final String DATABASE_NAME = "db.sqlite";

	public DBHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String createTableStr = "CREATE TABLE " + DB_KEYS.WHITE_LIST_TABLE
				+ " ( " + DB_KEYS.KEY_ID + " long primary key ,"
				+ DB_KEYS.KEY_PKGNAME + " TEXT not null, "
				+ DB_KEYS.KEY_ACTIVITY + " TEXT)";
		db.execSQL(createTableStr);
		createAppLogTable(db);
	}

	public void createAppLogTable(SQLiteDatabase db) {
		String createAppLogTableStr = "CREATE TABLE " + DB_KEYS.APP_LOG_TABLE
				+ " (" + DB_KEYS.KEY_ID
				+ " INTEGER PRIMARY KEY  NOT NULL , "
				+ DB_KEYS.KEY_PACKAGE_NAME + " TEXT NOT NULL  , "
				+ DB_KEYS.KEY_START_DATE + " INTEGER NOT NULL , "
				+ DB_KEYS.KEY_END_DATE + " INTEGER)";
		db.execSQL(createAppLogTableStr);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldversion, int newversion) {
		switch (newversion) {
		case 2:
			// adding App log
			createAppLogTable(db);
			break;

		default:
			break;
		}

	}

}
