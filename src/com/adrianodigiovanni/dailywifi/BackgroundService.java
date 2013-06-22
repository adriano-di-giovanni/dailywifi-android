package com.adrianodigiovanni.dailywifi;

import com.adrianodigiovanni.dailywifi.attempt.ActionType;
import com.adrianodigiovanni.dailywifi.attempt.Attempt;
import com.adrianodigiovanni.dailywifi.attempt.OnCompleteListener;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class BackgroundService extends Service {

	private static final String DEBUG_TAG = "BackgroundService";

	private static final String EXTRA_ACTION_TYPE = "actionType";

	/**
	 * Activities and receivers must call this static method to start the
	 * service
	 * 
	 * @see WifiStateChangeReceiver
	 * @see AddEditAccountActivity
	 */
	public static void startSelf(Context context, ActionType actionType) {
		if (null != actionType) {
			Intent serviceIntent = new Intent(context.getApplicationContext(),
					BackgroundService.class);
			serviceIntent.putExtra(EXTRA_ACTION_TYPE, actionType);
			context.startService(serviceIntent);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		final ActionType actionType = (ActionType) intent
				.getSerializableExtra(EXTRA_ACTION_TYPE);

		Log.d(DEBUG_TAG, actionType.toString());

		Attempt attempt = Attempt.getInstance(this);
		attempt.tryAction(actionType, new OnCompleteListener() {

			@Override
			public void onComplete(boolean result) {
				stopSelf();
			}
		});

		return START_STICKY;
	}
}
