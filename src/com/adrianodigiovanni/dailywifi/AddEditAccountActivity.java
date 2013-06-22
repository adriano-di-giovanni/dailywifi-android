package com.adrianodigiovanni.dailywifi;

import com.adrianodigiovanni.dailywifi.attempt.ActionType;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class AddEditAccountActivity extends Activity {

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

			findViewById(R.id.button_remove).setVisibility(View.VISIBLE);
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
			mEditTextSSID.setError(resources.getString(R.string.error_required_ssid));
			hasErrors = true;
		}

		if (username.isEmpty()) {
			mEditTextUsername.setError(resources.getString(R.string.error_required_username));
			hasErrors = true;
		}

		if (password.isEmpty()) {
			mEditTextPassword.setError(resources.getString(R.string.error_required_password));
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
			
			DWFService.startSelf(this, ActionType.LOGIN);

			finish();
		}
	}

	// TODO: Implement undo
	public void onRemove(View view) {
		Account.deleteByUri(this, mUri);
		finish();
	}
}
