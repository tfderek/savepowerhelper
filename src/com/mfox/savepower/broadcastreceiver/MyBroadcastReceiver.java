package com.mfox.savepower.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mfox.savepower.util.Utilities;
import com.mfox.savepower.service.MIntentService;
import com.mfox.savepower.service.ScreenOffService;

public class MyBroadcastReceiver extends BroadcastReceiver {
	private static final String TAG="MyBroadcastReceiver";
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action=intent.getAction();
		Log.i(TAG,action);
		Intent serviceIntent=new Intent(context,MIntentService.class);
		serviceIntent.setAction(action);
		if(action.equals(Intent.ACTION_PACKAGE_REMOVED)){
			String packageName = intent.getData().getSchemeSpecificPart();
			serviceIntent.putExtra("packageName", packageName);
		}
		context.startService(serviceIntent);
		
		if(action.equals(Intent.ACTION_BOOT_COMPLETED)){
			if(Utilities.isSaveFlag(context)){
				Intent service=new Intent(context, ScreenOffService.class);
				context.startService(service);	
		    }
		}
	}
}
