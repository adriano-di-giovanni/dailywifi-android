package com.adrianodigiovanni.dailywifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * This class is the receiver for WiFi network events 
 */
public class WifiStateChangeReceiver extends BroadcastReceiver {
	
	private static final String DEBUG_TAG = "WifiStateChangeReceiver";
	
	@SuppressWarnings("incomplete-switch")
	@Override
	public void onReceive(Context context, Intent intent) {

		String intentAction = intent.getAction();

		if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intentAction)) {
			
			NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			NetworkInfo.State networkState = networkInfo.getState();
			
			String mode = null;
			
			switch (networkState) {
			case CONNECTED:
				mode = DWFService.MODE_LOGIN;
				break;
			case DISCONNECTING:
				mode = DWFService.MODE_LOGOUT;
				break;
			}
			
			Log.d(DEBUG_TAG, "Network state changed");
						
			DWFService.startSelf(context, mode);
		}
	}
}
