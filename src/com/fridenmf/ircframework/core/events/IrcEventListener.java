package com.fridenmf.ircframework.core.events;

public interface IrcEventListener {
	
	/** This method gets called when the bot failed to join a channel */
	public void onFailToJoinEvent(OnFailToJoinEvent e);

	/** This method gets called after a connection to the server was made
	 * and after the nick and user has been sent.
	 * This is a convenient method to join channels from
	 */
	public void onConnect(OnConnectEvent e);
	
	/** This method gets called when the message of the day is received from the server
	 */
	public void onMessageOfTheDay(OnMessageOfTheDayEvent e);
	
	/** This method is called when someone changed a MODE on another user */
	public void onUserModeChange(OnUserModeChangeEvent e);
	
	/** This method is called when someone changed modes for one or more nicks in a channel
	 */
	public void onUserChannelModeChange(OnUserChannelModeChangeEvent e);
	
	/** This method is called when a user joins a channel */
	public void onUserJoin(OnUserJoinEvent e);
	
	/** This method is called when the bot joins a channel */
	public void onJoin(OnJoinEvent e);
	
	/** This method is called when the bot receives a list of nicks in a channel */
	public void onNames(OnNamesEvent e);

	/** This method is called when the bot receives a message in a channel */
	public void onMessage(OnMessageEvent e);
	
	/** This method is called when the bot receives a private message */
	public void onPrivateMessage(OnPrivateMessageEvent e);
	
	/** This method is called when the bot receives a PING request
	 * If not overrided, this method responds with a PONG */
	public void onPing(OnPingEvent e);
	
	/** This method is called when an invitation to a channel has ben received by the bot */
	public void onInvite(OnInviteEvent e);
	
	/** This method is called when someone has been kicked from a channel. 
	 * This can also be the bot itself. */
	public void onKick(OnKickEvent e);
	
	/** This method is called when a topic has been changed in any of the bot's channels */
	public void onTopicChange(OnTopicChangeEvent e);
	
	/** This method is called when a message failed to deliver
	 * The message itself is not on the event as it is not supported by the 
	 * IRC syntax, however, the channel and the reason is. */
	public void onMessageError(OnMessageErrorEvent e);
	
	/** This method is called when someone parts any of the bot's channels */
	public void onPart(OnPartEvent e);
	
	/** This method is called when someone changes a mode on a channel */
	public void onChannelModeChange(OnChannelModeChangeEvent e);
	
	/** This method is called when the server receives a line that was not handled
	 * THIS FRAMEWORK IS NOT DONE YET, PLEASE CONTACT ME(friden) IF YOU WISH THAT 
	 * SOME PARTICULAR UNHANDLED LINE SHOULD BE HANDLED */
	public void onUnhandledEvent(OnUnhandledEventEvent e);
	
	/** This method is called when the bot receives a notice in a channel */
	public void onNotice(OnNoticeEvent e);
	
	/** This method is called when a bot receives a private notice */
	public void onPrivateNotice(OnPrivateNoticeEvent e);
	
	/** This method is called when the bot iether goes away or turns away off.
	 * To determine wich, check e.isAway() */
	public void onAway(OnAwayEvent e);
	
	/** This method gets called when a try to message someone that was away has
	 * been made, and a reply was automaticly replied to the bot. */
	public void onAwayReply(OnAwayReplyEvent e);
	
	/** This method gets called when the bot tries to change to a nick that is already in use */
	public void onNickAlreadyTaken(OnNickAlreadyTakenEvent e);
	
	/** This method is called when a each new day */
	public void onDayChanged(OnDayChangedEvent e);
	
	/** Here for debugging purpose only */
	public void onTimedDebug(OnTimedDebugEvent e);
	
	/** This method is called when the bot receives a whois reply */
	public void onWhoisReply(OnWhoisReplyEvent e);
	
	/** This method is called when the server replies that a nick does not exist */
	public void onNoSuchNick(OnNoSuckNickEvent e);

	/** This method is called when someone changes nick */
	public void onNick(OnNickEvent e);
	
	/** This is a method called when the connection was closed */
	public void onConnectionClosed(OnConnectionClosedEvent e);
	
}
