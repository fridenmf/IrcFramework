package com.fridenmf.ircframework.core.events;

public class OnNamesEvent extends IrcEvent {

	private String channel; 
	private String[] nicks;
	private String[] prefixes;
	
	public OnNamesEvent(String channel, String[] nicks, String[] prefixes) {
		this.channel = channel;
		this.nicks = nicks;
		this.prefixes = prefixes;
	}

	public String getChannel() {
		return channel;
	}

	public String[] getNicks() {
		return nicks;
	}

	public String[] getPrefixes() {
		return prefixes;
	}
	
	@Override
	public String getEventName() {
		return "OnNamesEvent";
	}
	
}
