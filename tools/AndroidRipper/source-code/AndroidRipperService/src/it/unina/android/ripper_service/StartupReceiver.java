package it.unina.android.ripper_service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * android.intent.action.BOOT_COMPLETED Broadcast Receiver
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class StartupReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		Intent myIntent = new Intent(context, AndroidRipperService.class);
		context.startService(myIntent);
	}
	
}