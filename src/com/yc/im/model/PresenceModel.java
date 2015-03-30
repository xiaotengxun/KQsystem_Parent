package com.yc.im.model;

public class PresenceModel {
	public static final String STATE_AVAILABLE = "available";
	public static final String STATE_UNAVAILABLE = "unavailable";
	private String state = STATE_AVAILABLE;
	private String id = "";

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
