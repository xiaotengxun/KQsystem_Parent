package com.yc.im.util;

import android.app.Application;
import android.content.Intent;

import com.yc.jar.ThreadPoolUtil;

import edu.sdjzu.parent.R;

public class DemoApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		init();
	}

	private void init() {
		// startService(new Intent(getString(R.string.service_action)));
		ThreadPoolUtil.initThreadPool();
	}
}
