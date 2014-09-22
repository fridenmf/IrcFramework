package com.friden.ircframework.extendable;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import com.friden.ircframework.events.IrcEvent;
import com.friden.ircframework.events.IrcEventListener;
import com.friden.ircframework.events.OnAwayEvent;
import com.friden.ircframework.events.OnAwayReplyEvent;
import com.friden.ircframework.events.OnChannelModeChangeEvent;
import com.friden.ircframework.events.OnConnectEvent;
import com.friden.ircframework.events.OnConnectionClosedEvent;
import com.friden.ircframework.events.OnDayChangedEvent;
import com.friden.ircframework.events.OnFailToJoinEvent;
import com.friden.ircframework.events.OnInviteEvent;
import com.friden.ircframework.events.OnJoinEvent;
import com.friden.ircframework.events.OnKickEvent;
import com.friden.ircframework.events.OnMessageErrorEvent;
import com.friden.ircframework.events.OnMessageEvent;
import com.friden.ircframework.events.OnMessageOfTheDayEvent;
import com.friden.ircframework.events.OnNamesEvent;
import com.friden.ircframework.events.OnNickAlreadyTakenEvent;
import com.friden.ircframework.events.OnNickEvent;
import com.friden.ircframework.events.OnNoSuckNickEvent;
import com.friden.ircframework.events.OnNoticeEvent;
import com.friden.ircframework.events.OnPartEvent;
import com.friden.ircframework.events.OnPingEvent;
import com.friden.ircframework.events.OnPrivateMessageEvent;
import com.friden.ircframework.events.OnPrivateNoticeEvent;
import com.friden.ircframework.events.OnTimedDebugEvent;
import com.friden.ircframework.events.OnTopicChangeEvent;
import com.friden.ircframework.events.OnUnhandledEventEvent;
import com.friden.ircframework.events.OnUserChannelModeChangeEvent;
import com.friden.ircframework.events.OnUserJoinEvent;
import com.friden.ircframework.events.OnUserModeChangeEvent;
import com.friden.ircframework.events.OnWhoisReplyEvent;
import com.friden.ircframework.timedEvents.TimedDebugEvent;
import com.friden.ircframework.timedEvents.TimedEventHandler;
import com.friden.ircframework.timedEvents.TimedEventSleepThread;
import com.friden.ircframework.timedEvents.TimedMessage;
import com.friden.ircframework.utilities.IrcUtilities;




public abstract class IrcPlugin extends Thread implements IrcEventListener {

	private TimedEventHandler timedEventHandler = null;
	private TimedEventSleepThread timedEventSleepThread = null;
	
	private String pluginName = null;
	
	private Semaphore eventSem = new Semaphore(0);
	private Semaphore mutexSem = new Semaphore(1);
	
	private ArrayList<IrcEvent> ircEvents = new ArrayList<IrcEvent>();
	
	private boolean running = false;
	
	private IrcBot ircBot = null;
	private boolean restartable = true;
	
	private boolean alreadyStarted = false;
	
	private Semaphore restartSem = new Semaphore(1);
	
	public IrcPlugin(IrcBot ircBot, String pluginName){
		this.ircBot = ircBot;
		this.pluginName = pluginName;
		
		timedEventHandler = new TimedEventHandler(this);
		timedEventSleepThread = new TimedEventSleepThread(timedEventHandler);
		
		timedEventHandler.start();
		timedEventSleepThread.start();
	}
	
	@Override
	public synchronized void start() {
		running = true;
		super.start();
	}
	
	public void stopPlugin(){
		running = false;
	}
	
	public boolean isAlreadyStarted() {
		return alreadyStarted;
	}

	protected void restart() {
		running = true;
		restartSem.release();
	}
	
	@Override
	public void run() {
		
		alreadyStarted = true;
		
		/* For restarting the thread */
		while(restartable){
			
			restartSem.acquireUninterruptibly();
		
			while(running){
				
				try {
					eventSem.acquire();
				} catch (InterruptedException e) {
					/* Was interrupted, try again */
					continue;
				}
				
				/* There is an event in the queue*/
				try {
					mutexSem.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
					continue;
				}
				IrcEvent ircEvent = ircEvents.remove(0);
				mutexSem.release();
				
				onSomething(ircEvent);
				
			}
			
		}
		
	}
	
	public void addEvent(IrcEvent ircEvent){
		
		boolean hasMutex = false;
		
		while(!hasMutex){
			try {
				mutexSem.acquire();
				hasMutex = true;
			} catch (InterruptedException e) {
				/* Was interrupted, try again */
			}
		}
		
		ircEvents.add(ircEvent);
		
		mutexSem.release();
		eventSem.release();
	}
	
	public String getPluginName(){
		return pluginName;
	}
	
