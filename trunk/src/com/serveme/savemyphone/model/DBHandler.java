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
		// TODO Auto-generated method stub
		String createTableStr = "CREATE TABLE " + DB_KEYS.WHITE_LIST_TABLE + " ( "+ DB_KEYS.KEY_PKGNAME + " TEXT PRIMARY KEY)";
		db.execSQL(createTableStr);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldversion, int newversion) {
		// TODO Auto-generated method stub
		if (oldversion != newversion) {
			// later
			db.execSQL("DROP TABLE IF EXISTS '" + DB_KEYS.WHITE_LIST_TABLE + "'");
			db.execSQL("DROP TABLE IF EXISTS 'admin'");
			onCreate(db);
		}

	}


}
