package com.friden.ircframework.events;

public class OnInviteEvent extends IrcEvent {

	private String nick, user, host, channel;

	public OnInviteEvent(String nick, String user, String host, String channel) {
		this.nick = nick;
		this.user = user;
		this.host = host;
		this.channel = channel;
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
	
	@Override
	public String getEventName() {
		return "OnInviteEvent";
	}
	
}
