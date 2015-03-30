package com.yc.im.boot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.example.androidclient.R;
import com.yc.im.model.PresenceModel;
import com.yc.im.util.ChatUtil;
import com.yc.jar.ThreadPoolUtil;

public class LoginAct extends Activity {
	private Button btnLogin;
	private EditText accountEdit;
	private String accoutStr = "";
	private Handler mHandler;
	private static final int LOGIN_SUCCESS = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		init();

	}

	private void init() {
		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case LOGIN_SUCCESS:
					getSharedPreferences("chat", 0).edit().putString("id", accoutStr).commit();
					Log.i("chen", "transfer act");
					startActivity(new Intent(LoginAct.this, ChatFriendlistAct.class));
					break;
				}
				super.handleMessage(msg);
			}

		};
		btnLogin = (Button) findViewById(R.id.button);
		accountEdit = (EditText) findViewById(R.id.user_account);
		btnLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				accoutStr = accountEdit.getText().toString();
				if (accoutStr != null && !accoutStr.equals("")) {
					Runnable runn = new Runnable() {
						@Override
						public void run() {
							PresenceModel pre = new PresenceModel();
							pre.setId(accoutStr);
							pre.setState(pre.STATE_AVAILABLE);
							Log.i("chen", "transfer 11111");
							new ChatUtil().sendPresence(pre);
							Log.i("chen", "transfer 2222");
							mHandler.sendEmptyMessage(LOGIN_SUCCESS);
							Log.i("chen", "transfer 33333");
						}
					};
					ThreadPoolUtil.getThreadPool().execute(runn);
				} else {

				}
			}
		});
	}

}
