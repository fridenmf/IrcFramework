package com.friden.ircframework.events;

public class OnKickEvent extends IrcEvent{

	private String kicker_nick, kicker_user, kicker_host, nick, channel, reason;

	public OnKickEvent(String kicker_nick, String kicker_user, String kicker_host, String nick, String channel) {
		this(kicker_nick, kicker_user, kicker_host, nick, channel, null);
	}
	
	public OnKickEvent(String kicker_nick, String kicker_user, String kicker_host, String nick, String channel, String reason) {
		this.kicker_nick = kicker_nick;
		this.kicker_user = kicker_user;
		this.kicker_host = kicker_host;
		this.nick = nick;
		this.channel = channel;
		this.reason = reason;
	}
	
	public String getKicker_nick() {
		return kicker_nick;
	}

	public String getKicker_user() {
		return kicker_user;
	}

	public String getKicker_host() {
		return kicker_host;
	}

	public String getNick() {
		return nick;
	}

	public String getChannel() {
		return channel;
	}

	public String getReason() {
		return reason;
	}

	@Override
	public String getEventName() {
		return "OnKickEvent";
	}
	
}
