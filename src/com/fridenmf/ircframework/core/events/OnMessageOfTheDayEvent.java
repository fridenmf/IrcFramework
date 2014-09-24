package com.fridenmf.ircframework.core.events;

public class OnMessageOfTheDayEvent extends IrcEvent {

	private String messageOfTheDay;
	
	public OnMessageOfTheDayEvent(String motd){
		this.messageOfTheDay = motd;
	}
	
	public String getMessageOfTheDay(){
		return messageOfTheDay;
	}
	
	@Override
	public String getEventName() {
		return "OnMessageOfTheDayEvent";
	}
	
}
