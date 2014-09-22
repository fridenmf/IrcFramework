package com.friden.ircframework.examples;

import com.friden.ircframework.extendable.IrcBot;

public class SimpleBot extends IrcBot{

	public static void main(String[] arg){
		new SimpleBot();
	}
	
	public SimpleBot() {
	
		addPlugin(new SimplePlugin(this, "SimpleBot"));
		
		connect("irc.server.org", 6667, "SimpleBot", "noUserName");
		
	}
	
}
