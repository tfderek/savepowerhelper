package com.mfox.savepower.service;

import com.mfox.savepower.util.Utilities;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MIntentService extends IntentService {
	
	private static final String TAG="MIntentService";
	
	public MIntentService(){
		super("com.mfox.savepower.service.MIntentService");
	}

	public MIntentService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		String action=intent.getAction();
		Log.i(TAG,action);
		Context context=getApplicationContext();
		if(action.equals(Intent.ACTION_USER_PRESENT)){
			Utilities.stopSavePower(context);
		}else if(action.equals(Intent.ACTION_PACKAGE_REMOVED)){
			String packageName = intent.getStringExtra("packageName");
			Utilities.deleteApp(context,packageName);
		}else if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF))
			Utilities.startSavePower(context);
	}

}
