package com.fridenmf.ircframework.examples;

import com.fridenmf.ircframework.core.extendable.IrcBot;

public class HelloBot extends IrcBot {

	public static void main(String[] arg){
		new HelloBot();
	}
	
	public HelloBot() {
		addPlugin(new HelloVerbosePlugin(this, "VerbosePlugin"));
		addPlugin(new HelloEmptyPlugin(this, "EmptyPlugin"));
		
		connect("irc.server.org", 6667, "BotNick", "BotUser", "BotDescription");
	}
}
