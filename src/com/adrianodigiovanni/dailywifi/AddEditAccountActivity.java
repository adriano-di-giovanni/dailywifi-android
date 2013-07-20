package com.adrianodigiovanni.dailywifi;

import com.adrianodigiovanni.app.AbstractPortraitActivity;
import com.adrianodigiovanni.dailywifi.attempt.ActionType;
import com.adrianodigiovanni.net.WifiHelper;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

// TODO: mostra password

public class AddEditAccountActivity extends AbstractPortraitActivity {

	private static final String TAG = "AddEditAccountActivity";

	private Account mAccount = null;

	private EditText mEditTextSSID;
	private EditText mEditTextUsername;
	private EditText mEditTextPassword;

	private Uri mUri = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addeditaccount);

		mEditTextSSID = (EditText) findViewById(R.id.edit_text_ssid);
		mEditTextUsername = (EditText) findViewById(R.id.edit_text_username);
		mEditTextPassword = (EditText) findViewById(R.id.edit_text_password);

		Intent intent = getIntent();
		if (intent.hasExtra(AccountsProvider.CONTENT_ITEM_TYPE)) {
			mUri = intent
					.getParcelableExtra(AccountsProvider.CONTENT_ITEM_TYPE);

			fill(mUri);
		}
	}

	private void fill(Uri uri) {
		mAccount = Account.getByUri(this, uri);

		mEditTextSSID.setText(mAccount.getSSID());
		mEditTextUsername.setText(mAccount.getUsername());
		mEditTextPassword.setText(mAccount.getPassword());
	}

	public void onCancel(View view) {
		finish();
	}

	public void onSave(View view) {

		boolean hasErrors = false;

		String ssid = mEditTextSSID.getText().toString().trim();
		String username = mEditTextUsername.getText().toString().trim();
		String password = mEditTextPassword.getText().toString().trim();

		Resources resources = getResources();

		if (ssid.isEmpty()) {
			mEditTextSSID.setError(resources
					.getString(R.string.ssidIsRequired));
			hasErrors = true;
		}

		if (username.isEmpty()) {
			mEditTextUsername.setError(resources
					.getString(R.string.usernameIsRequired));
			hasErrors = true;
		}

		if (password.isEmpty()) {
			mEditTextPassword.setError(resources
					.getString(R.string.passwordIsRequired));
			hasErrors = true;
		}

		if (!hasErrors) {
			if (null == mAccount) {
				mAccount = new Account();
			}
			mAccount.setSSID(ssid);
			mAccount.setUsername(username);
			mAccount.setPassword(password);
			Account.saveWithUri(this, mUri, mAccount);

			WifiInfo wifiInfo = WifiHelper.getWifiInfo(this);

			// Service must be started only if the edited account is for the
			// same WiFi network the device is connected to
			if (null != wifiInfo) {
				String wifiInfoSSID = wifiInfo.getSSID();
				if (null != wifiInfoSSID && wifiInfoSSID.equalsIgnoreCase(ssid)) {
					BackgroundService.startSelf(this, ActionType.LOGIN);
				}
			}

			finish();
		}
	}
}
