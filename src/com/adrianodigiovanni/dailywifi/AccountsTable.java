package com.adrianodigiovanni.dailywifi;

import android.database.sqlite.SQLiteDatabase;

/**
 * This class represents the Accounts table in the database
 */
public class AccountsTable {

	public static final String TABLE_NAME = "accounts";

	/**
	 * The name for the primary key column
	 */
	public static final String COLUMN_ID = "_id";
	
	/**
	 * The name for the column indicating the WiFi network's SSID
	 */
	public static final String COLUMN_SSID = "ssid";
	
	/**
	 * The name for the column indicating the account username
	 */
	public static final String COLUMN_USERNAME = "username";
	
	/**
	 * The name for the column indicating the account password
	 */
	public static final String COLUMN_PASSWORD = "password";

	/**
	 * The name for the boolean column indicating if the newtork is DailyWiFi
	 * compatible
	 */
	public static final String COLUMN_IS_COMPATIBLE = "isCompatible";

	/**
	 * The name for the column indicating if username and password are correct
	 */
	public static final String COLUMN_IS_ACCOUNT_VALID = "isAccountValid";

	public static final int INDEX_ID = 0;
	public static final int INDEX_SSID = 1;
	public static final int INDEX_USERNAME = 2;
	public static final int INDEX_PASSWORD = 3;
	public static final int INDEX_IS_COMPATIBLE = 4;
	public static final int INDEX_IS_ACCOUNT_VALID = 5;

	/**
	 * The int value used for COLUMN_IS_COMPATIBLE and COLUMN_IS_ACCOUNT_VALID
	 * to indicate that's not possible to determine if the WiFi network is
	 * DailyWiFi compatible and credentials are correct.
	 * 
	 * @see #COLUMN_IS_COMPATIBLE
	 * @see #COLUMN_IS_ACCOUNT_VALID
	 */
	public static final int TRISTATE_NOT_APPLICABLE = -1;

	/**
	 * The int value used for COLUMN_IS_COMPATIBLE and COLUMN_IS_ACCOUNT_VALID
	 * to indicate WiFi network is not DailyWiFi compatible or credentials are
	 * incorrect.
	 * 
	 * @see #COLUMN_IS_COMPATIBLE
	 * @see #COLUMN_IS_ACCOUNT_VALID
	 */
	public static final int TRISTATE_FALSE = 0;

	/**
	 * The int value used for COLUMN_IS_COMPATIBLE and COLUMN_IS_ACCOUNT_VALID
	 * to indicate WiFi network is DailyWiFi compatible or credentials are
	 * correct.
	 * 
	 * @see #COLUMN_IS_COMPATIBLE
	 * @see #COLUMN_IS_ACCOUNT_VALID
	 */
	public static final int TRISTATE_TRUE = 1;

	private static final String TABLE_CREATE_SQL = "CREATE TABLE " + TABLE_NAME
			+ " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_SSID + " TEXT NOT NULL, " + COLUMN_USERNAME
			+ " TEXT NOT NULL, " + COLUMN_PASSWORD + " TEXT NOT NULL"
			+ COLUMN_IS_COMPATIBLE + " INTEGER NOT NULL DEFAULT -1"
			+ COLUMN_IS_ACCOUNT_VALID + " INTEGER NOT NULL DEFAULT -1" + ");";

	private static final String TABLE_DROP_SQL = "DROP TABLE IF EXISTS "
			+ TABLE_NAME;

	/**
	 * @see AccountsDatabaseHelper#onCreate(SQLiteDatabase)
	 */
	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_CREATE_SQL);
	}

	// TODO: copy data from old table to new table
	/**
	 * @see AccountsDatabaseHelper#onUpgrade(SQLiteDatabase, int, int) 
	 */
	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		db.execSQL(TABLE_DROP_SQL);
	}

}
