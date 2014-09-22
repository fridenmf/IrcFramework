package com.friden.ircframework.events;

public class OnPingEvent extends IrcEvent{
	
	private String message;

	public OnPingEvent(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
	
	@Override
	public String getEventName() {
		return "OnPingEvent";
	}
	
}
