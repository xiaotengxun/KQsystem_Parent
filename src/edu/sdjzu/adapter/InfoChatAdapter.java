package edu.sdjzu.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import edu.sdjzu.model.ChatInfo;
import edu.sdjzu.parent.R;

public class InfoChatAdapter extends BaseAdapter {
	private List<ChatInfo> listChat = new ArrayList<ChatInfo>();
	private Context context;
	private LayoutInflater mInflater;

	public InfoChatAdapter(List<ChatInfo> listChat, Context context) {
		super();
		this.listChat = listChat;
		this.context = context;
		mInflater = LayoutInflater.from(context);
	}

	public void setData(List<ChatInfo> listChat){
		this.listChat=listChat;
	}
	@Override
	public int getCount() {
		return listChat.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder vH;
		if (null == convertView) {
			convertView = mInflater.inflate(R.layout.chat_item, null);
		}
		vH = (ViewHolder) convertView.getTag();
		if (null == vH) {
			vH = new ViewHolder();
			vH.imLeft = (ImageView) convertView.findViewById(R.id.chat_left_bg);
			vH.imRight = (ImageView) convertView.findViewById(R.id.chat_right_bg);
			vH.tvLeft = (TextView) convertView.findViewById(R.id.chat_left_tv);
			vH.tvRight = (TextView) convertView.findViewById(R.id.chat_right_tv);
			vH.tvTimeRight=(TextView)convertView.findViewById(R.id.chat_right_time);
			vH.tvTimeLeft=(TextView)convertView.findViewById(R.id.chat_left_time);
		}
		ChatInfo chatInfo = listChat.get(position);
		int p = chatInfo.getBothsend();
		if (1 == p) {//��ʾ���
			vH.imLeft.setVisibility(View.VISIBLE);
			vH.imRight.setVisibility(View.INVISIBLE);
			vH.tvLeft.setVisibility(View.VISIBLE);
			vH.tvRight.setVisibility(View.INVISIBLE);
			vH.tvLeft.setText(chatInfo.getMsg());
			vH.tvTimeLeft.setText(chatInfo.getTime());
			vH.tvTimeLeft.setVisibility(View.VISIBLE);
			vH.tvTimeRight.setVisibility(View.INVISIBLE);
		} else {
			vH.imLeft.setVisibility(View.INVISIBLE);
			vH.imRight.setVisibility(View.VISIBLE);
			vH.tvLeft.setVisibility(View.INVISIBLE);
			vH.tvRight.setVisibility(View.VISIBLE);
			vH.tvRight.setText(chatInfo.getMsg());
			vH.tvTimeRight.setText(chatInfo.getTime());
			vH.tvTimeLeft.setVisibility(View.INVISIBLE);
			vH.tvTimeRight.setVisibility(View.VISIBLE);
		}
		return convertView;
	}

	private class ViewHolder {
		TextView tvLeft, tvRight;
		ImageView imLeft, imRight;
		TextView tvTimeLeft,tvTimeRight;
	};
}
