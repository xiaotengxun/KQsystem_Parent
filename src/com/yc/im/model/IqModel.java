package com.yc.im.model;

public class IqModel {
	public static final String STATE_AVAILABLE = "available";
	public static final String STATE_UNAVAILABLE = "unavailable";
	private String id = "";
	private final String type = "roster";
	private final String query = "request";
	private String rosterName="";
	private String rosterId="";
	private String state=STATE_AVAILABLE;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public String getQuery() {
		return query;
	}

	public String getRosterId() {
		return rosterId;
	}

	public void setRosterId(String rosterId) {
		this.rosterId = rosterId;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public static String getStateAvailable() {
		return STATE_AVAILABLE;
	}

	public String getRosterName() {
		return rosterName;
	}

	public void setRosterName(String rosterName) {
		this.rosterName = rosterName;
	}

}
