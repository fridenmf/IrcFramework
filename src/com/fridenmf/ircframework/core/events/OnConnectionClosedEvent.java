package com.fridenmf.ircframework.core.events;

public class OnConnectionClosedEvent extends IrcEvent {

	private String reason;
	
	public OnConnectionClosedEvent(String reason) {
		this.reason = reason;
	}
	
	public String getReason(){
		return this.reason;
	}

	@Override
	public String getEventName() {
		return "OnConnectionClosedEvent";
	}
	
}