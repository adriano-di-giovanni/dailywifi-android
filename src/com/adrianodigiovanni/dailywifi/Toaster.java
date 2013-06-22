package com.adrianodigiovanni.dailywifi;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Singleton Toast manager
 * 
 * @see android.widget.Toast
 */
public class Toaster {
	
	private static final String DEBUG_TAG = "Toaster";

	private static Toaster mInstance = null;

	private Context mContext;
	private Toast mToast;
	private TextView mTextView;

	/**
	 * Gets singleton instance
	 */
	public static Toaster getInstance(Context context) {
		if (null == mInstance) {
			mInstance = new Toaster(context.getApplicationContext());
		}
		return mInstance;
	}

	private Toaster(Context context) {
		mContext = context;
		createToast();
	}

	private void createToast() {
		mToast = new Toast(mContext);

		String serviceName = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(serviceName);

		View layout = inflater.inflate(R.layout.toast_layout, null);

		mTextView = (TextView) layout.findViewById(R.id.toast_text);

		mToast.setDuration(Toast.LENGTH_LONG);
		mToast.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 0);
		mToast.setView(layout);
	}

	/**
	 * Shows Toast
	 * 
	 * @param text
	 *            The text you want to show
	 */
	public void showToast(CharSequence text) {
		Log.d(DEBUG_TAG, (String) text);
//		mToast.cancel();
		mTextView.setText(text);
		mToast.show();
	}
}
