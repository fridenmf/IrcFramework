package com.friden.ircframework.events;

public class OnTopicChangeEvent extends IrcEvent {

	private String nick, user, host, channel, topic;

	public OnTopicChangeEvent(String nick, String user, String host, String channel, String topic) {
		this.nick = nick;
		this.user = user;
		this.host = host;
		this.channel = channel;
		this.topic = topic;
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

	public String getTopic() {
		return topic;
	}

	@Override
	public String getEventName() {
		return "OnTopicChangeEvent";
	}
	
}
