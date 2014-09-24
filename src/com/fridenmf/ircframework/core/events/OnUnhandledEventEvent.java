package com.fridenmf.ircframework.core.events;

public class OnUnhandledEventEvent extends IrcEvent {

	private String line;
	
	public OnUnhandledEventEvent(String line){
		this.line = line;
	}
	
	public String getLine(){
		return this.line;
	}

	@Override
	public String getEventName() {
		return "OnUnhandledEventEvent";
	}
	
}
