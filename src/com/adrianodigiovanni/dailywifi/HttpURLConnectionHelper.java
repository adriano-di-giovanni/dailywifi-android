package com.adrianodigiovanni.dailywifi;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class HttpURLConnectionHelper {
	
	private HttpURLConnectionHelper() {
	}
	
	public static HttpURLConnection connectTo(URL url) throws IOException {
		URLConnection urlConnection = url.openConnection();
		HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
		httpURLConnection.setUseCaches(false);
		
		@SuppressWarnings("unused")
		int responseCode = httpURLConnection.getResponseCode();
		
		return httpURLConnection;
	}
	
	public static URL getResponseURL(URL url) {
		HttpURLConnection httpURLConnection = null;
		URL responseURL = null;
		try {
			httpURLConnection = connectTo(url);
			responseURL = httpURLConnection.getURL();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != httpURLConnection) {
				httpURLConnection.disconnect();
			}
		}
		return responseURL;
	}
}
