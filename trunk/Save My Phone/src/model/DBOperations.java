package model;

import java.util.ArrayList;
import java.util.List;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBOperations {

		private static DBOperations db;
		private DBHandler dbhandler;
		private SQLiteDatabase database;
		private Cursor cursor;
		
		private DBOperations(Context context){
			dbhandler = new DBHandler(context);
		}
		
		public static DBOperations getInstance(Context c){
			return db != null ? db : (db = new DBOperations(c));
		}
		
		public void insertöApp(String packagename){
			ContentValues values = new ContentValues();
			values.put(DB_KEYS.KEY_PKGNAME, packagename);
			database = dbhandler.getWritableDatabase();
			database.insert(DB_KEYS.WHITE_LIST_TABLE, null, values);
			database.close();
		}
		
		public void deleteApp(String packagename) {
			SQLiteDatabase db = dbhandler.getWritableDatabase();
			db.delete(DB_KEYS.WHITE_LIST_TABLE, DB_KEYS.KEY_PKGNAME + " = ?",
					new String[] { packagename });
			db.close();
		}
		
		public List<String> getWhiteListApps(){
			List<String> appslist = new ArrayList<String>();
			database = dbhandler.getReadableDatabase();
			cursor = database.query(DB_KEYS.WHITE_LIST_TABLE, null, null, null, null, null, DB_KEYS.KEY_PKGNAME);
			// looping through all rows and adding to list
		    if (cursor.moveToFirst()) {
		        do {
		        	appslist.add((cursor.getString((cursor.getColumnIndexOrThrow(DB_KEYS.KEY_PKGNAME)))));
		        	Log.v("ooooo", cursor.getString((cursor.getColumnIndexOrThrow(DB_KEYS.KEY_PKGNAME))));
		        } while (cursor.moveToNext());
		    }
		    
			return appslist;
		}
		
		
		

		
}
