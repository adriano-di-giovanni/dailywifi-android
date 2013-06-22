package com.adrianodigiovanni.dailywifi;

import com.adrianodigiovanni.dailywifi.attempt.ActionType;

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
			
			ActionType actionType = null;
			
			switch (networkState) {
			case CONNECTED:
				actionType = ActionType.LOGIN;
				break;
			case DISCONNECTING:
				actionType = ActionType.LOGOUT;
				break;
			}
			
			Log.d(DEBUG_TAG, "Network state changed");
						
			BackgroundService.startSelf(context, actionType);
		}
	}
}
