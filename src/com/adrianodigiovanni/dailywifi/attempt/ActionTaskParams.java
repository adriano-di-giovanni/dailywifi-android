package com.adrianodigiovanni.dailywifi.attempt;

import android.content.Context;
import android.net.wifi.WifiInfo;

class ActionTaskParams {
	
	private Context mContext;
	private WifiInfo mWifiInfo;
	private ActionType mActionType;
	private OnCompleteListener mListener;
	
	public ActionTaskParams(Context context, WifiInfo wifiInfo,
			ActionType actionType, OnCompleteListener listener) {
		
		mContext = context;
		mWifiInfo = wifiInfo;
		mActionType = actionType;
		mListener = listener;
	}
	
	public Context getContext() {
		return mContext;
	}
	
	public WifiInfo getWifiInfo() {
		return mWifiInfo;
	}
	
	public ActionType getActionType() {
		return mActionType;
	}
	
	public OnCompleteListener getListener() {
		return mListener;
	}
}
