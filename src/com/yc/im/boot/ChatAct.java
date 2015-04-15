package com.yc.im.boot;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.yc.im.model.MsgModel;
import com.yc.im.util.ChatUtil;
import com.yc.jar.ChatConnectTool;
import com.yc.jar.ChatNewInfoOnListener;
import com.yc.jar.ThreadPoolUtil;
import com.yc.jar.XMLPaserUtil;

import edu.sdjzu.parent.R;

public class ChatAct extends Activity {
	private ListView chatListView;
	private List<Integer> listMark = new ArrayList<Integer>();
	private List<String> ListChat = new ArrayList<String>();
	private ChatAdapter adapter;
	private Handler mHandler;
	private static final int MESSAGE_COME = 0;
	private Button btnSend;
	private EditText inputView;
	private String chatStr = "";
	private Runnable chatTask;
	private String otherId = "";
	private MsgModel msg = new MsgModel();
	private List<MsgModel> listMsg = new ArrayList<MsgModel>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.roster_chat);
		otherId = getIntent().getStringExtra("otherId");
		msg.setToId(otherId);
		msg.setFromId(getSharedPreferences("chat", 0).getString("id", ""));
		initView();
	}

	private void initView() {
		chatTask = new Runnable() {

			@Override
			public void run() {
				msg.setMsg(chatStr);
				listMsg = new ArrayList<MsgModel>();
				listMsg.add(msg);
				new ChatUtil().sendMessage(listMsg);

			}
		};
		btnSend = (Button) findViewById(R.id.btn_send);
		inputView = (EditText) findViewById(R.id.chat_input);
		chatListView = (ListView) findViewById(R.id.roster_chat);
		adapter = new ChatAdapter(listMark, ListChat, getApplicationContext());
		chatListView.setAdapter(adapter);
		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case MESSAGE_COME:
					adapter.setData(listMark, ListChat);
					adapter.notifyDataSetChanged();
					chatListView.setSelection(listMark.size() - 1);
					break;
				}
			}

		};

		ChatConnectTool.setOnChateNewInfoListener(new ChatNewInfoOnListener() {

			@Override
			public void getNewMessage(String xmlStr) {
				XMLPaserUtil par = new XMLPaserUtil();
				par.beginParsal(xmlStr);
				List<Object> list = par.getParseResult();
				if (par.getParseObjectCategory() == par.TYPE_MESSAGE) {
					Log.i("chen", "getMessage");
					for (Object object : list) {
						MsgModel ms = (MsgModel) object;
						listMark.add(0);
						ListChat.add(ms.getMsg());
						mHandler.sendEmptyMessage(MESSAGE_COME);
					}
				}
			}
		});

		btnSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				chatStr = inputView.getText().toString().trim();
				if (!chatStr.equals("")) {
					ThreadPoolUtil.getThreadPool().execute(chatTask);
				}

			}
		});

	}

	class ChatAdapter extends BaseAdapter {
		private List<Integer> listM = new ArrayList<Integer>();
		private List<String> ListC = new ArrayList<String>();
		private Context context;
		private LayoutInflater mInflater;

		public ChatAdapter(List<Integer> listM, List<String> listC, Context context) {
			super();
			this.listM = listM;
			ListC = listC;
			this.context = context;
			this.mInflater = LayoutInflater.from(context);

		}

		public void setData(List<Integer> listM, List<String> listC) {
			this.listM = listM;
			ListC = listC;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listM.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = mInflater.inflate(R.layout.roster_chat_item, null);
			TextView left = (TextView) convertView.findViewById(R.id.other_chat);
			TextView right = (TextView) convertView.findViewById(R.id.user_chat);
			if (listM.get(position) == 0) {
				left.setVisibility(View.VISIBLE);
				left.setText(ListC.get(position));
			} else if (listM.get(position) == 1) {
				right.setVisibility(View.VISIBLE);
				right.setText(ListC.get(position));
			}
			return convertView;
		}

	};
}
