package com.adrianodigiovanni.dailywifi.attempt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import com.adrianodigiovanni.dailywifi.Account;
import com.adrianodigiovanni.dailywifi.R;
import com.adrianodigiovanni.net.HttpURLConnectionHelper;
import com.adrianodigiovanni.net.HttpsURLConnectionHelper;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
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
	private int mNotificationId = 0;

	@Override
	protected Boolean doInBackground(ActionTaskParams... params) {

		mParams = params[0];
		Context context = mParams.getContext();
		ActionType actionType = mParams.getActionType();

		boolean result = false;

		WifiInfo wifiInfo = mParams.getWifiInfo();

		Account account;
		String ssid;
		URL responseURL;
		DWFInfo dwfInfo;
		URL actionTaskURL;
		HttpsURLConnection httpsURLConnection = null;
		int responseCode;

		if (null != wifiInfo && null != wifiInfo.getSSID()
				&& 0 != wifiInfo.getIpAddress()) {

			// publishProgress(ActionProgress.ACTIVE_NETWORK_IS_WIFI);

			ssid = wifiInfo.getSSID();

			account = Account.getBySSID(context, ssid);

			if (null != account) {

				publishProgress(ActionProgress.ACCOUNT_EXISTS);

				responseURL = HttpURLConnectionHelper.getResponseURL(TEST_URL);

				boolean isCaptiveAndLoggedOut = !responseURL.equals(TEST_URL);

				if (isCaptiveAndLoggedOut) {

					account.setRedirectURL(responseURL);

					// publishProgress(ActionProgress.ACTIVE_NETWORK_IS_CAPTIVE_AND_LOGGED_OUT);

					dwfInfo = DWFInfo.fromRedirectURL(responseURL, ssid,
							context);

					// network is dailywifi compatible
					if (null != dwfInfo) {

						account.setIsCompatible(true);

						switch (actionType) {
						case LOGIN:
							actionTaskURL = dwfInfo.getLoginURL(account);

							try {

								httpsURLConnection = HttpsURLConnectionHelper
										.connectTo(actionTaskURL, "POST");

								responseCode = httpsURLConnection
										.getResponseCode();

								if (dwfInfo.isLoggedIn(responseCode)) {
									publishProgress(ActionProgress.LOGGED_IN);
									account.setIsValid(true);
									account.setLastAccess(System
											.currentTimeMillis());
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

							break;
						case LOGOUT:
							publishProgress(ActionProgress.ALREADY_LOGGED_OUT);
							break;
						}
					} else {
						publishProgress(ActionProgress.ACTIVE_NETWORK_IS_NOT_COMPATIBLE);
						account.setIsCompatible(false);
					}
				} else {

					responseURL = account.getRedirectURL();

					// if device is logged in before account creation try to get
					// info from the SSID
					// TODO: create a task to recover information about wi fi
					// network accounts with responseURL == null while device is
					// online
					// TODO: Ask Federico to verify logout API resource on FreeWiFiGenova
					dwfInfo = (null != responseURL) ? DWFInfo.fromRedirectURL(
							responseURL, ssid, context) : DWFInfo.fromSSID(
							ssid, context);

					if (null != dwfInfo) {

						switch (actionType) {
						case LOGIN:
							publishProgress(ActionProgress.ALREADY_LOGGED_IN);
							break;
						case LOGOUT:
							actionTaskURL = dwfInfo.getLogoutURL(account);

							try {

								httpsURLConnection = HttpsURLConnectionHelper
										.connectTo(actionTaskURL, "POST");

								responseCode = httpsURLConnection
										.getResponseCode();

								if (dwfInfo.isLoggedOut(responseCode)) {
									publishProgress(ActionProgress.LOGGED_OUT);
									result = true;
								}
							} catch (IOException e) {
								// do nothing
							} finally {
								if (null != httpsURLConnection) {
									httpsURLConnection.disconnect();
								}
							}
							break;
						}
					} else {
						publishProgress(ActionProgress.ALREADY_LOGGED_IN);
					}
				}

				account.save(context);
			}

		}

		return Boolean.valueOf(result);
	}

	@Override
	protected void onProgressUpdate(ActionProgress... values) {
		Context context = mParams.getContext();

		ActionProgress actionProgress = values[0];

		String ssid = mParams.getWifiInfo().getSSID();

		String contentTitle = context
				.getString(context.getApplicationInfo().labelRes);
		String contentText;
		int priority = NotificationCompat.PRIORITY_DEFAULT;
		int smallIcon = R.drawable.ic_stat_notify_app;
		boolean indeterminate = true;
		int defaults = 0;

		switch (actionProgress) {
		case ACCOUNT_EXISTS:
			mNotificationId++;
			contentText = context.getString(R.string.accountExists, ssid);
			break;
		case ACTIVE_NETWORK_IS_NOT_COMPATIBLE:
			contentText = context.getString(R.string.networkIsNotCompatible,
					ssid);
			indeterminate = false;
			break;
		case CREDENTIALS_ARE_NOT_VALID:
			contentText = context.getString(R.string.invalidCredentials, ssid);
			priority = NotificationCompat.PRIORITY_MAX;
			indeterminate = false;
			defaults = Notification.DEFAULT_ALL;
			break;
		case LOGGED_IN:
			contentText = context.getString(R.string.loggedIn, ssid);
			priority = NotificationCompat.PRIORITY_HIGH;
			indeterminate = false;
			defaults = Notification.DEFAULT_ALL;
			break;
		case ALREADY_LOGGED_IN:
			contentText = context.getString(R.string.alreadyLoggedIn, ssid);
			indeterminate = false;
			break;
		case LOGGED_OUT:
			contentText = context.getString(R.string.loggedOut, ssid);
			indeterminate = false;
			break;
		case ALREADY_LOGGED_OUT: 
			contentText = context.getString(R.string.alreadyLoggedOut, ssid);
			indeterminate = false;
			break;
		default:
			contentText = actionProgress.toString();
			break;
		}

		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				context);

		builder.setAutoCancel(true); // TODO: add intent
		builder.setContentTitle(contentTitle);
		builder.setContentText(contentText);
		builder.setDefaults(defaults);
		builder.setPriority(priority);
		builder.setProgress(0, 0, indeterminate);
		builder.setSmallIcon(smallIcon);

		String serviceName = Context.NOTIFICATION_SERVICE;
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(serviceName);
		notificationManager.notify(mNotificationId, builder.build());
	}

	@Override
	protected void onPostExecute(Boolean result) {
		mParams.getListener().onComplete(result.booleanValue());
	}
}
