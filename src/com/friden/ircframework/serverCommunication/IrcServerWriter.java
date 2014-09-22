package com.friden.ircframework.serverCommunication;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class IrcServerWriter extends Thread {

	private BufferedWriter bw = null;
	
	private ArrayList<String> messageQueue = null;
	private Semaphore messageSem = new Semaphore(0);
	private Semaphore mutexSem = new Semaphore(1);
	
	private boolean running = false;
	private boolean verboseOut = false;
	
	public void setVerbose(boolean b){
		this.verboseOut = b;
	}
	
	@Override
	public synchronized void start() {
		running = true;
		super.start();
	}
	
	public void stopWriter(){
		running = false;
	}
	
	@Override
	public void run() {
		
		while(running){
			try {
				messageSem.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
				continue;
			}
			try {
				
				mutexSem.acquireUninterruptibly();
				String toSend = messageQueue.remove(0);
				mutexSem.release();
				
				if(verboseOut){
					System.out.println("--> "+toSend.substring(0, toSend.length()-2));
				}
				
				bw.write(toSend);
				bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public IrcServerWriter(Socket socket) {
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

	public void close() {
		running = false;
	}

	public void invite(String channel, String nick) {
		channel = fixChannel(channel);
		addMessage("INVITE "+nick+" "+channel);
	}
	
}