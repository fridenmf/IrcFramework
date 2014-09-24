package com.fridenmf.ircframework.core.events;

public class OnUserJoinEvent extends IrcEvent {

	private String channel; 
	private String nick; 
	private String user; 
	private String host;
	
	public OnUserJoinEvent(String channel, String nick, String user, String host) {
		this.channel = channel;
		this.nick = nick;
		this.user = user;
		this.host = host;
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
	
	@Override
	public String getEventName() {
		return "OnUserJoinEvent";
	}
	
}
