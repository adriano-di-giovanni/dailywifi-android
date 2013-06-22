package com.adrianodigiovanni.dailywifi.attempt;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import com.adrianodigiovanni.dailywifi.Account;
import com.adrianodigiovanni.dailywifi.Toaster;
import com.adrianodigiovanni.net.HttpURLConnectionHelper;
import com.adrianodigiovanni.net.HttpsURLConnectionHelper;

import android.net.wifi.WifiInfo;
import android.os.AsyncTask;

public class ActionTask extends
		AsyncTask<ActionTaskParams, ActionProgress, Boolean> {

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
	
	private ActionTaskParams mParams;

	@Override
	protected Boolean doInBackground(ActionTaskParams... params) {

		mParams = params[0];

		boolean result = false;

		WifiInfo wifiInfo = mParams.getWifiInfo();

		if (null != wifiInfo && null != wifiInfo.getSSID()
				&& 0 != wifiInfo.getIpAddress()) {

			publishProgress(ActionProgress.ACTIVE_NETWORK_IS_WIFI);

			Account account = Account.getBySSID(mParams.getContext(),
					wifiInfo.getSSID());

			if (null != account) {

				publishProgress(ActionProgress.ACCOUNT_EXISTS);

				URL responseURL = HttpURLConnectionHelper
						.getResponseURL(TEST_URL);

				boolean isCaptiveOrLoggedOut = !responseURL
						.equals(TEST_URL);

				if (isCaptiveOrLoggedOut) {

					publishProgress(ActionProgress.ACTIVE_NETWORK_IS_CAPTIVE_OR_LOGGED_OUT);

					if (ActionType.LOGIN == mParams.getActionType()) {

						URL loginURL = getLoginURL(responseURL, account);

						HttpsURLConnection httpsURLConnection = null;
						try {
							httpsURLConnection = HttpsURLConnectionHelper
									.connectTo(loginURL, "POST");
							int responseCode = httpsURLConnection
									.getResponseCode();

							// TODO: set network compatibility if
							// responseCode
							// is not 404

							// Log.d(DEBUG_TAG,
							// "Response code is: "
							// + Integer.toString(responseCode));

							switch (responseCode) {
							case HttpsURLConnection.HTTP_OK:
								publishProgress(ActionProgress.LOGGED_IN);
								// credentials are valid
								account.setIsValid(true);
								result = true;
								break;
							case HttpsURLConnection.HTTP_FORBIDDEN:
								publishProgress(ActionProgress.CREDENTIALS_ARE_NOT_VALID);
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

					if (ActionType.LOGOUT == mParams.getActionType()) {
						// check if network is captive and DailyWiFi
						// compatible to logout
					}
				}

				account.save(mParams.getContext());
			}
			
		}

			return Boolean.valueOf(result);
	}
	
	@Override
	protected void onProgressUpdate(ActionProgress... values) {
		Toaster toaster = Toaster.getInstance(mParams.getContext());
		toaster.showToast(values[0].toString());
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		mParams.getListener().onComplete(result.booleanValue());
	}

	private static URL getLoginURL(URL baseURL, Account account) {
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
