package com.friden.ircframework.events;

public class OnMessageEvent extends IrcEvent {

	private String channel; 
	private String nick; 
	private String user; 
	private String host;
	private String message;
	
	public OnMessageEvent(String channel, String nick, String user, String host, String message) {
		this.channel = channel;
		this.nick = nick;
		this.user = user;
		this.host = host;
		this.message = message;
	}

	public String getChannel() {
		return channel;
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
		return "OnMessageEvent";
	}
	
}
