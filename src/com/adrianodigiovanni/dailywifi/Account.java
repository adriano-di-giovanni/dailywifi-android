package com.adrianodigiovanni.dailywifi;

import com.adrianodigiovanni.dailywifi.database.AccountsTable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/**
 * This class represents a WiFi network account input by the user.
 */
public class Account {
	private static final String[] mProjection = { AccountsTable.COLUMN_ID,
			AccountsTable.COLUMN_SSID, AccountsTable.COLUMN_USERNAME,
			AccountsTable.COLUMN_PASSWORD, AccountsTable.COLUMN_IS_COMPATIBLE,
			AccountsTable.COLUMN_IS_VALID };

	@SuppressWarnings("unused")
	private int mID;
	private String mSSID;
	private String mUsername;
	private String mPassword;
	private int mIsCompatible;
	private int mIsValid;

	// TODO: SSID must be unique

	/**
	 * Gets the account by SSID
	 * 
	 * @return The account matching SSID or null if any matches found
	 */
	public static Account getBySSID(Context context, String ssid) {
		final String selection = AccountsTable.COLUMN_SSID + "=?";
		final String[] selectionArgs = { ssid };

		Cursor cursor = context.getContentResolver().query(
				AccountsProvider.CONTENT_URI, mProjection, selection,
				selectionArgs, null);

		return accountFromCursor(cursor);
	}

	/**
	 * Gets the account by URI
	 * 
	 * @return The account matching _ID in URI or null if any matches found
	 */
	public static Account getByUri(Context context, Uri uri) {

		Cursor cursor = context.getContentResolver().query(uri, mProjection,
				null, null, null);

		return accountFromCursor(cursor);
	}

	private static Account accountFromCursor(Cursor cursor) {
		Account account = null;
		if (null != cursor) {
			cursor.moveToFirst();
			account = new Account(
					cursor.getInt(cursor
							.getColumnIndexOrThrow(AccountsTable.COLUMN_ID)),
					cursor.getString(cursor
							.getColumnIndexOrThrow(AccountsTable.COLUMN_SSID)),
					cursor.getString(cursor
							.getColumnIndexOrThrow(AccountsTable.COLUMN_USERNAME)),
					cursor.getString(cursor
							.getColumnIndexOrThrow(AccountsTable.COLUMN_PASSWORD)),
					cursor.getInt(cursor
							.getColumnIndexOrThrow(AccountsTable.COLUMN_IS_COMPATIBLE)),
					cursor.getInt(cursor
							.getColumnIndexOrThrow(AccountsTable.COLUMN_IS_VALID)));
			cursor.close();
		}
		return account;
	}

	/**
	 * Updates account in the database using URI to determine _ID. It also
	 * starts the service to determine if active WiFi network is the one the
	 * account is related to.
	 * 
	 * @see DWFService
	 */
	public static void saveWithUri(Context context, Uri uri, Account account) {
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

	/**
	 * Deletes the account from the database using URI to determne _ID
	 */
	public static void deleteByUri(Context context, Uri uri) {
		context.getContentResolver().delete(uri, null, null);
	}

	public Account() {
		this(0, null, null, null, AccountsTable.TRISTATE_NOT_APPLICABLE,
				AccountsTable.TRISTATE_NOT_APPLICABLE);
	}

	public Account(String ssid, String username, String password) {
		this(0, ssid, username, password,
				AccountsTable.TRISTATE_NOT_APPLICABLE,
				AccountsTable.TRISTATE_NOT_APPLICABLE);
	}

	private Account(int id, String ssid, String username, String password,
			int isCompatible, int isValid) {
		mID = id;
		mSSID = ssid;
		mUsername = username;
		mPassword = password;
		mIsCompatible = isCompatible;
		mIsValid = isValid;
	}

	public String getSSID() {
		return mSSID;
	}

	/**
	 * Sets SSID for the account. It also resets info about network
	 * compatibility with DailyWiFi protocol and credentials validity.
	 */
	public void setSSID(String ssid) {
		if (ssid.equals(mSSID))
			resetTriStateMembers();
		mSSID = ssid;
	}

	public String getUsername() {
		return mUsername;
	}

	/**
	 * Sets username for the account. It also resets info about network
	 * compatibility with DailyWiFi protocol and credentials validity.
	 */
	public void setUsername(String username) {
		if (username.equals(mUsername))
			resetTriStateMembers();
		mUsername = username;
	}

	public String getPassword() {
		return mPassword;
	}
	
	/**
	 * Sets password for the account. It also resets info about network
	 * compatibility with DailyWiFi protocol and credentials validity.
	 */
	public void setPassword(String password) {
		if (password.equals(mPassword))
			resetTriStateMembers();
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
	 * @param value
	 *            A boolean primitive value to indicate compatibility.
	 */
	public void setIsCompatible(boolean value) {
		mIsCompatible = toTriState(value);
	}

	/**
	 * Sets account credentials validity
	 * 
	 * @param value
	 *            A boolean primitive value to indicate credentials validity.
	 */
	public void setIsValid(boolean value) {
		mIsValid = toTriState(value);
	}

	private static int toTriState(boolean fromBoolean) {
		return (fromBoolean) ? AccountsTable.TRISTATE_TRUE
				: AccountsTable.TRISTATE_FALSE;
	}

	private void resetTriStateMembers() {
		mIsCompatible = AccountsTable.TRISTATE_NOT_APPLICABLE;
		mIsValid = AccountsTable.TRISTATE_NOT_APPLICABLE;
	}

	private static Boolean getBoolean(int fromTriState) {
		Boolean is = null;
		if (AccountsTable.TRISTATE_TRUE == fromTriState) {
			is = Boolean.TRUE;
		} else if (AccountsTable.TRISTATE_FALSE == fromTriState) {
			is = Boolean.FALSE;
		}
		return is;
	}
}
