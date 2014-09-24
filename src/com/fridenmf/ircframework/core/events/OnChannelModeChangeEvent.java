package com.fridenmf.ircframework.core.events;

public class OnChannelModeChangeEvent extends IrcEvent {

	private String nick, user, host, channel, mode;
	
	public OnChannelModeChangeEvent(String nick, String user, String host, String channel, String mode) {
		this.nick = nick;
		this.user = user;
		this.host = host;
		this.channel = channel;
		this.mode = mode;
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

	public String getMode() {
		return mode;
	}

	@Override
	public String getEventName() {
		return "OnChannelModeChangeEvent";
	}
	
}
