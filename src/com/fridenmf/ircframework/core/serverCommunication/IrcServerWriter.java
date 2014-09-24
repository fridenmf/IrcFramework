package com.fridenmf.ircframework.core.serverCommunication;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import com.fridenmf.ircframework.core.extendable.IrcBot;

public class IrcServerWriter extends Thread {
	
	private IrcBot ircBot = null;

	private BufferedWriter bw = null;

	private ArrayList<String> messageQueue = null;
	
	private Semaphore messageSem = new Semaphore(0);
	private Semaphore mutexSem   = new Semaphore(1);

	private boolean running      = false;
	private boolean verbose      = false;
	private boolean verboseError = false;
	private boolean isConnected  = false;
	
	private boolean isStoppedByConnectionError = false;
	
	public boolean getIsStoppedByConnectionError(){
		return isStoppedByConnectionError;
	}

	public void hasConnected(){
		isConnected = true;
		messageSem.release();
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

	public void stopWriter(){
		running = false;
		messageSem.release();
	}

	@Override
	public void run() {

		while(running){

			messageSem.acquireUninterruptibly();

			mutexSem.acquireUninterruptibly();
			String toSend = getNextMessage();
			boolean gotMore = !messageQueue.isEmpty();
			mutexSem.release();

			if(toSend != null){
				if(verbose){
					log("--> "+toSend.substring(0, toSend.length()-2));
				}
				try {
					bw.write(toSend);
					bw.flush();
				} catch (IOException e) {
					isStoppedByConnectionError = true;
					logError("Got an IO Exception, stopping thread");
					running = false;
					continue;
				}
			}

			if(gotMore && messageSem.availablePermits() <= 0){
				messageSem.release();
			}

		}

		try {
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ircBot.writerIsDead();
	}

	public boolean nickUserOrPong(String str){
		return str.startsWith("NICK") || str.startsWith("USER") || str.startsWith("PONG");
	}

	public String getFirstNickUserOrPong(){
		for (int i = 0; i < messageQueue.size(); i++) {
			if(nickUserOrPong(messageQueue.get(i))){
				return messageQueue.remove(i);
			}
		}
		return null;
	}

	private String getNextMessage() {
		String result = null;
		if(isConnected){
			if(!messageQueue.isEmpty()){
				result = messageQueue.remove(0);
			}
		}else{
			result = getFirstNickUserOrPong();
		}
		return result;
	}

	public IrcServerWriter(IrcBot ircBot, Socket socket) {
		this.ircBot = ircBot;
		messageQueue = new ArrayList<String>();
		try {
			bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addMessage(String message){
		mutexSem.acquireUninterruptibly();
		messageQueue.add(message+"\r\n");
		mutexSem.release();
		messageSem.release();
	}

	public void quitServer(String message) {
		if(message != null){
			addMessage("QUIT :"+message);
		}else{
			addMessage("QUIT");
		}

	}

	private String fixChannel(String channel) {
		if(channel == null){
			return null;
		}else if(!channel.startsWith("#")){
			return "#"+channel;
		}else{
			return channel;
		}
	}

	public void sendMessage(String channel, String message) {
		if(channel != null && message != null){
			channel = fixChannel(channel);
			addMessage("PRIVMSG "+channel+" :"+message);
		}
	}

	public void sendPrivateMessage(String nick, String message) {
		if(nick != null && message != null){
			addMessage("PRIVMSG "+nick+" :"+message);
		}
	}

	public void sendNotice(String channel, String message) {
		if(channel != null && message != null){
			channel = fixChannel(channel);
			addMessage("NOTICE "+channel+" :"+message);
		}
	}

	public void sendPrivateNotice(String nick, String message) {
		if(nick != null && message != null){
			addMessage("NOTICE "+nick+" :"+message);
		}
	}

	public void join(String channel) {
		if(channel != null){
			channel = fixChannel(channel);
			addMessage("JOIN "+channel);
		}
	}

	public void nick(String nick) {
		if(nick == null){
			return;
		}
		addMessage("NICK "+nick);
	}

	public void user(String user, String description) {
		if(user == null){
			return;
		}
		addMessage("USER "+user+" 8 * : "+(description==null?"":description));
	}

	public void pong(String message) {
		if(message == null){
			return;
		}
		addMessage("PONG "+message);
	}

	public void whois(String nick) {
		if(nick == null){
			return;
		}
		addMessage("WHOIS "+nick);
	}

	public void whowas(String nick) {
		if(nick == null){
			return;
		}
		addMessage("WHOWAS "+nick);
	}

	public void kick(String channel, String nick, String reason) {
		if(nick == null || channel == null){
			return;
		}
		channel = fixChannel(channel);
		addMessage("KICK "+channel+" "+nick+(reason==null?"":" :"+reason));
	}

	public void away(String reason) {
		addMessage("AWAY"+(reason==null?"":" "+reason));
	}

	public void rehash() {
		addMessage("REHASH");
	}

	public void users() {
		addMessage("USERS");
	}

	public void part(String channel, String message) {
		if(channel == null){
			return;
		}
		channel = fixChannel(channel);
		addMessage("PART "+channel+" "+(message==null?"":" "+message));
	}

	public void userMode(String modes, String nick){
		if(modes == null || nick == null){
			return;
		}
		addMessage("MODE "+modes+" "+nick);
	}

	public void userModeInChannel(String channel, String modes, String nicks){
		if(channel == null || modes == null || nicks == null){
			return;
		}
		channel = fixChannel(channel);
		addMessage("MODE "+channel+" "+modes+" "+nicks);
	}

	public void channelMode(String channel, String modes){
		if(channel == null || modes == null){
			return;
		}
		channel = fixChannel(channel);
		addMessage("MODE "+channel+" "+modes);
	}

	public void getTopic(String channel) {
		if(channel == null){
			return;
		}
		channel = fixChannel(channel);
		addMessage("TOPIC "+channel);
	}

	public void setTopic(String channel, String topic){
		if(channel == null){
			return;
		}
		channel = fixChannel(channel);
		addMessage("TOPIC "+channel+" :"+(topic==null?"":topic));
	}

	public void raw(String raw) {
		if(raw == null){
			return;
		}
		addMessage(raw);
	}

	public void mode(String channel, String modes, String nicks) {
		channel = fixChannel(channel);
		addMessage("MODE "+channel+" "+modes+" "+nicks);
	}

	public void modeChannel(String channel, String modes) {
		channel = fixChannel(channel);
		addMessage("MODE "+channel+" "+modes);
	}

	public void modeUser(String nick, String modes) {
		addMessage("MODE "+nick+" "+modes);
	}

	public void names(String channel) {
		addMessage("NAMES "+channel);
	}

	public void quit(String message) {
		addMessage("QUIT "+(message==null?"":" "+message));
	}

	public void invite(String channel, String nick) {
		channel = fixChannel(channel);
		addMessage("INVITE "+nick+" "+channel);
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