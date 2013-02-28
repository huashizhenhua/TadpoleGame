package com.itap.voiceemoticon.api.util;

/**
 * @author Lukasz Wisniewski
 */
public class WSError extends Throwable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String message;

	public WSError() {
	}
	
	public WSError(String message) {
		this.message = message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}