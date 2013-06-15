package com.adrianodigiovanni.dailywifi;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class DWFService extends Service {
	
	private static final String DEBUG_TAG = "DWFService";
	
	public static void startSelf(Context context) {
		Intent serviceIntent = new Intent(context, DWFService.class);
		context.startService(serviceIntent);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		DWFNetwork dwfNetwork = DWFNetwork.getInstance(this);
		
		// TODO: logout on wifi disabled
		
		dwfNetwork.login(new DWFNetwork.OnLoginCompleteListener() {
			
			@Override
			public void onLoginComplete(boolean success) {
				Log.d(DEBUG_TAG, Boolean.toString(success));
				stopSelf();
			}
		});
		
		return START_STICKY;
	}
}
