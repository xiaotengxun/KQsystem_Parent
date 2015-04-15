package edu.sdjzu.xmpp;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.util.StringUtils;

import android.content.Context;
import android.content.Intent;
import android.util.JsonReader;
import android.util.Log;
import edu.sdjzu.model.ChatInfo;
import edu.sdjzu.parent.R;
import edu.sdjzu.parenttool.ParentDtTool;
import edu.sdjzu.parenttool.ParentUtil;

@SuppressWarnings("deprecation")
public class AmackManage {
	private static String tag = "chen";
	private static String serverAddress = "202.194.81.12";
	private static int serverPort = 5222;
	private static XMPPConnection connection = null;
	private final static int connectServerTimes = 5;// 连接服务器的尝试次数
	public final static int USER_REGISTER_EXSITED = 0;
	public final static int USER_REGISTER_ERROR = 1;
	public final static int USER_REGISTER_SUCCESS = 2;
	public final static int USER_STATE_ONLINE = 3;// 用户在线
	public final static int USER_STATE_HIDE = 4;// 用户影身
	public final static int USER_STATE_BUSY = 5;// 用户忙碌
	public final static int USER_STATE_LEAVE = 6;// 用户离开
	public final static int USER_STATE_Q = 7;// 用户离开
	public final static int USER_STATE_OFFLINE = 8;// 用户离线
	private final static int REGISTER_MAX_TIMES = 30;

	public AmackManage() {
		// try {
		// Class.forName("org.jivesoftware.smack.ReconnectionManager");
		// Log.i(tag, "加载org.jivesoftware.smack.ReconnectionManager  成功！");
		// } catch (ClassNotFoundException e) {
		// Log.i(tag, "加载org.jivesoftware.smack.ReconnectionManager   失败");
		// e.printStackTrace();
		// }
	}

