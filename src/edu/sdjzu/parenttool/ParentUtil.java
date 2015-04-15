package edu.sdjzu.parenttool;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Base64;
import edu.sdjzu.attr.Attr;
import edu.sdjzu.parent.LoginAct;
import edu.sdjzu.parent.ParentIndexAct;
import edu.sdjzu.parent.R;

public class ParentUtil {
	public final static int NEW_CHAT_INFO = 0;
	public final static int NEW_KQ_INFO = 1;
	public final static int TIME_VIBER_LONG=2000;
	public final static int TIME_VIBER_SHORT=500;

	public static void vibratorPhone(Context context, int times) {
		Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
		vib.vibrate(times);
	}

	private static int kqId = 1;
	private static int chatId = 2;

	/**
	 * 新的考勤信息通知
	 * 
	 * @param msgContent
	 */
	@SuppressWarnings("deprecation")
	public static void noticeNewInfo(Context context, Intent intent, String msgTip1,String msgTip2, int infoType) {
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification n;
		int id;
		if (NEW_CHAT_INFO == infoType) {
			id = chatId;
			n = new Notification(R.drawable.xinxin1, context.getString(R.string.kq_new_chat_tip),
					System.currentTimeMillis());
		} else if (NEW_KQ_INFO == infoType) {
			id = kqId;
			n = new Notification(R.drawable.xinxin1, context.getString(R.string.kq_new_kq_tip),
					System.currentTimeMillis());
		} else {
			return;
		}
		n.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
		n.flags = Notification.FLAG_AUTO_CANCEL;
		if (Attr.isLogin) {
			intent.setClass(context, ParentIndexAct.class);
		} else {
			intent.setClass(context, LoginAct.class);
		}
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		n.setLatestEventInfo(context, msgTip2, msgTip1, contentIntent);
		nm.notify(id, n);
		vibratorPhone(context, TIME_VIBER_LONG);
	}

	public static void notionCancel(Context context, int infoType) {
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		if (NEW_CHAT_INFO == infoType) {
			nm.cancel(chatId);
		} else if (NEW_KQ_INFO == infoType) {
			nm.cancel(kqId);
		}
	}
	
	public static String encodeBase64(String str) {
		return Base64.encodeToString(str.getBytes(), 0);
	}

	public static String decodeBase64(String str) {
		return new String(Base64.decode(str.getBytes(), 0));
	}
	public static String getTime() {
		Calendar ca = Calendar.getInstance();
		Date nowTime = ca.getTime();
		SimpleDateFormat datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String datetimeString = datetime.format(nowTime);
		return datetimeString;
	}

}
