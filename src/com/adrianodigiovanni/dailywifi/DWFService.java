package com.adrianodigiovanni.dailywifi;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Background service 
 */
public class DWFService extends Service {

	private static final String DEBUG_TAG = "DWFService";

	private static final String EXTRA_MODE = "mode";
	public static final String MODE_LOGIN = "login";
	public static final String MODE_LOGOUT = "logout";

	/**
	 * Activities and receivers must call this static method to start the service 
	 * 
	 * @see WifiStateChangeReceiver
	 * @see AddEditAccountActivity
	 */
	public static void startSelf(Context context, String mode) {
		if (null != mode) {
			Intent serviceIntent = new Intent(context.getApplicationContext(), DWFService.class);
			serviceIntent.putExtra(EXTRA_MODE, mode);
			context.startService(serviceIntent);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		final String mode = intent.getStringExtra(EXTRA_MODE);

		DWFNetwork dwfNetwork = DWFNetwork.getInstance(this);

		if (mode.equals(MODE_LOGIN)) {
			Log.d(DEBUG_TAG, "Service trying to login...");
			
			dwfNetwork.login(new DWFNetwork.OnCompleteListener() {
				
				@Override
				public void onComplete(boolean success) {
					Log.d(DEBUG_TAG, Boolean.toString(success));
					stopSelf();
				}
			});			
		}

		return START_STICKY;
	}
}
