package com.fridenmf.ircframework.core.events;


public class OnWhoisReplyEvent extends IrcEvent {

	private String nick, user, host, description;
	private String[] channels, prefixes;
	private boolean isRegistered, isAdmin, isAvailableForHelp;
	private int secondsIdle, signonTime;

	public OnWhoisReplyEvent(String nick, String user, String host, 
			String description, String[] channels, String[] prefixes,
			boolean isRegistered, boolean isAdmin, boolean isAvailableForHelp,
			int secondsIdle, int signonTime) {
		super();
		this.nick = nick;
		this.user = user;
		this.host = host;
		this.description = description;
		this.channels = channels;
		this.prefixes = prefixes;
		this.isRegistered = isRegistered;
		this.isAdmin = isAdmin;
		this.isAvailableForHelp = isAvailableForHelp;
		this.secondsIdle = secondsIdle;
		this.signonTime = signonTime;
	}
	
	@Override
	public String getEventName() {
		return "OnWhoisReplyEvent";
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

	public String getDescription() {
		return description;
	}

	public String[] getChannels() {
		return channels;
	}

	public String[] getPrefixes() {
		return prefixes;
	}

	public boolean isRegistered() {
		return isRegistered;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public boolean isAvailableForHelp() {
		return isAvailableForHelp;
	}

	public int getSecondsIdle() {
		return secondsIdle;
	}

	public int getSignonTime() {
		return signonTime;
	}

}
