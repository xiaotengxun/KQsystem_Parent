package com.yc.jar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

public class ChatConnectTool {
	private static Socket socket = null;
	private static String serverUrl = "192.168.10.20";
	private static int port = 6543;
	private static PrintWriter out = null;
	private static BufferedReader in = null;
	private static boolean isFinishListening = false;
	private static ChatNewInfoOnListener chatNewInfoListener = null;
	private static final int readDataOverTime = 60 * 1000;// 读取数据流的超时时间
	private static final int checkConnectServerOffTime = 60 * 1000;// 检测心跳时间间隔
	private static boolean isNetAvailable = true;

	public ChatConnectTool() {

	}

	public static void initSocket() {
		if (socket == null && isNetAvailable) {
			Log.i("chen", "reconnect to server");
			try {

				socket = new Socket(serverUrl, port);
				// 作用：每隔一段时间检查服务器是否处于活动状态，如果服务器端长时间没响应，自动关闭客户端socket
				// 防止服务器端无效时，客户端长时间处于连接状态
				socket.setKeepAlive(true);
				// 设置客户端socket关闭时，close（）方法起作用时延迟1分钟关闭，如果1分钟内尽量将未发送的数据包发送出去
				socket.setSoTimeout(readDataOverTime);
				socket.setTcpNoDelay(true);
				// *设置socket 读取数据流的超时时间
				socket.setSoLinger(true, 60);
				// 代表可以立即向服务器端发送单字节数据
				socket.setOOBInline(true);
				out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				setNetStateListener();
			} catch (UnknownHostException e) {
				out = null;
				in = null;
				socket = null;
				Log.i("chen", "" + e);
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				initSocket();
			} catch (IOException e) {
				out = null;
				in = null;
				socket = null;
				Log.i("chen", "" + e);
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				initSocket();
			}
		}
	}

	public synchronized static void sendXmlToServer(String xmlStr) {
		if (out != null && isNetAvailable) {
			// xmlStr = new
			// String(Base64.encode(xmlStr.getBytes(),0,xmlStr.length(),0));
			out.write(xmlStr);
			out.flush();
		}
	}

	private static void checkConnectServer() {
		if (socket == null || !isNetAvailable) {
			return;
		}
		try {
			socket.sendUrgentData(0xff);
			Log.i("chen", "心跳保持");
		} catch (IOException e) {
			Log.i("chen", "心跳异常" + e);
			disconnect();
			initSocket();
			beginListen();
		}
	}

	public static void setOnChateNewInfoListener(ChatNewInfoOnListener listen) {
		chatNewInfoListener = listen;
	}

	private static void beginListen() {
		if (in == null || out == null || !isNetAvailable) {
			Log.i("chen", "输入输出流初始化失败");
			return;
		}
		isFinishListening = false;
		StringBuffer response = new StringBuffer();
		char[] buffer = new char[1024];
		boolean isData = false;
		while (!isFinishListening) {// ////
			int readCounts = 0;
			response = new StringBuffer();
			isData = false;
			try {
				Log.i("chen", "begin read");
				while ((readCounts = in.read(buffer)) != -1) {
					// Log.i("chen", "receiver read=" + readCounts);
					isData = true;
					String str = (new String(buffer)).substring(0, readCounts);
					Log.i("chen", "str:" + str);
					if (str.equals("ChEnShUwAnByEByE")) {
						break;
					}
					response.append(str);
				}
				if (isData) {
					Log.i("chen", "msg:" + response.toString());
					if (chatNewInfoListener != null) {
						chatNewInfoListener.getNewMessage(response.toString());
					}
				}
			} catch (IOException e) {
				Log.i("chen", "读取服务器数据异常" + e);

				e.printStackTrace();
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			checkConnectServer();
		}// /////////////////////////////////

	}

	private static void setNetStateListener() {
		ServiceBroadReceiver.setChatStateChangeListener(new ChatConnectStateListener() {
			@Override
			public void reInitSocket() {
				Log.i("chen", "reInitSocket");
				isNetAvailable = true;
				disconnect();
				initSocket();
				beginListen();
			}

			@Override
			public void closeSocket() {
				Log.i("chen", "closeSocket");
				isNetAvailable = false;
				disconnect();
				Thread.currentThread().interrupt();
			}
		});
	}

	public synchronized static void disconnect() {
		isFinishListening = true;
		if (socket != null) {
			try {
				out.close();
				in.close();
				socket.close();
			} catch (IOException e) {
				Log.i("chen", "与服务器断开时发生异常" + e);
				e.printStackTrace();
			}
			socket = null;
			out = null;
			in = null;
		}
	}

}
