package com.adrianodigiovanni.dailywifi;

import android.content.ComponentName;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;

public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
	private static final String TAG = "SettingsFragment";
	
	public static final String KEY_PREF_ENABLED = "pref_enabled";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals(KEY_PREF_ENABLED)) {
			PackageManager packageManager = getActivity().getPackageManager();
			ComponentName componentName = new ComponentName("com.adrianodigiovanni.dailywifi", "BackgroundService");
						
			if (sharedPreferences.getBoolean(key, true)) {
				packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
			} else {
				packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
			}
			
			Log.d(TAG, Integer.toString(packageManager.getComponentEnabledSetting(componentName)));
		}
	}
}
