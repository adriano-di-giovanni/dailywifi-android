package com.adrianodigiovanni.dailywifi.attempt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import com.adrianodigiovanni.dailywifi.Account;
import com.adrianodigiovanni.net.HttpsURLConnectionHelper;

import android.util.Log;

/**
 * This class encapsulates info about a DailyWiFi compatible network such as API
 * version and entry point
 */
public class DWFInfo {

	private static final String TAG = "DWFInfo";
	private static final String JSON_FILE = "dailywifi.json";
	private static final String JSON_MIME_TYPE = "application/json";

	private static final String VERSION_1_0_0 = "1.0.0";

	private String mApiVersion;
	private String mApiEntryPoint;

	/**
	 * Returns a DWFInfo object if network is DailyWiFi compatible.
	 * 
	 * @param responseURL
	 *            The response URL from {@link ActionTask}
	 * @return DWFInfo or @null if network is not DailyWiFi compatible.
	 */
	public static DWFInfo fromResponseURL(URL responseURL) {

		DWFInfo dwfInfo = null;

		URL url = getURL(responseURL);
		HttpsURLConnection httpsURLConnection = null;
		int responseCode;
		String contentType;
		JSONObject jsonObject;
		String apiVersion;
		String apiEntryPoint;

		try {
			httpsURLConnection = HttpsURLConnectionHelper.connectTo(url);
			responseCode = httpsURLConnection.getResponseCode();

			if (HttpsURLConnection.HTTP_OK == responseCode) {
				contentType = httpsURLConnection.getContentType();
				if (JSON_MIME_TYPE.equalsIgnoreCase(contentType)) {
					jsonObject = jsonFromStream(httpsURLConnection
							.getInputStream());

					if (null != jsonObject) {
						apiVersion = getApiVersion(jsonObject);
						if (null != apiVersion) {
							apiEntryPoint = getApiEntryPoint(jsonObject);
							if (null != apiEntryPoint) {
								dwfInfo = new DWFInfo(apiVersion, apiEntryPoint);
							}
						}
					}
				}
			}
		} catch (IOException e) {
			// do nothing
		} finally {
			if (null != httpsURLConnection) {
				httpsURLConnection.disconnect();
			}
		}

		return dwfInfo;
	}

	private DWFInfo(String apiVersion, String apiEntryPoint) {
		mApiVersion = apiVersion;
		mApiEntryPoint = apiEntryPoint;
	}

	/**
	 * Returns login URL for the DailyWiFi compatible network
	 * 
	 * @param account
	 *            The {@link Account} object containing info such as @link
	 *            {@link Account#getUsername()} and
	 *            {@link Account#getPassword()}
	 * @return URL or @null
	 */
	public URL getLoginURL(Account account) {

		URL url = null;
		String slug;

		if (null != account) {

			// TODO: handle API versions if needed

			slug = "account/login?username=" + account.getUsername()
					+ "&password=" + account.getPassword();

			try {
				url = new URL(mApiEntryPoint + slug);
				Log.d(TAG, url.toString());
			} catch (MalformedURLException e) {
				// do nothing
			}
		}

		return url;
	}
	
	public URL getLogoutURL(Account account) {
		
		URL url = null;
		String slug;
		
		if (null != account) {
			
			// TODO: handle API versions if needed
			
			slug = "account/logout?username=" + account.getUsername();
			
			try {
				url = new URL(mApiEntryPoint + slug);
				Log.d(TAG, url.toString());
			} catch (MalformedURLException e) {
				// do nothing
			}
		}
		
		return url;
	}
	
	public boolean isLoggedIn(int responseCode) {
		// TODO: handle API versions if neede
		return HttpsURLConnection.HTTP_OK == responseCode;
	}

	/**
	 * Returns the URL to info JSON file
	 * 
	 * @param responseURL
	 *            The response URL from {@link ActionTask}
	 * @return URL to info JSON file
	 */
	private static URL getURL(URL responseURL) {
		URL url = null;
		try {
			String file = responseURL.getPath().replaceFirst("/$", "") + "/"
					+ JSON_FILE;
			url = new URL(responseURL.getProtocol(), responseURL.getHost(),
					responseURL.getPort(), file);
		} catch (MalformedURLException e) {
			// do nothing
		}
		return url;
	}

	/**
	 * Gets json object from input stream
	 * 
	 * @param inputStream
	 * @return @link {@link JSONObject} or @null if invalid JSON syntax
	 */
	private static JSONObject jsonFromStream(InputStream inputStream) {

		JSONObject jsonObject = null;

		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		StringBuilder stringBuilder = new StringBuilder();
		String line;

		try {
			while (null != (line = bufferedReader.readLine())) {
				stringBuilder.append(line);
			}
		} catch (IOException e) {
			// do nothing
		}

		Log.d(TAG, stringBuilder.toString());

		try {
			jsonObject = new JSONObject(stringBuilder.toString());
		} catch (JSONException e) {
			// do nothing
		}

		return jsonObject;
	}

	private static String getApiVersion(JSONObject jsonObject) {
		String apiVersion = null;
		try {
			apiVersion = jsonObject.getJSONObject("api").getString("version");
		} catch (JSONException e) {
			// do nothing
		}
		return apiVersion;
	}

	private static String getApiEntryPoint(JSONObject jsonObject) {
		String apiEntryPoint = null;
		try {
			apiEntryPoint = jsonObject.getJSONObject("api")
					.getString("entryPoint").replaceFirst("/$", "")
					+ "/";
		} catch (JSONException e) {
			// do nothing
		}
		return apiEntryPoint;
	}
}
