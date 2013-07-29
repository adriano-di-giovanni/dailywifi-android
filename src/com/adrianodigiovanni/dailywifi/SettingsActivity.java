package com.adrianodigiovanni.dailywifi;

import android.app.ActionBar;
import android.os.Bundle;

import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.adrianodigiovanni.app.AbstractPortraitFragmentActivity;

public class SettingsActivity extends AbstractPortraitFragmentActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new SettingsFragment()).commit();
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
}
