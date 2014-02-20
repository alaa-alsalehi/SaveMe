package com.serveme.savemyphone.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.serveme.analytics.AnalyticsExceptionParser;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build.VERSION;
import android.util.Log;

public class DBOperations {

	public static boolean sdcard_mounted = true;
	private static DBOperations dboperations;
	private static Set<Launcher> whitelist; // for user and admin activity - for view -
	private Set<Launcher> whitelistPackages; // for service
	private DBHandler dbhandler;
	private Context context;
	
	private DBOperations(Context context) {
		this.context = context;
		dbhandler = new DBHandler(context);
	}

	public static DBOperations getInstance(Context c) {
		return dboperations != null ? dboperations : (dboperations = new DBOperations(c));
	}

	public void insertoApp(Launcher launcher) {
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
		database.delete(DB_KEYS.WHITE_LIST_TABLE,DB_KEYS.KEY_PKGNAME + " = ? and " + DB_KEYS.KEY_ACTIVITY + " = ?",	new String[] { launcher.getPackageName(), launcher.getActivity() });
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
			Log.v("whitelist", "created againt!");
			whitelist = new HashSet<Launcher>();
			SQLiteDatabase database = dbhandler.getReadableDatabase();
			Cursor cursor = database.query(DB_KEYS.WHITE_LIST_TABLE, null,
					null, null, null, null, DB_KEYS.KEY_PKGNAME);
			// loop through all rows and add it to white list
			if (cursor.moveToFirst()) {
				if(sdcard_mounted){
					do {
						String packageName = cursor.getString((cursor
								.getColumnIndexOrThrow(DB_KEYS.KEY_PKGNAME)));
						String activity = cursor.getString((cursor
								.getColumnIndexOrThrow(DB_KEYS.KEY_ACTIVITY)));
						Launcher lanucher = new Launcher(packageName, activity);
						whitelist.add(lanucher);
					} while (cursor.moveToNext());
				} else {
					do {
						String packageName = cursor.getString((cursor.getColumnIndexOrThrow(DB_KEYS.KEY_PKGNAME)));
						String activity = cursor.getString((cursor.getColumnIndexOrThrow(DB_KEYS.KEY_ACTIVITY)));
						if(!isInstalledOnSdCard(context,packageName)){
							Launcher lanucher = new Launcher(packageName, activity);
							whitelist.add(lanucher);
						}						
					} while (cursor.moveToNext());
				}
			}
			database.close();
		}
		return whitelist;
	}
	
	public void reCreateWhiteList(){
		if(whitelist != null){
			Iterator<Launcher> myIterator = whitelist.iterator();
			while (myIterator.hasNext()) {
				myIterator.next();
				myIterator.remove();
			}
			whitelist = null;
		}
	}

	public Set<Launcher> getWhiteListPackages() {
		if (whitelistPackages == null) {
			whitelistPackages = new HashSet<Launcher>();
			SQLiteDatabase database = dbhandler.getReadableDatabase();
			Cursor cursor = database.query(DB_KEYS.WHITE_LIST_TABLE, null,
					null, null, null, null, DB_KEYS.KEY_PKGNAME);
			// loop through all rows and add it to white list
			if (cursor.moveToFirst()) {
				if(sdcard_mounted){
					do {
						String packageName = cursor.getString((cursor
								.getColumnIndexOrThrow(DB_KEYS.KEY_PKGNAME)));
						Launcher lanucher = new Launcher(packageName, null);
						whitelistPackages.add(lanucher);
					} while (cursor.moveToNext());
				} else {
					do {
						String packageName = cursor.getString((cursor.getColumnIndexOrThrow(DB_KEYS.KEY_PKGNAME)));
						if(!isInstalledOnSdCard(context,packageName)){
							Launcher lanucher = new Launcher(packageName, null);
							whitelistPackages.add(lanucher);
						}	
					} while (cursor.moveToNext());
				}
			}
			database.close();
		}
		return whitelistPackages;
	}
	
	
	  
	public static boolean isInstalledOnSdCard(Context context, String packageName) {

	    PackageManager pm = context.getPackageManager();
        ApplicationInfo appinfo = null;
		try {
			appinfo = pm.getApplicationInfo(packageName, 0);
		} catch (NameNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
	    // check for API level 8 and higher
	    if (VERSION.SDK_INT > android.os.Build.VERSION_CODES.ECLAIR_MR1) 
	    {
	      try {
	    	if ((appinfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == ApplicationInfo.FLAG_EXTERNAL_STORAGE) {
		    	return true; // installed in SD Card but available
	    	} 
	      } catch(NullPointerException npe){
	    	  return true; // installed in SD Card and not available
	      }
	    } 
	    // check for API level 7 - check files dir
	    else 
	    {
		    try {
		    	String  sourceDir = appinfo.dataDir;
		    	if(sourceDir.startsWith("/data/")) {
		    		return false;
		      	} else { // if (sourceDir.contains("/mnt/") || sourceDir.contains("/sdcard/"))
		      		return true;
		      	}
		    } catch (Throwable e) {
		      // ignore
		    }
	    }
		return false;
	}
}
