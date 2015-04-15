package edu.sdjzu.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import edu.sdjzu.attr.Attr;
import edu.sdjzu.localtool.InternetStatus;
import edu.sdjzu.model.KQInfo;
import edu.sdjzu.parent.LoginAct;
import edu.sdjzu.parent.R;
import edu.sdjzu.parenttool.ParentDtTool;
import edu.sdjzu.xmpp.AmackManage;

public class RemoteService extends Service {
	private InternetStatus internetStatus = null;
	private final int INFO_GET_TIME = 5000;// 检测服务器是否有考勤信息到来的时间间隔
	private ParentDtTool manageTool = null;
	private Runnable getInfoTask;// 从服务器获得考勤信息的任务
	private boolean isInfoGetting = false;// 是否正在从服务器获得考勤信息的标识
	private Runnable userLoginTask;
	private final int CHAT_LOGIN_SUCCESS = 0;
	private final String tag = "chen";
	private boolean isRegistered = false;
	private Handler mHandler;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		flags = START_STICKY;
		startGetNewKqInfo();
		return super.onStartCommand(intent, flags, startId);
	}

	private void startGetNewKqInfo() {
		isInfoGetting = true;
		new Thread(getInfoTask).start();
	}

	private void init() {
		manageTool = new ParentDtTool(getApplicationContext());
		internetStatus = new InternetStatus(getApplicationContext());
		getInfoTask = new Runnable() {
			@Override
			public void run() {
				while (isInfoGetting) {
					List<String> list = manageTool.getLatestKqInfoByParentNo(Attr.userName);
					List<KQInfo> listKq = new ArrayList<KQInfo>();
					for (String s : list) {
						String[] sArray = s.split("、");
						if (sArray.length >= 2) {
							KQInfo kqInfo = new KQInfo();
							kqInfo.setDateTime(sArray[1]);
							kqInfo.setMsg(sArray[0]);
							kqInfo.setTname(sArray[2]);
							kqInfo.setIsRead(0);
							listKq.add(kqInfo);
						}
					}
					if (listKq.size() > 0) {
						manageTool.insertKqInfo(listKq);
						Intent intent = new Intent(getString(R.string.ACTION_NEW_KQ));
						// intent.putExtra("info", (Serializable) listKq);
						sendBroadcast(intent);
					}
					try {
						Thread.sleep(INFO_GET_TIME);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}
		};
		userLoginTask = new Runnable() {

			@Override
			public void run() {
				if (AmackManage.checkConnection(reconnectUserName, isRegistered)) {
					mHandler.sendEmptyMessage(CHAT_LOGIN_SUCCESS);
				}
			}
		};
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case CHAT_LOGIN_SUCCESS:
					Log.i(tag, "注册消息监听！");
					AmackManage.addReconnectionListener();
					// AmackManage.getOffLine();
					AmackManage.getOnLineMsg(getApplicationContext());
					break;
				}
				super.handleMessage(msg);
			}
		};
	}

	private String reconnectUserName = "";

	@Override
	public void onCreate() {
		init();
		reconnectUserName = getSharedPreferences(Attr.sharePrefenceName, 0).getString(Attr.loginUserName, "chen");
		isRegistered = getSharedPreferences(Attr.sharePrefenceName, 0).getBoolean(Attr.userRegisterKey, false);
		if (null != reconnectUserName && !reconnectUserName.equals("")) {
			new Thread(userLoginTask).start();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		isInfoGetting = false;
	}

}
