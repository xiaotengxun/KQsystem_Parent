package edu.sdjzu.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import edu.sdjzu.adapter.InfoAdapter;
import edu.sdjzu.adapter.InfoUserAdapter;
import edu.sdjzu.attr.Attr;
import edu.sdjzu.model.ChatInfo;
import edu.sdjzu.parent.ChatAct;
import edu.sdjzu.parent.R;
import edu.sdjzu.parenttool.ParentDtTool;
import edu.sdjzu.parenttool.ParentUtil;

public class InfoFrag extends Fragment {
	private NewChatInfoReceiver newChatInfoReceiver = null;
	private Activity act = null;
	private ListView listViewInfo, listViewAddUser;
	private View menu, addUserBgBtn, userView;
	private TextView deleteTV, cancelTV;
	private Handler mHandler;
	private final int NEW_CHAT_INFO = 0;
	private String tag = "chen";
	private List<ChatInfo> listChatInfo = new ArrayList<ChatInfo>();
	private InfoAdapter infoAdapter;
	private InfoUserAdapter infoUserAdapter;
	private ParentDtTool parentTool;
	private boolean isLongClick = false;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		act = getActivity();
		animIn = AnimationUtils.loadAnimation(act, R.anim.slide_right_in_chatinfo);
		animOut = AnimationUtils.loadAnimation(act, R.anim.slide_right_out_chatinfo);
		animOut.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				userView.setVisibility(View.INVISIBLE);
			}
		});
		parentTool = new ParentDtTool(act);
		init();
		registerReceiver();
		super.onActivityCreated(savedInstanceState);
	}

	private Animation animIn, animOut;

	private void init() {
		menu = getView().findViewById(R.id.rel_menu);
		addUserBgBtn = getView().findViewById(R.id.info_add);
		userView = getView().findViewById(R.id.info_add_user_bg);
		deleteTV = (TextView) getView().findViewById(R.id.info_menu_delete);
		cancelTV = (TextView) getView().findViewById(R.id.info_menu_cancel);
		listViewAddUser = (ListView) getView().findViewById(R.id.info_add_user_lsv);
		listViewInfo = (ListView) getView().findViewById(R.id.info_lsv);
		listChatInfo = parentTool.getChatGroup(Attr.userName);
		infoAdapter = new InfoAdapter(listChatInfo, act);
		listViewInfo.setAdapter(infoAdapter);

		// infoUserAdapter = new InfoUserAdapter(parentTool.getAllStu(), act);
		listViewAddUser.setAdapter(infoUserAdapter);
		listViewInfo.setFocusable(false);
		listViewInfo.setFocusableInTouchMode(false);
		if (listChatInfo.size() > 0) {
			listViewInfo.setVisibility(View.VISIBLE);
		}
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case NEW_CHAT_INFO:
					infoAdapter.setData(listChatInfo);
					infoAdapter.notifyDataSetChanged();
					break;
				}
			}
		};

		addUserBgBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				userView.setVisibility(View.VISIBLE);
				userView.startAnimation(animIn);
			}
		});
		userView.setFocusable(true);
		userView.setFocusableInTouchMode(true);
		userView.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == event.KEYCODE_BACK && KeyEvent.ACTION_UP == event.getAction())
					if (userView.getVisibility() == View.VISIBLE) {
						userView.startAnimation(animOut);
						return true;
					}
				return false;
			}
		});
		listViewAddUser.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String no = ((TextView) view.findViewById(R.id.info_user_sno)).getText().toString();
				String name = ((TextView) view.findViewById(R.id.info_user_name)).getText().toString();
				if (null != no && name != null && !no.equals("") && !name.equals("")) {
					chatUserSno = no;
					userView.setVisibility(View.INVISIBLE);
					Intent intent = new Intent(act, ChatAct.class);
					intent.putExtra("no", no);
					intent.putExtra("name", name);
					startActivity(intent);
				}

			}
		});

		listViewInfo.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (!isLongClick) {
					String no = (String) view.getTag(R.id.second_tag);
					String name = (String) view.getTag(R.id.third_tag);

					if (null != no && name != null && !no.equals("") && !name.equals("")) {
						userView.setVisibility(View.INVISIBLE);
						chatUserSno = no;
						Log.i("chen", "chat   no=" + no + "    name=" + name);
						Intent intent = new Intent(act, ChatAct.class);
						intent.putExtra("no", no);
						intent.putExtra("name", name);
						startActivity(intent);
					}
				}

			}
		});
		listViewInfo.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				if (!infoAdapter.isShowDeleteBtn()) {
					isLongClick = true;
					ParentUtil.vibratorPhone(getActivity(), ParentUtil.TIME_VIBER_SHORT);
					infoAdapter.setShowDeleteBtn(true);
					infoAdapter.notifyDataSetChanged();
					menu.setVisibility(View.VISIBLE);
				}
				return false;
			}
		});
		deleteTV.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				infoAdapter.deleteSelected();
				menu.setVisibility(View.INVISIBLE);
				listChatInfo = parentTool.getChatGroup(Attr.userName);
				infoAdapter.setData(listChatInfo);
				infoAdapter.setShowDeleteBtn(false);
				infoAdapter.notifyDataSetChanged();
				isLongClick = false;
			}
		});
		cancelTV.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (infoAdapter.isShowDeleteBtn()) {
					infoAdapter.setShowDeleteBtn(false);
					infoAdapter.notifyDataSetChanged();
					menu.setVisibility(View.INVISIBLE);
					isLongClick = false;
				}

			}
		});

	}

	private String chatUserSno = "";// 正在交谈的家长的编号

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.info, null);
	}

	private void registerReceiver() {
		IntentFilter intentFilter = new IntentFilter(getString(R.string.ACTION_NEW_CHATINFO_ACTION));
		intentFilter.setPriority(800);
		if (null == newChatInfoReceiver) {
			newChatInfoReceiver = new NewChatInfoReceiver();
			act.registerReceiver(newChatInfoReceiver, intentFilter);
		}
	}

	private class NewChatInfoReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i("chen", "NewChatInfoReceiver");
			ParentUtil.vibratorPhone(getActivity(), ParentUtil.TIME_VIBER_SHORT);
			ChatInfo chatInfo = (ChatInfo) intent.getSerializableExtra("msg");
			if (!chatInfo.getReceiverNo().equals(chatUserSno)) {
				Message msg = new Message();
				msg.what = NEW_CHAT_INFO;
				msg.obj = chatInfo;
				int size = listChatInfo.size();
				for (int i = 0; i < size; i++) {
					if (listChatInfo.get(i).getReceiverNo().equals(chatInfo.getReceiverNo())) {
						listChatInfo.remove(listChatInfo.get(i));
						List<ChatInfo> list = new ArrayList<ChatInfo>(listChatInfo);
						listChatInfo.add(chatInfo);
						listChatInfo.addAll(list);
					}
					mHandler.sendMessage(msg);
					abortBroadcast();
				}
			}

		}
	}

	@Override
	public void onDestroy() {
		if (null != newChatInfoReceiver) {
			act.unregisterReceiver(newChatInfoReceiver);
			newChatInfoReceiver = null;
		}
		super.onDestroy();
	}

	@Override
	public void onResume() {
	
		chatUserSno = "";
		ParentUtil.notionCancel(getActivity(), ParentUtil.NEW_CHAT_INFO);
		listChatInfo = parentTool.getChatGroup(Attr.userName);
		mHandler.sendEmptyMessage(NEW_CHAT_INFO);
		super.onResume();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

}
