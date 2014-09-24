package com.fridenmf.ircframework.core.events;

public class OnUserModeChangeEvent extends IrcEvent {

	private String nick1; 
	private String nick2; 
	private String mode;
	
	public OnUserModeChangeEvent(String nick1, String nick2, String mode) {
		this.nick1 = nick1;
		this.nick2 = nick2;
		this.mode = mode;
	}

	public String getNick1() {
		return nick1;
	}

	public String getNick2() {
		return nick2;
	}

	public String getMode() {
		return mode;
	}
	
	@Override
	public String getEventName() {
		return "OnUserModeEvent";
	}
	
}
