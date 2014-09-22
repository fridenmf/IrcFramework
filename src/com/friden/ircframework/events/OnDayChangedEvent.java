package com.friden.ircframework.events;


public class OnDayChangedEvent extends IrcEvent {

	@Override
	public String getEventName() {
		return "DayChangedEvent";
	}

}
