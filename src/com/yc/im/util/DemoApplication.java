package com.yc.im.util;

import com.example.androidclient.R;
import com.yc.jar.ThreadPoolUtil;

import android.app.Application;
import android.content.Intent;

public class DemoApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		init();
	}

	private void init(){
		startService(new Intent(getString(R.string.service_action)));
		ThreadPoolUtil.initThreadPool();
	}
}
