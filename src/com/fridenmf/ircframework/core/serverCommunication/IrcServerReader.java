package com.fridenmf.ircframework.core.serverCommunication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

import com.fridenmf.ircframework.core.events.IrcEvent;
import com.fridenmf.ircframework.core.events.OnAwayEvent;
import com.fridenmf.ircframework.core.events.OnAwayReplyEvent;
import com.fridenmf.ircframework.core.events.OnChannelModeChangeEvent;
import com.fridenmf.ircframework.core.events.OnConnectEvent;
import com.fridenmf.ircframework.core.events.OnConnectionClosedEvent;
import com.fridenmf.ircframework.core.events.OnFailToJoinEvent;
import com.fridenmf.ircframework.core.events.OnInviteEvent;
import com.fridenmf.ircframework.core.events.OnJoinEvent;
import com.fridenmf.ircframework.core.events.OnKickEvent;
import com.fridenmf.ircframework.core.events.OnMessageErrorEvent;
import com.fridenmf.ircframework.core.events.OnMessageEvent;
import com.fridenmf.ircframework.core.events.OnMessageOfTheDayEvent;
import com.fridenmf.ircframework.core.events.OnNamesEvent;
import com.fridenmf.ircframework.core.events.OnNickAlreadyTakenEvent;
import com.fridenmf.ircframework.core.events.OnNickEvent;
import com.fridenmf.ircframework.core.events.OnNoSuckNickEvent;
import com.fridenmf.ircframework.core.events.OnNoticeEvent;
import com.fridenmf.ircframework.core.events.OnPartEvent;
import com.fridenmf.ircframework.core.events.OnPingEvent;
import com.fridenmf.ircframework.core.events.OnPrivateMessageEvent;
import com.fridenmf.ircframework.core.events.OnPrivateNoticeEvent;
import com.fridenmf.ircframework.core.events.OnTopicChangeEvent;
import com.fridenmf.ircframework.core.events.OnUnhandledEventEvent;
import com.fridenmf.ircframework.core.events.OnUserChannelModeChangeEvent;
import com.fridenmf.ircframework.core.events.OnUserJoinEvent;
import com.fridenmf.ircframework.core.events.OnUserModeChangeEvent;
import com.fridenmf.ircframework.core.events.OnWhoisReplyEvent;
import com.fridenmf.ircframework.core.extendable.IrcBot;
import com.fridenmf.ircframework.core.extendable.IrcPlugin;
import com.fridenmf.ircframework.core.utilities.IrcUtilities;

public class IrcServerReader extends Thread{

	private IrcBot ircBot     = null;
	private BufferedReader br = null;
	
	private boolean running   = false;
	
	private boolean verbose      = false;
	private boolean verboseError = false;
	
	private boolean isUsingUnderlineAscii = false;
	private boolean isUsingInvertAscii    = false;
	private boolean isUsingColorsAscii    = false;
	private boolean isUsingBoldAscii      = false;
	
	private boolean isStoppedByConnectionError = false;
	
	private Socket socket = null;
	
	public boolean getIsStoppedByConnectionError(){
		return isStoppedByConnectionError;
	}
	
