package com.fridenmf.ircframework.examples;

import com.fridenmf.ircframework.core.events.OnMessageEvent;
import com.fridenmf.ircframework.core.extendable.IrcBot;
import com.fridenmf.ircframework.core.extendable.IrcPlugin;

public class HelloEmptyPlugin extends IrcPlugin {

	public HelloEmptyPlugin(IrcBot ircBot, String pluginName) {
		super(ircBot, pluginName);
	}
	
	@Override
	public void onMessage(OnMessageEvent e) {
		System.out.println("Got a message in channel "+e.getChannel()+": "+e.getMessage());
	}
}
