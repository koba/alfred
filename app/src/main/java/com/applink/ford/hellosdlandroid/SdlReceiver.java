package com.applink.ford.hellosdlandroid;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SdlReceiver  extends BroadcastReceiver {		
	public void onReceive(Context context, Intent intent) {
		final BluetoothDevice bluetoothDevice = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

		Log.d("Receiver", intent.getAction());
		
		// if SYNC connected to phone via bluetooth, start service (which starts proxy)
		if (intent.getAction().compareTo(BluetoothDevice.ACTION_ACL_CONNECTED) == 0) {
			Log.d("Receiver:ACL_CONNECTED", intent.getAction());
			com.applink.ford.hellosdlandroid.SdlService serviceInstance = com.applink.ford.hellosdlandroid.SdlService.getInstance();
			if (serviceInstance == null) {
				Intent startIntent = new Intent(context, com.applink.ford.hellosdlandroid.SdlService.class);
				startIntent.putExtras(intent);
				context.startService(startIntent);
			}
		}
		else if (intent.getAction().equals(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
			Log.d("Receiver:AUDIO_NOISY", intent.getAction());
			// signal your service to stop playback
		}
	}
}