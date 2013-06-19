package com.adrianodigiovanni.dailywifi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/**
 * This class represents a WiFi network account.
 */
public class Account {
	private String mSSID;
	private String mUsername;
	private String mPassword;
	private int mIsCompatible;
	private int mIsValid;

	public static Account fetch(Context context, Uri uri) {

		Account account = null;

		final String[] projection = { AccountsTable.COLUMN_SSID,
				AccountsTable.COLUMN_USERNAME, AccountsTable.COLUMN_PASSWORD };

		Cursor cursor = context.getContentResolver().query(uri, projection,
				null, null, null);

		if (null != cursor) {
			cursor.moveToFirst();
			account = new Account(
					cursor.getString(cursor
							.getColumnIndexOrThrow(AccountsTable.COLUMN_SSID)),
					cursor.getString(cursor
							.getColumnIndexOrThrow(AccountsTable.COLUMN_USERNAME)),
					cursor.getString(cursor
							.getColumnIndexOrThrow(AccountsTable.COLUMN_PASSWORD)));
			cursor.close();
		}

		return account;
	}

	public static void save(Context context, Uri uri, Account account) {
		ContentValues values = new ContentValues();
		values.put(AccountsTable.COLUMN_SSID, account.getSSID());
		values.put(AccountsTable.COLUMN_USERNAME, account.getUsername());
		values.put(AccountsTable.COLUMN_PASSWORD, account.getPassword());

		if (null == uri) {
			context.getContentResolver().insert(AccountsProvider.CONTENT_URI,
					values);
		} else {
			context.getContentResolver().update(uri, values, null, null);
		}

		DWFService.startSelf(context);
	}

	public static void delete(Context context, Uri uri) {
		context.getContentResolver().delete(uri, null, null);
	}

	public Account() {
		this(null, null, null, AccountsTable.TRISTATE_NOT_APPLICABLE,
				AccountsTable.TRISTATE_NOT_APPLICABLE);
	}

	public Account(String ssid, String username, String password) {
		this(ssid, username, password, AccountsTable.TRISTATE_NOT_APPLICABLE,
				AccountsTable.TRISTATE_NOT_APPLICABLE);
	}

	private Account(String ssid, String username, String password,
			int isCompatible, int isValid) {
		mSSID = ssid;
		mUsername = username;
		mPassword = password;
		mIsCompatible = isCompatible;
		mIsValid = isValid;
	}

	public String getSSID() {
		return mSSID;
	}

	public void setSSID(String ssid) {
		mSSID = ssid;
	}

	public String getUsername() {
		return mUsername;
	}

	public void setUsername(String username) {
		mUsername = username;
	}

	public String getPassword() {
		return mPassword;
	}

	public void setPassword(String password) {
		mPassword = password;
	}

	/**
	 * Gets WiFi network compatibility with DailyWiFi protocol.
	 * 
	 * @return A Boolean object representing the primitive values TRUE or FALSE.
	 *         Can be null if no login attempt has been made yet.
	 */
	public Boolean getIsCompatible() {
		return getBoolean(mIsCompatible);
	}

	/**
	 * Gets account credentials validity
	 * 
	 * @return A Boolean object representing the primitive values TRUE of FALSE.
	 *         Can be null if no login attempt has been made yet.
	 */
	public Boolean getIsValid() {
		return getBoolean(mIsValid);
	}

	/**
	 * Sets WiFi compatibility with DailyWiFi protocol.
	 * 
	 * @param A
	 *            boolean primitive value to indicate compatibility.
	 */
	public void setIsCompatible(boolean value) {
		mIsCompatible = toTriState(value);
	}

	/**
	 * Sets account credentials validity
	 * 
	 * @param A
	 *            boolean primitive value to indicate credentials validity.
	 */
	public void setIsValid(boolean value) {
		mIsValid = toTriState(value);
	}

	private int toTriState(boolean fromBoolean) {
		return (fromBoolean) ? AccountsTable.TRISTATE_TRUE
				: AccountsTable.TRISTATE_FALSE;
	}

	private Boolean getBoolean(int fromTriState) {
		Boolean is = null;
		if (AccountsTable.TRISTATE_TRUE == fromTriState) {
			is = Boolean.TRUE;
		} else if (AccountsTable.TRISTATE_FALSE == fromTriState) {
			is = Boolean.FALSE;
		}
		return is;
	}
}
