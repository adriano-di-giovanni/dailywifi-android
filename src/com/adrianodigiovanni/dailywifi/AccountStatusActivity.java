package com.adrianodigiovanni.dailywifi;

import java.util.Date;

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.adrianodigiovanni.app.AbstractPortraitActivity;
import com.adrianodigiovanni.dailywifi.attempt.ActionType;
import com.adrianodigiovanni.net.WifiHelper;

public class AccountStatusActivity extends AbstractPortraitActivity {
	
	private static final String TAG = "AccountStatusActivity";

	private Uri mUri = null;
	private Account mAccount = null;

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
	
	private void fill() {
		
		Long lastUsed = mAccount.getLastAccess();
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
		Boolean strobe;
		String text;
		
		textView = (TextView) findViewById(R.id.textViewSSID);
		textView.setText(mAccount.getSSID());
		
		textView = (TextView) findViewById(R.id.textViewUsername);
		textView.setText(mAccount.getUsername());
		
		strobe = mAccount.getIsCompatible();
		if (null != strobe) {
			text = getString((strobe.booleanValue()) ? R.string.yes : R.string.no);
		} else {
			text = getString(R.string.notAvailable); 
		}
		textView = (TextView) findViewById(R.id.textViewCompatible);
		textView.setText(text);
		
		strobe = mAccount.getIsValid();
		if (null != strobe) {
			text = getString((strobe.booleanValue()) ? R.string.yes : R.string.no);
		} else {
			text = getString(R.string.notAvailable);
		}
		textView = (TextView) findViewById(R.id.textViewCredentialsAreValid);
		textView.setText(text);
		
		textView = (TextView) findViewById(R.id.textViewLastAccess);
		textView.setText(formattedDate);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mAccount = Account.getByUri(this, mUri);
		fill();
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
	
	public void onLogout(View view) {
		WifiInfo wifiInfo = WifiHelper.getWifiInfo(this);
		// Service must be started only if the edited account is for the
		// same WiFi network the device is connected to
		if (null != wifiInfo) {
			String ssid = mAccount.getSSID();
			String wifiInfoSSID = wifiInfo.getSSID();
			if (null != wifiInfoSSID && wifiInfoSSID.equalsIgnoreCase(ssid)) {
				BackgroundService.startSelf(this, ActionType.LOGOUT);
			}
		}
	}
}
