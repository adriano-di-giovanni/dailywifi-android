package com.adrianodigiovanni.dailywifi;

import android.database.sqlite.SQLiteDatabase;

public class AccountsTable {

	public static final String TABLE_NAME = "accounts";

	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_SSID = "ssid";
	public static final String COLUMN_USERNAME = "username";
	public static final String COLUMN_PASSWORD = "password";
	
	public static final int INDEX_ID = 0;
	public static final int INDEX_SSID = 1;
	public static final int INDEX_USERNAME = 2;
	public static final int INDEX_PASSWORD = 3;

	private static final String TABLE_CREATE_SQL = "CREATE TABLE " + TABLE_NAME
			+ " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_SSID + " TEXT NOT NULL, " + COLUMN_USERNAME
			+ " TEXT NOT NULL, " + COLUMN_PASSWORD + " TEXT NOT NULL" + ");";

	private static final String TABLE_DROP_SQL = "DROP TABLE IF EXISTS "
			+ TABLE_NAME;

	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_CREATE_SQL);
	}

	// TODO: copy data from old table to new table
	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		db.execSQL(TABLE_DROP_SQL);
	}

}