	private void onSomething(IrcEvent e){
		if(e instanceof OnConnectEvent){
			onConnect((OnConnectEvent)e);
		}else if(e instanceof OnPingEvent){ 
			onPing((OnPingEvent)e);
		}else if(e instanceof OnMessageOfTheDayEvent){ 
			onMessageOfTheDay((OnMessageOfTheDayEvent)e);
		}else if(e instanceof OnFailToJoinEvent){ 
			onFailToJoinEvent((OnFailToJoinEvent)e);
		}else if(e instanceof OnUserModeChangeEvent){ 
			onUserModeChange((OnUserModeChangeEvent)e);
		}else if(e instanceof OnUserChannelModeChangeEvent){ 
			onUserChannelModeChange((OnUserChannelModeChangeEvent)e);
		}else if(e instanceof OnUserJoinEvent){ 
			onUserJoin((OnUserJoinEvent)e);
		}else if(e instanceof OnJoinEvent){ 
			onJoin((OnJoinEvent)e);
		}else if(e instanceof OnNamesEvent){ 
			onNames((OnNamesEvent)e);
		}else if(e instanceof OnMessageEvent){ 
			onMessage((OnMessageEvent)e);
		}else if(e instanceof OnKickEvent){ 
			onKick((OnKickEvent)e);
		}else if(e instanceof OnPrivateMessageEvent){ 
			onPrivateMessage((OnPrivateMessageEvent)e);
		}else if(e instanceof OnMessageErrorEvent){ 
			onMessageError((OnMessageErrorEvent)e);
		}else if(e instanceof OnTopicChangeEvent){ 
			onTopicChange((OnTopicChangeEvent)e);
		}else if(e instanceof OnInviteEvent){ 
			onInvite((OnInviteEvent)e);
		}else if(e instanceof OnPartEvent){ 
			onPart((OnPartEvent)e);
		}else if(e instanceof OnChannelModeChangeEvent){ 
			onChannelModeChange((OnChannelModeChangeEvent)e);
		}else if(e instanceof OnUnhandledEventEvent){ 
			onUnhandledEvent((OnUnhandledEventEvent)e);
		}else if(e instanceof OnNoticeEvent){ 
			onNotice((OnNoticeEvent)e);
		}else if(e instanceof OnPrivateNoticeEvent){ 
			onPrivateNotice((OnPrivateNoticeEvent)e);
		}else if(e instanceof OnAwayEvent){ 
			onAway((OnAwayEvent)e);
		}else if(e instanceof OnAwayReplyEvent){ 
			onAwayReply((OnAwayReplyEvent)e);
		}else if(e instanceof OnNickAlreadyTakenEvent){ 
			onNickAlreadyTaken((OnNickAlreadyTakenEvent)e);
		}else if(e instanceof OnDayChangedEvent){ 
			onDayChanged((OnDayChangedEvent)e);
		}else if(e instanceof OnTimedDebugEvent){ 
			onTimedDebug((OnTimedDebugEvent)e);
		}else if(e instanceof OnNoSuckNickEvent){ 
			onNoSuchNick((OnNoSuckNickEvent)e);
		}else if(e instanceof OnWhoisReplyEvent){ 
			onWhoisReply((OnWhoisReplyEvent)e);
		}else if(e instanceof OnNickEvent){ 
			onNick((OnNickEvent)e);
		}else if(e instanceof OnConnectionClosedEvent){ 
			running = false;
			onConnectionClosed((OnConnectionClosedEvent)e);
		}else{
			System.err.println("UNHANDLED EVENT IN IRCPLUGIN: "+e.getEventName());
		}
	}
	
	@Override
	public void onConnectionClosed(OnConnectionClosedEvent e) {	}
	
	@Override
	public void onConnect(OnConnectEvent e) { }
	
	@Override
	public void onFailToJoinEvent(OnFailToJoinEvent e) { }

	@Override
	public void onMessageOfTheDay(OnMessageOfTheDayEvent e) { }
	
	@Override
	public void onUserModeChange(OnUserModeChangeEvent e) { }
	
	@Override
	public void onUserJoin(OnUserJoinEvent e) { }
	
	@Override
	public void onJoin(OnJoinEvent e) { }

	@Override
	public void onNames(OnNamesEvent e) { }
	
	@Override
	public void onMessage(OnMessageEvent e) {	}
	
	@Override
	public void onPrivateMessage(OnPrivateMessageEvent e) { }
	
	@Override
	public void onUserChannelModeChange(OnUserChannelModeChangeEvent e) { }
	
	@Override
	public void onInvite(OnInviteEvent e) { }
	
	@Override
	public void onKick(OnKickEvent e) {	}
	
	@Override
	public void onTopicChange(OnTopicChangeEvent e) { }
	
	@Override
	public void onMessageError(OnMessageErrorEvent e) {	}

	@Override
	public void onChannelModeChange(OnChannelModeChangeEvent e) { }
	
	@Override
	public void onUnhandledEvent(OnUnhandledEventEvent e) {	}
	
	@Override
	public void onPart(OnPartEvent e) {	}
	
	@Override
	public void onNotice(OnNoticeEvent e) { }
	
	@Override
	public void onPrivateNotice(OnPrivateNoticeEvent e) { }

	@Override
	public void onAway(OnAwayEvent e) { }
	
	@Override
	public void onAwayReply(OnAwayReplyEvent e) { }
	
	@Override
	public void onNickAlreadyTaken(OnNickAlreadyTakenEvent e) { }
	
