package com.yc.jar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import android.app.Activity;
import android.util.Log;
import android.util.Xml;

import com.yc.im.model.IqModel;
import com.yc.im.model.MsgModel;
import com.yc.im.model.PresenceModel;

/**
 * @since 2014.12.24
 * @author chenshuwan
 * @see xml文件解析器
 *
 */
public class XMLPaserUtil {
	private XmlSerializer serializer = null;
	public final static int TYPE_IQ = 0;
	public final static int TYPE_PRESENCE = 1;
	public final static int TYPE_MESSAGE = 2;
	private int type = -1;
	private Activity act;
	private List<Object> listResult = new ArrayList<Object>();

	public XMLPaserUtil() {
	}

	public void initContent(Activity act) {
		this.act = act;
	}

	/**
	 * 返回解析所得的对象
	 * 
	 * @return
	 */
	public List<Object> getParseResult() {
		return listResult;
	}

	/**
	 * 返回解析对象的类型
	 * 
	 * @return
	 */
	public int getParseObjectCategory() {
		return type;
	}

	public void beginParsal(String xmlStr) {
		listResult = new ArrayList<Object>();
		type = -1;

		XmlPullParser parser = Xml.newPullParser();
		StringReader reader = new StringReader(xmlStr);
		String userId = "";
		String iqType = "";
		String iqQuery = "";
		MsgModel msg = new MsgModel();
		try {
			// InputStream in = act.getAssets().open("books.xml");
			parser.setInput(reader);
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					if (parser.getName().equalsIgnoreCase("presence")) {
						type = TYPE_PRESENCE;

					} else if (parser.getName().equalsIgnoreCase("message")) {
						userId = parser.getAttributeValue(null, "fromId");
						if (userId != null && !userId.equals("")) {
							type = TYPE_MESSAGE;
							msg = new MsgModel();
						}

					} else if (parser.getName().equalsIgnoreCase("iq")) {
						userId = parser.getAttributeValue(null, "id");
						if (userId != null && !userId.equals("")) {
							type = TYPE_IQ;
							iqQuery = parser.getAttributeValue(null, "query");
							iqType = parser.getAttributeValue(null, "type");
						}

					} else if (parser.getName().equalsIgnoreCase("item")) {
						if (type == TYPE_MESSAGE) {
							msg.setFromId(userId);
							msg.setToId(parser.getAttributeValue(null, "toId"));
							eventType = parser.next();
							if (parser.getText() != null) {
								msg.setMsg(parser.getText());
							}
							listResult.add(msg);
						} else if (type == TYPE_IQ) {
							IqModel iq = new IqModel();
							iq.setId(userId);
							if (parser.getAttributeValue(null, "rosterId") != null
									&& !parser.getAttributeValue(null, "rosterId").equals("")) {
								iq.setRosterId(parser.getAttributeValue(null, "rosterId"));
								iq.setState(parser.getAttributeValue(null, "state"));
								eventType = parser.next();
								if (parser.getText() != null) {
									iq.setRosterName(parser.getText());
								}
								listResult.add(iq);
							}

						}

					}
					break;
				case XmlPullParser.END_TAG:
					break;
				case XmlPullParser.END_DOCUMENT:
					break;
				}
				try {
					eventType = parser.next();
				} catch (XmlPullParserException e) {
					return;
				}
			}
		} catch (XmlPullParserException e) {
			Log.i("chen", "解析器异常" + e);
			e.printStackTrace();
		} catch (IOException e) {
			Log.i("chen", "解析器打开异常" + e);
			e.printStackTrace();
		}
		if (type == TYPE_IQ && listResult.size() <= 0) {
			type = -1;
		} else if (type == TYPE_PRESENCE && listResult.size() <= 0) {
			type = -1;
		}
	}

	/**
	 * 构建在线状态
	 * 
	 * @param pModel
	 * @return 返回xml状态文件
	 */
	public String createPresence(PresenceModel pModel) {
		String result = "";
		if (pModel == null) {
			return result;
		}
		serializer = Xml.newSerializer();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			serializer.setOutput(baos, "UTF-8");
			serializer.startDocument("UTF-8", true);
			serializer.startTag(null, "stream");
			serializer.startTag(null, "presence");
			serializer.attribute(null, "id", pModel.getId());
			serializer.attribute(null, "state", pModel.getState());
			serializer.endTag(null, "presence");
			serializer.endTag(null, "stream");
			serializer.endDocument();
			baos.flush();
			byte[] xmlData = baos.toByteArray();
			result = new String(xmlData, "UTF-8");
		} catch (IllegalArgumentException e) {
			result = "";
			e.printStackTrace();
		} catch (IllegalStateException e) {
			result = "";
			e.printStackTrace();
		} catch (IOException e) {
			result = "";
			e.printStackTrace();
		} finally {
			try {
				baos.close();
				baos = null;
			} catch (IOException e) {
				baos = null;
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 构造要聊天的内容格式xml
	 * 
	 * @param listMsg
	 *            发送的信息对象
	 * @param listMsgBody
	 *            Body发送的信息内容
	 * @return xml发送内容
	 */
	public String createMessage(List<MsgModel> listMsg) {
		String result = "";
		if (listMsg.size() == 0) {
			return result;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		serializer = Xml.newSerializer();
		try {
			serializer.setOutput(baos, "UTF-8");
			serializer.startDocument("UTF-8", true);
			serializer.startTag(null, "stream");
			for (MsgModel msg : listMsg) {
				serializer.startTag(null, "message");
				serializer.attribute(null, "fromId", msg.getFromId());
				serializer.startTag(null, "item");
				serializer.attribute(null, "toId", msg.getToId());
				serializer.text(msg.getMsg());
				serializer.endTag(null, "item");
				serializer.endTag(null, "message");
			}
			serializer.endTag(null, "stream");
			serializer.endDocument();
			baos.flush();
			byte[] xmlData = baos.toByteArray();
			result = new String(xmlData, "UTF-8");

		} catch (IllegalArgumentException e) {
			result = "";
			e.printStackTrace();
		} catch (IllegalStateException e) {
			result = "";
			e.printStackTrace();
		} catch (IOException e) {
			result = "";
			e.printStackTrace();
		} finally {
			try {
				baos.close();
				baos = null;
			} catch (IOException e) {
				baos = null;
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 向服务器进行请求花名册
	 * 
	 * @return 要请求的xml数据格式
	 */
	public String createRequestIq(IqModel iqM) {
		String result = "";
		if (iqM == null) {
			return result;
		}
		serializer = Xml.newSerializer();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			serializer.setOutput(baos, "UTF-8");
			serializer.startDocument("UTF-8", true);
			serializer.startTag(null, "stream");
			serializer.startTag(null, "iq");
			serializer.attribute(null, "id", iqM.getId());
			serializer.attribute(null, "type", iqM.getType());
			serializer.attribute(null, "query", iqM.getQuery());
			serializer.endTag(null, "iq");
			serializer.endTag(null, "stream");
			serializer.endDocument();
			baos.flush();
			byte[] xmlData = baos.toByteArray();
			result = new String(xmlData, "UTF-8");
		} catch (IllegalArgumentException e) {
			result = "";
			e.printStackTrace();
		} catch (IllegalStateException e) {
			result = "";
			e.printStackTrace();
		} catch (IOException e) {
			result = "";
			e.printStackTrace();
		} finally {
			try {
				baos.close();
				baos = null;
			} catch (IOException e) {
				baos = null;
				e.printStackTrace();
			}
		}
		return result;
	}

}
