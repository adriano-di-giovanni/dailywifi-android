package com.adrianodigiovanni.dailywifi;

import java.net.MalformedURLException;
import java.net.URL;

import com.adrianodigiovanni.dailywifi.database.AccountsTable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * This class represents a WiFi network account input by the user.
 */
public class Account {
	private static final String TAG = "Account";

	private static final String[] mProjection = { AccountsTable.COLUMN_ID,
			AccountsTable.COLUMN_SSID, AccountsTable.COLUMN_USERNAME,
			AccountsTable.COLUMN_PASSWORD, AccountsTable.COLUMN_IS_COMPATIBLE,
			AccountsTable.COLUMN_IS_VALID, AccountsTable.COLUMN_REDIRECT_URL,
			AccountsTable.COLUMN_LAST_USED };

	private int mID;
	private String mSSID;
	private String mUsername;
	private String mPassword;
	private int mIsCompatible;
	private int mIsValid;
	private String mRedirectURL;
	private Long mLastUsed;

	// TODO: SSID must be unique

	/**
	 * Gets the account by SSID
	 * 
	 * @param ssid
	 *            The SSID to search for. Search ignores case.
	 * @return The account matching SSID or null if any matches found
	 */
	public static Account getBySSID(Context context, String ssid) {
		final String selection = "UPPER(" + AccountsTable.COLUMN_SSID
				+ ")=UPPER(?)";
		final String[] selectionArgs = { ssid };

		Cursor cursor = context.getContentResolver().query(
				AccountsProvider.CONTENT_URI, mProjection, selection,
				selectionArgs, null);

		Log.d(TAG, "Checking if an account exists for network with SSID "
				+ ssid);

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

		Log.d(TAG, "Checking if an account exists by Uri " + uri.toString());

		return accountFromCursor(cursor);
	}

	private static Account accountFromCursor(Cursor cursor) {
		Account account = null;
		if (null != cursor) {
			if (0 != cursor.getCount()) {
				cursor.moveToFirst();
				
				int columnIndex = cursor.getColumnIndex(AccountsTable.COLUMN_LAST_USED);
				Long lastUsed = null;
				
				if (!cursor.isNull(columnIndex)) {
					lastUsed = Long.valueOf(cursor.getLong(columnIndex));
				}
				
				account = new Account(
						cursor.getInt(cursor
								.getColumnIndex(AccountsTable.COLUMN_ID)),
						cursor.getString(cursor
								.getColumnIndex(AccountsTable.COLUMN_SSID)),
						cursor.getString(cursor
								.getColumnIndex(AccountsTable.COLUMN_USERNAME)),
						cursor.getString(cursor
								.getColumnIndex(AccountsTable.COLUMN_PASSWORD)),
						cursor.getInt(cursor
								.getColumnIndex(AccountsTable.COLUMN_IS_COMPATIBLE)),
						cursor.getInt(cursor
								.getColumnIndex(AccountsTable.COLUMN_IS_VALID)),
						cursor.getString(cursor
								.getColumnIndex(AccountsTable.COLUMN_REDIRECT_URL)),
						lastUsed);
				cursor.close();

				Log.d(TAG, "Account found: " + account.toString());
			}
		}
		return account;
	}

	/**
	 * Updates account in the database using URI to determine _ID.
	 * 
	 * @see BackgroundService
	 */
	public static void saveWithUri(Context context, Uri uri, Account account) {
		ContentValues contentValues = account.getContentValues();
		
		if (null == uri) {
			Log.d(TAG, "Saving new account: " + account.toString());

			context.getContentResolver().insert(AccountsProvider.CONTENT_URI,
					contentValues);
		} else {
			Log.d(TAG, "Updating account: " + account.toString());

			context.getContentResolver().update(uri, contentValues, null, null);
		}
	}

	/**
	 * Deletes the account from the database using URI to determne _ID
	 */
	public static void deleteByUri(Context context, Uri uri) {
		Log.d(TAG, "Deleting account by Uri " + uri.toString());
		context.getContentResolver().delete(uri, null, null);
	}

	public Account() {
		this(0, null, null, null, AccountsTable.TRISTATE_NOT_APPLICABLE,
				AccountsTable.TRISTATE_NOT_APPLICABLE, null, null);
	}

	public Account(String ssid, String username, String password) {
		this(0, ssid, username, password,
				AccountsTable.TRISTATE_NOT_APPLICABLE,
				AccountsTable.TRISTATE_NOT_APPLICABLE, null, null);
	}

