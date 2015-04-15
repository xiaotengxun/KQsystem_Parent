package edu.sdjzu.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import edu.sdjzu.attr.Attr;
import edu.sdjzu.model.ChatInfo;
import edu.sdjzu.parent.R;
import edu.sdjzu.parenttool.ParentDtTool;

public class InfoAdapter extends BaseAdapter {
	private boolean isDeleteBtnShow = false;
	private List<ChatInfo> listChatInfo = new ArrayList<ChatInfo>();
	private Context context;
	private LayoutInflater mInflater;
	private List<String> listDelete = new ArrayList<String>();

	public InfoAdapter(List<ChatInfo> listChatInfo, Context context) {
		super();
		this.listChatInfo = listChatInfo;
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
	}

	public void setData(List<ChatInfo> list) {
		this.listChatInfo = list;
	}

	@Override
	public int getCount() {
		return listChatInfo.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public void setShowDeleteBtn(boolean b) {
		isDeleteBtnShow = b;
		if (isDeleteBtnShow == false) {
			listDelete.clear();
		}
	}

	public boolean isShowDeleteBtn() {
		return isDeleteBtnShow;
	}

	private int deleteSelectedId = R.drawable.kq_info_delete_yes;
	private int deleteUnSelectedId = R.drawable.kq_info_delete_no;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		if (null == convertView) {
			convertView = mInflater.inflate(R.layout.info_listview_item, null);
		}
		vh = (ViewHolder) convertView.getTag(R.id.first_tag);
		if (null == vh) {
			vh = new ViewHolder();
			vh.tvDate = (TextView) convertView.findViewById(R.id.info_time);
			vh.tvMsg = (TextView) convertView.findViewById(R.id.info_msg);
			vh.tvTitle = (TextView) convertView.findViewById(R.id.info_ttitle);
			vh.imDelete = (ImageView) convertView.findViewById(R.id.info_delete);
			vh.imDelete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int id = (Integer) v.getTag(R.id.first_tag);
					String sno = (String) v.getTag(R.id.second_tag);
					Log.i("chen", "sno=" + sno);
					if (deleteSelectedId == id) {
						((ImageView) v).setImageResource(deleteUnSelectedId);
						v.setTag(R.id.first_tag, deleteUnSelectedId);
						listDelete.remove(sno);
					} else {
						((ImageView) v).setImageResource(deleteSelectedId);
						v.setTag(R.id.first_tag, deleteSelectedId);
						listDelete.add(sno);
					}

				}
			});
			convertView.setTag(R.id.first_tag, vh);
		}
		ChatInfo chatInfo = listChatInfo.get(position);
		vh.tvDate.setText(chatInfo.getTime());
		vh.tvMsg.setText(chatInfo.getMsg());
		vh.tvTitle.setText(chatInfo.getReceiveName());
		if (0 == chatInfo.getIsRead()) {
			vh.tvTitle.setTextColor(Color.RED);
		} else {
			vh.tvTitle.setTextColor(Color.BLACK);
		}
		vh.imDelete.setTag(R.id.first_tag, deleteSelectedId);
		if (isDeleteBtnShow) {
			vh.imDelete.setVisibility(View.VISIBLE);
			vh.imDelete.setImageResource(deleteUnSelectedId);
			vh.imDelete.setTag(R.id.first_tag, deleteUnSelectedId);
		} else {
			vh.imDelete.setVisibility(View.INVISIBLE);
		}
		vh.imDelete.setTag(R.id.second_tag, chatInfo.getReceiverNo());

		convertView.setTag(R.id.second_tag, chatInfo.getReceiverNo());// ////////////////////////////
		convertView.setTag(R.id.third_tag, chatInfo.getReceiveName());
		return convertView;
	}

	public void deleteSelected() {
		if (listDelete.size() > 0) {
			ParentDtTool parentTool = new ParentDtTool(context);
			parentTool.deleteChatGroup(listDelete, Attr.userName);
		}
	}

	private class ViewHolder {
		TextView tvMsg, tvTitle, tvDate;
		ImageView imDelete;
	};

}
