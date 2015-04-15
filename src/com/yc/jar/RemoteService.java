package com.yc.jar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import edu.sdjzu.parent.R;

public class RemoteService extends Service {
	private AlarmManager mAlarmManager = null;
	private PendingIntent mPendingIntent = null;
	private Runnable connectToServerTask;

	@Override
	public void onCreate() {
		// initAlarm();

		connectToServerTask = new Runnable() {

			@Override
			public void run() {
				ChatConnectTool.initSocket();
			}
		};
		super.onCreate();
	}

	private void initAlarm() {
//		Intent intent = new Intent(getString(R.string.service_action));
//		mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//		mPendingIntent = PendingIntent.getService(this, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
//		long now = System.currentTimeMillis();
//		mAlarmManager.setInexactRepeating(AlarmManager.RTC, now, 60000, mPendingIntent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		flags = START_STICKY;
		ThreadPoolUtil.getThreadPool().execute(connectToServerTask);
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {

		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
