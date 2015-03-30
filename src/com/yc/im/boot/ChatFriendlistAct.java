package com.yc.im.boot;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.androidclient.R;
import com.yc.im.model.IqModel;
import com.yc.im.util.ChatUtil;
import com.yc.jar.ChatConnectTool;
import com.yc.jar.ChatNewInfoOnListener;
import com.yc.jar.ThreadPoolUtil;
import com.yc.jar.XMLPaserUtil;

public class ChatFriendlistAct extends Activity {
	private ListView rosterListView;
	private List<IqModel> listRoster = new ArrayList<IqModel>();
	private RosterAdapter adapter;
	private Handler mHandler;
	private static final int ROSTER_GET_SUCCESS = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.roster_show);
		initView();
		getRoster();
	}

	private void initView() {

		rosterListView = (ListView) findViewById(R.id.roster_list);
		adapter = new RosterAdapter(getApplicationContext(), listRoster);
		rosterListView.setAdapter(adapter);
		ChatConnectTool.setOnChateNewInfoListener(new ChatNewInfoOnListener() {

			@Override
			public void getNewMessage(String xmlStr) {
				// Log.i("chen", "xmlStr=" + xmlStr);
				XMLPaserUtil parser = new XMLPaserUtil();
				parser.beginParsal(xmlStr);
				if (parser.getParseObjectCategory() == parser.TYPE_IQ) {
					List<Object> list = parser.getParseResult();
					listRoster = new ArrayList<IqModel>();
					for (Object object : list) {
						IqModel iq = (IqModel) object;
						listRoster.add(iq);
					}
					mHandler.sendEmptyMessage(ROSTER_GET_SUCCESS);

				}

			}
		});
		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case ROSTER_GET_SUCCESS:
					adapter.setListData(listRoster);
					adapter.notifyDataSetChanged();
					break;
				}
			}

		};

		rosterListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String ids = (String) view.getTag();
				Intent intent = new Intent();
				intent.putExtra("otherId", ids);
				intent.setClass(ChatFriendlistAct.this, ChatAct.class);
				startActivity(intent);

			}
		});
	}

	private void getRoster() {
		Runnable runn = new Runnable() {

			@Override
			public void run() {
				IqModel iq = new IqModel();
				iq.setId(getSharedPreferences("chat", 0).getString("id", ""));
				if (!iq.getId().equals("")) {
					new ChatUtil().sendIq(iq);
				}

			}
		};
		ThreadPoolUtil.getThreadPool().execute(runn);
	}

	class RosterAdapter extends BaseAdapter {
		private Context context;
		private LayoutInflater mInflater;
		private List<IqModel> listR = new ArrayList<IqModel>();

		public RosterAdapter(Context context, List<IqModel> listR) {
			this.context = context;
			this.listR = listR;
			mInflater = LayoutInflater.from(context);
		}

		public void setListData(List<IqModel> listR) {
			this.listR = listR;
		}

		@Override
		public int getCount() {
			return listR.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = mInflater.inflate(R.layout.roster_show_item, null);
			TextView tv = (TextView) convertView.findViewById(R.id.roster_user);
			tv.setText(listR.get(position).getRosterId());
			convertView.setTag(listR.get(position).getRosterId());
			return convertView;
		}

	}

	@Override
	protected void onPause() {
		finish();
		super.onPause();
	};

}
