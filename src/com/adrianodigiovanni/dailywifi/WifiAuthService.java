package com.adrianodigiovanni.dailywifi;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

public class WifiAuthService extends Service {

	public static final String DAILYWIFI_URL = "http://dailywifi.org/success.html";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (WifiHelper.isActiveNetworkDailyWifi(this)) {
			try {
				URL url = new URL(DAILYWIFI_URL);
				new SignOnIfDailyWiFiCaptiveNetworkTask().execute(url);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		stopSelf();

		return START_STICKY;
	}

	private static class SignOnIfDailyWiFiCaptiveNetworkTask extends
			AsyncTask<URL, Void, Void> {

		private static final String TAG = "DetectCaptiveNetwork";

		@Override
		protected Void doInBackground(URL... urls) {

			// Check if active Wi-Fi network is a captive one
			URL url = urls[0];
			URL responseURL = HttpURLConnectionHelper.getResponseURL(url);
			boolean isCaptiveNetwork = !url.equals(responseURL);

			// TODO: Check if active Wi-Fi network supports DailyWiFi API

			if (isCaptiveNetwork) {
				// TODO: Replace with production code
				try {
					URL signOnURL = new URL(responseURL.getProtocol(),
							responseURL.getHost(), 8081,
							"api/v1/account/login?username=3408937951&password=2e0e191b67");
					HttpsURLConnection httpsURLConnection = HttpsURLConnectionHelper
							.connectTo(signOnURL, "POST");

					int responseCode = httpsURLConnection.getResponseCode();
					Log.d(TAG, Integer.toString(responseCode));
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			return null;
		}
	}
}
