package com.adrianodigiovanni.dailywifi;

public class Account {
	private String mSSID;
	private String mUsername;
	private String mPassword;
	private int mIsCompatible;
	private int mIsValid;

	public Account(String ssid, String username, String password) {
		this(ssid, username, password, AccountsTable.TRISTATE_NOT_APPLICABLE,
				AccountsTable.TRISTATE_NOT_APPLICABLE);
	}

	private Account(String ssid, String username, String password,
			int isCompatible, int isValid) {
		mSSID = ssid;
		mUsername = username;
		mPassword = password;
		mIsCompatible = isCompatible;
		mIsValid = isValid;
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
	
	public Boolean getIsCompatible() {
		return getBoolean(mIsCompatible);
	}
	
	public Boolean getIsValid() {
		return getBoolean(mIsValid);
	}
	
	public void setIsCompatible(Boolean value) {
		mIsCompatible = toTriState(value);
	}
	
	public void setIsValid(Boolean value) {
		mIsValid = toTriState(value);
	}
	
	private int toTriState(Boolean fromBoolean) {
		int is = AccountsTable.TRISTATE_NOT_APPLICABLE;
		if (null != fromBoolean) {
			is = (fromBoolean.booleanValue()) ? AccountsTable.TRISTATE_TRUE : AccountsTable.TRISTATE_FALSE;
		}
		return is;
	}
	
	private Boolean getBoolean(int fromTriState) {
		Boolean is = null;
		if (AccountsTable.TRISTATE_TRUE == fromTriState) {
			is = Boolean.TRUE;
		} else if (AccountsTable.TRISTATE_FALSE == fromTriState) {
			is = Boolean.FALSE;
		}
		return is;
	}
}
