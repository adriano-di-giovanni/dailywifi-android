package com.adrianodigiovanni.dailywifi;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpsURLConnectionHelper {

	private HttpsURLConnectionHelper() {
	}

	public static HttpsURLConnection connectTo(URL url, String requestMethod) throws IOException {
		trustEverything();
		URLConnection urlConnection = url.openConnection();
		HttpsURLConnection httpsURLConnection = (HttpsURLConnection) urlConnection;
		httpsURLConnection.setRequestMethod(requestMethod);
		httpsURLConnection.connect();

		@SuppressWarnings("unused")
		int responseCode = httpsURLConnection.getResponseCode();

		return httpsURLConnection;
	}
	
	public static HttpsURLConnection connectTo(URL url) throws IOException {
		return connectTo(url, "GET");
	}
	
	private static void trustEverything() {
	    TrustManager[] trustManager = new TrustManager[] {new TrustEverythingTrustManager()};

	    // Let us create the factory where we can set some parameters for the connection
	    SSLContext sslContext = null;
	    try {
	        sslContext = SSLContext.getInstance("SSL");
	        sslContext.init(null, trustManager, new java.security.SecureRandom());
	    } catch (NoSuchAlgorithmException e) {
	        // do nothing
	    }catch (KeyManagementException e) {
	        // do nothing
	    }

	    HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
	}

	// TODO: it is not safe to trust everything
	private static class TrustEverythingTrustManager implements X509TrustManager {
		
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public void checkClientTrusted(
				java.security.cert.X509Certificate[] certs, String authType) {
		}

		public void checkServerTrusted(
				java.security.cert.X509Certificate[] certs, String authType) {
		}
	}
	
    public static class VerifyEverythingHostnameVerifier implements HostnameVerifier {

        public boolean verify(String string, SSLSession sslSession) {
            return true;
        }
    }

}
