package com.friden.ircframework.events;


public class OnAwayReplyEvent extends IrcEvent {

	private String nick, message;
	
	public OnAwayReplyEvent(String nick, String message) {
		this.nick = nick;
		this.message = message;
	}
	
	public String getNick() {
		return nick;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String getEventName() {
		return "OnAwayReply";
	}

}
