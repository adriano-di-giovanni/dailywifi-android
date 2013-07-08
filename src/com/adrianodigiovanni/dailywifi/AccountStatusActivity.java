package com.adrianodigiovanni.dailywifi;

import java.util.Date;

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.adrianodigiovanni.app.AbstractPortraitActivity;

public class AccountStatusActivity extends AbstractPortraitActivity {
	
	private static final String TAG = "AccountStatusActivity";

	private Uri mUri = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_accountstatus);

		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		Intent intent = getIntent();
		if (intent.hasExtra(AccountsProvider.CONTENT_ITEM_TYPE)) {
			mUri = intent
					.getParcelableExtra(AccountsProvider.CONTENT_ITEM_TYPE);
		}
	}
	
	private void fill(Account account) {
		
		Long lastUsed = account.getLastAccess();
		String formattedDate = getString(R.string.notAvailable);
		
		if (null != lastUsed) {
			Date date = new Date(lastUsed.longValue());
			java.text.DateFormat dateFormat = DateFormat
					.getDateFormat(getApplicationContext());
			java.text.DateFormat timeFormat = DateFormat
					.getTimeFormat(getApplicationContext());
			formattedDate = dateFormat.format(date) + " "
					+ timeFormat.format(date);
		}
		
		TextView textView;
		
		textView = (TextView) findViewById(R.id.textViewSSID);
		textView.setText(account.getSSID());
		
		textView = (TextView) findViewById(R.id.textViewUsername);
		textView.setText(account.getUsername());
		
		textView = (TextView) findViewById(R.id.textViewLastAccess);
		textView.setText(formattedDate);			
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Account account = Account.getByUri(this, mUri);
		fill(account);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int itemId = item.getItemId();

		switch (itemId) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onEdit(View view) {
		Intent intent = new Intent(this, AddEditAccountActivity.class);
		intent.putExtra(AccountsProvider.CONTENT_ITEM_TYPE, mUri);
		startActivity(intent);
	}

	// TODO: Implement undo
	public void onRemove(View view) {
		if (null != mUri) {
			Account.deleteByUri(this, mUri);
		}
		finish();
	}
}
