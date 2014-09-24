package com.fridenmf.ircframework.core.events;


public class OnNickEvent extends IrcEvent {

	private String nickFrom = null;
	private String nickTo = null;
	
	public OnNickEvent(String nickFrom, String nickTo){
		this.nickFrom = nickFrom;
		this.nickTo = nickTo;
	}
	
	public String getNickFrom(){
		return this.nickFrom;
	}
	
	public String getNickTo(){
		return this.nickTo;
	}
	
	@Override
	public String getEventName() {
		return "OnNickEvent";
	}

}