	@Override
	public void onDayChanged(OnDayChangedEvent e) { }
	
	@Override
	public void onTimedDebug(OnTimedDebugEvent e) {	}
	
	@Override
	public void onWhoisReply(OnWhoisReplyEvent e) {	}
	
	@Override
	public void onNoSuchNick(OnNoSuckNickEvent e) {	}
	
	@Override
	public void onNick(OnNickEvent e) {	}
	
	@Override
	public void onPing(OnPingEvent e) {
		ircBot.getIrcWriter().pong(e.getMessage());
	}
	
	public void join(String channel) {
		ircBot.getIrcWriter().join(channel);
	}
	
	public void nick(String nick){
		ircBot.getIrcWriter().nick(nick);
	}
	
	public void user(String user, String description){
		ircBot.getIrcWriter().user(user, description == null ? "" : description);
	}
	
	public void whois(String nick) {
		ircBot.getIrcWriter().whois(nick);
	}
	
	public void whowas(String nick) {
		ircBot.getIrcWriter().whowas(nick);
	}
	
	public void sendMessage(String channel, String message) {
		ircBot.getIrcWriter().sendMessage(channel, message);
	}
	
	public void sendPrivateMessage(String nick, String message) {
		ircBot.getIrcWriter().sendPrivateMessage(nick, message);
	}
	
	public void sendNotice(String channel, String message) {
		ircBot.getIrcWriter().sendNotice(channel, message);
	}
	
	public void sendPrivateNotice(String nick, String message) {
		ircBot.getIrcWriter().sendPrivateNotice(nick, message);
	}
	
	public void part(String channel, String message){
		ircBot.getIrcWriter().part(channel, message);
	}
	
	public void part(String channel){
		ircBot.getIrcWriter().part(channel, null);
	}
	
	public void kick(String channel, String nick){
		ircBot.getIrcWriter().kick(channel, nick, null);
	}
	
	public void kick(String channel, String nick, String reason){
		ircBot.getIrcWriter().kick(channel, nick, reason);
	}
	
	public void away_off() {
		ircBot.getIrcWriter().away(null);
	}
	
	public void away_on(String message) {
		ircBot.getIrcWriter().away(message);
	}
	
	public void sendRAW(String raw) {
		ircBot.getIrcWriter().raw(raw);
	}
	
	/**
	 * Requests the list of nicks of visible users in a channel 
	 */
	public void names(String channel) {
		if(channel == null || channel.length() == 0){
			return;
		}
		ircBot.getIrcWriter().names(channel);
	}
	
	public void mode(String channel, String[] modes, String[] nicks) {
		if(channel == null){
			System.err.println("channel == null");
			return;
		}
		if(modes == null){
			System.err.println("modes == null");
			return;
		}
		if(nicks == null){
			System.err.println("nicks == null");
			return;
		}
		if(modes.length != nicks.length){
			throw new IllegalArgumentException();
		}
		
		String mergedModes = IrcUtilities.stringArrayToString(modes, "");
		String strippedModes = mergedModes.replaceAll("[+-]", "");
		
		if(strippedModes.length() != nicks.length){
			throw new IllegalArgumentException("The number of modes ("+strippedModes.length()+") did not match the number of nicks ("+nicks.length+")");
		}
		
		ircBot.getIrcWriter().mode(channel, mergedModes, IrcUtilities.stringArrayToString(nicks, " "));
	}

	/**
	 * @param mode A mode, examples: +o or -v
	 * @param nick nick to change mode for
	 */
	public void mode(String channel, String mode, String nick) {
		if(channel == null || mode == null || nick == null){
			return;
		}
		if(mode.replaceAll("[+-]", "").length() != 1 || nick.indexOf(" ") != -1){
			throw new IllegalArgumentException();
		}
		ircBot.getIrcWriter().mode(channel, mode, nick);
	}
	
	public long scheduleMessage(long target, String message){
		TimedMessage timedMessage = new TimedMessage(target, message);
		timedEventSleepThread.addTimedEvent(timedMessage);
		return timedMessage.getIdentifier();
	}
	
	public long scheduleTimedDebug(){
		timedEventSleepThread.waitForQueueMutex();
		TimedDebugEvent timedDebugEvent = new TimedDebugEvent(System.currentTimeMillis() + 2000);
		timedEventSleepThread.releaseQueueMutex();
		timedEventSleepThread.addTimedEvent(timedDebugEvent);
		return timedDebugEvent.getIdentifier();
	}
	
	/**
	 * UNIMPLEMENTED 
	 */
	public void removeScheduleEvent(long eventId){
		//TODO do this
	}
	
	/**
	 * Method to set topic in a channel
	 */
	public void setTopic(String channel, String topic){
		ircBot.getIrcWriter().setTopic(channel, topic);
	}
	
	/**
	 * Quits the server with a message. If no message is wanted, use null as parameter 
	 */
	public void quit(String message){
		ircBot.getIrcWriter().quit(message);
	}
	
	/**
	 * Invites a nick to a channel
	 */
	public void invite(String channel, String nick){
		ircBot.getIrcWriter().invite(channel, nick);
	}

}