	public IrcServerReader(IrcBot ircBot, Socket socket) {
		this.socket = socket;
		this.ircBot = ircBot;
		try {
			this.br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setVerbose(boolean b){
		this.verbose = b;
	}
	
	public void setVerboseError(boolean b){
		this.verboseError = b;
	}
	
	@Override
	public synchronized void start() {
		running = true;
		super.start();
	}
	
	@Override
	public void run() {
		
		/* 
		 * TODO LIST
		 * check that its possible to change nick and that ircbot.getNick() works for the new nick
		 * */
		
		while(running){
			
			try {
				String line = nextLine();
				if(line == null){
					running = false;
					isStoppedByConnectionError = true;
					continue;
				}
				log("<-- "+line);
				String[] splitted = line.split("[ ]");
				if(splitted != null && splitted.length >= 1 && splitted[0].equals("PING")){
					fireEvent(new OnPingEvent(splitted[1]));
				}else if(splitted != null && splitted.length >= 2){
					
					if(splitted[1].equals("375")){
						handleMessageOfTheDay(br);	
					}else if(splitted[1].equals("001")){
						handleOnConnect(line, splitted, br);
					}else if(splitted[1].equals("MODE")){
						if(splitted[2].startsWith("#")){
							if(splitted.length == 4){
								handleOnChannelModeChange(line, splitted);
							}else{
								handleOnUserChannelModeChange(line, splitted);
							}
						}else{
							handleOnUserModeChange(splitted);
						}
					}else if(splitted[1].equals(":Closing") && splitted[2].equals("Link:")){
						handleOnConnectionClosedEvent(line, splitted);
					}else if(splitted[1].equals("471") || splitted[1].equals("473") || splitted[1].equals("474") || splitted[1].equals("475")){
						handleOnFailToJoin(line, splitted);
					}else if(splitted[1].equals("404")){
						handleOnMessageError(line, splitted);
					}else if(splitted[1].equals("401")){
						handleOnNoSuchNick(line, splitted);
					}else if(splitted[1].equals("306")){
						handleOnAway(line, true);
					}else if(splitted[1].equals("305")){
						handleOnAway(line, false);
					}else if(splitted[1].equals("301")){
						handleOnReplyAway(line, splitted);
					}else if(splitted[1].equals("433")){
						handleOnNickAlreadyTaken(line, splitted);
					}else if(splitted[1].equals("353")){
						handleOnNames(line);
					}else if(splitted[1].equals("311")){
						handleOnWhoisReply(line, splitted);
					}else if(splitted[1].equals("JOIN")){
						
						String nick = getNickFromSplit(splitted[0]);
						String channel = splitted[2].substring(1);
						
						if(nick.equals(ircBot.getNick())){
							handleOnJoin(channel, line, splitted, br);
						}else{
							handleOnUserJoin(channel, nick, splitted);
						}
						
					}else if(splitted[1].equals("PRIVMSG")){
						handleMessages(line, splitted);
					}else if(splitted[1].equals("KICK")){
						handleOnKick(line, splitted);
					}else if(splitted[1].equals("NICK")){
						handleOnNick(line, splitted);
					}else if(splitted[1].equals("PART")){
						handleOnPart(line, splitted);
					}else if(splitted[1].equals("INVITE")){
						handleOnInvite(line, splitted);
					}else if(splitted[1].equals("TOPIC")){
						handleOnTopic(line, splitted);
					}else if(splitted[1].equals("NOTICE")){
						handleOnNotices(line, splitted);
					}else{
						handleUnhandledEvent(line);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ircBot.readerIsDead();
		
	}
	
	private void handleOnConnectionClosedEvent(String line, String[] splitted) {
		
//		  0       1      2          3         4      5
//		ERROR :Closing Link: Bot[url.com] (Quit: Bot)
		
		int startIndex = 6 + 
			splitted[0].length() + 
			splitted[1].length() +
			splitted[2].length() +
			splitted[3].length();
		
		String reason = line.substring(startIndex, line.length());
		
		running = false;
				
		fireEvent(new OnConnectionClosedEvent(reason));
		
	}

	private void handleOnNick(String line, String[] splitted) {
		String nickFrom = getNickFromSplit(line);
		String nickTo = line.substring(1 + line.substring(1).indexOf(":") + 1);
		
		fireEvent(new OnNickEvent(nickFrom, nickTo));
	}

	private void handleOnNoSuchNick(String line, String[] splitted) {
		String nick = splitted[2];
		String reason = line.substring(1 + line.substring(1).indexOf(":") + 1);
		
		fireEvent(new OnNoSuckNickEvent(nick, reason));
	}
	
	private void handleOnMessageError(String line, String[] splitted) {
		String channel = splitted[3];
		String reason = line.substring(1 + line.substring(1).indexOf(":") + 1);
		
		fireEvent(new OnMessageErrorEvent(channel, reason));
	}

	private void handleOnWhoisReply(String line, String[] splitted) {
		String nick = splitted[3];
		String user = splitted[4];
		
		if(user.startsWith("~") || user.startsWith("!")){
			user = user.substring(1);
		}
		
		String host = splitted[5];
		String description = line.substring(1 + line.substring(1).indexOf(":") + 1);
		
		boolean isRegistered       = false;
		boolean isAdmin            = false;
		boolean isAvailableForHelp = false;
		
		int secondsIdle = -1;
		int signonTime  = -1;
		
		String[] channels = null;
		String[] prefixes = null;
		
		boolean done = false;
		while(!done){
			
			line = nextLine();
			if(line == null){
				return;
			}
			
			splitted = line.split("[ ]");
			
			if(splitted[1].equals("307")){
				isRegistered = true;
			}else if(splitted[1].equals("310")){
				isAvailableForHelp = true;
			}else if(splitted[1].equals("313")){
				isAdmin = true;
			}else if(splitted[1].equals("317")){
				secondsIdle = Integer.parseInt(splitted[4]);
				signonTime = Integer.parseInt(splitted[5]);
			}else if(splitted[1].equals("319")){
				String allChannels = line.substring(1 + line.substring(1).indexOf(":") + 1);
				channels = allChannels.split("[ ]");
				prefixes = new String[channels.length];
				for (int i = 0; i < channels.length; i++) {
					if(IrcUtilities.nickHasPrefix(channels[i])){
						prefixes[i] = ""+channels[i].charAt(0);
						channels[i] = channels[i].substring(1);
					}else{
						prefixes[i] = "";
					}
				}
			}else if(splitted[1].equals("318")){
				done = true;
			}else{
				logError("Unhandled whois reply line: "+line);
			}
		}
		
		fireEvent(new OnWhoisReplyEvent(nick, user, host, description, channels, prefixes, isRegistered, isAdmin, isAvailableForHelp, secondsIdle, signonTime));
		
	}

	private void handleOnNames(String line) {

		boolean done = false;
		
		ArrayList<String> allNicks = new ArrayList<String>();
		ArrayList<String> allPrefixes = new ArrayList<String>();
		
		String channel = null;
		
		while(!done){
			
			String[] splitted = line.split("[ ]");
			if(channel == null){
				channel = splitted[4];
			}else if(!channel.equals(splitted[4])){
				System.err.println("Received NAMES from two different channels at the same time. Not supported yet");
				return;
			}
			
			String[] nicks = line.substring(1 + line.substring(1).indexOf(":") + 1).split("[ ]");
			
			for (int i = 0; i < nicks.length; i++) {
				if(IrcUtilities.nickHasPrefix(nicks[i])){
					allPrefixes.add(""+nicks[i].charAt(0));
					allNicks.add(nicks[i].substring(1));
				}else{
					allPrefixes.add("");
					allNicks.add(nicks[i]);
				}
			}
			
			String nextLine = nextLine();
			if(nextLine.split("[ ]")[1].equals("366")){
				done = true;
			}
		
		}
		
		fireEvent(new OnNamesEvent(channel, allNicks.toArray(new String[allNicks.size()]), allPrefixes.toArray(new String[allPrefixes.size()])));
	}

	private void handleOnNickAlreadyTaken(String line, String[] splitted) {
		String nick = splitted[3];
		String message  = line.substring(1 + line.substring(1).indexOf(":") + 1);
		
		fireEvent(new OnNickAlreadyTakenEvent(nick, message));
	}

	private void handleOnReplyAway(String line, String[] splitted) {
		String nick = splitted[3];
		String message = line.substring(1 + line.substring(1).indexOf(":") + 1);
		
		fireEvent(new OnAwayReplyEvent(nick, message));
	}

	private void handleOnAway(String line, boolean isAway) {
		String message = line.substring(1 + line.substring(1).indexOf(":") + 1);
		
		fireEvent(new OnAwayEvent(message, isAway));
	}

	private void handleOnNotices(String line, String[] splitted) {
//		:NickServ!services@services.irc.server.org NOTICE Bot :You must have been using this nick for at least 30 seconds to register.
		
		if(!splitted[0].contains("!")){
			fireEvent(new OnUnhandledEventEvent(line));
			return;
		}
		
		String nick = getNickFromSplit(splitted[0]);
		String user = getUserFromSplit(splitted[0]);
		String host = getHostFromSplit(splitted[0]);
		String nickOrChannel = splitted[2];
		String message = line.substring(1 + line.substring(1).indexOf(":") + 1);
		
		if(isChannel(nickOrChannel)){
			fireEvent(new OnNoticeEvent(nick, user, host, nickOrChannel, message));
		}else{
			//TODO check if getNick is needs to be recoded
			if(!nickOrChannel.equals(ircBot.getNick())){
				fireEvent(new OnUnhandledEventEvent(line));
			}else{
				fireEvent(new OnPrivateNoticeEvent(nick, user, host, message));
			}
		}
	}

	private void handleUnhandledEvent(String line) {
		fireEvent(new OnUnhandledEventEvent(line));
	}

	private void handleOnPart(String line, String[] splitted) {
		String nick = getNickFromSplit(splitted[0]);
		String user = getUserFromSplit(splitted[0]);
		String host = getHostFromSplit(splitted[0]);
		String channel = splitted[2];
		String message = splitted.length >= 4 ? line.substring(1 + line.substring(1).indexOf(":") + 1) : null;

		fireEvent(new OnPartEvent(nick, user, host, channel, message));
	}

	private void handleOnTopic(String line, String[] splitted) {
		String nick = getNickFromSplit(splitted[0]);
		String user = getUserFromSplit(splitted[0]);
		String host = getHostFromSplit(splitted[0]);
		String channel = splitted[2];
		String topic = line.substring(1 + line.substring(1).indexOf(":") + 1);
		
		fireEvent(new OnTopicChangeEvent(nick, user, host, channel, topic));
	}

	private void handleOnKick(String line, String[] splitted) {
		String kicker_nick = getNickFromSplit(splitted[0]);
		String kicker_user = getUserFromSplit(splitted[0]);
		String kicker_host = getHostFromSplit(splitted[0]);
		String channel = splitted[2];
		String nick = splitted[3];
		String reason = line.substring(1 + line.substring(1).indexOf(":") + 1);
		
		if(reason.equals(kicker_nick)){
			fireEvent(new OnKickEvent(kicker_nick, kicker_user, kicker_host, nick, channel));
		}else{
			fireEvent(new OnKickEvent(kicker_nick, kicker_user, kicker_host, nick, channel, reason));
		}
	}

	private void handleOnInvite(String line, String[] splitted) {
		String nick = getNickFromSplit(splitted[0]);
		String user = getUserFromSplit(splitted[0]);
		String host = getHostFromSplit(splitted[0]);
		String channel = splitted[3].substring(1);
		
		fireEvent(new OnInviteEvent(nick, user, host, channel));
	}

	private void handleMessages(String line, String[] splitted) {
		String nick = getNickFromSplit(splitted[0]);
		String user = getUserFromSplit(splitted[0]);
		String host = getHostFromSplit(splitted[0]);
		String userOrChannel = splitted[2];
		String message = line.substring(1 + line.substring(1).indexOf(":") + 1);
		
		if(userOrChannel.startsWith("#")){
			fireEvent(new OnMessageEvent(userOrChannel, nick, user, host, message));
		}else{
			fireEvent(new OnPrivateMessageEvent(nick, user, host, message));
		}
	}

	private void handleOnUserJoin(String channel, String nick, String[] splitted) {
		String user = getUserFromSplit(splitted[0]);
		String host = getHostFromSplit(splitted[0]);
		
		fireEvent(new OnUserJoinEvent(channel, nick, user, host));
	}

	private void handleOnJoin(String channel, String line, String[] splitted, BufferedReader br2) throws IOException {
		ArrayList<String> namesArray = new ArrayList<String>();
		
		boolean namesEnd = false;
		while(!namesEnd){
			String namesLine = nextLine();
			
			String[] namesSplit = namesLine.split("[ ]");
			if(namesSplit != null && namesSplit.length >= 2){
				if(namesSplit[1].equals("366")){
					namesEnd = true;
					continue;
				}else if(namesSplit[1].equals("353")){
					namesSplit = namesLine.substring(1).split(":")[1].split("[ ]");
					for (int j = 0; j < namesSplit.length; j++) {
						namesArray.add(namesSplit[j]);
					}
				}
			}
		}
		
		String[] names = namesArray.toArray(new String[namesArray.size()]);
		String[] prefixes = new String[names.length];
		for (int i = 0; i < names.length; i++) {
			if(IrcUtilities.nickHasPrefix(names[i])){
				prefixes[i] = ""+names[i].charAt(0);
				names[i] = names[i].substring(1);
			}else{
				prefixes[i] = "";
			}
		}
		
		fireEvent(new OnJoinEvent(channel, names, prefixes));
	}

	private void handleOnFailToJoin(String line, String[] splitted) {
		String channel = splitted[3];
		String reason = line.substring(1 + line.substring(1).indexOf(":") + 1);
		
		fireEvent(new OnFailToJoinEvent(channel, reason));
	}

	private void handleOnChannelModeChange(String line, String[] splitted) {
		String nick = getNickFromSplit(splitted[0]);
		String user = getUserFromSplit(splitted[0]);
		String host = getHostFromSplit(splitted[0]);
		String channel = splitted[2];
		String mode    = splitted[3];
		
		fireEvent(new OnChannelModeChangeEvent(nick, user, host, channel, mode));
	}
	
	private void handleOnUserChannelModeChange(String line, String[] splitted) {
		if(splitted.length < 4){
			logError("handleOnUserChannelModeChange received invalid splitted: "+Arrays.toString(splitted));
			return;
		}
		
		String nick = getNickFromSplit(splitted[0]);
		String user = getUserFromSplit(splitted[0]);
		String host = getHostFromSplit(splitted[0]);
		String channel = splitted[2];
		String[] nicks = Arrays.copyOfRange(splitted, 4, splitted.length);
		
		ArrayList<String> userChannelModes = new ArrayList<String>();
		String channelModes = "";
		
		char[] modeChars = splitted[3].toCharArray();
		
		char modifier = '+';
		
		for (int i = 0; i < modeChars.length; i++) {
			if(modeChars[i] == '+' || modeChars[i] == '-'){
				modifier = modeChars[i];
			}else if(Character.toString(modeChars[i]).matches("[rmisp]")){
				channelModes = channelModes + modifier + modeChars[i];
			}else{
				userChannelModes.add(modifier+""+modeChars[i]);
				//TODO replace this with two lists
			}
		}
		
		if(channelModes.length() > 0){
			fireEvent(new OnChannelModeChangeEvent(nick, user, host, channel, channelModes));
		}
		
		if(nicks.length == userChannelModes.size()){
			String[] usrChMdsArr = userChannelModes.toArray(new String[userChannelModes.size()]);
			fireEvent(new OnUserChannelModeChangeEvent(nick, user, host, channel, usrChMdsArr, nicks));
		}else{
			logError("handleOnUserChannelModeChange: number of nicks ("+Arrays.toString(nicks)+") didnt correspond to number of modes, probably there is a mode here that should be recognized as a channel mode: "+userChannelModes.toString());
		}
	}

	private void handleOnUserModeChange(String[] splitted) {
		String nick1 = splitted[0].substring(1);
		String nick2 = splitted[2];
		String mode = splitted[3].substring(1);
		
		fireEvent(new OnUserModeChangeEvent(nick1, nick2, mode));
	}

	private void handleOnConnect(String line, String[] splitted, BufferedReader br) {
		String serverName = splitted[0].substring(1);
		fireEvent(new OnConnectEvent(serverName));
	}

	private void handleMessageOfTheDay(BufferedReader br) throws IOException {
		String motd = null;
		
		boolean motdEnd = false;
		while(!motdEnd){
			String motdLine = nextLine();
			String[] splittedMotd = motdLine.split("[ ]");
			if(splittedMotd != null && splittedMotd.length >= 2 && splittedMotd[1].equals("376")){
				motdEnd = true;
				continue;
			}else{
				splittedMotd = motdLine.split("[:]");
				if(splittedMotd != null && splittedMotd.length >= 2){
					motdLine = splittedMotd[2];
					if(motd == null){
						motd = motdLine;
					}else{
						motd = motd+"\n"+motdLine;
					}
				}
			}
		}
		
		fireEvent(new OnMessageOfTheDayEvent(motd));
	}

	public void fireEvent(IrcEvent event){
		ArrayList<IrcPlugin> plugins = ircBot.getPlugins();
		for (int i = 0; i < plugins.size(); i++) {
			plugins.get(i).addEvent(event);
		}
	}
	
	public String nextLine(){
		String line = null;
		try {
			line = br.readLine();
		} catch (IOException e) {
			/* This will only happen as the connection is broken */
			fireEvent(new OnConnectionClosedEvent("read line was null"));
			return null;
		}
		if(line == null){
			/* This will only happen as the connection is broken */
			fireEvent(new OnConnectionClosedEvent("read line was null"));
			return null;
		}
		return IrcUtilities.cleanText(line, !isUsingBoldAscii, !isUsingColorsAscii, !isUsingUnderlineAscii, !isUsingInvertAscii);
	}
	
	public String getNickFromSplit(String split){
		return split.substring(1, split.indexOf("!"));
	}
	
	public String getUserFromSplit(String split){
		String user = split.substring(split.indexOf("!") + 1, split.indexOf("@"));
		if(user.startsWith("~")){
			return user.substring(1);
		}else{
			return user;
		}
	}
	
	public String getHostFromSplit(String split){
		return split.substring(split.indexOf("@") + 1);
	}
	
	public boolean isChannel(String nickOrChannel){
		return nickOrChannel.startsWith("#");
	}

	public void stopReader() {
		running = false;
		try {
			br.close();
			if(!socket.isClosed()){
				socket.shutdownInput();
				socket.shutdownOutput();
			}
			socket.close();
		} catch (IOException e) {
//			e.printStackTrace();
		}
	}
	
	public void log(String message){
		if(verbose){
			System.out.println(message);
		}
	}
	
	public void logError(String message){
		if(verboseError){
			System.err.println(message);
		}
	}

}