	private Account(int id, String ssid, String username, String password,
			int isCompatible, int isValid, String redirectURL, Long lastUsed) {
		mID = id;
		mSSID = ssid;
		mUsername = username;
		mPassword = password;
		mIsCompatible = isCompatible;
		mIsValid = isValid;
		mRedirectURL = redirectURL;
		mLastUsed = lastUsed;

		Log.d(TAG, "Account created: " + toString());
	}

	public String getSSID() {
		return mSSID;
	}

	/**
	 * Sets SSID for the account. It also resets info about network
	 * compatibility with DailyWiFi protocol and credentials validity.
	 */
	public void setSSID(String ssid) {
		if (!ssid.equals(mSSID)) {
			resetMembers();
		}
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
		if (!username.equals(mUsername)) {
			resetMembers();
		}
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
		if (!password.equals(mPassword)) {
			resetMembers();
		}
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
	 * Sets WiFi compatibility with DailyWiFi protocol.
	 * 
	 * @param value
	 *            A boolean primitive value to indicate compatibility.
	 */
	public void setIsCompatible(boolean value) {
		mIsCompatible = toTriState(value);
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
	 * Sets account credentials validity
	 * 
	 * @param value
	 *            A boolean primitive value to indicate credentials validity.
	 */
	public void setIsValid(boolean value) {
		mIsValid = toTriState(value);
	}

	/**
	 * Gets captive network redirect URL
	 * 
	 * @return A String representing the redirect URL or null
	 */
	public URL getRedirectURL() {
		URL url = null;
		if (null != mRedirectURL) {
			try {
				url = new URL(mRedirectURL);
			} catch (MalformedURLException e) {
				// do nothing
			}
		}
		return url;
	}

	public void setRedirectURL(URL url) {
		mRedirectURL = url.toString();
	}

	/**
	 * Returns the last login time in milliseconds since January 1, 1970
	 * 00:00:00 UTC
	 */
	public Long getLastUsed() {
		return mLastUsed;
	}

	/**
	 * @param timeMillis
	 *            Last login time in milliseconds since January 1, 1970 00:00:00
	 *            UTC
	 */
	public void setLastUsed(long timeMillis) {
		mLastUsed = Long.valueOf(timeMillis);
	}

	/**
	 * Saves the account in the database
	 */
	public void save(Context context) {
		Uri uri = null;
		if (0 != mID) {
			uri = Uri.withAppendedPath(AccountsProvider.CONTENT_ID_URI,
					Integer.toString(mID));
		}
		Account.saveWithUri(context, uri, this);
	}

	/**
	 * Deletes the account in the database
	 */
	public void delete(Context context) {
		if (0 != mID) {
			Uri uri = Uri.withAppendedPath(AccountsProvider.CONTENT_ID_URI,
					Integer.toString(mID));
			Account.deleteByUri(context, uri);
		}
	}

	public String toString() {
		final String NEW_LINE = System.getProperty("line.separator");
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Account {" + NEW_LINE);
		stringBuilder.append("SSID: " + mSSID + ", " + NEW_LINE);
		stringBuilder.append("Username: " + mUsername + ", " + NEW_LINE);
		stringBuilder.append("Password: " + mPassword + ", " + NEW_LINE);
		stringBuilder
				.append("isCompatible: " + mIsCompatible + ", " + NEW_LINE);
		stringBuilder.append("isValid: " + mIsValid + ", " + NEW_LINE);
		stringBuilder.append("redirectURL: " + mRedirectURL + ", " + NEW_LINE);
		stringBuilder.append("lastUsed: " + mLastUsed + ", " + NEW_LINE);
		stringBuilder.append("}" + NEW_LINE);
		return stringBuilder.toString();
	}

	private ContentValues getContentValues() {
		ContentValues contentValues = new ContentValues();
		contentValues.put(AccountsTable.COLUMN_SSID, mSSID);
		contentValues.put(AccountsTable.COLUMN_USERNAME, mUsername);
		contentValues.put(AccountsTable.COLUMN_PASSWORD, mPassword);
		contentValues.put(AccountsTable.COLUMN_IS_COMPATIBLE, mIsCompatible);
		contentValues.put(AccountsTable.COLUMN_IS_VALID, mIsValid);
		contentValues.put(AccountsTable.COLUMN_REDIRECT_URL, mRedirectURL);
		contentValues.put(AccountsTable.COLUMN_LAST_USED, mLastUsed);
		return contentValues;
	}

	private static int toTriState(boolean fromBoolean) {
		return (fromBoolean) ? AccountsTable.TRISTATE_TRUE
				: AccountsTable.TRISTATE_FALSE;
	}

	private void resetMembers() {
		mIsCompatible = AccountsTable.TRISTATE_NOT_APPLICABLE;
		mIsValid = AccountsTable.TRISTATE_NOT_APPLICABLE;
		mRedirectURL = null;
		mLastUsed = null;
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
