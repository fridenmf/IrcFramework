package com.fridenmf.ircframework.core.events;


public class OnPrivateNoticeEvent extends IrcEvent {

	private String nick, user, host, message;
	
	public OnPrivateNoticeEvent(String nick, String user, String host, String message) {
		this.nick = nick;
		this.user = user;
		this.host = host;
		this.message = message;
	}
	
	public String getNick() {
		return nick;
	}
 
	public String getUser() {
		return user;
	}

	public String getHost() {
		return host;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String getEventName() {
		return "OnPrivateNoticeEvent";
	}

}
