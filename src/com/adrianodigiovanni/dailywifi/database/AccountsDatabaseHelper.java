package com.adrianodigiovanni.dailywifi.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This class is a database helper used to create, upgrade and open the
 * underlying SQLite database
 */
public class AccountsDatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "dailywifi.db";
	private static final int DATABASE_VERSION = 1;

	public AccountsDatabaseHelper(Context context) {
		super(context.getApplicationContext(), DATABASE_NAME, null,
				DATABASE_VERSION);
	}

	/**
	 * Invoked when database does not exist. The method delegates creation to
	 * homologous methods in table classes
	 * 
	 * @see AccountsTable
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		AccountsTable.onCreate(db);
	}

	/**
	 * Invoked when database version changes. The method delegates upgrade to
	 * homologous methods in table classes
	 * 
	 * @see AccountsTable
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		AccountsTable.onUpgrade(db, oldVersion, newVersion);
	}
}
