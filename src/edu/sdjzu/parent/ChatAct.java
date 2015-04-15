package edu.sdjzu.parent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import edu.sdjzu.adapter.InfoChatAdapter;
import edu.sdjzu.attr.Attr;
import edu.sdjzu.model.ChatInfo;
import edu.sdjzu.parenttool.ParentDtTool;
import edu.sdjzu.parenttool.ParentUtil;
import edu.sdjzu.xmpp.AmackManage;

public class ChatAct extends Activity {
	private ListView listVewChat;
	private List<ChatInfo> listChat = new ArrayList<ChatInfo>();
	private EditText inputEditView;
	private ImageView sendBtn, backBtn;
	private InfoChatAdapter adapter;
	private String mNo = "", mName = "";
	private ParentDtTool parentTool;
	private TextView titleTV;
	private String stuName = "";
	private final int NEW_CHAT_INFO = 0;
	private Handler mHandler;
	private InputMethodManager imm;
	private NewChatInfoReceiver newChatInfoReceiver = null;
	private final int TIME_MSG_SHOW_DELEY = 100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info_chat);
		mNo = getIntent().getStringExtra("no");
		mName = getIntent().getStringExtra("name");
		parentTool = new ParentDtTool(this);
		stuName = parentTool.getStuName(Attr.userName);
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		init();
		registerReceiver();
	}

	private void registerReceiver() {
		IntentFilter intentFilter = new IntentFilter(getString(R.string.ACTION_NEW_CHATINFO_ACTION));
		intentFilter.setPriority(790);
		if (null == newChatInfoReceiver) {
			newChatInfoReceiver = new NewChatInfoReceiver();
			registerReceiver(newChatInfoReceiver, intentFilter);
		}
	}

	private void addGif() {
		int qqId = new Random().nextInt(4) + 1;
		try {
			Field field = R.drawable.class.getDeclaredField("qq" + qqId);
			int resourceId = Integer.parseInt(field.get(null).toString());
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resourceId);
			ImageSpan imageSpan = new ImageSpan(ChatAct.this, bitmap);
			SpannableString spanString = new SpannableString("qqq" + qqId);
			spanString.setSpan(imageSpan, 0, spanString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			inputEditView.append(spanString);
			Log.i("chen", "input=" + inputEditView.getText().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void init() {
		listVewChat = (ListView) findViewById(R.id.chat_lsv);
		inputEditView = (EditText) findViewById(R.id.chat_input_content);
		sendBtn = (ImageView) findViewById(R.id.chat_send);
		backBtn = (ImageView) findViewById(R.id.back);
		titleTV = (TextView) findViewById(R.id.chat_title);
		titleTV.setText(mName + "(导员)");
		listChat = parentTool.getChatDetail(mNo);
		adapter = new InfoChatAdapter(listChat, getApplicationContext());
		listVewChat.setAdapter(adapter);
		listVewChat.setSelection(listChat.size() - 1);
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		sendBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// addGif();
				final String str = inputEditView.getText().toString();
				if (null != str && !str.equals("")) {
					inputEditView.setText("");
					if (imm.isActive()) {
						// imm.toggleSoftInput(0,
						// InputMethodManager.HIDE_NOT_ALWAYS);
						imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
					}
					new Thread() {
						public void run() {
							Log.i("chen", "str=>>>>" + str);
							sendMsg(str);
						};
					}.start();
				}
			}
		});
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case NEW_CHAT_INFO:
					adapter.setData(listChat);
					adapter.notifyDataSetChanged();
					listVewChat.setSelection(listChat.size() - 1);
					break;
				}
			}
		};
	}

	private void sendMsg(String str) {
		ChatInfo chatInfo = new ChatInfo();
		chatInfo.setBothsend(0);
		chatInfo.setIsRead(1);
		chatInfo.setMsg(str);
		chatInfo.setReceiveName(mName);
		chatInfo.setReceiverNo(mNo);
		chatInfo.setReceiveType("m");
		chatInfo.setSenderNo(Attr.userName);
		chatInfo.setSendName(stuName);
		chatInfo.setSendType("p");
		chatInfo.setTime(ParentUtil.getTime());
		AmackManage.newChat(chatInfo);
		List<ChatInfo> list = new ArrayList<ChatInfo>();
		list.add(chatInfo);
		listChat.add(chatInfo);
		new ParentDtTool(ChatAct.this).insertNewChatInfo(list);
		mHandler.sendEmptyMessageDelayed(NEW_CHAT_INFO, TIME_MSG_SHOW_DELEY);
	}

	private class NewChatInfoReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i("chen", "ChatAct new info");
			ChatInfo chatInfo = (ChatInfo) intent.getSerializableExtra("msg");
			Message msg = new Message();
			msg.what = NEW_CHAT_INFO;
			// int size = listChat.size();
			// for (int i = 0; i < size; i++) {
			// if
			// (listChat.get(i).getReceiverNo().equals(chatInfo.getReceiverNo()))
			// {
			// listChat.remove(listChat.get(i));
			listChat.add(chatInfo);
			// }
			// }
			mHandler.sendMessage(msg);
			abortBroadcast();
		}
	}

	@Override
	protected void onDestroy() {
		if (null != newChatInfoReceiver) {
			unregisterReceiver(newChatInfoReceiver);
			newChatInfoReceiver = null;
		}
		super.onDestroy();
	}

}
