package com.alborgis.ting.mainapp.common.push;

import com.alborgis.ting.mainapp.MainApp;

import android.app.IntentService;
import android.content.Intent;

public class GcmIntentService extends IntentService {

	MainApp app;
	
    public GcmIntentService() {
        super("GcmIntentService");
        app = (MainApp)getApplication();
    }

    @Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
    protected void onHandleIntent(Intent intent) {
    	// Process the push notification
        TINGPushHandler.handleNotificationIntent(this, intent);

        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    
}
