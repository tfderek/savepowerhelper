package com.mfox.savepower.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class ScreenOffService extends Service {	

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		IntentFilter filter=new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.setPriority(Integer.MAX_VALUE);
		registerReceiver(mBroadCastReceiver, filter);
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return Service.START_STICKY;
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mBroadCastReceiver);
	}
	
	private BroadcastReceiver mBroadCastReceiver=new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Log.i("zyl","ACTION_SCREEN_OFF");
			if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
				Intent serviceIntent=new Intent(context,MIntentService.class);
				serviceIntent.setAction(Intent.ACTION_SCREEN_OFF);
				context.startService(serviceIntent);
			}
		}
	};
}
