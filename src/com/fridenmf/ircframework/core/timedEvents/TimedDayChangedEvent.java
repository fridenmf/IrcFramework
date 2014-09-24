package com.fridenmf.ircframework.core.timedEvents;

public class TimedDayChangedEvent extends TimedEvent {

	public TimedDayChangedEvent(long target) {
		super(target);
	}
	
	@Override
	public String getDescription() {
		return "TimedNewDayEvent will trigger: "+getTarget();
	}

}
