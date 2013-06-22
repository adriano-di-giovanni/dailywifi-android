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

	private enum AccountOperation {
		LOGIN, LOGOUT
	}

	private enum AccountOperationState {
		ACTIVE_NETWORK_IS_WIFI, ACCOUNT_FOUND, ACTIVE_NETWORK_IS_CAPTIVE, LOGGED_IN, CREDENTIALS_ARE_NOT_VALID
	}

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

	/**
	 * Gets the singleton instance
	 */
	public static DWFNetwork getInstance(Context context) {
		if (null == mInstance) {
			mInstance = new DWFNetwork(context);
		}
		return mInstance;
	}

	private DWFNetwork(Context context) {
		mContext = context.getApplicationContext();
	}

	/**
	 * Starts login
	 */
	public void login(OnCompleteListener listener) {
		AccountTaskParams params = new AccountTaskParams(
				AccountOperation.LOGIN, listener);
		new AccountTask().execute(params);
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

	public interface OnCompleteListener {
		void onComplete(boolean success);
	}

	private class AccountTaskParams {

		private AccountOperation mAccountOperation;

		private OnCompleteListener mListener;

		public AccountTaskParams(AccountOperation accountOperation,
				OnCompleteListener listener) {
			mAccountOperation = accountOperation;
			mListener = listener;
		}

		public AccountOperation getAccountOperation() {
			return mAccountOperation;
		}

		public OnCompleteListener getListener() {
			return mListener;
		}
	}

	private class AccountTask extends
			AsyncTask<AccountTaskParams, AccountOperationState, Boolean> {

		AccountTaskParams mParams;

		@Override
		protected Boolean doInBackground(AccountTaskParams... params) {

			mParams = params[0];

			boolean result = false;

			WifiInfo wifiInfo = getWifiInfo();

			if (isConnected(wifiInfo)) {

				publishProgress(AccountOperationState.ACTIVE_NETWORK_IS_WIFI);
				
				Account account = Account.getBySSID(mContext,
						wifiInfo.getSSID());

				if (null != account) {

					publishProgress(AccountOperationState.ACCOUNT_FOUND);
					
					URL responseURL = HttpURLConnectionHelper
							.getResponseURL(TEST_URL);

					boolean isCaptiveOrLoggedOut = !responseURL
							.equals(TEST_URL);

					if (isCaptiveOrLoggedOut) {
						
						publishProgress(AccountOperationState.ACTIVE_NETWORK_IS_CAPTIVE);
						
						if (AccountOperation.LOGIN == mParams
								.getAccountOperation()) {
							
							URL loginURL = getLoginURL(responseURL, account);
							
							HttpsURLConnection httpsURLConnection = null;
							try {
								httpsURLConnection = HttpsURLConnectionHelper
										.connectTo(loginURL, "POST");
								int responseCode = httpsURLConnection
										.getResponseCode();

								// TODO: set network compatibility if responseCode
								// is not 404

								// Log.d(DEBUG_TAG,
								// "Response code is: "
								// + Integer.toString(responseCode));

								switch (responseCode) {
								case HttpsURLConnection.HTTP_OK:
									publishProgress(AccountOperationState.LOGGED_IN);
									// credentials are valid
									account.setIsValid(true);
									result = true;
									break;
								case HttpsURLConnection.HTTP_FORBIDDEN:
									publishProgress(AccountOperationState.CREDENTIALS_ARE_NOT_VALID);
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
						}
					} else {
						
						if (AccountOperation.LOGOUT == mParams.getAccountOperation()) {
							// check if network is captive and DailyWiFi compatible to logout
						}
					}
					
					account.save(mContext);
				}
			}

			return Boolean.valueOf(result);
		}

		@Override
		protected void onProgressUpdate(AccountOperationState... values) {
			AccountOperationState state = values[0];

			switch (state) {
			case ACTIVE_NETWORK_IS_WIFI:
				break;
			case ACCOUNT_FOUND:
				break;
			case ACTIVE_NETWORK_IS_CAPTIVE:
				break;
			case LOGGED_IN:
				break;
			case CREDENTIALS_ARE_NOT_VALID:
				break;
			}

			Log.d(DEBUG_TAG, state.toString());
			Toaster toaster = Toaster.getInstance(mContext);
			toaster.showToast(state.toString());
		}

		@Override
		protected void onPostExecute(Boolean result) {
			mParams.getListener().onComplete(result.booleanValue());
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
