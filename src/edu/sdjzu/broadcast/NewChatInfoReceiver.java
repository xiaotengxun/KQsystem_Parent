package edu.sdjzu.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import edu.sdjzu.parent.R;
import edu.sdjzu.parenttool.ParentUtil;

public class NewChatInfoReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		intent.putExtra("index", 2);
		ParentUtil.noticeNewInfo(context, intent, context.getString(R.string.kq_new_chat_tip),
				context.getString(R.string.kq_new_chat_tip), ParentUtil.NEW_CHAT_INFO);
	}

}
