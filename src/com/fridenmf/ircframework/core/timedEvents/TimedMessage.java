package com.fridenmf.ircframework.core.timedEvents;

public class TimedMessage extends TimedEvent{

	private String message = null;
	
	public TimedMessage(long target, String message) {
		super(target);
		this.message = message;
	}
	
	@Override
	public String getDescription() {
		return "Message: "+message+", will be sent: "+getTarget();
	}
	
}
