package com.adrianodigiovanni.net;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class WifiHelper {

	private WifiHelper() {
	}

	/**
	 * Gets WiFi Info
	 * @return A {@link android.net.wifi.WifiInfo} object or null if active network is not WiFi
	 */
	public static WifiInfo getWifiInfo(Context context) {
		WifiInfo wifiInfo = null;
		String serviceName = Context.WIFI_SERVICE;
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(serviceName);
		if (wifiManager.isWifiEnabled()) {
			wifiInfo = wifiManager.getConnectionInfo();
		}
		return wifiInfo;
	}
}
