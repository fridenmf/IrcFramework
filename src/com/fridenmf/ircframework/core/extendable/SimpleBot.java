package com.fridenmf.ircframework.core.extendable;

/**
 * This class is used for implementations that only need one plugin 
 * @author friden
 */
public class SimpleBot extends IrcPlugin {
	
	/* This implementation is a bit hacky, but it does the trick */
	
	private class PluginBot extends IrcBot {
		public PluginBot(String host, int port, String nick, String user, String description, IrcPlugin ip){
			addPlugin(ip);
			connect(host, port, nick, user, description);
		}
	}
	
	public SimpleBot(String host, int port, String nick, String user) {
		this(host, port, nick, user, null);
	}
	
	public SimpleBot(String host, int port, String nick, String user, String description) {
		super(null, null);
		pluginName = "IrcPlugin";
		ircBot = new PluginBot(host, port, nick, user, description, this);
	}

}
