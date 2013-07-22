package model;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBOperations {

		private static DBOperations db;
		private DBHandler dbhandler;
		private Cursor cursor;
		
		private DBOperations(Context context){
			dbhandler = new DBHandler(context);
		}
		
		public static DBOperations getInstance(Context c){
			return db != null ? db : (db = new DBOperations(c));
		}
		
		public void insert�App(String packagename){
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
		
		public List<String> getWhiteListApps(){
			List<String> appslist = new ArrayList<String>();
			SQLiteDatabase database = dbhandler.getReadableDatabase();
			cursor = database.query(DB_KEYS.WHITE_LIST_TABLE, null, null, null, null, null, DB_KEYS.KEY_PKGNAME);
			// looping through all rows and adding to list
		    if (cursor.moveToFirst()) {
		        do {
		        	appslist.add((cursor.getString((cursor.getColumnIndexOrThrow(DB_KEYS.KEY_PKGNAME)))));
		        } while (cursor.moveToNext());
		    }
		    database.close();
			return appslist;
		}
		
		public void updateStatus(int status){
			ContentValues values = new ContentValues();
			values.put(DB_KEYS.KEY_STATUS, status);
			SQLiteDatabase database = dbhandler.getWritableDatabase();
			database.update(DB_KEYS.STATUS_TABLE, values, null, null);
			database.close();
		}
		
		public int getStatus(){
			SQLiteDatabase database = dbhandler.getReadableDatabase();
			cursor = database.query(DB_KEYS.STATUS_TABLE, null, null, null, null, null, null);
			cursor.moveToFirst();
			return cursor.getInt((cursor.getColumnIndexOrThrow(DB_KEYS.KEY_STATUS)));
		}

		public void updatePassword(String password){
			ContentValues values = new ContentValues();
			values.put(DB_KEYS.KEY_PASSWORD, password);
			SQLiteDatabase database = dbhandler.getWritableDatabase();
			database.update(DB_KEYS.ADMIN_TABLE, null, null, null);
			database.close();
		}
		
		public String getPassword(){
			SQLiteDatabase database = dbhandler.getReadableDatabase();
			cursor = database.query(DB_KEYS.ADMIN_TABLE, null, null, null, null, null, null);
			cursor.moveToFirst();
			return cursor.getString((cursor.getColumnIndexOrThrow(DB_KEYS.KEY_PASSWORD)));
		}
		
}
