package edu.sdjzu.model;

import java.io.Serializable;

public class ChatInfo implements Serializable {
	String id;
	String senderNo, receiverNo, msg;
	// boolean isDone, bothsend;//
	// bothsend=0��ʾ������ݳ�ȥ��bothsend=1��ʾ���յ���ݣ�isDone��ʾ���ͻ��������Ƿ�ɹ�
	int bothsend;
	int isRead = 0;
	String time;
	String sendName, receiveName;
	String sendType, receiveType;// �ҳ���ѧ����ʦ������Ա

	public ChatInfo(String senderNo, String receiverNo, String msg, int isRead, int bothsend, String time,
			String sendName, String receiveName, String sendType, String receiveType) {
		super();
		this.senderNo = senderNo;
		this.receiverNo = receiverNo;
		this.msg = msg;
		this.isRead = isRead;
		this.bothsend = bothsend;
		this.time = time;
		this.sendName = sendName;
		this.receiveName = receiveName;
		this.sendType = sendType;
		this.receiveType = receiveType;
	}

	public ChatInfo() {
		super();
	}

	public String getSenderNo() {
		return senderNo;
	}

	public void setSenderNo(String senderNo) {
		this.senderNo = senderNo;
	}

	public String getReceiverNo() {
		return receiverNo;
	}

	public void setReceiverNo(String receiverNo) {
		this.receiverNo = receiverNo;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getIsRead() {
		return isRead;
	}

	public void setIsRead(int isRead) {
		this.isRead = isRead;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getSendName() {
		return sendName;
	}

	public void setSendName(String sendName) {
		this.sendName = sendName;
	}

	public String getReceiveName() {
		return receiveName;
	}

	public void setReceiveName(String receiveName) {
		this.receiveName = receiveName;
	}

	public String getSendType() {
		return sendType;
	}

	public void setSendType(String sendType) {
		this.sendType = sendType;
	}

	public String getReceiveType() {
		return receiveType;
	}

	public void setReceiveType(String receiveType) {
		this.receiveType = receiveType;
	}

	public int getBothsend() {
		return bothsend;
	}

	public void setBothsend(int bothsend) {
		this.bothsend = bothsend;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
