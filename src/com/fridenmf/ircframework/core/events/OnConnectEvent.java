package com.fridenmf.ircframework.core.events;

public class OnConnectEvent extends IrcEvent {

	private String serverName;
	
	public OnConnectEvent(String serverName) {
		this.serverName = serverName;
	}
	
	public String getServerName(){
		return this.serverName;
	}

	@Override
	public String getEventName() {
		return "OnConnectEvent";
	}
	
}
