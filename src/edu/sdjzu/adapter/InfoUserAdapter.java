package edu.sdjzu.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import edu.sdjzu.model.Students;
import edu.sdjzu.parent.R;

public class InfoUserAdapter extends BaseAdapter {
	private List<Students> listStu = new ArrayList<Students>();
	private Context context;
	private LayoutInflater mInflater;

	public InfoUserAdapter(List<Students> list, Context context) {
		super();
		this.listStu = list;
		this.context = context;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return listStu.size();
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
		ViewHolder vh = null;
		if (null == convertView) {
			convertView = mInflater.inflate(R.layout.info_user_item, null);
		}
		vh = (ViewHolder) convertView.getTag();
		if (null == vh) {
			vh = new ViewHolder();
			vh.nameTV = (TextView) convertView.findViewById(R.id.info_user_name);
			vh.sclassTV = (TextView) convertView.findViewById(R.id.info_user_class);
			vh.snoTV = (TextView) convertView.findViewById(R.id.info_user_sno);
			convertView.setTag(vh);
		}
		Students stu = listStu.get(position);
		vh.nameTV.setText(stu.getStuName());
		vh.sclassTV.setText(stu.getStuClass());
		vh.snoTV.setText(stu.getStuNo());
		return convertView;
	}

	private class ViewHolder {
		TextView nameTV, snoTV, sclassTV;
	}

}
