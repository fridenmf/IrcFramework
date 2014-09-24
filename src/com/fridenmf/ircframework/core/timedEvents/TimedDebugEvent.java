package com.fridenmf.ircframework.core.timedEvents;

public class TimedDebugEvent extends TimedEvent {

	public TimedDebugEvent(long target) {
		super(target);
	}

	@Override
	public String getDescription() {
		return "This is just a debug message";
	}
	
}