	// 连接服务器
	public static boolean connectServer() {
		ConnectionConfiguration config = new ConnectionConfiguration(serverAddress, serverPort);
		config.setReconnectionAllowed(true);
		config.setSecurityMode(ConnectionConfiguration.SecurityMode.enabled);
		config.setSASLAuthenticationEnabled(true);
		config.setTruststorePath("/system/etc/security/cacerts.bks");
		config.setTruststorePassword("changeit");
		config.setTruststoreType("bks");
		config.setSASLAuthenticationEnabled(false);
		connection = new XMPPConnection(config);
		Log.i(tag, "开始连接服务器");
		try {
			connection.connect();
			Log.i(tag, "连接服务器成功");
			return true;
		} catch (XMPPException e) {
			for (int i = 0; i < connectServerTimes; ++i) {
				try {
					Log.i(tag, "尝试连接" + i + 1 + "次");
					connection.connect();
					Log.i(tag, "连接服务器成功");
					return true;
				} catch (XMPPException e1) {
					e1.printStackTrace();
				}
			}
			Log.i(tag, "连接服务器失败");
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * 网络不稳定时重新连接服务器
	 */
	public static void addReconnectionListener() {
		connection.addConnectionListener(new ConnectionListener() {

			@Override
			public void reconnectionSuccessful() {
				Log.i(tag, "reconnectionSuccessful");

			}

			@Override
			public void reconnectionFailed(Exception arg0) {
				Log.i(tag, "reconnectionFailed>>" + arg0.toString());

			}

			@Override
			public void reconnectingIn(int arg0) {
				Log.i(tag, "reconnectingIn>>>" + arg0);

			}

			@Override
			public void connectionClosedOnError(Exception arg0) {
				Log.i(tag, "connectionClosedOnError>>>" + arg0);
			}

			@Override
			public void connectionClosed() {
				Log.i(tag, "connectionClosed");

			}
		});

	}

	public static boolean checkConnection(String userName, boolean isRegistered) {
		Log.i("chen", "checkConnection");
		if (null == connection) {
			login(userName, userName, isRegistered);
		}
		return connection.isConnected();
	}

	/**
	 * 
	 * @param account
	 * @param password
	 * @return 0:为连接服务器，1：注册成功，其余：失败
	 */
	private static int register(String account, String password) {
		if (connection == null) {
			Log.i(tag, "reguster connection = null");
			return 0;
		}
		Registration reg = new Registration();
		reg.setType(IQ.Type.SET);
		reg.setTo(connection.getServiceName());
		reg.setUsername(account);//
		// 注意这里createAccount注册时，参数是username，不是jid，是“@”前面的部分。
		reg.setPassword(password);
		reg.addAttribute("android", "geolo_createUser_android");//
		// 这边addAttribute不能为空，否则出错。所以做个标志是android手机创建的吧！！！！！
		PacketFilter filter = new AndFilter(new PacketIDFilter(reg.getPacketID()), new PacketTypeFilter(IQ.class));
		PacketCollector collector = connection.createPacketCollector(filter);
		connection.sendPacket(reg);
		IQ result = (IQ) collector.nextResult(SmackConfiguration.getPacketReplyTimeout());
		// Stop queuing results
		collector.cancel();// 停止请求results（是否成功的结果）
		if (result == null) {
			Log.i(tag, "No response from server.");
			return USER_REGISTER_ERROR;
		} else if (result.getType() == IQ.Type.RESULT) {
			Log.i("chen", "注册成功");
			return USER_REGISTER_SUCCESS;
		} else { // if (result.getType() == IQ.Type.ERROR)
			if (result.getError().toString().equalsIgnoreCase("conflict(409)")) {
				Log.i(tag, "ssssIQ.Type.ERROR: " + result.getError().toString());// 用户已经存在
				return USER_REGISTER_EXSITED;
			} else {
				Log.i(tag, "IQ.Type.ERROR: " + result.getError().toString());
				return USER_REGISTER_ERROR;
			}
		}

	}

	private final static int loginTimes = 5;// 尝试登陆的最大次数

	/**
	 * 登录
	 * 
	 * @param a
	 *            登录帐号
	 * @param p
	 *            登录密码
	 * @return
	 */
	private static boolean loginUser(String a, String p) {
		if (connection == null) {
			return false;
		}
		try {
			/** 登录 */
			Log.i(tag, "开始登陆");
			connection.login(a, p);
			Log.i("chen", "登陆成功");
			return true;
		} catch (Exception e) {
			for (int i = 0; i < loginTimes; i++) {
				try {
					connection.login(a, p);
					Log.i("chen", "登陆成功");
					return true;
				} catch (XMPPException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			e.printStackTrace();
		}
		Log.i("chen", "登陆失败");
		return false;
	}

	/**
	 * 用户登陆
	 * 
	 * @param userName
	 * @param pwd
	 * @param sp
	 * @return
	 */
	public static boolean login(String userName, String pwd, boolean isRegistered) {
		Log.i(tag, "是否已经注册:" + isRegistered);
		if (connectServer()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			int result = -1;
			for (int i = 0; i < REGISTER_MAX_TIMES && !isRegistered; ++i) {
				if ((result = AmackManage.register(userName, pwd)) == AmackManage.USER_REGISTER_ERROR) {
				} else {
					isRegistered = true;
					break;
				}
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (isRegistered) {
				if (loginUser(userName, pwd)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 修改密码
	 * 
	 * @param connection
	 * @return
	 */
	public static boolean changePassword(XMPPConnection connection, String pwd) {
		if (connection == null) {
			return false;
		}
		try {
			connection.getAccountManager().changePassword(pwd);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 更改用户状态
	 */
	@SuppressWarnings("deprecation")
	public void setPresence(int code) {
		if (connection == null)
			return;
		Presence presence;
		switch (code) {
		case USER_STATE_ONLINE:
			presence = new Presence(Presence.Type.available);
			connection.sendPacket(presence);
			Log.v("state", "设置在线");
			break;
		case USER_STATE_Q:
			presence = new Presence(Presence.Type.available);
			presence.setMode(Presence.Mode.chat);
			connection.sendPacket(presence);
			Log.v("state", "设置Q我吧");
			System.out.println(presence.toXML());
			break;
		case USER_STATE_BUSY:
			presence = new Presence(Presence.Type.available);
			presence.setMode(Presence.Mode.dnd);
			connection.sendPacket(presence);
			Log.v("state", "设置忙碌");
			System.out.println(presence.toXML());
			break;
		case USER_STATE_LEAVE:
			presence = new Presence(Presence.Type.available);
			presence.setMode(Presence.Mode.away);
			connection.sendPacket(presence);
			Log.v("state", "设置离开");
			System.out.println(presence.toXML());
			break;
		case USER_STATE_HIDE:// 隐身
			Roster roster = connection.getRoster();
			Collection<RosterEntry> entries = roster.getEntries();
			for (RosterEntry entry : entries) {
				presence = new Presence(Presence.Type.unavailable);
				presence.setPacketID(Packet.ID_NOT_AVAILABLE);
				presence.setFrom(connection.getUser());
				presence.setTo(entry.getUser());
				connection.sendPacket(presence);
				System.out.println(presence.toXML());
			}
			// 向同一用户的其他客户端发送隐身状态
			presence = new Presence(Presence.Type.unavailable);
			presence.setPacketID(Packet.ID_NOT_AVAILABLE);
			presence.setFrom(connection.getUser());
			presence.setTo(StringUtils.parseBareAddress(connection.getUser()));
			connection.sendPacket(presence);
			break;
		case USER_STATE_OFFLINE:
			presence = new Presence(Presence.Type.unavailable);
			connection.sendPacket(presence);
			Log.v("state", "设置离线");
			break;
		default:
			break;
		}
	}

	/**
	 * 删除当前用户
	 * 
	 * @param connection
	 * @return
	 */
	public static boolean deleteAccount() {
		try {
			connection.getAccountManager().deleteAccount();
			connection = null;
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 发送消息
	 * 
	 * @param to
	 * @param msg
	 */
	private  void sendTalkMsg(String to, String msg) {
		Chat chat = connection.getChatManager().createChat(to, null);
		try {
			chat.sendMessage(msg);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取离线的消息
	 * 
	 * @return
	 */
	private static List<org.jivesoftware.smack.packet.Message> getOffLine() {
		List<org.jivesoftware.smack.packet.Message> msglist = new ArrayList<org.jivesoftware.smack.packet.Message>();
		// OfflineMessageManager offlineMessageManager = new
		// OfflineMessageManager(connection);
		// // 获取离线消息,线程阻塞 不能Toast
		// try {
		// Iterator<org.jivesoftware.smack.packet.Message> it =
		// offlineMessageManager.getMessages();
		// while (it.hasNext()) {
		// org.jivesoftware.smack.packet.Message message = it.next();
		// msglist.add(message);
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// } finally {
		// try {
		// // 设置在线
		// Presence presence = new Presence(Presence.Type.available);
		// connection.sendPacket(presence);
		// offlineMessageManager.deleteMessages();
		// } catch (XMPPException e) {
		// e.printStackTrace();
		// }
		// }
		return msglist;
	}

	/**
	 * 获取在线消息
	 * 
	 * @return
	 */
	public static void getOnLineMsg(final Context context) {
		ChatManager cm = connection.getChatManager();
		cm.addChatListener(new ChatManagerListener() {
			@Override
			public void chatCreated(Chat chat, boolean able) {
				chat.addMessageListener(new MessageListener() {

					@Override
					public void processMessage(Chat arg0, Message message) {
						String msg = ParentUtil.decodeBase64(message.getBody());
						Log.i(tag, "parent getMsg>>>>" + msg);
						List<ChatInfo> list = new ArrayList<ChatInfo>();
						ChatInfo chatInfo = getMsgByJson(msg);
						if (null != chatInfo) {
							list.add(chatInfo);
							new ParentDtTool(context).insertNewChatInfo(list);
							Log.i(tag, "parent getMsg>>>>  sendBoadcast");
							sendBroadcastMsg(context, chatInfo);
						}
					}
				});
			}
		});
	}

	/**
	 * 文本广播
	 * 
	 * @param context
	 */
	private static void sendBroadcastMsg(Context context, ChatInfo chatInfo) {
		Intent intent = new Intent();
		intent.setAction(context.getString(R.string.ACTION_NEW_CHATINFO_ACTION));
		intent.putExtra("msg", chatInfo);
		intent.putExtra("info", chatInfo.getMsg());
		context.sendOrderedBroadcast(intent, null);
	}

	private static String serverName = "@win-cn8f96d0cqi";

	/**
	 * 只需给出receiverNo,senderNo,receiveType,receiveType,msg
	 * 
	 * @param chatinfo
	 * @throws XMPPException
	 */
	// @WIN-CN8F96D0CQI
	public static void newChat(ChatInfo chatinfo) {
		ChatManager cm = connection.getChatManager();
		final Chat chatF = cm.createChat(chatinfo.getReceiverNo() + serverName, new MessageListener() {

			@Override
			public void processMessage(Chat arg0, Message arg1) {
			}
		});
		String msg = "[{\"sendType\":\"" + chatinfo.getSendType() + "\",\"senderNo\":\"" + chatinfo.getSenderNo()
				+ "\",\"sendName\":\"" + chatinfo.getSendName() + "\",\"receiveName\":\"" + chatinfo.getReceiveName()
				+ "\",\"receiveType\":\"" + chatinfo.getReceiveType() + "\",\"receiveNo\":\""
				+ chatinfo.getReceiverNo() + "\",\"msg\":\"" + chatinfo.getMsg() + "\",\"time\":\""
				+ chatinfo.getTime() + "\"}]";
		msg = ParentUtil.encodeBase64(msg);
		try {
			chatF.sendMessage(msg);
		} catch (XMPPException e) {
			Log.i("chen", "发送消息失败:" + chatinfo.getMsg());
			e.printStackTrace();
		}
	}

	private static ChatInfo getMsgByJson(String msgall) {
		// String Js="[{\"name\":\"chen\",\"age\":21}]";
		Hashtable<String, String> msgs = new Hashtable<String, String>();
		@SuppressWarnings("resource")
		JsonReader reader = new JsonReader(new StringReader(msgall));
		ChatInfo chatInfo = new ChatInfo();
		try {
			reader.beginArray();
			reader.beginObject();
			while (reader.hasNext()) {
				String name = reader.nextName();
				if (name.equals("senderNo"))
					chatInfo.setReceiverNo(reader.nextString());
				else if (name.equals("sendName"))
					chatInfo.setReceiveName(reader.nextString());
				else if (name.equals("sendType"))
					chatInfo.setReceiveType(reader.nextString());
				else if (name.equals("receiveNo"))
					chatInfo.setSenderNo(reader.nextString());
				else if (name.equals("receiveName"))
					chatInfo.setSendName(reader.nextString());
				else if (name.equals("receiveType"))
					chatInfo.setSendType(reader.nextString());
				else if (name.equals("msg"))
					chatInfo.setMsg(reader.nextString());
				else if (name.equals("time")) {
					chatInfo.setTime(reader.nextString());
				}
			}
			reader.endObject();
			reader.endArray();
			chatInfo.setBothsend(1);
			chatInfo.setIsRead(0);
			return chatInfo;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
