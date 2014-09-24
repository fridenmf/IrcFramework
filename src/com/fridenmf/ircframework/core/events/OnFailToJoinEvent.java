package com.fridenmf.ircframework.core.events;

public class OnFailToJoinEvent extends IrcEvent {

	private String channel, reason;

	public OnFailToJoinEvent(String channel, String reason) {
		super();
		this.channel = channel;
		this.reason = reason;
	}

	public String getChannel() {
		return channel;
	}

	public String getReason() {
		return reason;
	}
	
	@Override
	public String getEventName() {
		return "OnFailToJoinEvent";
	}
	
}
