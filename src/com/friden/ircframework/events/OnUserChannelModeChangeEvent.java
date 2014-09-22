package com.friden.ircframework.events;

public class OnUserChannelModeChangeEvent extends IrcEvent {

	private String nick, user, host, channel;
	private String[] nicks, modes;
	
	public OnUserChannelModeChangeEvent(String nick, String user, String host, String channel, String[] modes, String[] nicks) {
		this.nick = nick;
		this.user = user;
		this.host = host;
		this.channel = channel;
		this.modes = modes;
		this.nicks = nicks;
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

	public String[] getModes() {
		return modes;
	}

	public String[] getNicks() {
		return nicks;
	}

	@Override
	public String getEventName() {
		return "OnUserChannelModeChangeEvent";
	}
	
}
