package com.fridenmf.ircframework.core.events;

public class OnJoinEvent extends IrcEvent {

	private String channel = null;
	private String[] names = null;
	private String[] prefixes = null;
	
	public OnJoinEvent(String channel, String[] names, String[] prefixes){
		this.channel = channel;
		this.names = names;
		this.prefixes = prefixes;
	}
	
	public String getChannel(){
		return this.channel;
	}
	
	public String[] getNames(){
		return this.names;
	}
	
	public String[] getPrefixes(){
		return this.prefixes;
	}
	
	@Override
	public String getEventName() {
		return "OnJoinEvent";
	}

}