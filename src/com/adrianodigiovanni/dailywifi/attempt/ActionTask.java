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
import android.util.Log;

public class ActionTask extends
		AsyncTask<ActionTaskParams, ActionProgress, Boolean> {

	private static final String TAG = "ActionTask";

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

		Account account;
		URL responseURL;
		DWFInfo dwfInfo;
		URL actionTaskURL;
		HttpsURLConnection httpsURLConnection = null;
		int responseCode;

		if (null != wifiInfo && null != wifiInfo.getSSID()
				&& 0 != wifiInfo.getIpAddress()) {

			publishProgress(ActionProgress.ACTIVE_NETWORK_IS_WIFI);

			account = Account.getBySSID(mParams.getContext(),
					wifiInfo.getSSID());

			if (null != account) {

				publishProgress(ActionProgress.ACCOUNT_EXISTS);

				responseURL = HttpURLConnectionHelper.getResponseURL(TEST_URL);

				boolean isCaptiveAndLoggedOut = !responseURL.equals(TEST_URL);

				if (isCaptiveAndLoggedOut) {

					publishProgress(ActionProgress.ACTIVE_NETWORK_IS_CAPTIVE_AND_LOGGED_OUT);

					dwfInfo = DWFInfo.fromResponseURL(responseURL);

					// network is dailywifi compatible
					if (null != dwfInfo) {
						if (ActionType.LOGIN == mParams.getActionType()) {

							actionTaskURL = dwfInfo.getLoginURL(account);

							try {
								
								httpsURLConnection = HttpsURLConnectionHelper
										.connectTo(actionTaskURL, "POST");
								
								responseCode = httpsURLConnection
										.getResponseCode();
								
								if (dwfInfo.isLoggedIn(responseCode)) {
									publishProgress(ActionProgress.LOGGED_IN);
									account.setIsValid(true);
									result = true;
								} else {
									publishProgress(ActionProgress.CREDENTIALS_ARE_NOT_VALID);
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
}
