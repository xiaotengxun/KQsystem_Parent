package edu.sdjzu.broadcast;

import edu.sdjzu.parent.R;
import edu.sdjzu.parenttool.ParentUtil;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NewKqInfoReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		String msg = intent.getStringExtra("info");
		if (null == msg) {
			msg = "";
		}
		intent.putExtra("index", 1);
		intent.putExtra("index", 2);
		ParentUtil.noticeNewInfo(context, intent, msg,
				context.getString(R.string.kq_new_chat_tip), ParentUtil.NEW_CHAT_INFO);
	}

}
