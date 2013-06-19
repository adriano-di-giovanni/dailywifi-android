package com.adrianodigiovanni.dailywifi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import com.adrianodigiovanni.net.HttpURLConnectionHelper;
import com.adrianodigiovanni.net.HttpsURLConnectionHelper;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;

/**
 * This class represents the WiFi network to connect to.
 */
public class DWFNetwork {

	private static final String DEBUG_TAG = "DWFNetwork";

	private static DWFNetwork mInstance;

	// used to detect captive networks
	private static final URL TEST_URL;
	static {
		URL url;
		try {
			url = new URL("http://dailywifi.org/success.html");
		} catch (MalformedURLException e) {
			url = null;
		}
		TEST_URL = url;
	}

	protected Context mContext;

	public static DWFNetwork getInstance(Context context) {
		if (null == mInstance) {
			mInstance = new DWFNetwork(context);
		}
		return mInstance;
	}

	private DWFNetwork(Context context) {
		mContext = context.getApplicationContext();
	}

	public void login(OnLoginCompleteListener listener) {
		new LoginTask().execute(listener);
	}

	private WifiInfo getWifiInfo() {
		WifiInfo wifiInfo = null;
		String serviceName = Context.WIFI_SERVICE;
		WifiManager wifiManager = (WifiManager) mContext
				.getSystemService(serviceName);
		if (wifiManager.isWifiEnabled()) {
			wifiInfo = wifiManager.getConnectionInfo();
		}
		return wifiInfo;
	}

	private static boolean isConnected(WifiInfo wifiInfo) {
		boolean result = false;

		if (null != wifiInfo) {
			String ssid = wifiInfo.getSSID();
			int ipAddress = wifiInfo.getIpAddress();

			if (null != ssid && 0 != ipAddress) {
				result = true;
			}
		}

		return result;
	}

	public interface OnLoginCompleteListener {
		void onLoginComplete(boolean success);
	}

	private class LoginTask extends
			AsyncTask<OnLoginCompleteListener, Void, Boolean> {

		private OnLoginCompleteListener mListener = null;

		@Override
		protected Boolean doInBackground(OnLoginCompleteListener... params) {

			mListener = params[0];

			boolean result = false;

			WifiInfo wifiInfo = getWifiInfo();

			if (isConnected(wifiInfo)) {
				Log.d(DEBUG_TAG, "Connected to WiFi network");
				Account account = Account.getBySSID(mContext,
						wifiInfo.getSSID());

				// account for network with that SSID must be in the database
				if (null != account) {
					Log.d(DEBUG_TAG,
							"Account found for the active WiFi network");

					URL responseURL = HttpURLConnectionHelper
							.getResponseURL(TEST_URL);

					boolean isCaptive = !responseURL.equals(TEST_URL);

					// network must be captive
					if (isCaptive) {
						Log.d(DEBUG_TAG, "Active network is captive");

						// TODO: check if network supports DWF API and set
						// network compatibility accordingly

						URL loginURL = getLoginURL(responseURL, account);

						HttpsURLConnection httpsURLConnection = null;
						try {
							httpsURLConnection = HttpsURLConnectionHelper
									.connectTo(loginURL, "POST");
							int responseCode = httpsURLConnection
									.getResponseCode();

							// TODO: set network compatibility if responseCode
							// is not 404

							Log.d(DEBUG_TAG,
									"Response code is: "
											+ Integer.toString(responseCode));

							switch (responseCode) {
							case HttpsURLConnection.HTTP_OK:
								// credentials are valid
								account.setIsValid(true);
								result = true;
								break;
							case HttpsURLConnection.HTTP_FORBIDDEN:
								// credentials are not valid
								account.setIsValid(false);
							}

						} catch (IOException e) {
							// do nothing
						} finally {
							if (null != httpsURLConnection) {
								httpsURLConnection.disconnect();
							}
						}
					} else {
						account.setIsCompatible(false);
					}
					account.save(mContext);
				}
			}

			return Boolean.valueOf(result);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			mListener.onLoginComplete(result.booleanValue());
		}

		private URL getLoginURL(URL baseURL, Account account) {
			URL url = null;
			try {
				url = new URL(baseURL.getProtocol(), baseURL.getHost(), 8081,
						"api/v1/account/login?username="
								+ account.getUsername() + "&password="
								+ account.getPassword());
			} catch (MalformedURLException e) {
				// do nothing
			}
			return url;
		}
	}
}
