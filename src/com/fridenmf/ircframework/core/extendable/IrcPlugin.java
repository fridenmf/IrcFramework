package com.fridenmf.ircframework.core.extendable;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import com.fridenmf.ircframework.core.events.IrcEvent;
import com.fridenmf.ircframework.core.events.IrcEventListener;
import com.fridenmf.ircframework.core.events.OnAwayEvent;
import com.fridenmf.ircframework.core.events.OnAwayReplyEvent;
import com.fridenmf.ircframework.core.events.OnChannelModeChangeEvent;
import com.fridenmf.ircframework.core.events.OnConnectEvent;
import com.fridenmf.ircframework.core.events.OnConnectionClosedEvent;
import com.fridenmf.ircframework.core.events.OnDayChangedEvent;
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
import com.fridenmf.ircframework.core.events.OnTimedDebugEvent;
import com.fridenmf.ircframework.core.events.OnTopicChangeEvent;
import com.fridenmf.ircframework.core.events.OnUnhandledEventEvent;
import com.fridenmf.ircframework.core.events.OnUserChannelModeChangeEvent;
import com.fridenmf.ircframework.core.events.OnUserJoinEvent;
import com.fridenmf.ircframework.core.events.OnUserModeChangeEvent;
import com.fridenmf.ircframework.core.events.OnWhoisReplyEvent;
import com.fridenmf.ircframework.core.timedEvents.TimedDebugEvent;
import com.fridenmf.ircframework.core.timedEvents.TimedEventHandler;
import com.fridenmf.ircframework.core.timedEvents.TimedEventSleepThread;
import com.fridenmf.ircframework.core.timedEvents.TimedMessage;
import com.fridenmf.ircframework.core.utilities.IrcUtilities;

/**
 * A plugin implements the behavior of a bot 
 * @author friden
 */
public abstract class IrcPlugin extends Thread implements IrcEventListener {

	private TimedEventHandler timedEventHandler = null;
	private TimedEventSleepThread timedEventSleepThread = null;

	protected IrcBot ircBot = null;
	protected String pluginName = null;
	
	private Semaphore eventSem = new Semaphore(0);
	private Semaphore mutexSem = new Semaphore(1);
	
	private ArrayList<IrcEvent> ircEvents = new ArrayList<IrcEvent>();
	
	private boolean isRunning       = false;
	private boolean isRestartable   = false;
	private boolean alreadyStarted  = false;
	private boolean stopped         = false;
	private boolean isLoggingErrors = false; 
	
	private Semaphore restartSem = new Semaphore(1);
	
	public IrcPlugin(IrcBot ircBot, String pluginName){
		this.ircBot     = ircBot;
		this.pluginName = pluginName;
		timedEventHandler     = new TimedEventHandler(this);
		timedEventSleepThread = new TimedEventSleepThread(timedEventHandler);
	}
	
	@Override
	public synchronized void start() {
		isRestartable = true;
		isRunning = true;
		timedEventHandler.start();
		timedEventSleepThread.start();
		super.start();
	}
	
	public void setIsLoggingErrors(boolean b){
		isLoggingErrors = b;
	}
	
	public void stopPlugin(){
		if(stopped){
			return;
		}
		
		stopped = true;
		isRunning = false;
		isRestartable = false;
		
		//TODO This is just to be sure it releases its lock, solve this nicer
		restartSem.release(100);
		eventSem.release(100);
	}
	
	public boolean isAlreadyStarted() {
		return alreadyStarted;
	}

	protected void restart() {
		isRunning = true;
		restartSem.release();
	}
	
	@Override
	public void run() {
		
		alreadyStarted = true;
		
		/* For restarting the thread */
		while(isRestartable){
			
			restartSem.acquireUninterruptibly();
		
			while(isRunning){
				
				IrcEvent ircEvent = null;
				
				eventSem.acquireUninterruptibly();
				mutexSem.acquireUninterruptibly();
				if(!ircEvents.isEmpty()){
					ircEvent = ircEvents.remove(0);
				}
				mutexSem.release();
				
				if(ircEvent != null){
					onSomething(ircEvent);
				}
			}
		}
	}
	
	public void addEvent(IrcEvent ircEvent){
		mutexSem.acquireUninterruptibly();
		ircEvents.add(ircEvent);
		mutexSem.release();
		eventSem.release();
	}
	
	public String getPluginName(){
		return pluginName;
	}
	
	private void onSomething(IrcEvent e){
		if(e instanceof OnConnectEvent){
			ircBot.getIrcWriter().hasConnected();
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
			isRunning = false;
			isRestartable = false;
			onConnectionClosed((OnConnectionClosedEvent)e);
		}else{
			logError("Unhandled event in plugin: "+e.getEventName());
		}
	}
	
	private void logError(String string) {
		if(isLoggingErrors){
			System.err.println(string);
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
			logError("channel == null");
			return;
		}
		if(modes == null){
			logError("modes == null");
			return;
		}
		if(nicks == null){
			logError("nicks == null");
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