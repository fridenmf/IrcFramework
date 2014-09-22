package com.friden.ircframework.examples;

import com.friden.ircframework.events.OnAwayReplyEvent;
import com.friden.ircframework.events.OnChannelModeChangeEvent;
import com.friden.ircframework.events.OnConnectEvent;
import com.friden.ircframework.events.OnFailToJoinEvent;
import com.friden.ircframework.events.OnInviteEvent;
import com.friden.ircframework.events.OnJoinEvent;
import com.friden.ircframework.events.OnKickEvent;
import com.friden.ircframework.events.OnMessageErrorEvent;
import com.friden.ircframework.events.OnMessageEvent;
import com.friden.ircframework.events.OnNamesEvent;
import com.friden.ircframework.events.OnNickAlreadyTakenEvent;
import com.friden.ircframework.events.OnNoticeEvent;
import com.friden.ircframework.events.OnPartEvent;
import com.friden.ircframework.events.OnPrivateNoticeEvent;
import com.friden.ircframework.events.OnTopicChangeEvent;
import com.friden.ircframework.events.OnUnhandledEventEvent;
import com.friden.ircframework.events.OnUserChannelModeChangeEvent;
import com.friden.ircframework.events.OnUserJoinEvent;
import com.friden.ircframework.events.OnUserModeChangeEvent;
import com.friden.ircframework.events.OnWhoisReplyEvent;
import com.friden.ircframework.extendable.IrcBot;
import com.friden.ircframework.extendable.IrcPlugin;
import com.friden.ircframework.utilities.IrcUtilities;


public class SimplePlugin extends IrcPlugin {

	public SimplePlugin(IrcBot ircBot, String pluginName) {
		super(ircBot, pluginName);
	}

	public void onPrivateMessage(com.friden.ircframework.events.OnPrivateMessageEvent e) {
		System.out.println(e.getNick()+": "+e.getMessage());
	}
	
	@Override
	public void onMessage(OnMessageEvent e) {
		System.out.println(e.getChannel()+" "+e.getNick()+": "+e.getMessage());
	}
	
	@Override
	public void onInvite(OnInviteEvent e) {
		System.out.println("Was invited to "+e.getChannel()+" by "+e.getNick()+", accepting invitation");
		join(e.getChannel());
	}
	
	@Override
	public void onConnect(OnConnectEvent e) {
		System.out.println("Connected to "+e.getServerName()+", joining #test123");
		join("#test123");
	}
	
	@Override
	public void onUserJoin(OnUserJoinEvent e) {
		System.out.println(e.getNick()+" has joined "+e.getChannel());
	}
	
	@Override
	public void onJoin(OnJoinEvent e) {
		
		System.out.println("Joined "+e.getChannel());
		
		System.out.print("Names: ");
		for (int i = 0; i < e.getNames().length; i++) {
			System.out.print(e.getNames()[i]+" ");
		}
		System.out.println();
		
		System.out.println("Requesting names again to trigger onNames");
		names(e.getChannel());
		
	}
	
	@Override
	public void onKick(OnKickEvent e) {
		System.out.println(e.getNick()+" was kicked from "+e.getChannel()+" by "+e.getKicker_nick()+(e.getReason()==null?" without reason":" with reason \""+e.getReason()+"\""));
	}
	
	@Override
	public void onUserModeChange(OnUserModeChangeEvent e) {
		System.out.println(e.getNick1()+" changed mode "+e.getMode()+" on "+e.getNick2());
	}
	
	@Override
	public void onUserChannelModeChange(OnUserChannelModeChangeEvent e) {
		System.out.println(e.getNick()+" changed mode "+IrcUtilities.stringArrayToString(e.getModes(), "")+" for "+IrcUtilities.stringArrayToString(e.getNicks(), " "));
	}
	
	@Override
	public void onFailToJoinEvent(OnFailToJoinEvent e) {
		System.out.println("Could not join "+e.getChannel()+", reason: "+e.getReason());
	}
	
	@Override
	public void onMessageError(OnMessageErrorEvent e) {
		System.out.println("Could not send to "+e.getChannel()+", reason: "+e.getReason());
	}
	
	@Override
	public void onNames(OnNamesEvent e) {
		System.out.println("Got names from "+e.getChannel()+": ");
		System.out.println(IrcUtilities.stringArrayToString(e.getNicks(), ", "));
	}
	
	@Override
	public void onTopicChange(OnTopicChangeEvent e) {
		System.out.println(e.getNick()+" changed topic for channel "+e.getChannel()+" to "+e.getTopic());
	}
	
	@Override
	public void onPart(OnPartEvent e) {
		System.out.println(e.getNick()+" parted from channel "+e.getChannel()+ (e.getMessage()==null?"":" with message: "+e.getMessage()));
	}
	
	@Override
	public void onChannelModeChange(OnChannelModeChangeEvent e) {
		System.out.println(e.getNick()+" changed mode "+e.getMode()+" on "+e.getChannel());
	}
	
	@Override
	public void onUnhandledEvent(OnUnhandledEventEvent e) {
		System.err.println("Unhandled ln: "+e.getLine());
	}
	
	@Override
	public void onNotice(OnNoticeEvent e) {
		System.out.println("Notice from "+e.getNick()+" in channel "+e.getChannel()+": "+e.getMessage());
	}
	
	@Override
	public void onPrivateNotice(OnPrivateNoticeEvent e) {
		System.out.println("Notice from "+e.getNick()+": "+e.getMessage());
	}
	
	@Override
	public void onAwayReply(OnAwayReplyEvent e) {
		System.out.println(e.getNick()+" is away. Automatic away reply: "+e.getMessage());
	}
	
	@Override
	public void onNickAlreadyTaken(OnNickAlreadyTakenEvent e) {
		System.out.println("Nick "+e.getNick()+" was already taken");
		/* Here is typically a good place to choose a new nick, ex: nick(SimplePlugin_);*/
	}
	
	@Override
	public void onWhoisReply(OnWhoisReplyEvent e) {
		System.out.println("Got an Whois reply:");
		if(e.getNick() != null){ System.out.println("Nick: "+e.getNick()); }
		if(e.getUser() != null){ System.out.println("User: "+e.getUser()); }
		if(e.getHost() != null){ System.out.println("Host: "+e.getHost()); }
		if(e.getDescription() != null){ System.out.println("Description: "+e.getDescription()); }
		if(e.getChannels() != null){
			System.out.println("Channels: ");
			for (int i = 0; i < e.getChannels().length; i++) {
				System.out.println("  "+e.getChannels()[i]);
			}
		}
		if(e.getPrefixes() != null){ System.out.println("Prefixes: "+e.getPrefixes().length); }
		if(e.getSecondsIdle() != -1){ System.out.println("Seconds idle: "+e.getSecondsIdle()); }
		if(e.getSignonTime() != -1){ System.out.println("Sign on time:" +e.getSignonTime()); }
	}
	
}
