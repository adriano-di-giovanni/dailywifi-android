package com.adrianodigiovanni.dailywifi;

import android.content.Context;
import android.database.Cursor;

public class AccountsHelper {

	private AccountsHelper() {
	}

	public static Account getAccountBySSID(Context context, String ssid) {

		Account account = null;

		final String[] projection = { AccountsTable.COLUMN_SSID,
				AccountsTable.COLUMN_USERNAME, AccountsTable.COLUMN_PASSWORD };
		final String selection = AccountsTable.COLUMN_SSID + "=?";
		final String[] selectionArgs = { ssid };

		Cursor cursor = context.getContentResolver().query(
				AccountsProvider.CONTENT_URI, projection, selection,
				selectionArgs, null);

		if (null != cursor) {
			if (0 != cursor.getCount()) {
				cursor.moveToFirst();
				account = new Account(cursor.getString(0), cursor.getString(1),
						cursor.getString(2));
			}
			cursor.close();
		}
		return account;
	}
}
