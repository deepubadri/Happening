package com.mapsAPI;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

public class OnBootReceiver extends BroadcastReceiver {
	private static final int PERIOD=300000;  // 5 minutes

	@Override
	public void onReceive(Context context, Intent intent) {

		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE); 
		Intent i = new Intent(context, BackgroudService.class); 
		PendingIntent pi = PendingIntent.getService(context, 0, i, 0); am.cancel(pi); 
		// by my own convention, minutes <= 0 means notifications are disabled if (minutes > 0) {
		am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 15*60*1000, 15*60*1000, pi); 
	}
}