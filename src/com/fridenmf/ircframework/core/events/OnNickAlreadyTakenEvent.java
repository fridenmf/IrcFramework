package com.fridenmf.ircframework.core.events;


public class OnNickAlreadyTakenEvent extends IrcEvent {

	private String nick, message;

	public OnNickAlreadyTakenEvent(String nick, String message) {
		this.nick = nick;
		this.message = message;
	}
	
	public String getNick() {
		return nick;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String getEventName() {
		return "OnNickAlreadyTakenEvent";
	}

}
