package com.itap.voiceemoticon;

public class MsgDef {
	
	public static final int MSG_DIALOG_SHOW = generateId();
	public static final int MSG_DIALOG_HIDE = generateId();
	public static final int MSG_LOGIN_FINISH = generateId();
	public static final int MSG_USER_MAKE_DIALOG = generateId();

	private static int base = 10000;

	public static int generateId() {
		return base++;
	}
}
