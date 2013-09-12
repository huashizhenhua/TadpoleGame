package com.itap.voiceemoticon.activity;

public class Notification {
	public int id;
	public Object extObj;

	public Notification(int id, Object extObj) {
		this.id = id;
		this.extObj = extObj;
	}

	public Notification(int id) {
		this(id, null);
	}

	public void notifyToTarget() {
		NotificationCenter.getInstance().notify(this);
	}
}