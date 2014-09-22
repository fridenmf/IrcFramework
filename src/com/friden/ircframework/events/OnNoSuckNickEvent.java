package com.friden.ircframework.events;


public class OnNoSuckNickEvent extends IrcEvent {

	private String nick, reason;
	
	public OnNoSuckNickEvent(String nick, String reason){
		this.nick = nick;
		this.reason = reason;
	}
	
	public String getNick(){
		return nick;
	}
	
	public String getReason(){
		return reason;
	}
	
	@Override
	public String getEventName() {
		return "OnNoSuckNickEvent";
	}

}