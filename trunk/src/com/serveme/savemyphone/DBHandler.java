package com.serveme.savemyphone;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHandler extends SQLiteOpenHelper {

	// Application Context
	private Context APPContext;

	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Folder Path
	// private static final String DATABASE_FOLDER_PATH =
	// "/data/data/com.ansoft.externaldbapp/databases/";

	// Database Name
	private static final String DATABASE_NAME = "db.sqlite";

	// Database Path
	// private static final String DATABASE_PATH = DATABASE_FOLDER_PATH +
	// DATABASE_NAME;

	// Tables names
	private final String WHITE_LIST_TABLE = "white_list";
	private final String ADMIN_TABLE = "admin";

	// Table Columns names
	private final String KEY_APPNAME = "name";
	private final String KEY_PKGNAME = "package";
	private final String KEY_USERNAME = "username";
	private final String KEY_PASSWORD = "password";

	public DBHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.APPContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String createTableStr = "CREATE TABLE " + WHITE_LIST_TABLE + " ( "
				+ KEY_PKGNAME + " TEXT PRIMARY KEY, " + KEY_APPNAME + " TEXT)";
		db.execSQL(createTableStr);
		createTableStr = "CREATE TABLE " + ADMIN_TABLE + " ( " + KEY_USERNAME
				+ " TEXT, " + KEY_PASSWORD + " TEXT)";
		db.execSQL(createTableStr);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldversion, int newversion) {
		// TODO Auto-generated method stub
		if (oldversion != newversion) {
			// later
		}

	}

//	// Adding new contact
//	// The addContact() method accepts Contact object as parameter. We need to
//	// build ContentValues parameters using Contact object. Once we inserted
//	// data in database we need to close the database connection.
//	public void addContact(Contact contact) {
//		SQLiteDatabase db = this.getWritableDatabase();
//
//		ContentValues values = new ContentValues();
//		values.put(KEY_IMAGE, contact.getImage()); // Contact Name
//		values.put(KEY_NAME, contact.getName()); // Contact Name
//		values.put(KEY_PHONE, contact.getPhoneNumber()); // Contact Name
//		values.put(KEY_EMAIL, contact.getEmail()); // Contact Name
//
//		// Inserting Row
//		db.insert(CONTACTS_TABLE, null, values);
//		db.close(); // Closing database connection
//	}
//
//	// Getting single contact
//	// The following method getContact() will read single contact row. It
//	// accepts id as parameter and will return the matched row from the
//	// database.
//	public Contact getContact(int id) {
//		SQLiteDatabase db = this.getReadableDatabase();
//		String[] projection = new String[] { KEY_ID, KEY_IMAGE, KEY_NAME,
//				KEY_PHONE, KEY_PHONE };
//		String selection = KEY_ID + "=?";
//		String[] selectionArgs = new String[] { String.valueOf(id) };
//		String sortOrder = KEY_NAME + " DESC";
//		// defining the cursor which is a query
//		Cursor cursor = db.query(CONTACTS_TABLE, // The Table Name
//				projection, // The columns to return
//				selection, // The columns for the WHERE clause
//				selectionArgs, // The values for the WHERE clause
//				null, // don't group the rows, GROUP BY attribute
//				null, // don't filter by row groups, HAVING
//				sortOrder // The sort order
//				);
//
//		if (cursor != null)
//			cursor.moveToFirst();
//
//		Contact contact = new Contact(cursor.getInt(cursor
//				.getColumnIndexOrThrow("id")), cursor.getInt(cursor
//				.getColumnIndexOrThrow("image")), cursor.getString(cursor
//				.getColumnIndexOrThrow("name")), cursor.getString(cursor
//				.getColumnIndexOrThrow("phone")), cursor.getString(cursor
//				.getColumnIndexOrThrow("email")));
//
//		// return contact
//		return contact;
//	}
//
//	// Getting All Contacts
//	// getAllContacts() will return all contacts from database in array list
//	// format of Contact class type. You need to write a for loop to go through
//	// each contact.
//	public List<Contact> getAllContacts() {
//		List<Contact> contactList = new ArrayList<Contact>();
//		// Select All Query
//		String selectQuery = "SELECT  * FROM " + CONTACTS_TABLE;
//		SQLiteDatabase db = this.getWritableDatabase();
//		Cursor cursor = db.rawQuery(selectQuery, null);
//
//		// looping through all rows and adding to list
//		if (cursor.moveToFirst()) {
//			do {
//				Contact contact = new Contact();
//				contact.setID((cursor.getInt(cursor.getColumnIndexOrThrow("id"))));
//				contact.setImage(cursor.getInt(cursor
//						.getColumnIndexOrThrow("image")));
//				contact.setName(cursor.getString(cursor
//						.getColumnIndexOrThrow("name")));
//				contact.setPhoneNumber(cursor.getString(cursor
//						.getColumnIndexOrThrow("phone")));
//				contact.setEmail(cursor.getString(cursor
//						.getColumnIndexOrThrow("email")));
//				// Adding contact to list
//				contactList.add(contact);
//			} while (cursor.moveToNext());
//		}
//
//		// return contact list
//		return contactList;
//	}
//
//	// Getting contacts Count
//	// getContactsCount() will return total number of contacts in SQLite
//	// database.
//	public int getContactsCount() {
//		String countQuery = "SELECT  * FROM " + CONTACTS_TABLE;
//		SQLiteDatabase db = this.getReadableDatabase();
//		Cursor cursor = db.rawQuery(countQuery, null);
//		cursor.close();
//		// return count
//		return cursor.getCount();
//	}
//
//	// Updating single contact
//	// updateContact() will update single contact in database. This method
//	// accepts Contact class object as parameter.
//	public int updateContact(Contact contact) {
//		SQLiteDatabase db = this.getWritableDatabase();
//
//		ContentValues values = new ContentValues();
//		values.put(KEY_IMAGE, contact.getImage());
//		values.put(KEY_NAME, contact.getName());
//		values.put(KEY_PHONE, contact.getPhoneNumber());
//		values.put(KEY_EMAIL, contact.getEmail());
//
//		// updating row
//		return db.update(CONTACTS_TABLE, // Table Name
//				values, // ContentValues Object
//				KEY_ID + " = ?", // selection
//				new String[] { String.valueOf(contact.getID()) } // selectionArgs
//				);
//	}
//
//	// Deleting single contact
//	public void deleteContact(Contact contact) {
//		SQLiteDatabase db = this.getWritableDatabase();
//		db.delete(CONTACTS_TABLE, KEY_ID + " = ?",
//				new String[] { String.valueOf(contact.getID()) });
//		db.close();
//	}

}
