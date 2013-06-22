package com.adrianodigiovanni.dailywifi.attempt;

import com.adrianodigiovanni.net.WifiHelper;

import android.content.Context;

public class Attempt {

	private static Attempt mInstance = null;

	private Context mContext;

	public static Attempt getInstance(Context context) {
		if (null == mInstance) {
			mInstance = new Attempt(context.getApplicationContext());
		}
		return mInstance;
	}
	
	public void tryAction(ActionType actionType, OnCompleteListener listener) {
		ActionTaskParams params = new ActionTaskParams(mContext,
				WifiHelper.getWifiInfo(mContext), actionType,
				listener);
		new ActionTask().execute(params);
	}

	private Attempt(Context context) {
		mContext = context;
	}
}
