package com.friden.ircframework.events;

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
