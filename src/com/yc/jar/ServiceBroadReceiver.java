package com.yc.jar;

import com.example.androidclient.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

public class ServiceBroadReceiver extends BroadcastReceiver {
	private static ChatConnectStateListener chatConnectStateListener = null;
	InternetStatus internet;
	private Runnable connectToServerTask = new Runnable() {

		@Override
		public void run() {
			if (internet != null) {
				if (internet.isNetworkConnected()) {
					if (chatConnectStateListener != null) {
						chatConnectStateListener.reInitSocket();
					}
				} else {
					if (chatConnectStateListener != null) {
						chatConnectStateListener.closeSocket();
					}
				}
			}
		}
	};

	@Override
	public void onReceive(Context context, Intent intent) {
		internet = new InternetStatus(context);
		if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())
				|| Intent.ACTION_USER_PRESENT.equals(intent.getAction())) {
			Intent startServiceIntent = new Intent(context.getString(R.string.service_action));
			startServiceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startService(startServiceIntent);
		} else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
			Log.i("chen", "net change");
			ThreadPoolUtil.getThreadPool().execute(connectToServerTask);
		}
	}

	public static void setChatStateChangeListener(ChatConnectStateListener cs) {
		chatConnectStateListener = cs;
	}

}
