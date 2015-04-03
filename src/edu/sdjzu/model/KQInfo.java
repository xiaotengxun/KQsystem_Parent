package edu.sdjzu.model;

public class KQInfo {
	private String dateTime = "";
	private String msg="";
	private int id=0;
	private int isRead=0;
	public int getIsRead() {
		return isRead;
	}
	public void setIsRead(int isRead) {
		this.isRead = isRead;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDateTime() {
		return dateTime;
	}
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public KQInfo(String dateTime, String msg,int id,int isRead) {
		super();
		this.dateTime = dateTime;
		this.msg = msg;
		this.id=id;
		this.isRead=isRead;
	}
	public KQInfo(){}


}
