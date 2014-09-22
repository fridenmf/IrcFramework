package com.friden.ircframework.events;

public class OnPartEvent extends IrcEvent {

	private String nick, user, host, channel, message;
	
	public OnPartEvent(String nick, String user, String host, String channel, String message) {
		this.nick = nick;
		this.user = user;
		this.host = host;
		this.channel = channel;
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

	public String getChannel() {
		return channel;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String getEventName() {
		return "OnPartEvent";
	}

}
