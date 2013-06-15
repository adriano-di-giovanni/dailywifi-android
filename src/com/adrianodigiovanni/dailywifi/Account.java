package com.adrianodigiovanni.dailywifi;

public class Account {
	private String mSSID = null;
	private String mUsername = null;
	private String mPassword = null;

	public Account(String ssid, String username, String password) {
		mSSID = ssid;
		mUsername = username;
		mPassword = password;
	}
	
	public String getSSID() {
		return mSSID;
	}
	
	public void setSSID(String ssid) {
		mSSID = ssid;
	}
	
	public String getUsername() {
		return mUsername;
	}
	
	public void setUsername(String username) {
		mUsername = username;
	}
	
	public String getPassword() {
		return mPassword;
	}
	
	public void setPassword(String password) {
		mPassword = password;
	}
}
