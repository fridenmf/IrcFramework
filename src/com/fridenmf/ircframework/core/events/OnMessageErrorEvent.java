package com.fridenmf.ircframework.core.events;

public class OnMessageErrorEvent extends IrcEvent {

	private String channel, reason;
	
	public OnMessageErrorEvent(String channel, String reason){
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
		return "OnMessageErrorEvent";
	}

}
