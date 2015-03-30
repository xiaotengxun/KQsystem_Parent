package com.yc.im.util;

import java.util.List;

import android.util.Log;

import com.yc.im.model.IqModel;
import com.yc.im.model.MsgModel;
import com.yc.im.model.PresenceModel;
import com.yc.jar.ChatConnectTool;
import com.yc.jar.XMLPaserUtil;

public class ChatUtil {

	public ChatUtil() {
	}

	public void sendIq(IqModel iq) {
		String xml = new XMLPaserUtil().createRequestIq(iq);
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ChatConnectTool.sendXmlToServer(xml);
	}

	public void sendPresence(PresenceModel pre) {
		String xml = new XMLPaserUtil().createPresence(pre);
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Log.i("chen", "pre="+xml);
		ChatConnectTool.sendXmlToServer(xml);
	}

	public void sendMessage(List<MsgModel> listMsg) {
		String xml = new XMLPaserUtil().createMessage(listMsg);
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ChatConnectTool.sendXmlToServer(xml);
	}
}
