package com.fridenmf.ircframework.core.events;

public class OnAwayEvent extends IrcEvent {

	private String message;
	private boolean isAway;
	
	public OnAwayEvent(String message, boolean isAway) {
		this.message = message;
		this.isAway = isAway;
	}

	public String getMessage() {
		return message;
	}

	public boolean isAway() {
		return isAway;
	}

	@Override
	public String getEventName() {
		return "OnAwayEvent";
	}

}
